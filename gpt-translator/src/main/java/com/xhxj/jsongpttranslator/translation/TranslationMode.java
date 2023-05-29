package com.xhxj.jsongpttranslator.translation;

import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;

import java.util.List;

public interface TranslationMode {
    /**
     * 批量翻译
     * @param data
     */
    void batchTranslate(List<TranslationData> data);

    void translateOne(List<TranslationData> data);

    /**
     * 停止翻译
     */
    void stopTranslate();
}
