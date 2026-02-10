package com.smartcampus.controller;

import com.smartcampus.common.Result;
import com.smartcampus.exception.BusinessException;
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
    public Result<List<CourseResourceVO>> getCourseResources(@RequestParam(required = false) Long scheduleId,
                                                             @RequestParam(required = false) String chapter,
                                                             @RequestParam(required = false) String keyword,
                                                             HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // scheduleId传了：按课程查（学生/教师/管理员都可）
        if (scheduleId != null) {
            List<CourseResourceVO> list = courseResourceService.getCourseResources(scheduleId, chapter, keyword);
            return Result.success(list);
        }

        // scheduleId没传：用于“资源管理”场景（教师看自己上传的；管理员看全量）
        if (token == null || token.isEmpty()) {
            throw new BusinessException("缺少scheduleId参数，且未登录，无法获取资源列表");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getUserRoleFromToken(token);
        if ("teacher".equals(role)) {
            return Result.success(courseResourceService.getMyUploadedResources(userId, chapter, keyword));
        }
        if ("admin".equals(role)) {
            return Result.success(courseResourceService.getAllResources(chapter, keyword));
        }
        // student：没有scheduleId就没法判断看哪门课的资源
        throw new BusinessException("缺少scheduleId参数，无法获取课程资源列表");
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
