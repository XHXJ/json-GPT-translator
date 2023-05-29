package com.xhxj.jsongpttranslator.translation.async.edits;

import cn.hutool.json.JSONUtil;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.edits.Edit;
import com.unfbx.chatgpt.entity.edits.EditResponse;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.framework.chatgptconfig.ChatgptConfig;
import com.xhxj.jsongpttranslator.translation.ApiLimiter;
import com.xhxj.jsongpttranslator.translation.async.OkHttpClientConfigurator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
public class EditsTranslationAsyncServiceImpl implements EditsTranslationAsyncService {

    @Autowired
    private ApiLimiter apiLimiter;

    @Resource(name = "startFlag")
    private AtomicBoolean startFlag;

    @Autowired
    private ChatgptConfig chatgptConfig;

    @Autowired
    private OkHttpClientConfigurator okHttpClientConfigurator;


    @Override
    @Async("chatGptTaskExecutor")
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
            Edit edit = buildEdit(translationData);


            EditResponse response = openAiClient.edit(edit);
            log.info("批量翻译openai调用成功 {}", response.getChoices()[0].getText());


        } catch (com.unfbx.chatgpt.exception.BaseException e) {
            log.warn("批量翻译openai调用异常 {}", e.getMessage());
        } catch (Exception e) {
            log.error("批量翻译任务异常", e);
        }

        return null;
    }

    /**
     * 构建多行翻译的基本信息
     *
     * @param translationData
     * @return Edit
     */
    private Edit buildEdit(List<TranslationData> translationData) {
        //构建输入
        //将对象转换为Map
        Map<Long, String> translationDataMap = translationData.stream().collect(Collectors.toMap(TranslationData::getId, TranslationData::getOriginalText));
        String jsonStr = JSONUtil.toJsonStr(translationDataMap);
//        log.info("构建多行翻译的基本信息 {}", jsonStr);

        Edit edit = new Edit();
        edit.setModel(chatgptConfig.getEditsModel());
        edit.setInput(jsonStr);
        edit.setInstruction(chatgptConfig.getEditsInstruction());
        edit.setTemperature(chatgptConfig.getEditsTemperature());
        edit.setTopP(chatgptConfig.getEditsTopP());
        return edit;
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
