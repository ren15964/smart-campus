package com.smartcampus.controller;

import com.smartcampus.common.PageResult;
import com.smartcampus.common.Result;
import com.smartcampus.dto.CourseAddDTO;
import com.smartcampus.dto.CourseUpdateDTO;
import com.smartcampus.service.CourseService;
import com.smartcampus.vo.CourseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 课程控制器
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 课程列表
     */
    @GetMapping("/list")
    public Result<PageResult<CourseVO>> pageCourse(@RequestParam(defaultValue = "1") Integer current,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) String courseType) {
        PageResult<CourseVO> pageResult = courseService.pageCourse(current, size, keyword, courseType);
        return Result.success(pageResult);
    }

    /**
     * 课程详情
     */
    @GetMapping("/{id}")
    public Result<CourseVO> getCourseDetail(@PathVariable Long id) {
        CourseVO courseVO = courseService.getCourseDetail(id);
        return Result.success(courseVO);
    }

    /**
     * 添加课程（管理员）
     */
    @PostMapping("/add")
    public Result<String> addCourse(@Validated @RequestBody CourseAddDTO courseAddDTO) {
        courseService.addCourse(courseAddDTO);
        return Result.success("添加成功");
    }

    /**
     * 更新课程（管理员）
     */
    @PutMapping("/{id}")
    public Result<String> updateCourse(@PathVariable Long id, @Validated @RequestBody CourseUpdateDTO courseUpdateDTO) {
        courseUpdateDTO.setId(id);
        courseService.updateCourse(courseUpdateDTO);
        return Result.success("更新成功");
    }

    /**
     * 删除课程（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.success("删除成功");
    }
}
