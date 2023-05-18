package com.xhxj.jsongpttranslator.service.chatgpt;

import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestVo;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigVo;

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
    ChatGptConfigTestVo testChatGptConfig(ChatGptConfigVo chatgptConfigVo);
}
