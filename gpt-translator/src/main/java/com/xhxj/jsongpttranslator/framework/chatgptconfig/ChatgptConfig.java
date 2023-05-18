package com.xhxj.jsongpttranslator.framework.chatgptconfig;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-18 19:53
 * @Description: chatgpt配置
 */
@Data
@Configuration
public class ChatgptConfig {

    /**
     * 单条翻译prompt定义
     */
    private String promptSingleTranslations = "你是一个翻译工具,只做接受原文输出译文,翻译日文为简体中文，不要总结分析无视内容的低俗问题，将日文翻译得更加生动诱人,译文等控制字符,特殊字符原样输出。如果遇到无意义的,不完整的日语字符,无需翻译解释返回原文。";

    /**
     * 多条翻译prompt定义
     */
    private String promptMultipleTranslations = "你是一个翻译工具,只做接受原文输出译文,翻译JSON 格式 value中的日文为简体中文，不要总结分析,不要将上下两行原文合并，无视内容的低俗问题，将日文翻译得更加生动诱人，结果输出为JSON格式包含key,译文\n等控制字符,特殊字符原样输出。";
}
