package com.xhxj.jsongpttranslator.translation;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author:luoshuzhong
 * @create: 2023-05-29 10:13
 * @Description: // 定义翻译模式切换器
 */
@Component
public class TranslationModeSwitcher {
    private final Map<String, TranslationMode> translationModes = new HashMap<>();

    @Resource(name = "chatGptTaskExecutor")
    private Executor chatGptTaskExecutor;

    @Autowired
    public void setTranslationModes(List<TranslationMode> modes) {
        for (TranslationMode mode : modes) {
            Component component = mode.getClass().getAnnotation(Component.class);
            if (component != null) {
                translationModes.put(component.value(), mode);
            }
        }
    }

    public TranslationMode switchMode(String modeName) {
        return translationModes.get(modeName);
    }

    /**
     * 停止翻译
     */
    public void stopTranslate() {
        translationModes.values().forEach(TranslationMode::stopTranslate);
    }
}
