package com.xhxj.jsongpttranslator.framework.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-07 22:28
 * @Description: 读取Translator++导出的excel
 */
public class TranslatorExcelDataListener extends AnalysisEventListener<Map<Integer, String>> {

    private List<Map<Integer, String>> dataList = new ArrayList<>();

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    public List<Map<Integer, String>> getDataList() {
        return dataList;
    }
}
