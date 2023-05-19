package com.xhxj.jsongpttranslator.service.translationdata;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhxj.jsongpttranslator.controller.translationdata.vo.TranslationDataPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateFile;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateProjects;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.dal.hsqldb.TranslationDataMapper;
import com.xhxj.jsongpttranslator.framework.easyexcel.TranslatorExcelDataListener;
import com.xhxj.jsongpttranslator.service.translatefile.TranslateFileService;
import com.xhxj.jsongpttranslator.service.translateprojects.TranslateProjectsService;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author:luoshuzhong
 * @create: 2023-05-02 17:58
 * @Description: ManualTransFileService 实现类
 */
@Service
public class TranslationDataServiceImpl extends ServiceImpl<TranslationDataMapper, TranslationData> implements TranslationDataService {

    @Autowired
    @Lazy
    private TranslateProjectsService translateProjectsService;

    @Autowired
    @Lazy
    private TranslateFileService translateFileService;


    @Override
    public IPage<TranslationData> page(TranslationDataPageReqVO translationDataPageReqVO) {
        LambdaQueryWrapper<TranslationData> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(translationDataPageReqVO.getId() != null, TranslationData::getId, translationDataPageReqVO.getId())
                .eq(translationDataPageReqVO.getFileId() != null, TranslationData::getFileId, translationDataPageReqVO.getFileId())
                .eq(translationDataPageReqVO.getProjectId() != null, TranslationData::getProjectId, translationDataPageReqVO.getProjectId())
                .like(StringUtils.isNotBlank(translationDataPageReqVO.getOriginalText()), TranslationData::getOriginalText, translationDataPageReqVO.getOriginalText())
                .like(StringUtils.isNotBlank(translationDataPageReqVO.getTranslationText()), TranslationData::getTranslationText, translationDataPageReqVO.getTranslationText())
        ;
        if (ObjectUtils.isNotEmpty(translationDataPageReqVO.getIsTranslation())) {
            wrapper.isNotNull(translationDataPageReqVO.getIsTranslation(), TranslationData::getTranslationText);
            wrapper.isNull(!translationDataPageReqVO.getIsTranslation(), TranslationData::getTranslationText);
        }
        return this.page(new Page<>(translationDataPageReqVO.getPageNum(), translationDataPageReqVO.getPageSize()), wrapper);
    }

