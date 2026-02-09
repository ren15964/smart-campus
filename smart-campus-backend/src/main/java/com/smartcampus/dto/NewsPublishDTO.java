package com.smartcampus.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 新闻发布DTO
 */
@Data
public class NewsPublishDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String coverImage;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String category;
}
