package com.smartcampus.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程VO
 */
@Data
public class CourseVO {

    private Long id;

    private String courseCode;

    private String courseName;

    private BigDecimal credit;

    private Integer hours;

    private String courseType;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
