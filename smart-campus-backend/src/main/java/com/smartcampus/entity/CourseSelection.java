package com.smartcampus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 选课实体类
 */
@Data
@TableName("course_selection")
public class CourseSelection {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long scheduleId;

    private LocalDateTime selectionTime;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
