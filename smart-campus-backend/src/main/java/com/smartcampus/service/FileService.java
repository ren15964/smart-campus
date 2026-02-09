package com.smartcampus.service;

import com.smartcampus.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService {
    FileUploadVO uploadFile(MultipartFile file, String type);
}
