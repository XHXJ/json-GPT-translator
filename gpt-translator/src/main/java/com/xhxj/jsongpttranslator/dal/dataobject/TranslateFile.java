package com.xhxj.jsongpttranslator.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-06 19:52
 * @Description: 文件do类
 */
@Data
@Accessors(chain = true)
@TableName("TRANSLATE_FILES")
@KeySequence("distribution_amount_seq")
@Schema(name = "FILES", description = "文件do类")
public class TranslateFile {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer fileId;
    @Schema(description = "项目id")
    private Integer projectId;
    @Schema(description = "文件名称")
    private String fileName;
}