    @Override
    public Integer readJsonFile(MultipartFile file, String projectName) {
        try {
            //新建项目
            TranslateProjects translateProjects = new TranslateProjects()
                    .setProjectName(projectName);
            translateProjectsService.save(translateProjects);
            //新建文件
            TranslateFile translateFile = new TranslateFile()
                    .setProjectId(translateProjects.getProjectId())
                    .setFileName(file.getOriginalFilename());
            translateFileService.save(translateFile);
            //读取inputStream
            InputStream inputStream = file.getInputStream();
            //将inputStream转换为字符串
            String jsonStr = IoUtil.read(inputStream, StandardCharsets.UTF_8);
            //将字符串转换为json
            JSONObject json = JSONUtil.parseObj(jsonStr);

            List<TranslationData> list = json.keySet().stream()
                    .map(o -> new TranslationData()
                            .setFileId(translateFile.getFileId())
                            .setProjectId(translateProjects.getProjectId())
                            .setOriginalText(o))
                    .toList();
            batchInsert(list);
            return list.size();
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
    public String exportJson(Integer projects) {
        //寻找项目下所有的翻译数据
        List<TranslationData> translationData = this.baseMapper.selectList(new LambdaQueryWrapper<TranslationData>()
                .eq(TranslationData::getProjectId, projects)
                .isNotNull(TranslationData::getTranslationText)
        );
        //将list转换为json
        JSONObject json = JSONUtil.createObj();
        translationData.forEach(o -> json.set(o.getOriginalText(), o.getTranslationText()));
        return json.toString();
    }

    @Override
    public Integer readExcelFile(MultipartFile zipFile, String projectName) {
        //累计新增
        int count = 0;

        // 获取zip文件中的csv文件
        //创建项目名称
        TranslateProjects translateProjects = new TranslateProjects()
                .setProjectName(projectName);
        translateProjectsService.save(translateProjects);

        try {
            ZipInputStream zis = new ZipInputStream(zipFile.getInputStream(), StandardCharsets.UTF_8);
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                //获取文件名
                String[] parts = zipEntry.getName().split("/");
                String extractedFileName = parts[parts.length - 1];
                //创建翻译文件数据
                TranslateFile translateFile = new TranslateFile()
                        .setProjectId(translateProjects.getProjectId())
                        .setFileName(extractedFileName);
                //保存文件信息
                translateFileService.save(translateFile);


                if (extractedFileName.toLowerCase().endsWith(".xlsx")) {
                    //保存翻译原文数据
                    List<String> originalText = processExcelFile(zis);
                    if (originalText != null && originalText.size() != 0) {
                        List<TranslationData> translationDataList = new ArrayList<>();
                        //保存翻译原文数据
                        for (int i = 0; i < originalText.size(); i++) {
                            TranslationData translationData = new TranslationData()
                                    .setOriginalText(originalText.get(i))
                                    .setFileId(translateFile.getFileId())
                                    .setProjectId(translateProjects.getProjectId())
                                    .setSequence(i);
                            translationDataList.add(translationData);
                        }
                        batchInsert(translationDataList);
                        count += translationDataList.size();
                    }
                }
                zis.closeEntry(); // Move the closeEntry() method inside the while loop
                zipEntry = zis.getNextEntry();
            }
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return count;
    }

    /**
     * 批量插入
     *
     * @param translationDataList
     */
    private void batchInsert(List<TranslationData> translationDataList) {
        int batchSize = 1000;
        for (int i = 0; i < translationDataList.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, translationDataList.size());
            List<TranslationData> sublist = translationDataList.subList(i, endIndex);
            this.baseMapper.insertBatchSomeColumn(sublist);
        }
    }

    /**
     * 处理Excel文件
     *
     * @param zis
     * @return
     */
    private List<String> processExcelFile(ZipInputStream zis) {
        TranslatorExcelDataListener excelDataListener = new TranslatorExcelDataListener();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(zis, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        EasyExcel.read(inputStream, excelDataListener).sheet().doRead();
        return excelDataListener.getDataList().stream()
                .map(map -> map.get(0))
                .collect(Collectors.toList());
    }


    /**
     * 导出Excel文件
     *
     * @return
     */
    @Override
    public byte[] exportExcel(Integer projects) {
        //获取项目下的所有文件
        List<TranslateFile> translateFiles = translateFileService.list(new LambdaQueryWrapper<TranslateFile>().eq(TranslateFile::getProjectId, projects));
        //获取文件列表下的所有翻译数据
        List<TranslationData> translationDataList = this.baseMapper.selectList(new LambdaQueryWrapper<TranslationData>().eq(TranslationData::getProjectId, projects));
        //将翻译数据按照文件分组
        Map<Integer, List<TranslationData>> collect = translationDataList.stream().collect(Collectors.groupingBy(TranslationData::getFileId));

        //创建 ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //创建zip文件
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            //创建zip文件
            translateFiles.forEach(o -> {
                try {
                    //创建zip文件
                    zipOutputStream.putNextEntry(new ZipEntry(o.getFileName()));

                    //获取翻译数据
                    List<TranslationData> translationData = collect.get(o.getFileId());
                    //如果翻译数据为空，则跳过有些
                    if (translationData == null) {
                        return;
                    }
                    //创建表头
                    List<List<String>> head = ListUtils.newArrayList();
                    List<String> head0 = ListUtils.newArrayList();
                    head0.add("原文");
                    List<String> head1 = ListUtils.newArrayList();
                    head1.add("译文");
                    head.add(head0);
                    head.add(head1);
                    //创建表格
                    List<List<Object>> tableData = ListUtils.newArrayList();
                    for (TranslationData translationDatum : translationData) {
                        List<Object> data = ListUtils.newArrayList();
                        data.add(translationDatum.getOriginalText());
                        data.add(translationDatum.getTranslationText());
                        tableData.add(data);
                    }
                    EasyExcel.write(zipOutputStream).autoCloseStream(false).head(head).sheet("sheet1").doWrite(tableData);


                    zipOutputStream.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }


}
