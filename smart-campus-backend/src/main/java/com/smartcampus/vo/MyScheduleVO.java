package com.smartcampus.vo;

import lombok.Data;

/**
 * 我的课表VO
 */
@Data
public class MyScheduleVO {

    private Long scheduleId;

    private String courseName;

    private String teacherName;

    private Integer weekDay;

    private Integer startWeek;

    private Integer endWeek;

    private String startTime;

    private String endTime;

    private String classroom;
}
