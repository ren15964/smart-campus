package com.smartcampus.controller;

import com.smartcampus.common.PageResult;
import com.smartcampus.common.Result;
import com.smartcampus.dto.CourseScheduleAddDTO;
import com.smartcampus.dto.CourseScheduleUpdateDTO;
import com.smartcampus.service.CourseScheduleService;
import com.smartcampus.utils.JwtUtil;
import com.smartcampus.vo.CourseScheduleVO;
import com.smartcampus.vo.MyScheduleVO;
import com.smartcampus.vo.WeekViewCourseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 开课计划控制器
 */
@RestController
@RequestMapping("/course-schedule")
public class CourseScheduleController {

    @Autowired
    private CourseScheduleService courseScheduleService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 开课计划列表（管理员）
     */
    @GetMapping("/list")
    public Result<PageResult<CourseScheduleVO>> pageCourseSchedule(@RequestParam(defaultValue = "1") Integer current,
                                                                   @RequestParam(defaultValue = "10") Integer size,
                                                                   @RequestParam(required = false) String semester,
                                                                   @RequestParam(required = false) String courseName,
                                                                   @RequestParam(required = false) String teacherName) {
        PageResult<CourseScheduleVO> pageResult = courseScheduleService.pageCourseSchedule(current, size, semester, courseName, teacherName);
        return Result.success(pageResult);
    }

    /**
     * 添加开课计划
     */
    @PostMapping("/add")
    public Result<String> addCourseSchedule(@Validated @RequestBody CourseScheduleAddDTO courseScheduleAddDTO) {
        courseScheduleService.addCourseSchedule(courseScheduleAddDTO);
        return Result.success("添加成功");
    }

    /**
     * 更新开课计划
     */
    @PutMapping("/{id}")
    public Result<String> updateCourseSchedule(@PathVariable Long id, @Validated @RequestBody CourseScheduleUpdateDTO courseScheduleUpdateDTO) {
        courseScheduleUpdateDTO.setId(id);
        courseScheduleService.updateCourseSchedule(courseScheduleUpdateDTO);
        return Result.success("更新成功");
    }

    /**
     * 删除开课计划
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCourseSchedule(@PathVariable Long id) {
        courseScheduleService.deleteCourseSchedule(id);
        return Result.success("删除成功");
    }

    /**
     * 更新开课状态
     */
    @PutMapping("/{id}/status/{status}")
    public Result<String> updateCourseScheduleStatus(@PathVariable Long id, @PathVariable Integer status) {
        courseScheduleService.updateCourseScheduleStatus(id, status);
        return Result.success("状态更新成功");
    }

    /**
     * 我的课表（学生/教师）
     */
    @GetMapping("/my-schedule")
    public Result<List<MyScheduleVO>> getMySchedule(@RequestParam(required = false) String semester,
                                                      HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getUserRoleFromToken(token);
        List<MyScheduleVO> list = courseScheduleService.getMySchedule(userId, role, semester);
        return Result.success(list);
    }

    /**
     * 周课表视图
     */
    @GetMapping("/week-view")
    public Result<WeekViewCourseVO> getWeekViewSchedule(@RequestParam(required = false) String semester,
                                                        @RequestParam(required = false) Integer week,
                                                        HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getUserRoleFromToken(token);
        WeekViewCourseVO weekViewCourseVO = courseScheduleService.getWeekViewSchedule(userId, role, semester, week);
        return Result.success(weekViewCourseVO);
    }
}

