package com.xhxj.jsongpttranslator.service.translationdata;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.xhxj.jsongpttranslator.controller.translationdata.vo.TranslationDataPageReqVO;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateFile;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslateProjects;
import com.xhxj.jsongpttranslator.dal.dataobject.TranslationData;
import com.xhxj.jsongpttranslator.dal.hsqldb.TranslationDataMapper;
import com.xhxj.jsongpttranslator.service.translatefile.TranslateFileService;
import com.xhxj.jsongpttranslator.service.translateprojects.TranslateProjectsService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
    private TranslateProjectsService translateProjectsService;

    @Autowired
    private TranslateFileService translateFileService;


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

    @Override
    public Integer readCsvFile(MultipartFile zipFile) {
        // 获取zip文件中的csv文件

        //创建项目名称
        TranslateProjects translateProjects = new TranslateProjects()
                .setProjectName(zipFile.getOriginalFilename());
        translateProjectsService.save(translateProjects);

        try {
            ZipInputStream zis = new ZipInputStream(zipFile.getInputStream(), StandardCharsets.UTF_8);
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                String fileName = zipEntry.getName();
                //创建翻译文件数据
                TranslateFile translateFile = new TranslateFile()
                        .setProjectId(translateProjects.getProjectId())
                        .setFileName(fileName);
                //保存文件信息
                translateFileService.save(translateFile);


                if (fileName.toLowerCase().endsWith(".csv")) {
                    //保存翻译原文数据
                    List<String> originalText = processCsvFile(zis);
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
                        this.baseMapper.insertBatchSomeColumn(translationDataList);
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

        return null;
    }

    /**
     * 处理csv文件
     *
     * @param zis
     * @return
     */
    private List<String> processCsvFile(ZipInputStream zis) {
        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8)));
        try {
            // 读取 CSV 文件的标题行
            String[] header = reader.readNext();

            // 创建一个空的List，用于存储读取的数据
            List<String> dataList = new ArrayList<>();

            // 读取 CSV 文件中的每一行，将每行数据转换成一个Map，然后将Map添加到List中
            String[] line;
            while ((line = reader.readNext()) != null) {
                Map<String, String> dataMap = new HashMap<>();
                dataList.add(line[0]);
            }
            return dataList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 导出csv文件
     *
     * @return
     */
    @Override
    public byte[] exportCsv(Integer projects) {
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

                    //创建csv文件
                    ByteArrayOutputStream csvByteArrayOutputStream = new ByteArrayOutputStream();
                    try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(csvByteArrayOutputStream))) {
//                        //写入标题行
//                        String[] header = {"原始文本", "翻译文本", "机器翻译", "润色翻译", "最终翻译"};
//                        csvWriter.writeNext(header);

                        //写入数据行
                        for (TranslationData data : translationData) {
                            String[] row = {data.getOriginalText(), data.getTranslationText()};
                            csvWriter.writeNext(row);
                        }
                    }

                    //写入zip文件
                    zipOutputStream.write(csvByteArrayOutputStream.toByteArray());
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
