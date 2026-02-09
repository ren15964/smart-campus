package com.smartcampus.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 我的选课VO
 */
@Data
public class CourseSelectionVO {

    private Long selectionId;

    private Long scheduleId;

    private String courseCode;

    private String courseName;

    private String teacherName;

    private BigDecimal credit;

    private Integer weekDay;

    private String startTime;

    private String endTime;

    private String classroom;

    private LocalDateTime selectionTime;
}
