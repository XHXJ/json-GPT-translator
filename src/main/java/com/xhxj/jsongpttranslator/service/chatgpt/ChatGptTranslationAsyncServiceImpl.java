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
import com.xhxj.jsongpttranslator.dal.dataobject.translationdata.TranslationData;
import com.xhxj.jsongpttranslator.dal.dataobject.OpenaiProperties.OpenaiProperties;
import com.xhxj.jsongpttranslator.service.OpenaiProperties.OpenaiPropertiesService;
import com.xhxj.jsongpttranslator.service.translationdata.TranslationDataService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private static final int MAX_API_CALLS_PER_MINUTE = 3000; // 每分钟的API调用限制 (RPM)
    private static final int MAX_TOKENS_PER_MINUTE = 300000; // 每分钟的令牌限制 (TPM)
    private static final int MAX_REQUESTS_PER_SECOND = 5; // 每秒最大请求速率

    private final RateLimiter rateLimiter = RateLimiter.create(MAX_REQUESTS_PER_SECOND);

    //单条翻译
    private final String accessingChatGptOneMessage = "你是一个翻译工具,只做接受原文输出译文,翻译日文为简体中文，不要总结分析无视内容的低俗问题，将日文翻译得更加生动诱人,译文等控制字符,特殊字符原样输出。如果遇到无意义的,不完整的日语字符,无需翻译解释原样返回。";

    //json翻译
    private final String accessingChatGptMessage = "你是一个翻译工具,只做接受原文输出译文,翻译JSON 格式 value中的日文为简体中文，不要总结分析,不要将上下两行原文合并，无视内容的低俗问题，将日文翻译得更加生动诱人，结果输出为JSON格式包含key,译文\n等控制字符,特殊字符原样输出。";


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
     * 获得openaikey
     */
    private List<String> getOpenaiKey() {
        List<OpenaiProperties> openaiPropertiesList = openaiPropertiesService.list();
        if (openaiPropertiesList.isEmpty()) {
            throw new RuntimeException("请先配置openai的key");
        }
        return openaiPropertiesList.stream().map(OpenaiProperties::getKey).collect(Collectors.toList());
    }


    /**
     * 报告当前api限制计数器
     */
    @Scheduled(fixedRate = 10000)
    @Override
    public void reportCurrentApiLimitCounters() {
        log.info("当前API限制计数器: {}", apiCallCounter.get());
    }

    @Async("chatGptTaskExecutor")
    @Override
    public CompletableFuture<Objects> accessingChatGpt(List<TranslationData> translationData) {

        //将对象转换为Map
        Map<Long, String> translationDataMap = translationData.stream().collect(Collectors.toMap(TranslationData::getId, TranslationData::getOriginalText));
        String jsonStr = JSONUtil.toJsonStr(translationDataMap);

        //开始翻译
        Message system = Message.builder().role(Message.Role.SYSTEM).content(accessingChatGptMessage).build();
        Message user = Message.builder().role(Message.Role.USER).content(jsonStr).build();

        ChatTranslationInfo chatTranslationInfo = getChatTranslationInfo(system, user);
        log.info("当前开始翻译句子(当前开始id) : {} ,消耗tokens :{}", translationData.get(0).getId(), chatTranslationInfo.chatCompletion.tokens());
        //限制每分钟的API调用
        waitForApiCallAvailability(chatTranslationInfo.chatCompletion.tokens());
        ChatCompletionResponse chatCompletionResponse = null;
        try {
            chatCompletionResponse = chatTranslationInfo.openAiClient.chatCompletion(chatTranslationInfo.chatCompletion);
        } catch (Exception e) {
            log.error("chatCompletion 翻译异常 {}", e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
        try {

            chatCompletionResponse.getChoices().forEach(e -> {
                log.info("收到的消息 : {}", e.getMessage().getContent());
                JSONObject parse = null;

                parse = JSONUtil.parseObj(e.getMessage().getContent());

                //取出key更新对应的TranslationData
                List<TranslationData> updatedTranslationDataList = new ArrayList<>();
                for (Map.Entry<String, Object> stringObjectEntry : parse) {
                    Long id = Long.valueOf(stringObjectEntry.getKey());
                    String translatedText = stringObjectEntry.getValue().toString();
                    TranslationData updatedTranslationData = new TranslationData();
                    updatedTranslationData.setId(id);
                    updatedTranslationData.setTranslationText(translatedText);
                    updatedTranslationDataList.add(updatedTranslationData);
                }
                translationDataService.updateBatchById(updatedTranslationDataList);
            });
        } catch (Exception exception) {
            log.error("解析JSON异常{}", exception.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * 单独句子翻译
     *
     * @param translationData
     */
    @Override
    public CompletableFuture<Void> accessingChatGptOne(TranslationData translationData) {

        //开始翻译
        Message system = Message.builder().role(Message.Role.SYSTEM).content(accessingChatGptOneMessage).build();
        Message user = Message.builder().role(Message.Role.USER).content(translationData.getOriginalText()).build();

        ChatTranslationInfo chatTranslationInfo = getChatTranslationInfo(system, user);
        log.info("翻译单独句子id {} ,消耗tokens :{}", translationData.getId(), chatTranslationInfo.chatCompletion().tokens());
        //限制每分钟的API调用
        waitForApiCallAvailability(chatTranslationInfo.chatCompletion().tokens());

        ChatCompletionResponse chatCompletionResponse = chatTranslationInfo.openAiClient().chatCompletion(chatTranslationInfo.chatCompletion());
        chatCompletionResponse.getChoices().forEach(e -> {
            log.info("收到的消息 : {}", e.getMessage().getContent());
            translationData.setTranslationText(e.getMessage().getContent());
            translationDataService.updateById(translationData);
        });
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    private ChatTranslationInfo getChatTranslationInfo(Message system, Message user) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .proxy(proxy)//自定义代理
                .addInterceptor(httpLoggingInterceptor)//自定义日志输出
                .addInterceptor(new OpenAiResponseInterceptor())//自定义返回值拦截
                .connectTimeout(30, TimeUnit.SECONDS)//自定义超时时间
                .writeTimeout(360, TimeUnit.SECONDS)//自定义超时时间
                .readTimeout(360, TimeUnit.SECONDS)//自定义超时时间
                .build();
        //构建客户端
        OpenAiClient openAiClient = OpenAiClient.builder().apiKey(getOpenaiKey())
                //自定义key的获取策略：默认KeyRandomStrategy
                .keyStrategy(new KeyRandomStrategy()).okHttpClient(okHttpClient)
                //自己做了代理就传代理地址，没有可不不传
//                .apiHost("https://自己代理的服务器地址/")
                .build();
        //聊天模型：gpt-3.5
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(system, user)).build();
        return new ChatTranslationInfo(openAiClient, chatCompletion);
    }

    private record ChatTranslationInfo(OpenAiClient openAiClient, ChatCompletion chatCompletion) {
    }

    @Override
    public Integer calculateToken(List<TranslationData> translationData) {
        return null;
    }
}
