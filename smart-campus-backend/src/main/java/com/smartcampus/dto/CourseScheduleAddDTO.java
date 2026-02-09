package com.smartcampus.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 开课计划添加DTO
 */
@Data
public class CourseScheduleAddDTO {

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    @NotBlank(message = "学期不能为空")
    private String semester;

    @NotNull(message = "星期几不能为空")
    private Integer weekDay;

    @NotNull(message = "开始周次不能为空")
    private Integer startWeek;

    @NotNull(message = "结束周次不能为空")
    private Integer endWeek;

    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    private String classroom;

    private Integer capacity;
}
