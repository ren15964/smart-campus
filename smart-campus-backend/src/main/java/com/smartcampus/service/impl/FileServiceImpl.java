package com.smartcampus.service.impl;

import com.smartcampus.service.FileService;
import com.smartcampus.utils.FileUtil;
import com.smartcampus.common.ResultCode;
import com.smartcampus.exception.BusinessException;
import java.io.IOException;
import com.smartcampus.vo.FileUploadVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务实现类
 */
@Service
public class FileServiceImpl implements FileService {

    @Override
    public FileUploadVO uploadFile(MultipartFile file, String type) {
        try {
            String filePath = FileUtil.uploadFile(file, type);
            // 这里可以根据实际需求设置文件名、文件大小等信息
            FileUploadVO fileUploadVO = new FileUploadVO();
            fileUploadVO.setFileName(file.getOriginalFilename());
            fileUploadVO.setFilePath(filePath);
            fileUploadVO.setFileSize(file.getSize());
            return fileUploadVO;
        } catch (IOException e) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR);
        }
    }
}
