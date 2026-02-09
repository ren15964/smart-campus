package com.smartcampus.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 成绩统计VO
 */
@Data
public class GradeStatisticsVO {

    private BigDecimal totalCredits; // 总学分

    private BigDecimal averageGpa; // 平均绩点

    private Integer passedCourses; // 通过课程数

    private Integer failedCourses; // 未通过课程数
}
