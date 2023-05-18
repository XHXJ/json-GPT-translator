package com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo;

import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private String promptSingleTranslations;

    /**
     * 多条翻译prompt定义
     */
    @Schema(description = "多条翻译prompt定义")
    private String promptMultipleTranslations;

    /**
     * 测试数据多条
     */
    @Schema(description = "测试数据多条")
    List<TranslationData> translationDataList;

    /**
     * 测试数据单条
     */
    @Schema(description = "测试数据单条")
    TranslationData translationData;
}
