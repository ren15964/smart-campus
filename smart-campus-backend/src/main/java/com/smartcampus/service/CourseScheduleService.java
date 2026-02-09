package com.smartcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartcampus.common.PageResult;
import com.smartcampus.dto.CourseScheduleAddDTO;
import com.smartcampus.dto.CourseScheduleUpdateDTO;
import com.smartcampus.entity.CourseSchedule;
import com.smartcampus.vo.CourseScheduleVO;
import com.smartcampus.vo.MyScheduleVO;
import com.smartcampus.vo.WeekViewCourseVO;

import java.util.List;

/**
 * 开课计划服务接口
 */
public interface CourseScheduleService extends IService<CourseSchedule> {

    /**
     * 分页查询开课计划列表
     */
    PageResult<CourseScheduleVO> pageCourseSchedule(Integer current, Integer size, String semester, String courseName, String teacherName);

    /**
     * 添加开课计划
     */
    void addCourseSchedule(CourseScheduleAddDTO courseScheduleAddDTO);

    /**
     * 更新开课计划
     */
    void updateCourseSchedule(CourseScheduleUpdateDTO courseScheduleUpdateDTO);

    /**
     * 删除开课计划（逻辑删除）
     */
    void deleteCourseSchedule(Long id);

    /**
     * 更新开课状态
     */
    void updateCourseScheduleStatus(Long id, Integer status);

    /**
     * 获取个人课表（学生/教师）
     */
    List<MyScheduleVO> getMySchedule(Long userId, String role, String semester);

    /**
     * 获取按周显示的课表（学生/教师）
     */
    WeekViewCourseVO getWeekViewSchedule(Long userId, String role, String semester, Integer week);

    /**
     * 根据ID列表获取开课计划
     */
    List<CourseSchedule> getCourseSchedulesByIds(List<Long> scheduleIds);
}
