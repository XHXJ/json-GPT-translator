package com.xhxj.jsongpttranslator.translation.async.chat;

import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestRespVo;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestVo;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigVo;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.translation.async.TranslationAsyncService;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public interface ChatGptTranslationAsyncService extends TranslationAsyncService {

    /**
     * 测试翻译
     * @param chatgptConfigVo
     * @return
     */
    ChatGptConfigTestRespVo testChatGptConfig(ChatGptConfigTestVo chatgptConfigVo);


}
