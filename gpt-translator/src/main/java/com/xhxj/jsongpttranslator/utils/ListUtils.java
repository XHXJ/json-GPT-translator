package com.xhxj.jsongpttranslator.utils;

import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author:luoshuzhong
 * @create: 2023-05-29 15:17
 * @Description: list拆分工具类
 */
public class ListUtils {
    /**
     * 拆分list
     *
     * @param list
     * @return
     */
    public static List<List<TranslationData>> splitList(List<TranslationData> list, int partitionSize) {
        return IntStream.range(0, (list.size() + partitionSize - 1) / partitionSize)
                .mapToObj(i -> list.subList(i * partitionSize, Math.min(partitionSize * (i + 1), list.size()))).collect(Collectors.toList());
    }
}
