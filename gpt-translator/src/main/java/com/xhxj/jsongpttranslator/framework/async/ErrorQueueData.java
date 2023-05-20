package com.xhxj.jsongpttranslator.framework.async;

import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-20 01:56
 * @Description: 用于存储翻译过程中错误或者漏翻的队列
 */
@Configuration
public class ErrorQueueData {
    @Bean("missingRowData")
    public Map<Long, TranslationData> getMissingRowData() {
        return new ConcurrentHashMap<>();
    }

    @Bean("errorData")
    public Map<Long, TranslationData> getErrorData() {
        return new ConcurrentHashMap<>();
    }
}
