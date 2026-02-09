package com.smartcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartcampus.common.PageResult;
import com.smartcampus.dto.CourseAddDTO;
import com.smartcampus.dto.CourseUpdateDTO;
import com.smartcampus.entity.Course;
import com.smartcampus.vo.CourseVO;

/**
 * 课程服务接口
 */
public interface CourseService extends IService<Course> {

    /**
     * 分页查询课程列表
     */
    PageResult<CourseVO> pageCourse(Integer current, Integer size, String keyword, String courseType);

    /**
     * 获取课程详情
     */
    CourseVO getCourseDetail(Long id);

    /**
     * 添加课程
     */
    void addCourse(CourseAddDTO courseAddDTO);

    /**
     * 更新课程信息
     */
    void updateCourse(CourseUpdateDTO courseUpdateDTO);

    /**
     * 删除课程（逻辑删除）
     */
    void deleteCourse(Long id);
}
