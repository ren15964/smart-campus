package com.smartcampus.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.math.BigDecimal;

/**
 * 批量成绩录入DTO
 */
@Data
public class BatchGradeInputDTO {

    @NotNull(message = "开课计划ID不能为空")
    private Long scheduleId;

    @NotNull(message = "成绩列表不能为空")
    private List<GradeItemDTO> grades;

    @Data
    public static class GradeItemDTO {
        @NotNull(message = "学生ID不能为空")
        private Long studentId;

        private BigDecimal usualScore;

        private BigDecimal examScore;
    }
}
