package com.smartcampus.controller;

import com.smartcampus.common.PageResult;
import com.smartcampus.common.Result;
import com.smartcampus.dto.CourseSelectionDTO;
import com.smartcampus.service.CourseSelectionService;
import com.smartcampus.utils.JwtUtil;
import com.smartcampus.vo.AvailableCourseVO;
import com.smartcampus.vo.CourseSelectionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 选课控制器
 */
@RestController
@RequestMapping("/course-selection")
public class CourseSelectionController {

    @Autowired
    private CourseSelectionService courseSelectionService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 可选课程列表（学生）
     */
    @GetMapping("/available")
    public Result<PageResult<AvailableCourseVO>> getAvailableCourses(@RequestParam(defaultValue = "1") Integer current,
                                                                     @RequestParam(defaultValue = "10") Integer size,
                                                                     @RequestParam(required = false) String keyword,
                                                                     @RequestParam(required = false) String semester,
                                                                     HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long studentId = jwtUtil.getUserIdFromToken(token); // 假设studentId就是userId
        PageResult<AvailableCourseVO> pageResult = courseSelectionService.getAvailableCourses(current, size, keyword, semester, studentId);
        return Result.success(pageResult);
    }

    /**
     * 我的选课（学生）
     */
    @GetMapping("/my-courses")
    public Result<List<CourseSelectionVO>> getMySelectedCourses(@RequestParam(required = false) String semester,
                                                                HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long studentId = jwtUtil.getUserIdFromToken(token);
        List<CourseSelectionVO> list = courseSelectionService.getMySelectedCourses(studentId, semester);
        return Result.success(list);
    }

    /**
     * 选课（学生）
     */
    @PostMapping("/select")
    public Result<String> selectCourse(@Validated @RequestBody CourseSelectionDTO courseSelectionDTO,
                                       HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long studentId = jwtUtil.getUserIdFromToken(token);
        courseSelectionService.selectCourse(studentId, courseSelectionDTO);
        return Result.success("选课成功");
    }

    /**
     * 退课（学生）
     */
    @DeleteMapping("/{id}")
    public Result<String> withdrawCourse(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long studentId = jwtUtil.getUserIdFromToken(token);
        courseSelectionService.withdrawCourse(id, studentId);
        return Result.success("退课成功");
    }
}
