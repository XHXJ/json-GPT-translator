package com.xhxj.jsongpttranslator.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author:luoshuzhong
 * @create: 2023-05-02 17:53
 * @Description: ManualTransFile.json实体类对象
 */
@TableName("TRANSLATIONS")
@KeySequence("distribution_amount_seq")
@Accessors(chain = true)
@Data
@Schema(name = "TRANSLATION", description = "ManualTransFile.json实体类对象")
public class TranslationData implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "原文")
    private String originalText;
    @Schema(description = "翻译文本")
    private String translationText;
    @Schema(description = "句子序列")
    private Integer sequence;
    @Schema(description = "项目id")
    private Integer projectId;
    @Schema(description = "文件id")
    private Integer fileId;
}
