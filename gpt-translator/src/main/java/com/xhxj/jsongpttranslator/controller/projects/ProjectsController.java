package com.xhxj.jsongpttranslator.controller.projects;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xhxj.jsongpttranslator.controller.projects.vo.ProjectsPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateFile;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateProjects;
import com.xhxj.jsongpttranslator.framework.web.pojo.CommonResult;
import com.xhxj.jsongpttranslator.service.translatefile.TranslateFileService;
import com.xhxj.jsongpttranslator.service.translateprojects.TranslateProjectsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author:luoshuzhong
 * @create: 2023-05-17 09:48
 * @Description: 翻译项目控制器
 */
@Tag(name = "项目管理类")
@RequestMapping("/projects")
@RestController
public class ProjectsController {

    @Autowired
    private TranslateProjectsService projectsService;

    @Autowired
    private TranslateFileService fileService;

    //分页查询项目
    @GetMapping("/page")
    @Operation(summary = "分页查询项目列表")
    public CommonResult<IPage<TranslateProjects>> page(
            @ParameterObject @Valid ProjectsPageReqVO projectsPageReqVO) {
        return CommonResult.success(projectsService.page(projectsPageReqVO));
    }

    @GetMapping("/file-list")
    @Operation(summary = "获取文件列表")
    public CommonResult<List<TranslateFile>> fileListByProjectId(
            @Schema(description = "项目id", example = "1", type = "integer")
            @Valid @RequestParam("projectId") Long projectId) {
        return CommonResult.success(fileService.fileListByProjectId(projectId));
    }

    @GetMapping("/vue-projects-select")
    @Operation(summary = "前端选择器按名字查询项目列表")
    public CommonResult<List<TranslateProjects>> vueProjectsSelect(
            @ParameterObject @Valid ProjectsPageReqVO projectsPageReqVO) {
        return CommonResult.success(projectsService.vueSelect(projectsPageReqVO));
    }

    @GetMapping("/vue-file-select")
    @Operation(summary = "前端选择器按名字查询文件列表")
    public CommonResult<List<TranslateFile>> vueFileSelect(
            @Schema(description = "项目id", example = "1", type = "integer")
            @Valid @RequestParam(value = "projectId", required = false) Long projectId,
            @Schema(description = "文件名", example = "1", type = "string")
            @Valid @RequestParam("fileName") String fileName) {
        return CommonResult.success(fileService.vueSelect(projectId, fileName));
    }

}
