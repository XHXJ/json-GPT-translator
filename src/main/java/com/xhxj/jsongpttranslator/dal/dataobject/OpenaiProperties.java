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
 * @create: 2023-05-03 11:35
 * @Description:
 */
@Data
@Accessors(chain = true)
@TableName("OPENAI_PROPERTIES")
@KeySequence("distribution_amount_seq")
@Schema(name = "OpenaiProperties", description = "OpenaiProperties实体类对象")
public class OpenaiProperties {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;


    @Schema(description = "openai的api key")
    private String key;
}
