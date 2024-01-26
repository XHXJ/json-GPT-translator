package com.xhxj.jsongpttranslator.translation.async.chat;

import com.xhxj.jsongpttranslator.controller.openaiproperties.vo.ChatGptConfigTestRespVo;
import com.xhxj.jsongpttranslator.controller.openaiproperties.vo.ChatGptConfigTestVo;
import com.xhxj.jsongpttranslator.translation.async.TranslationAsyncService;

public interface ChatGptTranslationAsyncService extends TranslationAsyncService {

    /**
     * 测试翻译
     * @param chatgptConfigVo
     * @return
     */
    ChatGptConfigTestRespVo testChatGptConfig(ChatGptConfigTestVo chatgptConfigVo);


}
