package com.xhxj.jsongpttranslator.dal.hsqldb;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xhxj.jsongpttranslator.dal.dataobject.OpenaiProperties.OpenaiProperties;
import com.xhxj.jsongpttranslator.dal.dataobject.translationdata.TranslationData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OpenaiPropertiesMapper extends BaseMapper<OpenaiProperties> {
}
