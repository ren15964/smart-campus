package com.smartcampus.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业详情VO
 */
@Data
public class HomeworkDetailVO {

    private Long id;

    private String title;

    private String content;

    private String attachment;

    private LocalDateTime deadline;

    private BigDecimal totalScore;

    private String courseName;

    private String teacherName;

    private HomeworkSubmitInfoVO submitInfo; // 学生提交信息

    @Data
    public static class HomeworkSubmitInfoVO {
        private Long submitId;
        private String content;
        private String attachment;
        private LocalDateTime submitTime;
        private Integer status; // 0-未提交, 1-已提交, 2-已批改
        private BigDecimal score;
        private String comment;
        private Boolean isLate;
    }
}
