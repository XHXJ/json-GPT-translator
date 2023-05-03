package com.xhxj.jsongpttranslator.service.chatgpt;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhxj.jsongpttranslator.dal.dataobject.translationdata.TranslationData;
import com.xhxj.jsongpttranslator.service.translationdata.TranslationDataService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-03 02:32
 * @Description: chatgpt翻译实现
 */
@Service
@Slf4j
public class ChatGptTranslationServiceImpl implements ChatGptTranslationService {

    @Autowired
    private ChatGptTranslationAsyncService chatGptTranslationAsyncService;

    @Autowired
    private TranslationDataService translationDataService;

    //程序开始的唯一标识符
    private final AtomicBoolean startFlag = new AtomicBoolean(false);

    @Override
    @Async("taskExecutor")
    public void start() {
        if (!startFlag.compareAndSet(false, true)) {
            log.info("程序已经启动");
            return;
        }
        try {
            log.info("程序开始启动");
            //查询需要翻译的句子
            LambdaQueryWrapper<TranslationData> wrapper = new LambdaQueryWrapper<>();
            wrapper.isNull(TranslationData::getTranslationText);
            List<TranslationData> list = translationDataService.list(wrapper);

            afterSplittingTheList(list);

            //查询还未被翻译的句子
            LambdaQueryWrapper<TranslationData> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.isNull(TranslationData::getTranslationText);
            List<TranslationData> list2 = translationDataService.list(wrapper2);
            log.info("第一次处理漏翻,还有{}条句子未被翻译", list2.size());
            //去单条翻译未被翻译的句子
            afterSplittingTheList(list2);

            List<TranslationData> list3 = translationDataService.list(wrapper2);
            //如果还有未被翻译的句子，就单条去翻译
            log.info("第二次处理漏翻,还有{}条句子未被翻译", list2.size());
            if (list3.size() > 0) {
                List<CompletableFuture<Void>> task = new ArrayList<>(list3.size());
                for (TranslationData translationData : list3) {
                    task.add(chatGptTranslationAsyncService.accessingChatGptOne(translationData));
                }
                //等待所有任务完成
                CompletableFuture.allOf(task.toArray(new CompletableFuture<?>[0])).join();
            }

        } finally {
            startFlag.set(false);
        }
    }

    /**
     * @param list
     */
    private void afterSplittingTheList(List<TranslationData> list) {
        // 批次大小为 50
        int batchSize = 30;
        List<List<TranslationData>> batchList = splitList(list, batchSize);

        List<CompletableFuture<Objects>> task = new ArrayList<>(batchList.size());
        for (List<TranslationData> translationData : batchList) {
            task.add(chatGptTranslationAsyncService.accessingChatGpt(translationData));
        }
        //等待所有任务完成
        CompletableFuture.allOf(task.toArray(new CompletableFuture<?>[0])).join();
    }

    @NotNull
    private static List<List<TranslationData>> splitList(List<TranslationData> list, int batchSize) {

        return IntStream.range(0, (list.size() + batchSize - 1) / batchSize).mapToObj(i -> list.subList(i * batchSize, Math.min((i + 1) * batchSize, list.size()))).toList();
    }


}
