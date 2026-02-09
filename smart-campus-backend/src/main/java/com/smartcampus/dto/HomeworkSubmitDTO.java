package com.smartcampus.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 作业提交DTO
 */
@Data
public class HomeworkSubmitDTO {

    @NotNull(message = "作业ID不能为空")
    private Long homeworkId;

    private String content;

    // 文件通过MultipartFile传递，这里不需要定义
}
