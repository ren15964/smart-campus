package com.smartcampus.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 选课DTO
 */
@Data
public class CourseSelectionDTO {

    @NotNull(message = "开课计划ID不能为空")
    private Long scheduleId;
}
