package com.xhxj.jsongpttranslator.service.chatgpt;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestRespVo;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestVo;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.framework.chatgptconfig.ChatgptConfig;
import com.xhxj.jsongpttranslator.service.translationdata.TranslationDataService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
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

    @Resource(name = "missingRowData")
    private Map<Long, TranslationData> missingRowData;

    @Resource(name = "errorData")
    private Map<Long, TranslationData> errorData;

    @Autowired
    private ChatgptConfig chatgptConfig;

    //程序开始的唯一标识符
    private final AtomicBoolean startFlag = new AtomicBoolean(false);

    /**
     * 停止程序
     */
    @Override
    public void stop() {
        startFlag.set(false);
        //重启线程池
        log.info("程序已经设置停止");
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
            wrapper.isNull(TranslationData::getTranslationText)
                    .last("limit 1000");
            do {
                List<TranslationData> multipleSentences = translationDataService.list(wrapper);
                log.info("还有{}条句子未被翻译", multipleSentences.size());
                //未翻译的句子小于50条
                if (multipleSentences.size() < 50) {
                    break;
                }
                runBatchTranslation(multipleSentences);
                //处理缺行的数据
                missingRowDataTranslation();
            } while (startFlag.get());


            do {
                //翻译完所有的句子
                List<TranslationData> singleSentence = translationDataService.list(wrapper);
                //如果还有未被翻译的句子，就单条去翻译
                log.info("单条漏翻处理,还有{}条句子未被翻译", singleSentence.size());
                if (singleSentence.size() == 0) {
                    return;
                }
                //单条翻译
                runOneTranslation(singleSentence);
            } while (startFlag.get());


        } catch (Exception e) {
            log.error("程序运行出错", e);
        } finally {
            long endTime = System.currentTimeMillis(); // 记录结束时间
            long elapsedTimeMillis = endTime - startTime; // 计算耗时（毫秒）
            double elapsedTimeMinutes = elapsedTimeMillis / 60000.0; // 转换为分钟
            log.info(String.format("程序运行耗时：%.2f 分钟", elapsedTimeMinutes));
            startFlag.set(false);
        }
    }

    /**
     * 单条翻译
     *
     * @param singleSentence
     */
    private void runOneTranslation(List<TranslationData> singleSentence) {
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
    }

    /**
     * 处理缺行数据
     */
    private void missingRowDataTranslation() {
        try {
            log.info("开始处理缺行数据: {} 条", missingRowData.size());
            List<CompletableFuture<Void>> task = new ArrayList<>(missingRowData.size());
            missingRowData.forEach((k, v) -> task.add(chatGptTranslationAsyncService.accessingChatGptOne(v)));
            //等待所有任务完成
            CompletableFuture.allOf(task.toArray(new CompletableFuture<?>[0])).join();
            //清除缺行数据
            missingRowData.clear();
            //处理翻译错误的数据
            while (errorData.size() > 0) {
                log.info("开始处理调用错误的数据: {} 条", errorData.size());
                List<CompletableFuture<Void>> errorTask = new ArrayList<>(errorData.size());
                errorData.forEach((k, v) -> errorTask.add(chatGptTranslationAsyncService.accessingChatGptOne(v)));
                //等待所有任务完成
                CompletableFuture.allOf(errorTask.toArray(new CompletableFuture<?>[0])).join();
            }
            log.info("处理缺行数据完成");
        } catch (Exception e) {
            log.error("处理缺行数据失败:{}", e.getMessage());
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
        //最大tokens
        int maxTokens = 1900;
        //最小tokens
        int minTokens = 1500;
        //每次循环增加的tokens
        int addTokens = 2;
        if ("gpt-4-32k".equals(chatgptConfig.getModel())) {
            //如果是长文本模型，分割成200条一组
            final int partitionSize = 200;
            return IntStream.range(0, (list.size() + partitionSize - 1) / partitionSize)
                    .mapToObj(i -> list.subList(i * partitionSize, Math.min(partitionSize * (i + 1), list.size()))).collect(Collectors.toList());
        }
        if ("gpt-4".equals(chatgptConfig.getModel())) {
            //gpt-4 默认是8k
            final int partitionSize = 80;
            return IntStream.range(0, (list.size() + partitionSize - 1) / partitionSize)
                    .mapToObj(i -> list.subList(i * partitionSize, Math.min(partitionSize * (i + 1), list.size()))).collect(Collectors.toList());
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

    @Override
    public ChatGptConfigTestRespVo testChatGptConfig(ChatGptConfigTestVo chatgptConfigVo) {

        return chatGptTranslationAsyncService.testChatGptConfig(chatgptConfigVo);
    }
}
