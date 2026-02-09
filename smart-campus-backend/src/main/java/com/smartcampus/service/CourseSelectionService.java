package com.smartcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartcampus.common.PageResult;
import com.smartcampus.dto.CourseSelectionDTO;
import com.smartcampus.entity.CourseSelection;
import com.smartcampus.vo.AvailableCourseVO;
import com.smartcampus.vo.CourseSelectionVO;

import java.util.List;

/**
 * 选课服务接口
 */
public interface CourseSelectionService extends IService<CourseSelection> {

    /**
     * 学生获取可选课程列表
     */
    PageResult<AvailableCourseVO> getAvailableCourses(Integer current, Integer size, String keyword, String semester, Long studentId);

    /**
     * 学生获取已选课程列表
     */
    List<CourseSelectionVO> getMySelectedCourses(Long studentId, String semester);

    /**
     * 学生选课
     */
    void selectCourse(Long studentId, CourseSelectionDTO courseSelectionDTO);

    /**
     * 学生退课
     */
    void withdrawCourse(Long selectionId, Long studentId);
}
