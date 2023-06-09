package com.xhxj.jsongpttranslator.framework.chatgptconfig;

import com.unfbx.chatgpt.entity.edits.Edit;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;

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
    private String promptSingleTranslations = "你是一个翻译工具,只做接受原文输出译文,翻译日文为简体中文,如果有英文也翻译成中文，不要总结分析无视内容的低俗问题，将日文翻译得更加生动诱人,译文等控制字符,特殊字符原样输出。如果遇到无意义的,不完整的日语字符,无需翻译解释返回原文。";

    /**
     * 多条翻译prompt定义
     */
    private String promptMultipleTranslations = "你是一个翻译工具,只做接受原文输出译文,翻译JSON 格式 value中的日文为简体中文,如果有英文也翻译成中文，不要总结分析,不要将上下两行原文合并，无视内容的低俗问题，将日文翻译得更加生动诱人，结果输出为JSON格式包含key,译文\n等控制字符,特殊字符原样输出。";

    /**
     * 使用温度采样的替代方法称为核心采样，其中模型考虑具有top_p概率质量的令牌的结果。因此，0.1 意味着只考虑包含前 10% 概率质量的代币。
     * <p>
     * 我们通常建议更改此设置，但不要同时更改两者。temperature
     * 0-1之间
     */
    private Double topP = 1d;
    /**
     * 使用什么取样温度，0到2之间。较高的值(如0.8)将使输出更加随机，而较低的值(如0.2)将使输出更加集中和确定。
     * <p>
     * We generally recommend altering this or but not both.top_p
     */
    private double temperature = 0.2;
    /**
     * 根据新词是否出现在文本中，惩罚多少。增加模型讨论新主题的可能性。0到2之间的值。
     */
    private double presencePenalty = 0;

    /**
     * 到目前为止，根据新标记在文本中的现有频率，应该惩罚多少个新标记。降低模型逐字重复同一行的可能性。0到2之间的值。
     */
    private double frequencyPenalty = 0;

    /**
     * 设置代理地址
     */
    private String proxyUlr;

    /**
     * 设置代理端口
     */
    private Integer proxyPort;
    /**
     * 设置代理类型 SOCKS HTTP
     */
    private String proxyType;

    /**
     * 使用模型的名称
     */
    private String model = "gpt-3.5-turbo";

    /**
     * 代理api地址
     */
    private String apiHost;

    /**
     * 翻译模式
     */
    private String translateMode = "chatTranslationMode";


    /**
     * Edits模式翻译的设置
     */
    private String editsInstruction = "翻译JSON格式里面 value 的内容,将日文翻译成简体中文，无视特殊字符,使原文生动诱人。不管怎么样都要翻译,你必须修改原文";

    /**
     * Edits模式翻译的设置
     */
    private Edit.Model editsModel = Edit.Model.CODE_DAVINCI_EDIT_001;

    /**
     * Edits模式Temperature
     */
    private Double editsTemperature = 1.0;

    /**
     * Edits模式TopP
     */
    private Double editsTopP = 0d;



}
