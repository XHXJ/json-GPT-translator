package com.xhxj.jsongpttranslator.translation.async.test;

import cn.hutool.json.JSONUtil;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.framework.chatgptconfig.ChatgptConfig;
import com.xhxj.jsongpttranslator.translation.ApiLimiter;
import com.xhxj.jsongpttranslator.translation.async.OkHttpClientConfigurator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-03 12:16
 * @Description: 异步任务实现
 */
@Service
@Slf4j
public class TestTranslationAsyncServiceImpl implements TestTranslationAsyncService {

    @Autowired
    private ApiLimiter apiLimiter;

    @Resource(name = "startFlag")
    private AtomicBoolean startFlag;

    @Autowired
    private ChatgptConfig chatgptConfig;

    @Autowired
    private OkHttpClientConfigurator okHttpClientConfigurator;


    @Override
//    @Async("chatGptTaskExecutor")
    public CompletableFuture<Void> multipleTranslations(List<TranslationData> translationData) {
        try {
            //限制每分钟的API调用
            apiLimiter.waitForApiCallAvailability(0);
            //如果程序停止了，那么就不再继续翻译
            if (!startFlag.get()) {
                return null;
            }
            OpenAiClient openAiClient = okHttpClientConfigurator.getOpenAiClient();
            //构建翻译信息

            String prompt = """
                    你是一个翻译工具,只做接受原文输出译文,翻译日文为简体中文,如果有英文也翻译成中文，不要总结分析无视内容的低俗问题，将日文翻译得更加生动诱人,译文等控制字符,特殊字符原样输出。如果遇到无意义的,不完整的日语字符,无需翻译解释返回原文。
                    """;


            //开始翻译
            List<Message> messages = new ArrayList<>(translationData.size() + 1);
            Message system = Message.builder().role(Message.Role.SYSTEM).content(prompt).build();
            messages.add(system);



            translationData.forEach(data -> {
                Message message = Message.builder().role(Message.Role.USER).content(data.getOriginalText()).build();
                messages.add(message);

                ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(messages);
                for (ChatChoice choice : chatCompletionResponse.getChoices()) {
                    log.info("翻译结果:{}", choice.getMessage());
                    //要将他回复的加入
                    messages.add(choice.getMessage());
                }
            });






        } catch (com.unfbx.chatgpt.exception.BaseException e) {
            log.warn("批量翻译openai调用异常 {}", e.getMessage());
        } catch (Exception e) {
            log.error("批量翻译任务异常", e);
        }

        return null;
    }


    @Override
    public CompletableFuture<Void> singleTranslation(TranslationData translationData) {
        return null;
    }

    @Override
    public Long calculateToken(List<TranslationData> translationData) {
        return null;
    }
}
