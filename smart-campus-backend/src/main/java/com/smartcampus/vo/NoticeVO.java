package com.smartcampus.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知VO
 */
@Data
public class NoticeVO {

    private Long id;

    private String title;

    private String content;

    private String publisherName;

    private Integer priority;

    private String targetRole;

    private String attachment;

    private LocalDateTime publishTime;

    private Integer status;

    private Integer readCount;

    private Boolean isRead; // 当前用户是否已读
}
