package com.xhxj.jsongpttranslator.service.translationdata;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xhxj.jsongpttranslator.controller.translationdata.vo.TranslationDataPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TranslationDataService extends IService<TranslationData> {
    /**
     * 读取json文件
     *
     * @param file
     * @param projectName
     * @return
     */
    Integer readJsonFile(MultipartFile file, String projectName);

    IPage<TranslationData> page(TranslationDataPageReqVO translationDataPageReqVO);

    /**
     * 导出翻译好的json
     *
     * @return
     */
    String exportJson(Integer projects);

    /**
     * 读取Excel文件
     *
     * @param files       csv文件
     * @param projectName
     * @return
     */
    Integer readExcelFile(MultipartFile files, String projectName);

    byte[] exportExcel(Integer projects);

    void oneTranslate(List<Long> list);
}
