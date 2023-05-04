package com.xhxj.jsongpttranslator.service.chatgpt;

import com.unfbx.chatgpt.entity.chat.Message;
import com.xhxj.jsongpttranslator.dal.dataobject.translationdata.TranslationData;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public interface ChatGptTranslationAsyncService {
    CompletableFuture<Objects> accessingChatGpt(List<TranslationData> translationData);

    void reportCurrentApiLimitCounters();

    /**
     * 翻译单条句子
     * @param translationData
     */
    CompletableFuture<Void> accessingChatGptOne(TranslationData translationData);

    /**
     * 获取token
     * @param translationData
     * @return
     */
    Long calculateToken(List<TranslationData> translationData);
}
