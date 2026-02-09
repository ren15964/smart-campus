package com.smartcampus.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 周课表视图VO
 */
@Data
public class WeekViewCourseVO {

    private Integer week; // 周次

    private Map<String, List<CourseDetailVO>> courses; // 星期 -> 课程列表

    @Data
    public static class CourseDetailVO {
        private Long scheduleId;
        private String courseName;
        private String teacherName;
        private String startTime;
        private String endTime;
        private String classroom;
    }
}
