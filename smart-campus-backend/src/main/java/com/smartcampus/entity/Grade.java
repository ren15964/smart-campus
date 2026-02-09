package com.smartcampus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩实体类
 */
@Data
@TableName("grade")
public class Grade {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long scheduleId;

    private BigDecimal usualScore;

    private BigDecimal examScore;

    private BigDecimal totalScore;

    private BigDecimal gpa;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
