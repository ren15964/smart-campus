package com.smartcampus.controller;

import com.smartcampus.common.Result;
import com.smartcampus.dto.HomeworkGradeDTO;
import com.smartcampus.dto.HomeworkPublishDTO;
import com.smartcampus.dto.HomeworkSubmitDTO;
import com.smartcampus.service.HomeworkGradeService;
import com.smartcampus.service.HomeworkService;
import com.smartcampus.service.HomeworkSubmitService;
import com.smartcampus.utils.JwtUtil;
import com.smartcampus.vo.HomeworkDetailVO;
import com.smartcampus.vo.HomeworkSubmissionVO;
import com.smartcampus.vo.MyHomeworkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 作业控制器
 */
@RestController
@RequestMapping("/homework")
public class HomeworkController {

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private HomeworkSubmitService homeworkSubmitService;

    @Autowired
    private HomeworkGradeService homeworkGradeService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 作业列表（学生）
     */
    @GetMapping("/my-homework")
    public Result<List<MyHomeworkVO>> getMyHomeworks(@RequestParam(required = false) Long scheduleId,
                                                      @RequestParam(required = false) Integer status,
                                                      HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long studentId = jwtUtil.getUserIdFromToken(token);
        List<MyHomeworkVO> list = homeworkService.getMyHomeworks(studentId, scheduleId, status);
        return Result.success(list);
    }

    /**
     * 作业详情
     */
    @GetMapping("/{id}")
    public Result<HomeworkDetailVO> getHomeworkDetail(@PathVariable Long id,
                                                      HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long studentId = jwtUtil.getUserIdFromToken(token);
        HomeworkDetailVO homeworkDetailVO = homeworkService.getHomeworkDetail(id, studentId);
        return Result.success(homeworkDetailVO);
    }

    /**
     * 提交作业（学生）
     */
    @PostMapping("/submit")
    public Result<String> submitHomework(@Validated @ModelAttribute HomeworkSubmitDTO submitDTO,
                                         @RequestParam(value = "file", required = false) MultipartFile file,
                                         HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long studentId = jwtUtil.getUserIdFromToken(token);
        homeworkSubmitService.submitHomework(studentId, file, submitDTO);
        return Result.success("提交成功");
    }

    /**
     * 发布作业（教师）
     */
    @PostMapping("/publish")
    public Result<String> publishHomework(@Validated @RequestBody HomeworkPublishDTO homeworkPublishDTO,
                                        HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long teacherId = jwtUtil.getUserIdFromToken(token);
        homeworkService.publishHomework(teacherId, homeworkPublishDTO);
        return Result.success("发布成功");
    }

    /**
     * 删除作业（教师）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteHomework(@PathVariable Long id) {
        homeworkService.deleteHomework(id);
        return Result.success("删除成功");
    }

    /**
     * 作业提交列表（教师）
     */
    @GetMapping("/{id}/submissions")
    public Result<List<HomeworkSubmissionVO>> getHomeworkSubmissions(@PathVariable Long id) {
        List<HomeworkSubmissionVO> list = homeworkSubmitService.getHomeworkSubmissions(id);
        return Result.success(list);
    }

    /**
     * 批改作业（教师）
     */
    @PostMapping("/grade")
    public Result<String> gradeHomework(@Validated @RequestBody HomeworkGradeDTO homeworkGradeDTO,
                                        HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long graderId = jwtUtil.getUserIdFromToken(token);
        homeworkGradeService.gradeHomework(graderId, homeworkGradeDTO);
        return Result.success("批改成功");
    }
}
