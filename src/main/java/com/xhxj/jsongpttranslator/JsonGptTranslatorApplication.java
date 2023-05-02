package com.xhxj.jsongpttranslator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 78222
 */
@SpringBootApplication
@MapperScan("com.xhxj.jsongpttranslator.dal.hsqldb")
public class JsonGptTranslatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(JsonGptTranslatorApplication.class, args);
    }

}
