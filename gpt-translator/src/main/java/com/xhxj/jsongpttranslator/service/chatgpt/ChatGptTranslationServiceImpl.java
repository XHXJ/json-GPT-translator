package com.xhxj.jsongpttranslator.service.chatgpt;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.service.translationdata.TranslationDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

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

    /**
     * 停止程序
     */
    @Override
    public void stop() {
        startFlag.set(false);
        //重启线程池
        log.info("程序已经停止");
    }

    @Override
    @Async("taskExecutor")
    public void start() {
        if (!startFlag.compareAndSet(false, true)) {
            log.info("程序已经启动");
            return;
        }
        long startTime = System.currentTimeMillis(); // 记录开始时间
        try {
            log.info("程序开始");
            //查询需要翻译的句子
            LambdaQueryWrapper<TranslationData> wrapper = new LambdaQueryWrapper<>();
            wrapper.isNull(TranslationData::getTranslationText);
            do {
                List<TranslationData> multipleSentences = translationDataService.list(wrapper);
                log.info("还有{}条句子未被翻译", multipleSentences.size());
                //未翻译的句子小于200条
                if (multipleSentences.size() < 50) {
                    break;
                }
                runBatchTranslation(multipleSentences);
            } while (startFlag.get());


            do {
                //翻译完所有的句子
                List<TranslationData> singleSentence = translationDataService.list(wrapper);
                //如果还有未被翻译的句子，就单条去翻译
                log.info("单条漏翻处理,还有{}条句子未被翻译", singleSentence.size());
                if (singleSentence.size() == 0) {
                    return;
                }
                try {
                    List<CompletableFuture<Void>> task = new ArrayList<>(singleSentence.size());
                    for (TranslationData translationData : singleSentence) {
                        task.add(chatGptTranslationAsyncService.accessingChatGptOne(translationData));
                    }
                    //等待所有任务完成
                    CompletableFuture.allOf(task.toArray(new CompletableFuture<?>[0])).join();
                } catch (Exception e) {
                    log.error("单条翻译出错 {}", e.getMessage());
                }
            } while (startFlag.get());


        } catch (Exception e) {
            log.error("程序运行出错 {}", e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis(); // 记录结束时间
            long elapsedTimeMillis = endTime - startTime; // 计算耗时（毫秒）
            double elapsedTimeMinutes = elapsedTimeMillis / 60000.0; // 转换为分钟
            log.info(String.format("程序运行耗时：%.2f 分钟", elapsedTimeMinutes));
            startFlag.set(false);
        }
    }

    /**
     * 运行批量翻译
     *
     * @param list
     */
    private void runBatchTranslation(List<TranslationData> list) {
        try {

            List<List<TranslationData>> batchList = splitList(list);

            List<CompletableFuture<Objects>> task = new ArrayList<>(batchList.size());
            for (List<TranslationData> translationData : batchList) {
                task.add(chatGptTranslationAsyncService.accessingChatGpt(translationData));
            }
            //等待所有任务完成
            CompletableFuture.allOf(task.toArray(new CompletableFuture<?>[0])).join();
        } catch (Exception e) {
            log.error("批量翻译异常 {}", e.getMessage());
        }
    }

    /**
     * 分割list
     *
     * @param list 待分割的list
     * @return 分割后的list
     */
    private List<List<TranslationData>> splitList(List<TranslationData> list) {
        List<List<TranslationData>> resultList = new ArrayList<>();
        //翻译基准线
        int batchSize = 20;

        int index = 0;
        while (index < list.size()) {
            long totalTokens;
            int retryCount = 0; // 添加重试计数器
            do {
                totalTokens = chatGptTranslationAsyncService.calculateToken(list.subList(index, Math.min(index + batchSize, list.size())));

                if (totalTokens < 1500 && index + batchSize < list.size()) {
                    batchSize += 2;
                } else if (totalTokens > 1900) {
                    batchSize -= 2;
                } else {
                    break; // 当 totalTokens 在 1500-2000 之间时，跳出循环
                }

                retryCount++; // 每次循环都增加重试计数器
            } while (retryCount < 5); // 当重试次数达到5次时，跳出循环

            List<TranslationData> batch = list.subList(index, Math.min(index + batchSize, list.size()));
            resultList.add(batch);
            index += batchSize;
        }

        return resultList;
    }

}
