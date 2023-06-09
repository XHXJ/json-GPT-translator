package com.xhxj.jsongpttranslator.service.translateprojects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhxj.jsongpttranslator.controller.projects.vo.ProjectsPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateFile;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateProjects;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.dal.hsqldb.TranslateProjectsMapper;
import com.xhxj.jsongpttranslator.service.translatefile.TranslateFileService;
import com.xhxj.jsongpttranslator.service.translationdata.TranslationDataService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-06 22:14
 * @Description:
 */
@Service
public class TranslateProjectsServiceImpl extends ServiceImpl<TranslateProjectsMapper, TranslateProjects> implements TranslateProjectsService {

    @Autowired
    private TranslationDataService translationDataService;

    @Autowired
    private TranslateFileService translateFileService;

    @Override
    public IPage<TranslateProjects> page(ProjectsPageReqVO projectsPageReqVO) {
        LambdaQueryWrapper<TranslateProjects> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(projectsPageReqVO.getProjectId()), TranslateProjects::getProjectId, projectsPageReqVO.getProjectId());
        wrapper.like(StringUtils.isNotBlank(projectsPageReqVO.getProjectName()), TranslateProjects::getProjectName, projectsPageReqVO.getProjectName());
        wrapper.orderByDesc(TranslateProjects::getProjectId);
        Page<TranslateProjects> page = page(new Page<>(projectsPageReqVO.getPageNum(), projectsPageReqVO.getPageSize()), wrapper);
        if (ObjectUtils.isEmpty(page.getRecords())) {
            return page;
        }
        //获取翻译进度
        Map<Integer, Object> whetherToTranslate = this.baseMapper.queryByProjectIdWhetherToTranslate(page.getRecords().stream().map(TranslateProjects::getProjectId).toList());
        page.getRecords().forEach(v -> {
            Map<String, Object> data = (Map<String, Object>) whetherToTranslate.get(v.getProjectId());
            if (data == null) {
                //可能有些文件没有翻译数据
                v.setCompleted(0L);
                v.setNotCompleted(0L);
            } else {
                v.setCompleted((Long) data.get("COMPLETED"));
                v.setNotCompleted((Long) data.get("NOTCOMPLETED"));
            }
        });

        return page;
    }

    @Override
    public List<TranslateProjects> vueSelect(ProjectsPageReqVO projectsPageReqVO) {
        LambdaQueryWrapper<TranslateProjects> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(projectsPageReqVO.getProjectName()), TranslateProjects::getProjectName, projectsPageReqVO.getProjectName());
        return this.baseMapper.selectList(wrapper);
    }

    /**
     * 删除项目
     *
     * @param id
     * @return
     */
    @Override
    public Boolean deleteProjects(Long id) {
        //根据项目id删除翻译数据
        LambdaQueryWrapper<TranslationData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TranslationData::getProjectId, id);
        translationDataService.remove(wrapper);
        //删除项目下的文件
        translateFileService.remove(new LambdaQueryWrapper<TranslateFile>().eq(TranslateFile::getProjectId, id));
        //删除项目
        return removeById(id);
    }
}
