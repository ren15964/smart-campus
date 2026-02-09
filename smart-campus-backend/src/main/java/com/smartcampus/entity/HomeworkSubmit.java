package com.smartcampus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作业提交实体类
 */
@Data
@TableName("homework_submit")
public class HomeworkSubmit {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long homeworkId;

    private Long studentId;

    private String content;

    private String attachment;

    private LocalDateTime submitTime;

    private Integer status;

    private Integer isLate;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
