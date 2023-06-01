package com.xhxj.jsongpttranslator.controller.translationdata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author:zdthm2010@gmail.com
 * @create: 2023-05-03 00:45
 * @Description:
 */
@Data
@Schema(name = "TranslationDataPageReqVO", description = "翻译数据分页查询请求VO")
public class TranslationDataPageReqVO {
    @Schema(description = "页码", example = "1", type = "integer")
    private Integer pageNum = 1;
    @Schema(description = "每页条数", example = "10", type = "integer")
    private Integer pageSize = 10;
    @Schema(description = "id")
    private Long id ;
    @Schema(description = "原文")
    private String originalText;
    @Schema(description = "翻译文本")
    private String translationText;
    @Schema(description = "是否翻译", type = "boolean")
    private Boolean isTranslation;
    @Schema(description = "项目id")
    private Integer projectId;
    @Schema(description = "文件id")
    private Integer fileId;
    @Schema(description = "译文正则表达式")
    private String translationRegular;
    @Schema(description = "原文正则表达式")
    private String originalRegular;
}
