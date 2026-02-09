package com.smartcampus.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业提交列表VO (教师端)
 */
@Data
public class HomeworkSubmissionVO {

    private Long submitId;

    private Long studentId;

    private String studentNo;

    private String studentName;

    private String content;

    private String attachment;

    private LocalDateTime submitTime;

    private Boolean isLate;

    private Integer status; // 0-未提交, 1-已提交, 2-已批改

    private BigDecimal score;
}
