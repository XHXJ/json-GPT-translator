package com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-03 17:29
 * @Description:
 */
@Data
@Schema(name = "OpenaiPropertiesVO", description = "OpenaiPropertiesVO实体类对象")
public class OpenaiPropertiesVO {
    @Schema(description = "页码", example = "1", type = "integer")
    private Integer pageNum = 1;
    @Schema(description = "每页条数", example = "10", type = "integer")
    private Integer pageSize = 10;
    @Schema(description = "openai的key")
    private String key;
}
