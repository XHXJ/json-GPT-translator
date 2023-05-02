package com.xhxj.jsongpttranslator.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author:luoshuzhong
 * @create: 2023-05-02 17:53
 * @Description: ManualTransFile.json实体类对象
 */
@TableName("JSON_TRANSLATION")
@KeySequence("distribution_amount_seq")
@Data
@Schema(name = "ManualTransFileJson", description = "ManualTransFile.json实体类对象")
public class TranslationData {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "原文")
    private String originalText;
    @Schema(description = "翻译文本")
    private String translationText;

}
