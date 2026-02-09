package com.smartcampus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新闻实体类
 */
@Data
@TableName("news")
public class News {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String coverImage;

    private String content;

    private String category;

    private Long publisherId;

    private String publisherName;

    private Integer viewCount;

    private LocalDateTime publishTime;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
