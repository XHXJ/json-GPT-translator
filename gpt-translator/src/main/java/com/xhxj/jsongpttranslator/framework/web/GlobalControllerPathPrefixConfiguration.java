package com.xhxj.jsongpttranslator.framework.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-17 19:10
 * @Description: 接口配置统一前缀
 */
@Configuration
public class GlobalControllerPathPrefixConfiguration implements WebMvcConfigurer {
    public static final String PATH_PREFIX = "/api";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(PATH_PREFIX, HandlerTypePredicate.forAnnotation(RestController.class)
                .and(HandlerTypePredicate.forBasePackage("com.xhxj.jsongpttranslator.controller")));
    }
}
