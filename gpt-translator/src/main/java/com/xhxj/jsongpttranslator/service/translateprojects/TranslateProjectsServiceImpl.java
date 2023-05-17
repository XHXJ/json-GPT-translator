package com.xhxj.jsongpttranslator.service.translateprojects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhxj.jsongpttranslator.controller.projects.vo.ProjectsPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateProjects;
import com.xhxj.jsongpttranslator.dal.hsqldb.TranslateProjectsMapper;
import org.springframework.stereotype.Service;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-06 22:14
 * @Description:
 */
@Service
public class TranslateProjectsServiceImpl extends ServiceImpl<TranslateProjectsMapper, TranslateProjects> implements TranslateProjectsService {
    @Override
    public IPage<TranslateProjects> page(ProjectsPageReqVO projectsPageReqVO) {
        LambdaQueryWrapper<TranslateProjects> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(TranslateProjects::getProjectName, projectsPageReqVO.getProjectName());
        wrapper.orderByDesc(TranslateProjects::getProjectId);

        return page(new Page<>(projectsPageReqVO.getPageNum(),projectsPageReqVO.getPageSize()), wrapper);
    }
}
