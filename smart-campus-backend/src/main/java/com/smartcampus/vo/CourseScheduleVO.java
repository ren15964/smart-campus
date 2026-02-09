package com.smartcampus.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 开课计划VO
 */
@Data
public class CourseScheduleVO {

    private Long id;

    private Long courseId;

    private String courseName;

    private String courseCode;

    private Long teacherId;

    private String teacherName;

    private String semester;

    private Integer weekDay;

    private Integer startWeek;

    private Integer endWeek;

    private String startTime;

    private String endTime;

    private String classroom;

    private Integer capacity;

    private Integer selectedCount;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
