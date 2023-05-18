package com.xhxj.jsongpttranslator.controller.OpenaiProperties;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.OpenaiPropertiesVO;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestVo;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigVo;
import com.xhxj.jsongpttranslator.convert.ChatgptConfigConvert;
import com.xhxj.jsongpttranslator.dal.dataobject.OpenaiProperties;
import com.xhxj.jsongpttranslator.framework.chatgptconfig.ChatgptConfig;
import com.xhxj.jsongpttranslator.framework.web.pojo.CommonResult;
import com.xhxj.jsongpttranslator.service.chatgpt.ChatGptTranslationService;
import com.xhxj.jsongpttranslator.service.openaiproperties.OpenaiPropertiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

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

    @Autowired
    private ChatgptConfig chatgptConfig;

    @Autowired
    private ChatGptTranslationService chatGptTranslationService;

    /**
     * 上传openai的key
     */
    @PostMapping("/key")
    @Operation(summary = "上传openai的key")
    public CommonResult<Boolean> createKey(@RequestBody OpenaiPropertiesVO key) {
        OpenaiProperties openaiProperties = new OpenaiProperties();
        openaiProperties.setOpenaiKey(key.getKey());
        return CommonResult.success(openaiPropertiesService.save(openaiProperties));
    }

    /**
     * 分页查询key
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询key")
    public CommonResult<IPage<OpenaiProperties>> page(@ParameterObject @Valid OpenaiPropertiesVO page) {
        return CommonResult.success(openaiPropertiesService.page(new Page<>(page.getPageNum(), page.getPageSize())));
    }

    /**
     * 删除key
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除key")
    public CommonResult<Boolean> delete(@RequestParam("ids") Long[] ids) {
        return CommonResult.success(openaiPropertiesService.removeByIds(Arrays.asList(ids)));
    }



    @GetMapping("/chat-gpt-config")
    @Operation(summary = "获取chatGpt的配置")
    public CommonResult<ChatGptConfigVo> getChatGptConfig() {

        return CommonResult.success(ChatgptConfigConvert.INSTANCE.convert(chatgptConfig));
    }

    @PostMapping("/chat-gpt-config-test")
    @Operation(summary = "测试chatGpt的配置")
    public CommonResult<ChatGptConfigTestVo> testChatGptConfig(@RequestBody ChatGptConfigVo chatgptConfigVo) {

        return CommonResult.success(chatGptTranslationService.testChatGptConfig(chatgptConfigVo));
    }

    @PutMapping("/prompt-single")
    @Operation(summary = "修改gpt的prompt设置")
    public CommonResult<String> updateSingle(@RequestBody String newPrompt) {
        chatgptConfig.setPromptSingleTranslations(newPrompt);
        return CommonResult.success("修改成功");
    }

    @PutMapping("/prompt-multiple")
    @Operation(summary = "修改gpt的多条prompt设置")
    public CommonResult<String> updatePromptMultiple(@RequestBody String newPrompt) {
        chatgptConfig.setPromptMultipleTranslations(newPrompt);
        return CommonResult.success("修改成功");
    }
}
