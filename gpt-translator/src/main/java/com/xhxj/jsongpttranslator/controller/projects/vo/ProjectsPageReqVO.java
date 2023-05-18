package com.xhxj.jsongpttranslator.controller.projects.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author:luoshuzhong
 * @create: 2023-05-17 09:51
 * @Description: 项目分页查询
 */
@Data
@Schema(name = "ProjectsPageReqVO", description = "项目分页查询请求VO")
public class ProjectsPageReqVO {
    @Schema(description = "页码", example = "1", type = "integer")
    private Integer pageNum = 1;
    @Schema(description = "每页条数", example = "10", type = "integer")
    private Integer pageSize = 10;
    @Schema(description = "项目id")
    private Long projectId;
    @Schema(description = "项目名称")
    private String projectName;
}
