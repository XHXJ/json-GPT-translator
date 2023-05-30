package com.xhxj.jsongpttranslator.translation.mode;

import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.translation.TranslationMode;
import com.xhxj.jsongpttranslator.translation.async.test.TestTranslationAsyncService;
import com.xhxj.jsongpttranslator.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author:luoshuzhong
 * @create: 2023-05-29 10:12
 * @Description: 基于 Edits 实现翻译模式
 */
@Component("testTranslationMode")
@Slf4j
public class TestTranslationMode implements TranslationMode {
    @Autowired
    private TestTranslationAsyncService testTranslationAsyncService;

    @Override
    public void batchTranslate(List<TranslationData> data) {
        //开启Edits翻译模式
        log.info("开启test翻译模式,共{}条数据翻译", data.size());
        try {
            List<List<TranslationData>> batchList = ListUtils.splitList(data, 20);
            List<CompletableFuture<Void>> taskList = new ArrayList<>();
            for (List<TranslationData> translationData : batchList) {
                taskList.add(testTranslationAsyncService.multipleTranslations(translationData));
            }
            //等待所有任务完成
            CompletableFuture.allOf(taskList.toArray(new CompletableFuture<?>[0])).join();
        } catch (Exception e) {
            log.error("test翻译模式批量翻译异常", e);
        }

    }


    @Override
    public void translateOne(List<TranslationData> data) {
        log.info("开启Edits翻译模式");
    }

    @Override
    public void stopTranslate() {
        log.info("停止Edits翻译模式");

    }
}
