package com.smartcampus.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 学生个人成绩VO
 */
@Data
public class MyGradeVO {

    private Long id;

    private String courseName;

    private String courseCode;

    private BigDecimal credit;

    private String teacherName;

    private BigDecimal usualScore;

    private BigDecimal examScore;

    private BigDecimal totalScore;

    private BigDecimal gpa;

    private String semester;
}
