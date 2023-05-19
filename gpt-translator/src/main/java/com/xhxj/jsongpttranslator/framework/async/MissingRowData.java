package com.xhxj.jsongpttranslator.framework.async;

import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-20 01:56
 * @Description: 用于储存翻译中缺少行的队列数据
 */
@Configuration
public class MissingRowData {
    @Bean
    public Map<Long, TranslationData> getMissingRowData() {
        return new HashMap<>();
    }
}
