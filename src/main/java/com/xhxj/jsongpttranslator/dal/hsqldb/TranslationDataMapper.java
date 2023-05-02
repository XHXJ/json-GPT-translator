package com.xhxj.jsongpttranslator.dal.hsqldb;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface TranslationDataMapper extends BaseMapper<TranslationData> {
    /**
     * 批量插入
     *
     * @param entityList 实体列表
     * @return 影响行数
     */
    Integer insertBatchSomeColumn(Collection<TranslationData> entityList);
}
