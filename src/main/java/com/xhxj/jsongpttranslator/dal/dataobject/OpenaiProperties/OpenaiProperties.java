package com.xhxj.jsongpttranslator.dal.dataobject.OpenaiProperties;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-03 11:35
 * @Description:
 */
@Data
@TableName("OPENAI_PROPERTIES")
@KeySequence("distribution_amount_seq")
@Schema(name = "OpenaiProperties", description = "OpenaiProperties实体类对象")
public class OpenaiProperties {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * openai的key
     */

    private String key;
}
