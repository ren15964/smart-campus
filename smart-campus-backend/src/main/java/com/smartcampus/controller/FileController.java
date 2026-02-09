package com.smartcampus.controller;

import com.smartcampus.common.Result;
import com.smartcampus.service.FileService;
import com.smartcampus.vo.FileUploadVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 通用文件上传
     */
    @PostMapping("/upload")
    public Result<FileUploadVO> uploadFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "type", required = false) String type,
                                           HttpServletRequest request) {
        FileUploadVO fileUploadVO = fileService.uploadFile(file, type);
        return Result.success("上传成功", fileUploadVO);
    }
}
