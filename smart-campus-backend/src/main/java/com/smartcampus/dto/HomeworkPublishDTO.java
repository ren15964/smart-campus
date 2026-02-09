package com.smartcampus.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业发布DTO
 */
@Data
public class HomeworkPublishDTO {

    @NotNull(message = "开课计划ID不能为空")
    private Long scheduleId;

    @NotBlank(message = "作业标题不能为空")
    private String title;

    @NotBlank(message = "作业内容不能为空")
    private String content;

    private String attachment;

    @NotNull(message = "截止时间不能为空")
    private LocalDateTime deadline;

    private BigDecimal totalScore;
}
