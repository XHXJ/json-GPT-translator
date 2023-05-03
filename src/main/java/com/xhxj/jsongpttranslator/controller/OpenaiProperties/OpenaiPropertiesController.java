package com.xhxj.jsongpttranslator.controller.OpenaiProperties;

import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.OpenaiPropertiesVO;
import com.xhxj.jsongpttranslator.dal.dataobject.OpenaiProperties.OpenaiProperties;
import com.xhxj.jsongpttranslator.service.OpenaiProperties.OpenaiPropertiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-03 17:02
 * @Description:
 */
@Tag(name = "配置控制")
@RequestMapping("/openai-properties")
@RestController
public class OpenaiPropertiesController {

    @Autowired
    private OpenaiPropertiesService openaiPropertiesService;

    /**
     * 上传openai的key
     */
    @PostMapping("/upload-key")
    @Operation(summary = "上传openai的key")
    public String uploadKey(@RequestBody OpenaiPropertiesVO key) {
        OpenaiProperties openaiProperties = new OpenaiProperties();
        openaiProperties.setKey(key.getKey());
        openaiPropertiesService.save(openaiProperties);
        return "key设置成功: " + key.getKey();
    }
}
