package com.smartcampus.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新闻VO
 */
@Data
public class NewsVO {

    private Long id;

    private String title;

    private String coverImage;

    private String content;

    private String category;

    private String publisherName;

    private Integer viewCount;

    private LocalDateTime publishTime;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
