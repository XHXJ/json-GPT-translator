package com.xhxj.jsongpttranslator.service.chatgpt;

import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestRespVo;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestVo;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigVo;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;

import java.util.List;

public interface ChatGptTranslationService {
    /**
     * 开始翻译
     */
    void start();

    /**
     * 停止翻译
     */
    void stop();

    /**
     * 测试翻译
     */
    ChatGptConfigTestRespVo testChatGptConfig(ChatGptConfigTestVo chatgptConfigVo);


    /**
     * 批量单独翻译
     * @param translationDataList
     */
    void translateOne(List<TranslationData> translationDataList);
}
