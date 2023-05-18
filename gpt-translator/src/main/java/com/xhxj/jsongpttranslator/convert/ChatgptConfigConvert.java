package com.xhxj.jsongpttranslator.convert;

import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigVo;
import com.xhxj.jsongpttranslator.framework.chatgptconfig.ChatgptConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-18 20:24
 * @Description: 转换类
 */
@Mapper
public interface ChatgptConfigConvert {
    ChatgptConfigConvert INSTANCE = Mappers.getMapper(ChatgptConfigConvert.class);

    ChatGptConfigVo convert(ChatgptConfig chatgptConfig);
}
