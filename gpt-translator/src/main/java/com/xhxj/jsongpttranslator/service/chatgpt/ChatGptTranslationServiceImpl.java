package com.xhxj.jsongpttranslator.service.chatgpt;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestRespVo;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestVo;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.framework.chatgptconfig.ChatgptConfig;
import com.xhxj.jsongpttranslator.service.translationdata.TranslationDataService;
import com.xhxj.jsongpttranslator.translation.ApiLimiter;
import com.xhxj.jsongpttranslator.translation.TranslationMode;
import com.xhxj.jsongpttranslator.translation.TranslationModeSwitcher;
import com.xhxj.jsongpttranslator.translation.async.OkHttpClientConfigurator;
import com.xhxj.jsongpttranslator.translation.async.chat.ChatGptTranslationAsyncService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Autowired
    private ChatgptConfig chatgptConfig;

    @Resource(name = "startFlag")
    private AtomicBoolean startFlag;

    @Autowired
    private OkHttpClientConfigurator okHttpClientConfigurator;

    @Autowired
    private ApiLimiter apiLimiter;

    @Autowired
    private TranslationModeSwitcher translationModeSwitcher;

    //翻译模式
    private TranslationMode translationMode;


    /**
     * 停止程序
     */
    @Override
    public void stop() {
        startFlag.set(false);
        //停止所有翻译
        translationModeSwitcher.stopTranslate();
        //停止限制器
        apiLimiter.stop();
        //停止所有okhttp
        okHttpClientConfigurator.getOkHttpClient().dispatcher().cancelAll();
        log.info("程序已经设置停止");
    }

    /**
     * 初始化翻译器
     */
    private void initializeTranslator() {
        //获取翻译模式
        translationMode = translationModeSwitcher.switchMode(chatgptConfig.getTranslateMode());
        //开启限制器
        apiLimiter.start();
        //设置okhttp
        okHttpClientConfigurator.setOkHttpClient();
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
            //初始化翻译器
            initializeTranslator();

            do {
                List<TranslationData> multipleSentences = translationDataService.list(wrapper);
                log.info("还有{}条句子未被翻译", multipleSentences.size());
                //未翻译的句子小于50条
                if (multipleSentences.size() < 50) {
                    break;
                }
                //批量翻译
                translationMode.batchTranslate(multipleSentences);

            } while (startFlag.get());


            do {
                //翻译完所有的句子
                List<TranslationData> singleSentence = translationDataService.list(wrapper);
                //如果还有未被翻译的句子，就单条去翻译
                log.info("单条翻译处理,还有{}条句子未被翻译", singleSentence.size());
                if (singleSentence.size() == 0) {
                    return;
                }
                //单条翻译
                translationMode.translateOne(singleSentence);
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

    @Override
    public void translateOne(List<TranslationData> singleSentence) {
        if (!startFlag.compareAndSet(false, true)) {
            log.info("程序已经启动");
            return;
        }
        //初始化翻译器
        initializeTranslator();
        //如果还有未被翻译的句子，就单条去翻译
        log.info("单条翻译处理,还有{}条句子未被翻译", singleSentence.size());
        if (singleSentence.size() == 0) {
            return;
        }
        //单条翻译
        translationMode.translateOne(singleSentence);
        startFlag.set(false);
    }

    @Override
    public ChatGptConfigTestRespVo testChatGptConfig(ChatGptConfigTestVo chatgptConfigVo) {
        //设置okhttp
        okHttpClientConfigurator.setOkHttpClient();
        return chatGptTranslationAsyncService.testChatGptConfig(chatgptConfigVo);
    }
}
