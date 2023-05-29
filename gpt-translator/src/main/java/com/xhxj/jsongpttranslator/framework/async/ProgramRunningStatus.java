package com.xhxj.jsongpttranslator.framework.async;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author:luoshuzhong
 * @create: 2023-05-29 10:38
 * @Description: 程序运行状态
 */
@Configuration
public class ProgramRunningStatus {
    //程序开始的唯一标识符
    @Bean("startFlag")
    public AtomicBoolean startFlag() {
        return new AtomicBoolean(false);
    }


}
