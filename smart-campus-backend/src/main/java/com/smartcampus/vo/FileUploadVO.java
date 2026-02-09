package com.smartcampus.vo;

import lombok.Data;

/**
 * 文件上传视图对象
 */
@Data
public class FileUploadVO {
    private String fileName;
    private String filePath;
    private Long fileSize;
}
