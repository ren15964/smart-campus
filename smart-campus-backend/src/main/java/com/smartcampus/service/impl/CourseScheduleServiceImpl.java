package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcampus.common.PageResult;
import com.smartcampus.common.ResultCode;
import com.smartcampus.dto.CourseScheduleAddDTO;
import com.smartcampus.dto.CourseScheduleUpdateDTO;
import com.smartcampus.entity.Course;
import com.smartcampus.entity.CourseSchedule;
import com.smartcampus.entity.CourseSelection;
import com.smartcampus.entity.User;
import com.smartcampus.exception.BusinessException;
import com.smartcampus.mapper.CourseScheduleMapper;
import com.smartcampus.service.CourseScheduleService;
import com.smartcampus.service.CourseSelectionService;
import com.smartcampus.service.CourseService;
import com.smartcampus.service.UserService;
import com.smartcampus.vo.CourseScheduleVO;
import com.smartcampus.vo.MyScheduleVO;
import com.smartcampus.vo.WeekViewCourseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 开课计划服务实现类
 */
@Service
public class CourseScheduleServiceImpl extends ServiceImpl<CourseScheduleMapper, CourseSchedule> implements CourseScheduleService {

    @Autowired
    private CourseScheduleMapper courseScheduleMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private com.smartcampus.mapper.CourseSelectionMapper courseSelectionMapper;


    @Override
    public PageResult<CourseScheduleVO> pageCourseSchedule(Integer current, Integer size, String semester, String courseName, String teacherName) {
        Page<CourseSchedule> page = new Page<>(current, size);
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(semester)) {
            wrapper.eq(CourseSchedule::getSemester, semester);
        }

        // 根据课程名称和教师名称进行模糊查询，需要联表或二次查询处理
        // 这里先只处理直接字段的查询

        Page<CourseSchedule> courseSchedulePage = courseScheduleMapper.selectPage(page, wrapper);

        List<CourseScheduleVO> courseScheduleVOList = courseSchedulePage.getRecords().stream()
                .map(courseSchedule -> {
                    CourseScheduleVO courseScheduleVO = new CourseScheduleVO();
                    BeanUtils.copyProperties(courseSchedule, courseScheduleVO);

                    // 获取课程名称
                    Course course = courseService.getById(courseSchedule.getCourseId());
                    if (course != null) {
                        courseScheduleVO.setCourseName(course.getCourseName());
                        courseScheduleVO.setCourseCode(course.getCourseCode());
                    }

                    // 获取教师姓名
                    User teacher = userService.getById(courseSchedule.getTeacherId());
                    if (teacher != null) {
                        courseScheduleVO.setTeacherName(teacher.getRealName());
                    }
                    return courseScheduleVO;
                })
                .collect(Collectors.toList());

