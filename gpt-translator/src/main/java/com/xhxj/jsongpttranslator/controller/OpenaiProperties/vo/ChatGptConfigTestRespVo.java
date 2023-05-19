package com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-18 20:47
 * @Description: 配置测试返回体
 */
@Data
@Schema(name = "chatgptConfigTestVo", description = "chatgptConfigTestVo测试返回体")
public class ChatGptConfigTestRespVo {

    @Schema(description = "单条翻译结果")
    private String promptSingleTranslationsResult;

    @Schema(description = "多条翻译结果")
    private String promptMultipleTranslationsResult;

    @Schema(description = "多条翻译是否成功")
    private Boolean promptMultipleTranslationsSuccess;


}
