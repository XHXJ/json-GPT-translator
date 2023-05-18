package com.xhxj.jsongpttranslator.service.translateprojects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhxj.jsongpttranslator.controller.projects.vo.ProjectsPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateProjects;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.dal.hsqldb.TranslateProjectsMapper;
import com.xhxj.jsongpttranslator.service.translationdata.TranslationDataService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-06 22:14
 * @Description:
 */
@Service
public class TranslateProjectsServiceImpl extends ServiceImpl<TranslateProjectsMapper, TranslateProjects> implements TranslateProjectsService {

    @Autowired
    private TranslationDataService translationDataService;

    @Override
    public IPage<TranslateProjects> page(ProjectsPageReqVO projectsPageReqVO) {
        LambdaQueryWrapper<TranslateProjects> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(projectsPageReqVO.getProjectId()), TranslateProjects::getProjectId, projectsPageReqVO.getProjectId());
        wrapper.like(StringUtils.isNotBlank(projectsPageReqVO.getProjectName()), TranslateProjects::getProjectName, projectsPageReqVO.getProjectName());
        wrapper.orderByDesc(TranslateProjects::getProjectId);
        Page<TranslateProjects> page = page(new Page<>(projectsPageReqVO.getPageNum(), projectsPageReqVO.getPageSize()), wrapper);
        //获取翻译进度
        page.getRecords().forEach(v -> {
            v.setCompleted(translationDataService.count(new LambdaQueryWrapper<TranslationData>().eq(TranslationData::getProjectId, v.getProjectId()).isNotNull(TranslationData::getTranslationText)));
            v.setNotCompleted(translationDataService.count(new LambdaQueryWrapper<TranslationData>().eq(TranslationData::getProjectId, v.getProjectId()).isNull(TranslationData::getTranslationText)));
        });

        return page;
    }

    @Override
    public List<TranslateProjects> vueSelect(ProjectsPageReqVO projectsPageReqVO) {
        LambdaQueryWrapper<TranslateProjects> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(projectsPageReqVO.getProjectName()), TranslateProjects::getProjectName, projectsPageReqVO.getProjectName());
        return this.baseMapper.selectList(wrapper);
    }
}
