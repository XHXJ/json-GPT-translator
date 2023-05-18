package com.xhxj.jsongpttranslator.dal.hsqldb;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateFile;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TranslateFileMapper extends BaseMapper<TranslateFile> {
    /**
     * 根据文件id查询是否翻译数据
     * @param list
     * @return
     */
    @MapKey("FILEID")
    Map<Integer, Object> queryByFileIdWhetherToTranslate(@Param("list") List<Integer> list);
}
