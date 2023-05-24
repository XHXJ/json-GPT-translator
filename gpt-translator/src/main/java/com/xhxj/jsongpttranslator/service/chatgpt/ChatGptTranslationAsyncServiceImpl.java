package com.xhxj.jsongpttranslator.service.chatgpt;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.util.concurrent.RateLimiter;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import com.unfbx.chatgpt.interceptor.OpenAiResponseInterceptor;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestRespVo;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestVo;
import com.xhxj.jsongpttranslator.dal.dataobject.OpenaiProperties;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.framework.chatgptconfig.ChatgptConfig;
import com.xhxj.jsongpttranslator.framework.web.exception.ServiceException;
import com.xhxj.jsongpttranslator.service.openaiproperties.OpenaiPropertiesService;
import com.xhxj.jsongpttranslator.service.translationdata.TranslationDataService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xhxj.jsongpttranslator.controller.OpenaiProperties.error.ErrorCodeConstants.OPENAIKEY_DOES_NOT_EXIST;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-03 12:16
 * @Description: 异步任务实现
 */
@Service
@Slf4j
public class ChatGptTranslationAsyncServiceImpl implements ChatGptTranslationAsyncService {


    @Autowired
    private TranslationDataService translationDataService;

    @Autowired
    private OpenaiPropertiesService openaiPropertiesService;

    private final AtomicInteger apiCallCounter = new AtomicInteger(0);
    private final AtomicInteger apiCallRpmCounter = new AtomicInteger(0);
    private final Object lock = new Object();
    private long lastMinuteTimestamp = System.currentTimeMillis();
    private static int MAX_API_CALLS_PER_MINUTE = 3000; // 每分钟的API调用限制 (RPM)
    private static int MAX_TOKENS_PER_MINUTE = 300000; // 每分钟的令牌限制 (TPM)
    private static int MAX_REQUESTS_PER_SECOND = 2; // 每秒最大请求速率

    private final RateLimiter rateLimiter = RateLimiter.create(MAX_REQUESTS_PER_SECOND);


    @Resource(name = "missingRowData")
    private Map<Long, TranslationData> missingRowData;

    @Resource(name = "errorData")
    private Map<Long, TranslationData> errorData;

    /**
     * gpt配置
     */
    @Autowired
    private ChatgptConfig chatgptConfig;

    /**
     * 获得openaikey
     */
    private List<String> getOpenaiKey() {
        List<OpenaiProperties> openaiPropertiesList = openaiPropertiesService.list();
        if (openaiPropertiesList.isEmpty()) {
            throw new ServiceException(OPENAIKEY_DOES_NOT_EXIST);
        }
        apiRestrictionSettings(openaiPropertiesList.size());

        return openaiPropertiesList.stream().map(OpenaiProperties::getOpenaiKey).collect(Collectors.toList());
    }

    /**
     * api限制设置
     *
     * @param size key的数量
     */
    private static void apiRestrictionSettings(int size) {
        //设置翻译api的调用限制为key的数量倍数
        MAX_API_CALLS_PER_MINUTE = size * 3000;
        MAX_TOKENS_PER_MINUTE = size * 300000;
        MAX_REQUESTS_PER_SECOND = size * 5;
    }

    /**
     * 限制每分钟的API调用
     *
     * @param token 消耗的令牌
     */
    private void waitForApiCallAvailability(long token) {
        rateLimiter.acquire(); // 请求一个许可证，根据当前速率限制可能需要等待

        while (true) {
            synchronized (lock) {
                long currentTimestamp = System.currentTimeMillis();
                if (currentTimestamp - lastMinuteTimestamp >= 60000) {
                    // 如果已经过去一分钟，重置计数器和时间戳
                    apiCallCounter.set(0);
                    apiCallRpmCounter.set(0);
                    lastMinuteTimestamp = currentTimestamp;
                }
                if (apiCallCounter.get() + token <= MAX_TOKENS_PER_MINUTE && apiCallRpmCounter.get() < MAX_API_CALLS_PER_MINUTE) {
                    // 如果未超过限制，递增计数器
                    apiCallCounter.addAndGet(Math.toIntExact(token));
                    apiCallRpmCounter.incrementAndGet();
                    break;
                } else {
                    // 如果超过限制，使线程等待直到下一分钟
                    try {
                        long remainingMillis = 60000 - (currentTimestamp - lastMinuteTimestamp);
                        lock.wait(remainingMillis);
                    } catch (InterruptedException e) {
                        log.error("等待时线程异常退出", e);
                    }
                }
            }
        }
    }


    /**
     * 报告当前api限制计数器
     */
//    @Scheduled(fixedRate = 10000)
    @Override
    public void reportCurrentApiLimitCounters() {
        log.info("当前API限制计数器: {}", apiCallCounter.get());
    }

