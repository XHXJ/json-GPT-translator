package com.xhxj.jsongpttranslator.dal.hsqldb;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateProjects;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TranslateProjectsMapper extends BaseMapper<TranslateProjects> {
    /**
     * 根据项目id查询是否翻译数据
     * @param list
     * @return
     */
    @MapKey("PROJECTID")
    Map<Integer, Object> queryByProjectIdWhetherToTranslate(@Param("list") List<Integer> list);
}
