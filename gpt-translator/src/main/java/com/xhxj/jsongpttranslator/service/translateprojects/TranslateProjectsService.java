package com.xhxj.jsongpttranslator.service.translateprojects;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xhxj.jsongpttranslator.controller.projects.vo.ProjectsPageReqVO;
import com.xhxj.jsongpttranslator.controller.translationdata.vo.TranslationDataPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateProjects;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;

public interface TranslateProjectsService extends IService<TranslateProjects> {

    IPage<TranslateProjects> page(ProjectsPageReqVO projectsPageReqVO);


}
