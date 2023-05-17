package com.xhxj.jsongpttranslator.controller.projects;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xhxj.jsongpttranslator.controller.projects.vo.ProjectsPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateProjects;
import com.xhxj.jsongpttranslator.framework.web.pojo.CommonResult;
import com.xhxj.jsongpttranslator.service.translateprojects.TranslateProjectsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:luoshuzhong
 * @create: 2023-05-17 09:48
 * @Description: 翻译项目控制器
 */
@Tag(name = "翻译数据管理类")
@RequestMapping("/projects")
@RestController
public class ProjectsController {

    @Autowired
    private TranslateProjectsService projectsService;
    //分页查询项目
    @GetMapping("/page")
    @Operation(summary = "分页查询项目列表")
    public CommonResult<IPage<TranslateProjects>> page(
            @ParameterObject @Valid ProjectsPageReqVO projectsPageReqVO) {
        return CommonResult.success(projectsService.page(projectsPageReqVO));
    }

}
