package com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo;

import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-18 20:19
 * @Description: chatgptConfig的返回类
 */
@Data
@Schema(name = "chatgptConfigVo", description = "chatgptConfigVo返回体")
public class ChatGptConfigVo {
    /**
     * 单条翻译prompt定义
     */
    @Schema(description = "单条翻译prompt定义")
    @NotBlank(message = "单条翻译prompt定义不能为空")
    private String promptSingleTranslations;

    /**
     * 多条翻译prompt定义
     */
    @Schema(description = "多条翻译prompt定义")
    @NotBlank(message = "多条翻译prompt定义不能为空")
    private String promptMultipleTranslations;

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


}
