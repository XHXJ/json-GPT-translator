package com.xhxj.jsongpttranslator.controller.chatgpt;


import com.xhxj.jsongpttranslator.service.chatgpt.ChatGptTranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-03 02:23
 * @Description: Chatgpt翻译控制器
 */
@Tag(name = "chatgpt翻译控制")
@RequestMapping("/chatgpt")
@RestController
public class ChatGptTranslationController {

    @Autowired
    private ChatGptTranslationService chatGptTranslationService;

    @GetMapping("/start")
    @Operation(summary = "开始翻译")
    public void start() {
        chatGptTranslationService.start();
    }

//    @GetMapping("/stop")
//    @Operation(summary = "停止翻译")
//    public void stop() {
//        chatGptTranslationService.stop();
//    }

}
