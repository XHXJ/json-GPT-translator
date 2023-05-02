package com.xhxj.jsongpttranslator.controller.translationdata;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xhxj.jsongpttranslator.controller.translationdata.vo.TranslationDataPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.service.translationdata.TranslationDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author:luoshuzhong
 * @create: 2023-05-02 18:03
 * @Description: 开始
 */
@Tag(name = "翻译数据管理类")
@RequestMapping("/translation-data")
@RestController
public class TranslationDataController {

    @Autowired
    private TranslationDataService translationDataService;

    /**
     * 查询翻译列表
     *
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询翻译列表")
    public IPage<TranslationData> page(
            @ParameterObject @Valid TranslationDataPageReqVO translationDataPageReqVO) {
        return translationDataService.page(translationDataPageReqVO);
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传翻译json文件")
    public Integer uploadFile(
            @RequestParam("file")
            @Schema(description = "上传的文件", type = "string", format = "binary")
            @RequestPart(value = "file") final MultipartFile file) {
        return translationDataService.readJsonFile(file);
    }

}
