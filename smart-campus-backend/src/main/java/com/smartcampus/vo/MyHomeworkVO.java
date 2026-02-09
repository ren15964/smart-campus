package com.smartcampus.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生作业列表VO
 */
@Data
public class MyHomeworkVO {

    private Long homeworkId;

    private String title;

    private String courseName;

    private String teacherName;

    private LocalDateTime deadline;

    private BigDecimal totalScore;

    private Integer submitStatus; // 0-未提交, 1-已提交, 2-已批改

    private BigDecimal score;

    private Boolean isLate;
}
