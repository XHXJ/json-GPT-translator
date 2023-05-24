package com.xhxj.jsongpttranslator.service.translatefile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateFile;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateProjects;

import java.util.List;

public interface TranslateFileService extends IService<TranslateFile> {
    /**
     * 根据项目id获取文件列表
     * @param projectId
     * @return
     */
    List<TranslateFile> fileListByProjectId(Long projectId);

    /**
     * vue select 前端组件选择
     * @param projectId
     * @param fileName
     * @return
     */
    List<TranslateFile> vueSelect( Long projectId, String fileName);

    /**
     * 删除文件
     * @param id
     * @return
     */
    Boolean deleteFile(Long id);
}
