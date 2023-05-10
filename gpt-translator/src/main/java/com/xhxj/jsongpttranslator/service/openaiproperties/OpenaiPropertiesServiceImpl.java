package com.xhxj.jsongpttranslator.service.openaiproperties;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhxj.jsongpttranslator.dal.dataobject.OpenaiProperties;
import com.xhxj.jsongpttranslator.dal.hsqldb.OpenaiPropertiesMapper;
import org.springframework.stereotype.Service;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-03 16:59
 * @Description:
 */
@Service
public class OpenaiPropertiesServiceImpl extends ServiceImpl<OpenaiPropertiesMapper, OpenaiProperties> implements OpenaiPropertiesService {
}
