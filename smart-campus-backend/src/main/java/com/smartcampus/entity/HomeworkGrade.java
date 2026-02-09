package com.smartcampus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业成绩实体类
 */
@Data
@TableName("homework_grade")
public class HomeworkGrade {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long submitId;

    private BigDecimal score;

    private String comment;

    private Long graderId;

    private LocalDateTime gradeTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
