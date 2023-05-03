package com.xhxj.jsongpttranslator.service.translationdata;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhxj.jsongpttranslator.controller.translationdata.vo.TranslationDataPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.translationdata.TranslationData;
import com.xhxj.jsongpttranslator.dal.hsqldb.TranslationDataMapper;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author:luoshuzhong
 * @create: 2023-05-02 17:58
 * @Description: ManualTransFileService 实现类
 */
@Service
public class TranslationDataServiceImpl extends ServiceImpl<TranslationDataMapper, TranslationData> implements TranslationDataService {
    @Override
    public IPage<TranslationData> page(TranslationDataPageReqVO translationDataPageReqVO) {
        LambdaQueryWrapper<TranslationData> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(translationDataPageReqVO.getId() != null, TranslationData::getId, translationDataPageReqVO.getId())
                .isNotNull(BooleanUtils.toBoolean(translationDataPageReqVO.getIsTranslation()), TranslationData::getTranslationText)
                .like(StringUtils.isNotBlank(translationDataPageReqVO.getOriginalText()), TranslationData::getOriginalText, translationDataPageReqVO.getOriginalText())
                .like(StringUtils.isNotBlank(translationDataPageReqVO.getTranslationText()), TranslationData::getTranslationText, translationDataPageReqVO.getTranslationText())
        ;
        return this.page(new Page<>(translationDataPageReqVO.getPageNum(), translationDataPageReqVO.getPageSize()), wrapper);
    }

    @Override
    public Integer readJsonFile(MultipartFile file) {
        try {
            //读取inputStream
            InputStream inputStream = file.getInputStream();
            //将inputStream转换为字符串
            String jsonStr = IoUtil.read(inputStream, StandardCharsets.UTF_8);
            //将字符串转换为json
            JSONObject json = JSONUtil.parseObj(jsonStr);

            List<TranslationData> list = json.keySet().stream()
                    .map(o -> {
                        TranslationData translationData = new TranslationData();
                        translationData.setOriginalText(o);
                        return translationData;
                    })
                    .toList();
            return this.baseMapper.insertBatchSomeColumn(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 导出翻译好的json
     *
     * @return json字符串
     */
    @Override
    public String exportJson() {
        List<TranslationData> translationData = this.baseMapper.selectList(new LambdaQueryWrapper<TranslationData>().isNotNull(TranslationData::getTranslationText));
        //将list转换为json
        JSONObject json = JSONUtil.createObj();
        translationData.forEach(o -> json.set(o.getOriginalText(), o.getTranslationText()));
        return json.toString();
    }
}
