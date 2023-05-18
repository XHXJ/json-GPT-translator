package com.xhxj.jsongpttranslator.dal.dataobject;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-06 19:51
 * @Description: 项目do类
 */
@Data
@Accessors(chain = true)
@TableName("TRANSLATE_PROJECTS")
@KeySequence("distribution_amount_seq")
@Schema(name = "PROJECTS", description = "项目do类")
public class TranslateProjects {
    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Integer projectId;
    @Schema(description = "项目名称")
    private String projectName;

    @TableField(exist = false)
    @Schema(description = "没翻译的数量")
    private Long notCompleted;

    @TableField(exist = false)
    @Schema(description = "已翻译的数量")
    private Long completed;
}
