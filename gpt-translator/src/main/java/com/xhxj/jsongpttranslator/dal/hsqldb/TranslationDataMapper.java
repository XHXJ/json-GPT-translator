package com.xhxj.jsongpttranslator.dal.hsqldb;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhxj.jsongpttranslator.controller.translationdata.vo.TranslationDataPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    IPage<TranslationData> selectTranslationDataPage(@Param("page") Page<TranslationData> objectPage, @Param("reqVO") TranslationDataPageReqVO translationDataPageReqVO);
}
