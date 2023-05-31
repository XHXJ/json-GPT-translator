package com.xhxj.jsongpttranslator.controller.OpenaiProperties;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestRespVo;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigTestVo;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.ChatGptConfigVo;
import com.xhxj.jsongpttranslator.controller.OpenaiProperties.vo.OpenaiPropertiesVO;
import com.xhxj.jsongpttranslator.convert.ChatgptConfigConvert;
import com.xhxj.jsongpttranslator.dal.dataobject.OpenaiProperties;
import com.xhxj.jsongpttranslator.framework.chatgptconfig.ChatgptConfig;
import com.xhxj.jsongpttranslator.framework.web.pojo.CommonResult;
import com.xhxj.jsongpttranslator.service.chatgpt.ChatGptTranslationService;
import com.xhxj.jsongpttranslator.service.openaiproperties.OpenaiPropertiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

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
    public CommonResult<ChatGptConfigTestRespVo> testChatGptConfig(@Valid @RequestBody ChatGptConfigTestVo chatgptConfigVo) {

        return CommonResult.success(chatGptTranslationService.testChatGptConfig(chatgptConfigVo));
    }

    @PutMapping("/chat-gpt-config")
    @Operation(summary = "修改gpt的设置")
    public CommonResult<String> updateChatGptConfig(@Valid @RequestBody ChatGptConfigVo newConfig) {
        openaiPropertiesService.saveConfig(newConfig);
        //保存gpt的配置文件
        FileUtil.writeUtf8String(JSONUtil.toJsonStr(newConfig), "config.json");
        return CommonResult.success("修改成功");
    }


    @GetMapping("/chat-gpt-export")
    @Operation(summary = "导出chatGpt的配置")
    public void exportChatGptConfig(HttpServletResponse response) {

        try {
            // 获取翻译后的 JSON 字符串
            String jsonStr = JSONUtil.toJsonStr(ChatgptConfigConvert.INSTANCE.convert(chatgptConfig));

            // 设置响应头以指示浏览器下载文件
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=Config.json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            // 将文件内容写入响应
            response.getWriter().write(jsonStr);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            // 处理异常，例如记录错误或返回错误响应
            e.printStackTrace();
        }

    }

    @PostMapping(path = "/chat-gpt-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "导入chatGpt的配置")
    public CommonResult<String> importChatGptConfig(@RequestParam("file")
                                                    @Schema(description = "上传的文件", type = "string", format = "binary")
                                                    @RequestPart(value = "file") final MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        //将文件流转换为字符串
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String fileAsString = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        //将字符串转换为对象
        ChatGptConfigVo chatGptConfigVo = JSONUtil.toBean(fileAsString, ChatGptConfigVo.class);

        //设置配置
        openaiPropertiesService.saveConfig(chatGptConfigVo);

        return CommonResult.success("导入成功");
    }



}
