package com.xhxj.jsongpttranslator.framework.chatgpt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-03 11:35
 * @Description:
 */
@Configuration
@ConfigurationProperties(prefix = "openai")
@Data
public class OpenaiProperties {
    /**
     * openai的key
     */
    private String apiKey;
}
