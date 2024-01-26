package com.xhxj.jsongpttranslator.translation.async.chat;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.chat.ResponseFormat;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.xhxj.jsongpttranslator.controller.openaiproperties.vo.ChatGptConfigTestRespVo;
import com.xhxj.jsongpttranslator.controller.openaiproperties.vo.ChatGptConfigTestVo;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.framework.chatgptconfig.ChatgptConfig;
import com.xhxj.jsongpttranslator.service.openaiproperties.OpenaiPropertiesService;
import com.xhxj.jsongpttranslator.service.translationdata.TranslationDataService;
import com.xhxj.jsongpttranslator.translation.ApiLimiter;
import com.xhxj.jsongpttranslator.translation.async.OkHttpClientConfigurator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
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

    @Autowired
    private ApiLimiter apiLimiter;

    @Resource(name = "startFlag")
    private AtomicBoolean startFlag;

    @Resource(name = "missingRowData")
    private Map<Long, TranslationData> missingRowData;

    @Resource(name = "errorData")
    private Map<Long, TranslationData> errorData;

    @Autowired
    private OkHttpClientConfigurator okHttpClientConfigurator;

    /**
     * gpt配置
     */
    @Autowired
    private ChatgptConfig chatgptConfig;


    @Async("chatGptTaskExecutor")
    @Override
    public CompletableFuture<Void> multipleTranslations(List<TranslationData> translationData) {
        try {
            //构建请求信息
            ChatTranslationInfo chatTranslationInfo = getChatTranslationInfo(getMessages(translationData));
            //限制每分钟的API调用
            apiLimiter.waitForApiCallAvailability(chatTranslationInfo.chatCompletion.tokens());
            //如果程序停止了，那么就不再继续翻译
            if (!startFlag.get()) {
                return CompletableFuture.completedFuture(null);
            }
            log.info("当前开始翻译句子(当前开始id) : {} ,消耗tokens :{}", translationData.get(0).getId(), chatTranslationInfo.chatCompletion.tokens());

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
                        for (int i = 0; i < translationData.size(); i++) {
                            TranslationData translationDatum = translationData.get(i);
                            if (!parse.containsKey(translationDatum.getId().toString())) {
                                log.info("缺行的数据: {}", translationDatum);
                                missingRowData.put(translationDatum.getId(), translationDatum);

                                // 获取上一行和下一行，如果存在的话
                                Long id = translationDatum.getId();

                                TranslationData prevData = dataById.getOrDefault(id - 1, null);
                                TranslationData nextData = dataById.getOrDefault(id + 1, null);

                                //宁可翻错，不可漏翻

                                //在数据库中缺行的数据
                                if (prevData != null) {
                                    log.info("上一行的数据: {}", prevData);
                                    missingRowData.put(prevData.getId(), prevData);
                                }

                                if (nextData != null) {
                                    log.info("下一行的数据: {}", nextData);
                                    missingRowData.put(nextData.getId(), nextData);
                                }
                                // 获取上一行和下一行，如果存在的话
                                if (i > 0) {
                                    TranslationData prevData1 = translationData.get(i - 1);
                                    log.info("实际列表上一行的数据: {}", prevData);
                                    missingRowData.put(prevData1.getId(), prevData1);
                                }

                                if (i < translationData.size() - 1) {
                                    TranslationData nextData1 = translationData.get(i + 1);
                                    log.info("实际列表下一行的数据: {}", nextData1);
                                    missingRowData.put(nextData1.getId(), nextData1);
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
    public CompletableFuture<Void> singleTranslation(TranslationData translationData) {
        try {
            //开始翻译
            Message system = Message.builder().role(Message.Role.SYSTEM).content(chatgptConfig.getPromptSingleTranslations()).build();
            Message user = Message.builder().role(Message.Role.USER).content(translationData.getOriginalText()).build();

            ChatTranslationInfo chatTranslationInfo = getChatTranslationInfo(Arrays.asList(system, user));
            //限制每分钟的API调用
            apiLimiter.waitForApiCallAvailability(chatTranslationInfo.chatCompletion.tokens());
            //如果程序停止了，那么就不再继续翻译
            if (!startFlag.get()) {
                return CompletableFuture.completedFuture(null);
            }
            log.info("翻译单独句子id {} ,消耗tokens :{}", translationData.getId(), chatTranslationInfo.chatCompletion().tokens());
            ChatCompletionResponse chatCompletionResponse = chatTranslationInfo.openAiClient().chatCompletion(chatTranslationInfo.chatCompletion());
            chatCompletionResponse.getChoices().forEach(e -> {
                log.info("收到的消息 : {}", e.getMessage().getContent());
                translationData.setTranslationText(
                        //如果配置了单条翻译JSON key设置
                        isJsonModeAndKey() ?
                                JSONUtil.parseObj(e.getMessage().getContent()).getStr(chatgptConfig.getPromptSingleJsonKey())
                                :
                                e.getMessage().getContent()
                );
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

    /**
     * 判断是否启用JSON mode 模式是否设置key转换
     *
     * @return true 启用
     */
    private boolean isJsonModeAndKey() {
        return StringUtils.isNotBlank(chatgptConfig.getResponseFormat()) && StringUtils.isNotBlank(chatgptConfig.getPromptSingleJsonKey());
    }

    @NotNull
    private ChatTranslationInfo getChatTranslationInfo(List<Message> messages) {


        OkHttpClient client = okHttpClientConfigurator.getOkHttpClient();


        OpenAiClient.Builder openAiClientBuilder = OpenAiClient.builder()
                .apiKey(openaiPropertiesService.getOpenaiKey())
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(client);

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

        //如果配置了responseFormat就设置
        if (StringUtils.isNotBlank(chatgptConfig.getResponseFormat())) {
            chatCompletion.setResponseFormat(new ResponseFormat(chatgptConfig.getResponseFormat()));
        }
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
            //如果配置了单条翻译JSONkey设置
            chatGptConfigTestRespVo.setPromptSingleTranslationsResult(
                    isJsonModeAndKey() ?
                            JSONUtil.parseObj(e.getMessage().getContent()).getStr(chatgptConfig.getPromptSingleJsonKey())
                            :
                            e.getMessage().getContent()
            );
        });
        return chatGptConfigTestRespVo;

    }

}
