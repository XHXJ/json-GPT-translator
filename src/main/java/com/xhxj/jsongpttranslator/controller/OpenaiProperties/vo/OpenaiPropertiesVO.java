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
    @Schema(description = "openai的key")
    private String key;
}
