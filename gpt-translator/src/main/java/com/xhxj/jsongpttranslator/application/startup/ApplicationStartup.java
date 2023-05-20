package com.xhxj.jsongpttranslator.application.startup;


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
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String url = "http://localhost:8080";
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", url);
            Process p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
