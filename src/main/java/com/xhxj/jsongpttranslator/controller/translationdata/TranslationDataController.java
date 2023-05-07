package com.xhxj.jsongpttranslator.controller.translationdata;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xhxj.jsongpttranslator.controller.translationdata.vo.TranslationDataPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.service.translationdata.TranslationDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

    @PostMapping(path = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传翻译csv文件(压缩包)")
    public Integer uploadCsvFile(
            @RequestParam("file")
            @Schema(description = "上传的文件", type = "string", format = "binary")
            @RequestPart(value = "file") final MultipartFile file) {
        return translationDataService.readCsvFile(file);
    }

    @GetMapping("/export-csv")
    @Operation(summary = "导出翻译后的csv文件")
    public void exportCsv(HttpServletResponse response, @Schema(name = "projects", description = "项目id", type = "integer") @RequestParam(value = "projects") Integer projects) {
        try {
            // 获取翻译后的字节数组
            byte[] translatedCsv = translationDataService.exportCsv(projects);

            // 设置响应头以指示浏览器下载文件
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=translated_csv.zip");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentLength(translatedCsv.length);

            // 将文件内容写入响应
            response.getOutputStream().write(translatedCsv);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {
            // 处理异常，例如记录错误或返回错误响应
            e.printStackTrace();
        }
    }


    @GetMapping("/export-json")
    @Operation(summary = "导出翻译后的json文件")
    public void exportJson(HttpServletResponse response) {
        try {
            // 获取翻译后的 JSON 字符串
            String translatedJson = translationDataService.exportJson();

            // 设置响应头以指示浏览器下载文件
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=translated.json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            // 将文件内容写入响应
            response.getWriter().write(translatedJson);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            // 处理异常，例如记录错误或返回错误响应
            e.printStackTrace();
        }
    }


}
