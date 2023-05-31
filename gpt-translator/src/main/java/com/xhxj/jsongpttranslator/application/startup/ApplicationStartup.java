package com.xhxj.jsongpttranslator.application.startup;


import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigVo;
import com.xhxj.jsongpttranslator.service.openaiproperties.OpenaiPropertiesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-20 03:06
 * @Description: 启动时打开默认浏览器
 */
@Component
@Slf4j
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private OpenaiPropertiesService openaiPropertiesService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String url = "http://localhost:8080";
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", url);
            Process p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果配置文件存在就读取配置文件
        if (FileUtil.exist("config.json")) {
            //读取配置文件
            String config = FileUtil.readUtf8String("config.json");
            //转换json对象
            ChatGptConfigVo jsonObject = JSONUtil.toBean(config, ChatGptConfigVo.class);
            log.info("读取配置文件成功:{}", jsonObject);
            openaiPropertiesService.saveConfig(jsonObject);
        }

    }
}
