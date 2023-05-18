package com.xhxj.jsongpttranslator.service.translatefile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateFile;
import com.xhxj.jsongpttranslator.dal.hsqldb.TranslateFileMapper;
import com.xhxj.jsongpttranslator.service.translationdata.TranslationDataService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-06 22:17
 * @Description:
 */
@Service
public class TranslateFileServiceImpl extends ServiceImpl<TranslateFileMapper, TranslateFile> implements TranslateFileService {

    @Autowired
    private TranslationDataService translationDataService;

    /**
     * 根据项目id获取文件列表
     *
     * @param projectId
     * @return
     */
    @Override
    public List<TranslateFile> fileListByProjectId(Long projectId) {
        LambdaQueryWrapper<TranslateFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TranslateFile::getProjectId, projectId);
        List<TranslateFile> translateFiles = this.baseMapper.selectList(wrapper);
        //根据文件id查询是否翻译数据
        List<Integer> list = translateFiles.stream().map(TranslateFile::getFileId).toList();
        Map<Integer,Object> whetherToTranslate =  this.baseMapper.queryByFileIdWhetherToTranslate(list);
        translateFiles.forEach(v->{
            Map<String,Object> data = (Map<String, Object>) whetherToTranslate.get(v.getFileId());
            if (data == null) {
                //可能有些文件没有翻译数据
                v.setCompleted(0L);
                v.setNotCompleted(0L);
            }else {
                v.setCompleted((Long) data.get("COMPLETED"));
                v.setNotCompleted((Long) data.get("NOTCOMPLETED"));
            }

        });
        return translateFiles;
    }

    @Override
    public List<TranslateFile> vueSelect(Long projectId, String fileName) {
        LambdaQueryWrapper<TranslateFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(projectId), TranslateFile::getProjectId, projectId);
        wrapper.like(TranslateFile::getFileName, fileName);
        return this.baseMapper.selectList(wrapper);
    }
}
