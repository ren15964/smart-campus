package com.smartcampus.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 成绩录入DTO
 */
@Data
public class GradeInputDTO {

    @NotNull(message = "开课计划ID不能为空")
    private Long scheduleId;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    private BigDecimal usualScore;

    private BigDecimal examScore;
}