    @Async("chatGptTaskExecutor")
    @Override
    public CompletableFuture<Objects> accessingChatGpt(List<TranslationData> translationData) {
        try {
            //构建请求信息
            ChatTranslationInfo chatTranslationInfo = getChatTranslationInfo(getMessages(translationData));
            log.info("当前开始翻译句子(当前开始id) : {} ,消耗tokens :{}", translationData.get(0).getId(), chatTranslationInfo.chatCompletion.tokens());
            //限制每分钟的API调用
            waitForApiCallAvailability(chatTranslationInfo.chatCompletion.tokens());

            ChatCompletionResponse chatCompletionResponse = chatTranslationInfo.openAiClient.chatCompletion(chatTranslationInfo.chatCompletion);
            chatCompletionResponse.getChoices().forEach(e -> {
                log.info("收到的消息 : {}", e.getMessage().getContent());
                JSONObject parse = null;
                try {
                    parse = JSONUtil.parseObj(e.getMessage().getContent());
                    if (parse.size() != translationData.size()) {
                        //如果出现缺行将出现缺行的句子添加到队列
                        //寻找缺行的数据
                        Map<Long, TranslationData> dataById = translationData.stream()
                                .collect(Collectors.toMap(TranslationData::getId, Function.identity()));
                        for (TranslationData translationDatum : translationData) {
                            if (!parse.containsKey(translationDatum.getId().toString())) {
                                log.info("缺行的数据: {}", translationDatum);
                                missingRowData.put(translationDatum.getId(), translationDatum);

                                // 获取上一行和下一行，如果存在的话
                                Long id = translationDatum.getId();

                                TranslationData prevData = dataById.getOrDefault(id - 1, null);
                                TranslationData nextData = dataById.getOrDefault(id + 1, null);


                                if (prevData != null) {
                                    log.info("上一行的数据: {}", prevData);
                                    missingRowData.put(prevData.getId(), prevData);
                                }

                                if (nextData != null) {
                                    log.info("下一行的数据: {}", nextData);
                                    missingRowData.put(nextData.getId(), nextData);
                                }
                            }
                        }
                    }
                } catch (Exception jsonException) {
                    log.error("解析json异常 {}", e.getMessage());
                }

                //取出key更新对应的TranslationData
                List<TranslationData> updatedTranslationDataList = new ArrayList<>();
                if (parse != null) {
                    for (Map.Entry<String, Object> stringObjectEntry : parse) {
                        Long id = Long.valueOf(stringObjectEntry.getKey());
                        String translatedText = stringObjectEntry.getValue().toString();
                        TranslationData updatedTranslationData = new TranslationData();
                        updatedTranslationData.setId(id);
                        updatedTranslationData.setTranslationText(translatedText);
                        updatedTranslationDataList.add(updatedTranslationData);
                    }
                }
                translationDataService.updateBatchById(updatedTranslationDataList);
            });

        } catch (com.unfbx.chatgpt.exception.BaseException e) {
            log.warn("批量翻译openai调用异常 {}", e.getMessage());
        } catch (Exception e) {
            log.error("批量翻译任务异常", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    private List<Message> getMessages(List<TranslationData> translationData) {
        //将对象转换为Map
        Map<Long, String> translationDataMap = translationData.stream().collect(Collectors.toMap(TranslationData::getId, TranslationData::getOriginalText));
        String jsonStr = JSONUtil.toJsonStr(translationDataMap);

        //开始翻译
        Message system = Message.builder().role(Message.Role.SYSTEM).content(chatgptConfig.getPromptMultipleTranslations()).build();
        Message user = Message.builder().role(Message.Role.USER).content(jsonStr).build();

        return Arrays.asList(system, user);
    }

    /**
     * 单独句子翻译
     *
     * @param translationData
     */
    @Async("chatGptTaskExecutor")
    @Override
    public CompletableFuture<Void> accessingChatGptOne(TranslationData translationData) {
        try {
            //开始翻译
            Message system = Message.builder().role(Message.Role.SYSTEM).content(chatgptConfig.getPromptSingleTranslations()).build();
            Message user = Message.builder().role(Message.Role.USER).content(translationData.getOriginalText()).build();

            ChatTranslationInfo chatTranslationInfo = getChatTranslationInfo(Arrays.asList(system, user));
            log.info("翻译单独句子id {} ,消耗tokens :{}", translationData.getId(), chatTranslationInfo.chatCompletion().tokens());
            //限制每分钟的API调用
            waitForApiCallAvailability(chatTranslationInfo.chatCompletion().tokens());

            ChatCompletionResponse chatCompletionResponse = chatTranslationInfo.openAiClient().chatCompletion(chatTranslationInfo.chatCompletion());
            chatCompletionResponse.getChoices().forEach(e -> {
                log.info("收到的消息 : {}", e.getMessage().getContent());
                translationData.setTranslationText(e.getMessage().getContent());
                translationDataService.updateById(translationData);
            });
            //如果正常翻译成功要删除翻译错误的数据
            errorData.remove(translationData.getId());
        } catch (com.unfbx.chatgpt.exception.BaseException e) {
            //openai调用异常需要重新翻译
            log.warn("单条openai调用异常需要重新翻译 {}", e.getMessage());
            errorData.put(translationData.getId(), translationData);
        } catch (Exception e) {
            //其他异常也需要重新翻译
            log.error("单独句子翻译异常 {}", e.getMessage());
            errorData.put(translationData.getId(), translationData);
        }
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    private ChatTranslationInfo getChatTranslationInfo(List<Message> messages) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        Proxy proxy = null;
        //自定义代理
        if (StringUtils.isNotBlank(chatgptConfig.getProxyUlr())) {
            Proxy.Type proxyType = Proxy.Type.valueOf(chatgptConfig.getProxyType());
            proxy = new Proxy(proxyType, new InetSocketAddress(chatgptConfig.getProxyUlr(), chatgptConfig.getProxyPort()));
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)//自定义日志输出
                .addInterceptor(new OpenAiResponseInterceptor())//自定义返回值拦截
                .connectTimeout(30, TimeUnit.SECONDS)//自定义超时时间
                .writeTimeout(360, TimeUnit.SECONDS)//自定义超时时间
                .readTimeout(360, TimeUnit.SECONDS);//自定义超时时间

        if (proxy != null) {
            builder.proxy(proxy);
        }

        OkHttpClient okHttpClient = builder.build();
        //构建客户端

        OpenAiClient.Builder openAiClientBuilder = OpenAiClient.builder()
                .apiKey(getOpenaiKey())
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient);

        //如果有代理地址就设置
        if (StringUtils.isNotBlank(chatgptConfig.getApiHost())) {
            openAiClientBuilder.apiHost(chatgptConfig.getApiHost());
        }

        OpenAiClient openAiClient = openAiClientBuilder.build();


        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(chatgptConfig.getModel())
                .topP(chatgptConfig.getTopP())
                .temperature(chatgptConfig.getTemperature())
                .presencePenalty(chatgptConfig.getPresencePenalty())
                .frequencyPenalty(chatgptConfig.getFrequencyPenalty())
                .messages(messages)
                .build();
        return new ChatTranslationInfo(openAiClient, chatCompletion);
    }

    private record ChatTranslationInfo(OpenAiClient openAiClient, ChatCompletion chatCompletion) {
    }

    @Override
    public Long calculateToken(List<TranslationData> translationData) {
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(getMessages(translationData)).build();
        return chatCompletion.tokens();
    }

    /**
     * 测试配置
     *
     * @param chatgptConfigVo
     * @return
     */
    @Override
    public ChatGptConfigTestRespVo testChatGptConfig(ChatGptConfigTestVo chatgptConfigVo) {
        ChatGptConfigTestRespVo chatGptConfigTestRespVo = new ChatGptConfigTestRespVo();

        //将对象转换为Map
        Map<Long, String> translationDataMap = chatgptConfigVo.getTranslationDataList().stream().collect(Collectors.toMap(TranslationData::getId, TranslationData::getOriginalText));
        String jsonStr = JSONUtil.toJsonStr(translationDataMap);

        //开始翻译
        Message system = Message.builder().role(Message.Role.SYSTEM).content(chatgptConfigVo.getPromptMultipleTranslations()).build();
        Message user = Message.builder().role(Message.Role.USER).content(jsonStr).build();
        //构建请求信息
        ChatTranslationInfo chatTranslationInfo = getChatTranslationInfo(Arrays.asList(system, user));
        ChatCompletionResponse chatCompletionResponse = chatTranslationInfo.openAiClient.chatCompletion(chatTranslationInfo.chatCompletion);
        chatCompletionResponse.getChoices().forEach(e -> {
            //设置翻译结果成功
            chatGptConfigTestRespVo.setPromptMultipleTranslationsSuccess(true);
            try {
                JSONObject parse = JSONUtil.parseObj(e.getMessage().getContent());
                chatGptConfigTestRespVo.setPromptMultipleTranslationsResult(parse);
            } catch (Exception exception) {
                log.error("多条翻译测试失败 : {}", e.getMessage().getContent());
                chatGptConfigTestRespVo.setPromptMultipleTranslationsSuccess(false);
                chatGptConfigTestRespVo.setPromptMultipleTranslationsResult(e.getMessage().getContent());
            }

        });

        //测试单条翻译
        TranslationData translationData = chatgptConfigVo.getTranslationData();
        Message system1 = Message.builder().role(Message.Role.SYSTEM).content(chatgptConfigVo.getPromptSingleTranslations()).build();
        Message user1 = Message.builder().role(Message.Role.USER).content(translationData.getOriginalText()).build();

        ChatTranslationInfo chatTranslationInfo1 = getChatTranslationInfo(Arrays.asList(system1, user1));

        ChatCompletionResponse chatCompletionResponse1 = chatTranslationInfo1.openAiClient().chatCompletion(chatTranslationInfo1.chatCompletion());
        chatCompletionResponse1.getChoices().forEach(e -> {
            log.info("收到的消息 : {}", e.getMessage().getContent());
            chatGptConfigTestRespVo.setPromptSingleTranslationsResult(e.getMessage().getContent());
        });
        return chatGptConfigTestRespVo;

    }

}
