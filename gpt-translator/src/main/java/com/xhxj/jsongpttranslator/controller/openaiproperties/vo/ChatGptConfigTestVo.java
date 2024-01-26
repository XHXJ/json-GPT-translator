package com.xhxj.jsongpttranslator.controller.openaiproperties.vo;

import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-18 20:47
 * @Description: 配置测试返回体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "chatgptConfigTestVo", description = "chatgptConfigTestVo测试返回体")
public class ChatGptConfigTestVo extends ChatGptConfigVo {
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
