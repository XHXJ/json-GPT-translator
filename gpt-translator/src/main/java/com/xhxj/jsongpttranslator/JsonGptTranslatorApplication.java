package com.xhxj.jsongpttranslator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author 78222
 */
@SpringBootApplication
@MapperScan("com.xhxj.jsongpttranslator.dal.hsqldb")
@EnableAsync
@EnableScheduling
public class JsonGptTranslatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(JsonGptTranslatorApplication.class, args);
    }

}
