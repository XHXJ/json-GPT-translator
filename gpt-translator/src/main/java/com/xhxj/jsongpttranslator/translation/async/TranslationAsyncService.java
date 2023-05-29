package com.xhxj.jsongpttranslator.translation.async;

import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TranslationAsyncService {
    CompletableFuture<Void> multipleTranslations(List<TranslationData> translationData);

    /**
     * 翻译单条句子
     * @param translationData
     */
    CompletableFuture<Void> singleTranslation(TranslationData translationData);

    /**
     * 获取token
     * @param translationData
     * @return
     */
    Long calculateToken(List<TranslationData> translationData);
}