        return new PageResult<>(courseScheduleVOList, courseSchedulePage.getTotal(), courseSchedulePage.getSize(), courseSchedulePage.getCurrent(), courseSchedulePage.getPages());
    }

    @Override
    public void addCourseSchedule(CourseScheduleAddDTO courseScheduleAddDTO) {
        // 检查课程和教师是否存在
        if (courseService.getById(courseScheduleAddDTO.getCourseId()) == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        if (userService.getById(courseScheduleAddDTO.getTeacherId()) == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }

        CourseSchedule courseSchedule = new CourseSchedule();
        BeanUtils.copyProperties(courseScheduleAddDTO, courseSchedule);
        courseSchedule.setSelectedCount(0); // 初始已选人数为0
        courseSchedule.setStatus(1); // 默认开放选课
        courseSchedule.setCreateTime(LocalDateTime.now());
        courseSchedule.setUpdateTime(LocalDateTime.now());
        courseScheduleMapper.insert(courseSchedule);
    }

    @Override
    public void updateCourseSchedule(CourseScheduleUpdateDTO courseScheduleUpdateDTO) {
        CourseSchedule courseSchedule = courseScheduleMapper.selectById(courseScheduleUpdateDTO.getId());
        if (courseSchedule == null) {
            throw new BusinessException(ResultCode.COURSE_SCHEDULE_NOT_FOUND);
        }

        // 检查课程和教师是否存在
        if (courseService.getById(courseScheduleUpdateDTO.getCourseId()) == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        if (userService.getById(courseScheduleUpdateDTO.getTeacherId()) == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }

        BeanUtils.copyProperties(courseScheduleUpdateDTO, courseSchedule);
        courseSchedule.setUpdateTime(LocalDateTime.now());
        courseScheduleMapper.updateById(courseSchedule);
    }

    @Override
    public void deleteCourseSchedule(Long id) {
        CourseSchedule courseSchedule = courseScheduleMapper.selectById(id);
        if (courseSchedule == null) {
            throw new BusinessException(ResultCode.COURSE_SCHEDULE_NOT_FOUND);
        }
        // 逻辑删除
        courseSchedule.setIsDeleted(1);
        courseSchedule.setUpdateTime(LocalDateTime.now());
        courseScheduleMapper.updateById(courseSchedule);
    }

    @Override
    public void updateCourseScheduleStatus(Long id, Integer status) {
        CourseSchedule courseSchedule = courseScheduleMapper.selectById(id);
        if (courseSchedule == null) {
            throw new BusinessException(ResultCode.COURSE_SCHEDULE_NOT_FOUND);
        }
        courseSchedule.setStatus(status);
        courseSchedule.setUpdateTime(LocalDateTime.now());
        courseScheduleMapper.updateById(courseSchedule);
    }

    @Override
    public List<MyScheduleVO> getMySchedule(Long userId, String role, String semester) {
        List<CourseSchedule> schedules = new ArrayList<>();

        if ("student".equals(role)) {
            // 学生获取已选课程的开课计划
            LambdaQueryWrapper<CourseSelection> selectionWrapper = new LambdaQueryWrapper<>();
            selectionWrapper.eq(CourseSelection::getStudentId, userId);
            selectionWrapper.eq(CourseSelection::getStatus, 1); // 已选课
            // 直接通过CourseScheduleMapper查询，避免循环依赖
            List<Long> scheduleIds = courseSelectionMapper.selectList(selectionWrapper.select(CourseSelection::getScheduleId))
                    .stream().map(CourseSelection::getScheduleId).collect(Collectors.toList());

            if (!scheduleIds.isEmpty()) {
                LambdaQueryWrapper<CourseSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
                scheduleWrapper.in(CourseSchedule::getId, scheduleIds);
                scheduleWrapper.eq(CourseSchedule::getIsDeleted, 0);
                if (StringUtils.hasText(semester)) {
                    scheduleWrapper.eq(CourseSchedule::getSemester, semester);
                }
                schedules = courseScheduleMapper.selectList(scheduleWrapper);
            }

        } else if ("teacher".equals(role)) {
            // 教师获取所授课程的开课计划
            LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CourseSchedule::getTeacherId, userId);
            wrapper.eq(CourseSchedule::getIsDeleted, 0);
            if (StringUtils.hasText(semester)) {
                wrapper.eq(CourseSchedule::getSemester, semester);
            }
            schedules = courseScheduleMapper.selectList(wrapper);
        } else {
            throw new BusinessException("不支持的角色类型");
        }

        // 转换为VO并补充信息
        return schedules.stream()
                .map(schedule -> {
                    MyScheduleVO vo = new MyScheduleVO();
                    BeanUtils.copyProperties(schedule, vo);
                    vo.setScheduleId(schedule.getId());

                    Course course = courseService.getById(schedule.getCourseId());
                    if (course != null) {
                        vo.setCourseName(course.getCourseName());
                    }

                    User teacher = userService.getById(schedule.getTeacherId());
                    if (teacher != null) {
                        vo.setTeacherName(teacher.getRealName());
                    }
                    return vo;
                })
                .sorted(Comparator.comparing(MyScheduleVO::getWeekDay).thenComparing(MyScheduleVO::getStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public WeekViewCourseVO getWeekViewSchedule(Long userId, String role, String semester, Integer week) {
        List<MyScheduleVO> mySchedule = getMySchedule(userId, role, semester);
        WeekViewCourseVO weekView = new WeekViewCourseVO();
        weekView.setWeek(week);

        Map<String, List<WeekViewCourseVO.CourseDetailVO>> coursesByDay = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            coursesByDay.put(String.valueOf(i), new ArrayList<>());
        }

        for (MyScheduleVO schedule : mySchedule) {
            if (week >= schedule.getStartWeek() && week <= schedule.getEndWeek()) {
                WeekViewCourseVO.CourseDetailVO detailVO = new WeekViewCourseVO.CourseDetailVO();
                BeanUtils.copyProperties(schedule, detailVO);
                detailVO.setScheduleId(schedule.getScheduleId());
                coursesByDay.get(String.valueOf(schedule.getWeekDay())).add(detailVO);
            }
        }
        weekView.setCourses(coursesByDay);
        return weekView;
    }

    @Override
    public List<CourseSchedule> getCourseSchedulesByIds(List<Long> scheduleIds) {
        if (scheduleIds == null || scheduleIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CourseSchedule::getId, scheduleIds);
        wrapper.eq(CourseSchedule::getIsDeleted, 0);
        return courseScheduleMapper.selectList(wrapper);
    }
}