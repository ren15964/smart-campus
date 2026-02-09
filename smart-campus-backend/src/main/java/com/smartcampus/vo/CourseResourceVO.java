package com.smartcampus.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程资源VO
 */
@Data
public class CourseResourceVO {

    private Long id;

    private Long scheduleId;

    private String courseName;

    private String resourceName;

    private String resourceType;

    private String filePath;

    private Long fileSize;

    private String chapter;

    private String description;

    private Integer downloadCount;

    private String uploaderName;

    private LocalDateTime createTime;
}
