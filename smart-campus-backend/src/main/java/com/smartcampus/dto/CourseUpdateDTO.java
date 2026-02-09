package com.smartcampus.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 课程更新DTO
 */
@Data
public class CourseUpdateDTO {

    @NotNull(message = "课程ID不能为空")
    private Long id;

    @NotBlank(message = "课程名称不能为空")
    private String courseName;

    @NotNull(message = "学分不能为空")
    private BigDecimal credit;

    @NotNull(message = "学时不能为空")
    private Integer hours;

    private String courseType;

    private String description;
}
