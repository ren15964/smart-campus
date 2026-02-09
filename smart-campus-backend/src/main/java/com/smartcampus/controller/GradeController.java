package com.smartcampus.controller;

import com.smartcampus.common.Result;
import com.smartcampus.dto.BatchGradeInputDTO;
import com.smartcampus.dto.GradeInputDTO;
import com.smartcampus.service.GradeService;
import com.smartcampus.utils.JwtUtil;
import com.smartcampus.vo.CourseGradeVO;
import com.smartcampus.vo.GradeStatisticsVO;
import com.smartcampus.vo.MyGradeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 成绩控制器
 */
@RestController
@RequestMapping("/grade")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 成绩查询（学生）
     */
    @GetMapping("/my-grades")
    public Result<List<MyGradeVO>> getMyGrades(@RequestParam(required = false) String semester,
                                               HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long studentId = jwtUtil.getUserIdFromToken(token);
        List<MyGradeVO> list = gradeService.getMyGrades(studentId, semester);
        return Result.success(list);
    }

    /**
     * 成绩统计（学生）
     */
    @GetMapping("/statistics")
    public Result<GradeStatisticsVO> getGradeStatistics(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long studentId = jwtUtil.getUserIdFromToken(token);
        GradeStatisticsVO statisticsVO = gradeService.getGradeStatistics(studentId);
        return Result.success(statisticsVO);
    }

    /**
     * 课程成绩列表（教师）
     */
    @GetMapping("/course/{scheduleId}")
    public Result<List<CourseGradeVO>> getCourseGrades(@PathVariable Long scheduleId,
                                                      HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long teacherId = jwtUtil.getUserIdFromToken(token);
        List<CourseGradeVO> list = gradeService.getCourseGrades(scheduleId, teacherId);
        return Result.success(list);
    }

    /**
     * 录入成绩（教师）
     */
    @PostMapping("/input")
    public Result<String> inputGrade(@Validated @RequestBody GradeInputDTO gradeInputDTO,
                                     HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long teacherId = jwtUtil.getUserIdFromToken(token);
        gradeService.inputGrade(teacherId, gradeInputDTO);
        return Result.success("成绩录入成功");
    }

    /**
     * 批量录入成绩（教师）
     */
    @PostMapping("/batch-input")
    public Result<String> batchInputGrade(@Validated @RequestBody BatchGradeInputDTO batchGradeInputDTO,
                                          HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long teacherId = jwtUtil.getUserIdFromToken(token);
        gradeService.batchInputGrade(teacherId, batchGradeInputDTO);
        return Result.success("批量录入成功");
    }
}
