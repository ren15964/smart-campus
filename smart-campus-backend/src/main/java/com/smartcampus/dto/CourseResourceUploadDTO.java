package com.smartcampus.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 课程资源上传DTO
 */
@Data
public class CourseResourceUploadDTO {

    @NotNull(message = "开课计划ID不能为空")
    private Long scheduleId;

    @NotBlank(message = "资源名称不能为空")
    private String resourceName;

    @NotBlank(message = "资源类型不能为空")
    private String resourceType; // doc-文档, video-视频, ppt-课件, other-其他

    private String chapter;

    private String description;
}
