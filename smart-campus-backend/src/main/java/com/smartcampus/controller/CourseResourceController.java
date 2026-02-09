package com.smartcampus.controller;

import com.smartcampus.common.Result;
import com.smartcampus.dto.CourseResourceUploadDTO;
import com.smartcampus.service.CourseResourceService;
import com.smartcampus.utils.JwtUtil;
import com.smartcampus.vo.CourseResourceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 课程资源控制器
 */
@RestController
@RequestMapping("/resource")
public class CourseResourceController {

    @Autowired
    private CourseResourceService courseResourceService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 资源列表
     */
    @GetMapping("/list")
    public Result<List<CourseResourceVO>> getCourseResources(@RequestParam Long scheduleId,
                                                             @RequestParam(required = false) String chapter) {
        List<CourseResourceVO> list = courseResourceService.getCourseResources(scheduleId, chapter);
        return Result.success(list);
    }

    /**
     * 上传资源（教师）
     */
    @PostMapping("/upload")
    public Result<CourseResourceVO> uploadResource(@Validated @ModelAttribute CourseResourceUploadDTO uploadDTO,
                                                   @RequestParam("file") MultipartFile file,
                                                   HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long uploaderId = jwtUtil.getUserIdFromToken(token);
        CourseResourceVO courseResourceVO = courseResourceService.uploadResource(uploaderId, file, uploadDTO);
        return Result.success("上传成功", courseResourceVO);
    }

    /**
     * 下载资源
     */
    @GetMapping("/download/{id}")
    public void downloadResource(@PathVariable Long id, HttpServletResponse response) {
        courseResourceService.downloadResource(id, response);
    }

    /**
     * 删除资源（教师）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteResource(@PathVariable Long id) {
        courseResourceService.deleteResource(id);
        return Result.success("删除成功");
    }
}
