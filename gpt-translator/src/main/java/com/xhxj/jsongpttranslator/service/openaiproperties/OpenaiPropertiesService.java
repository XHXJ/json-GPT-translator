package com.xhxj.jsongpttranslator.service.openaiproperties;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigVo;
import com.xhxj.jsongpttranslator.dal.dataobject.OpenaiProperties;

import java.util.List;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-03 16:59
 * @Description:
 */
public interface OpenaiPropertiesService extends IService<OpenaiProperties> {

    List<String> getOpenaiKey();

    /**
     * 保存配置
     * @param chatGptConfigVo
     */
    void saveConfig(ChatGptConfigVo chatGptConfigVo);
}
