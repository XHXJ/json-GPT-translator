package com.xhxj.jsongpttranslator.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-08 21:21
 * @Description: 页面控制器
 */
@Controller
public class webController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
