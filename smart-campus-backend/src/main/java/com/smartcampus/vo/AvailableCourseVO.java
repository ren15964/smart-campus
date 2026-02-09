package com.smartcampus.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 可选课程VO
 */
@Data
public class AvailableCourseVO {

    private Long scheduleId;

    private String courseCode;

    private String courseName;

    private String teacherName;

    private BigDecimal credit;

    private Integer weekDay;

    private String startTime;

    private String endTime;

    private String classroom;

    private Integer capacity;

    private Integer selectedCount;

    private Boolean isSelected; // 当前学生是否已选择
}
