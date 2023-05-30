package com.xhxj.jsongpttranslator.translation.mode;

import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.framework.chatgptconfig.ChatgptConfig;
import com.xhxj.jsongpttranslator.translation.async.chat.ChatGptTranslationAsyncService;
import com.xhxj.jsongpttranslator.translation.TranslationMode;
import com.xhxj.jsongpttranslator.utils.ListUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author:luoshuzhong
 * @create: 2023-05-29 10:11
 * @Description: 基于 OpenAI 的 Chat API 实现翻译模式
 */
@Component("chatTranslationMode")
@Slf4j
public class ChatTranslationMode implements TranslationMode {

    @Resource(name = "missingRowData")
    private Map<Long, TranslationData> missingRowData;

    @Resource(name = "errorData")
    private Map<Long, TranslationData> errorData;

    @Autowired
    private ChatgptConfig chatgptConfig;

    @Autowired
    private ChatGptTranslationAsyncService chatGptTranslationAsyncService;
    private List<CompletableFuture<Void>> taskList = new ArrayList<>();

    @Override
    public void batchTranslate(List<TranslationData> data) {
        //开启OpenAI翻译模式
        log.info("开启Chat翻译模式批量翻译");
        //批量翻译
        runBatchTranslation(data);
        //处理缺行的数据
        missingRowDataTranslation();
    }


    @Override
    public void translateOne(List<TranslationData> data) {
        //开启OpenAI翻译模式
        log.info("开启Chat翻译模式单条翻译");
        try {
            for (TranslationData translationData : data) {
                taskList.add(chatGptTranslationAsyncService.singleTranslation(translationData));
            }
            //等待所有任务完成
            CompletableFuture.allOf(taskList.toArray(new CompletableFuture<?>[0])).join();
            //清空任务列表
            taskList.clear();
        } catch (Exception e) {
            log.error("单条翻译出错 {}", e.getMessage());
        }
    }

    @Override
    public void stopTranslate() {
        log.info("停止Chat翻译模式");
        //这里可以记录停止后保存漏翻的数据什么的
    }

    /**
     * 运行批量翻译
     *
     * @param list
     */
    private void runBatchTranslation(List<TranslationData> list) {
        try {
            List<List<TranslationData>> batchList = splitList(list);

            for (List<TranslationData> translationData : batchList) {
                taskList.add(chatGptTranslationAsyncService.multipleTranslations(translationData));
            }
            //等待所有任务完成
            CompletableFuture.allOf(taskList.toArray(new CompletableFuture<?>[0])).join();
            //清空任务列表
            taskList.clear();
        } catch (Exception e) {
            log.error("批量翻译异常 {}", e.getMessage());
        }
    }

    /**
     * 处理缺行数据
     */
    private void missingRowDataTranslation() {
        try {
            log.info("开始处理缺行数据: {} 条", missingRowData.size());
            missingRowData.forEach((k, v) -> taskList.add(chatGptTranslationAsyncService.singleTranslation(v)));
            //等待所有任务完成
            CompletableFuture.allOf(taskList.toArray(new CompletableFuture<?>[0])).join();
            //清除缺行数据
            missingRowData.clear();
            //处理翻译错误的数据
            while (errorData.size() > 0) {
                log.info("开始处理调用错误的数据: {} 条", errorData.size());
                errorData.forEach((k, v) -> taskList.add(chatGptTranslationAsyncService.singleTranslation(v)));
                //等待所有任务完成
                CompletableFuture.allOf(taskList.toArray(new CompletableFuture<?>[0])).join();
            }
            //清空任务列表
            taskList.clear();
            log.info("处理缺行数据完成");
        } catch (Exception e) {
            log.error("处理缺行数据失败:{}", e.getMessage());
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
        //最大tokens
        int maxTokens = 1900;
        //最小tokens
        int minTokens = 1500;
        //每次循环增加的tokens
        int addTokens = 2;
        if ("gpt-4-32k".equals(chatgptConfig.getModel())) {
            //如果是长文本模型，分割成200条一组
            final int partitionSize = 200;
            return ListUtils.splitList(list, partitionSize);
        }
        if ("gpt-4".equals(chatgptConfig.getModel())) {
            //gpt-4 默认是8k
            final int partitionSize = 80;
            return ListUtils.splitList(list, partitionSize);
        }

        int index = 0;
        while (index < list.size()) {
            long totalTokens;
            int retryCount = 0; // 添加重试计数器
            do {
                totalTokens = chatGptTranslationAsyncService.calculateToken(list.subList(index, Math.min(index + batchSize, list.size())));

                if (totalTokens < minTokens && index + batchSize < list.size()) {
                    batchSize += addTokens;
                } else if (totalTokens > maxTokens) {
                    batchSize -= addTokens;
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
