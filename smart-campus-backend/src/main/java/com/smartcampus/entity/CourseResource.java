package com.smartcampus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程资源实体类
 */
@Data
@TableName("course_resource")
public class CourseResource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long scheduleId;

    private String resourceName;

    private String resourceType;

    private String filePath;

    private Long fileSize;

    private String chapter;

    private String description;

    private Integer downloadCount;

    private Long uploaderId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
