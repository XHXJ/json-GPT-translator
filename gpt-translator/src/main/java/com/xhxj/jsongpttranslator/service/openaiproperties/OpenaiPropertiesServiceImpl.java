package com.xhxj.jsongpttranslator.service.openaiproperties;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhxj.jsongpttranslator.controller.openaiproperties.vo.ChatGptConfigVo;
import com.xhxj.jsongpttranslator.dal.dataobject.OpenaiProperties;
import com.xhxj.jsongpttranslator.dal.hsqldb.OpenaiPropertiesMapper;
import com.xhxj.jsongpttranslator.framework.chatgptconfig.ChatgptConfig;
import com.xhxj.jsongpttranslator.framework.web.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.xhxj.jsongpttranslator.framework.web.exception.error.ErrorCodeConstants.OPENAIKEY_DOES_NOT_EXIST;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-03 16:59
 * @Description:
 */
@Service
public class OpenaiPropertiesServiceImpl extends ServiceImpl<OpenaiPropertiesMapper, OpenaiProperties> implements OpenaiPropertiesService {

    @Autowired
    private ChatgptConfig chatgptConfig;

    @Override
    public List<String> getOpenaiKey() {
        List<OpenaiProperties> openaiProperties = this.baseMapper.selectList(null);
        if (openaiProperties.isEmpty()) {
            throw new ServiceException(OPENAIKEY_DOES_NOT_EXIST);
        }
        return openaiProperties.stream().map(OpenaiProperties::getOpenaiKey).collect(Collectors.toList());
    }

    @Override
    public void saveConfig(ChatGptConfigVo chatGptConfigVo) {
        chatgptConfig.setTranslateMode(chatGptConfigVo.getTranslateMode());
        chatgptConfig.setApiHost(chatGptConfigVo.getApiHost());
        chatgptConfig.setModel(chatGptConfigVo.getModel());
        chatgptConfig.setResponseFormat(chatGptConfigVo.getResponseFormat());
        chatgptConfig.setPromptSingleJsonKey(chatGptConfigVo.getPromptSingleJsonKey());
        chatgptConfig.setPromptSingleTranslations(chatGptConfigVo.getPromptSingleTranslations());
        chatgptConfig.setPromptMultipleTranslations(chatGptConfigVo.getPromptMultipleTranslations());
        chatgptConfig.setTopP(chatGptConfigVo.getTopP());
        chatgptConfig.setTemperature(chatGptConfigVo.getTemperature());
        chatgptConfig.setPresencePenalty(chatGptConfigVo.getPresencePenalty());
        chatgptConfig.setFrequencyPenalty(chatGptConfigVo.getFrequencyPenalty());
        chatgptConfig.setProxyUlr(chatGptConfigVo.getProxyUlr());
        chatgptConfig.setProxyPort(chatGptConfigVo.getProxyPort());
        chatgptConfig.setProxyType(chatGptConfigVo.getProxyType());
    }
}
