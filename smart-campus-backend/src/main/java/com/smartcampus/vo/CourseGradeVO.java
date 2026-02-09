package com.smartcampus.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 课程成绩VO
 */
@Data
public class CourseGradeVO {

    private Long gradeId;

    private String studentNo;

    private String studentName;

    private BigDecimal usualScore;

    private BigDecimal examScore;

    private BigDecimal totalScore;

    private BigDecimal gpa;
}
