package com.smartcampus.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 作业批改DTO
 */
@Data
public class HomeworkGradeDTO {

    @NotNull(message = "提交记录ID不能为空")
    private Long submitId;

    @NotNull(message = "分数不能为空")
    private BigDecimal score;

    private String comment;
}
