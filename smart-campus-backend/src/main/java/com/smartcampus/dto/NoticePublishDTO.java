package com.smartcampus.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 通知发布DTO
 */
@Data
public class NoticePublishDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotNull(message = "优先级不能为空")
    private Integer priority; // 1-普通, 2-重要, 3-紧急

    private String targetRole; // all, student, teacher

    private String attachment;
}
