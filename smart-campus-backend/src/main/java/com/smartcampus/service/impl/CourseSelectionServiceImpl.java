package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcampus.common.PageResult;
import com.smartcampus.common.ResultCode;
import com.smartcampus.dto.CourseSelectionDTO;
import com.smartcampus.entity.Course;
import com.smartcampus.entity.CourseSchedule;
import com.smartcampus.entity.CourseSelection;
import com.smartcampus.entity.User;
import com.smartcampus.exception.BusinessException;
import com.smartcampus.mapper.CourseSelectionMapper;
import com.smartcampus.service.CourseScheduleService;
import com.smartcampus.service.CourseSelectionService;
import com.smartcampus.service.CourseService;
import com.smartcampus.service.UserService;
import com.smartcampus.vo.AvailableCourseVO;
import com.smartcampus.vo.CourseSelectionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.smartcampus.entity.Course;
import com.smartcampus.entity.User;

/**
 * 选课服务实现类
 */
@Service
public class CourseSelectionServiceImpl extends ServiceImpl<CourseSelectionMapper, CourseSelection> implements CourseSelectionService {

    @Autowired
    private CourseSelectionMapper courseSelectionMapper;

    @Autowired
    private CourseScheduleService courseScheduleService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Override
    public PageResult<AvailableCourseVO> getAvailableCourses(Integer current, Integer size, String keyword, String semester, Long studentId) {
        Page<CourseSchedule> page = new Page<>(current, size);
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(semester)) {
            wrapper.eq(CourseSchedule::getSemester, semester);
        }

        // TODO: 根据keyword过滤课程名称或教师名称 (需要联表查询)

        // 只查询开放选课的课程
        wrapper.eq(CourseSchedule::getStatus, 1);

        Page<CourseSchedule> courseSchedulePage = courseScheduleService.page(page, wrapper);

        List<AvailableCourseVO> availableCourseVOList = courseSchedulePage.getRecords().stream()
                .map(schedule -> {
                    AvailableCourseVO vo = new AvailableCourseVO();
                    BeanUtils.copyProperties(schedule, vo);
                    vo.setScheduleId(schedule.getId());

                    Course course = courseService.getById(schedule.getCourseId());
                    if (course != null) {
                        vo.setCourseName(course.getCourseName());
                        vo.setCourseCode(course.getCourseCode());
                    }

                    User teacher = userService.getById(schedule.getTeacherId());
                    if (teacher != null) {
                        vo.setTeacherName(teacher.getRealName());
                    }

                    // 判断当前学生是否已选此课程
                    LambdaQueryWrapper<CourseSelection> selectionWrapper = new LambdaQueryWrapper<>();
                    selectionWrapper.eq(CourseSelection::getStudentId, studentId);
                    selectionWrapper.eq(CourseSelection::getScheduleId, schedule.getId());
                    vo.setIsSelected(courseSelectionMapper.selectCount(selectionWrapper) > 0);

                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(availableCourseVOList, courseSchedulePage.getTotal(), courseSchedulePage.getSize(), courseSchedulePage.getCurrent(), courseSchedulePage.getPages());
    }

    @Override
    public List<CourseSelectionVO> getMySelectedCourses(Long studentId, String semester) {
        LambdaQueryWrapper<CourseSelection> selectionWrapper = new LambdaQueryWrapper<>();
        selectionWrapper.eq(CourseSelection::getStudentId, studentId);
        selectionWrapper.eq(CourseSelection::getStatus, 1); // 已选课程

        List<CourseSelection> selections = courseSelectionMapper.selectList(selectionWrapper);

        List<Long> scheduleIds = selections.stream().map(CourseSelection::getScheduleId).collect(Collectors.toList());
        if (scheduleIds.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, CourseSchedule> scheduleMap = courseScheduleService.getCourseSchedulesByIds(scheduleIds)
                .stream().collect(Collectors.toMap(CourseSchedule::getId, Function.identity()));

        // 过滤并转换为VO
        return selections.stream()
                .filter(selection -> {
                    CourseSchedule schedule = scheduleMap.get(selection.getScheduleId());
                    return schedule != null && (semester == null || schedule.getSemester().equals(semester));
                })
                .map(selection -> {
                    CourseSelectionVO vo = new CourseSelectionVO();
                    BeanUtils.copyProperties(selection, vo);
                    vo.setSelectionId(selection.getId());

                    CourseSchedule schedule = scheduleMap.get(selection.getScheduleId());
                    if (schedule != null) {
                        BeanUtils.copyProperties(schedule, vo);
                        Course course = courseService.getById(schedule.getCourseId());
                        if (course != null) {
                            vo.setCourseName(course.getCourseName());
                            vo.setCourseCode(course.getCourseCode());
                            vo.setCredit(course.getCredit());
                        }
                        User teacher = userService.getById(schedule.getTeacherId());
                        if (teacher != null) {
                            vo.setTeacherName(teacher.getRealName());
                        }
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void selectCourse(Long studentId, CourseSelectionDTO courseSelectionDTO) {
        Long scheduleId = courseSelectionDTO.getScheduleId();
        CourseSchedule courseSchedule = courseScheduleService.getById(scheduleId);
        if (courseSchedule == null || courseSchedule.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.COURSE_SCHEDULE_NOT_FOUND);
        }

        // 检查是否开放选课
        if (courseSchedule.getStatus() == 0) {
            throw new BusinessException(ResultCode.NOT_IN_SELECTION_TIME);
        }

        // 检查容量
        if (courseSchedule.getSelectedCount() >= courseSchedule.getCapacity()) {
            throw new BusinessException(ResultCode.COURSE_FULL);
        }

        // 检查是否已选
        LambdaQueryWrapper<CourseSelection> existingSelectionWrapper = new LambdaQueryWrapper<>();
        existingSelectionWrapper.eq(CourseSelection::getStudentId, studentId);
        existingSelectionWrapper.eq(CourseSelection::getScheduleId, scheduleId);
        if (courseSelectionMapper.selectCount(existingSelectionWrapper) > 0) {
            throw new BusinessException(ResultCode.ALREADY_SELECTED);
        }

        // 检查时间冲突
        List<CourseSelectionVO> mySelectedCourses = getMySelectedCourses(studentId, courseSchedule.getSemester());
        for (CourseSelectionVO selectedCourse : mySelectedCourses) {
            // 假设我们只比较星期和开始结束时间
            if (selectedCourse.getWeekDay().equals(courseSchedule.getWeekDay()) &&
                    !(courseSchedule.getEndTime().compareTo(selectedCourse.getStartTime()) <= 0 ||
                            courseSchedule.getStartTime().compareTo(selectedCourse.getEndTime()) >= 0)) {
                throw new BusinessException(ResultCode.COURSE_TIME_CONFLICT);
            }
        }

        // 选课
        CourseSelection courseSelection = new CourseSelection();
        courseSelection.setStudentId(studentId);
        courseSelection.setScheduleId(scheduleId);
        courseSelection.setSelectionTime(LocalDateTime.now());
        courseSelection.setStatus(1); // 已选课
        courseSelection.setCreateTime(LocalDateTime.now());
        courseSelection.setUpdateTime(LocalDateTime.now());
        courseSelectionMapper.insert(courseSelection);

        // 更新开课计划已选人数
        courseSchedule.setSelectedCount(courseSchedule.getSelectedCount() + 1);
        courseScheduleService.updateById(courseSchedule);
    }

    @Override
    @Transactional
    public void withdrawCourse(Long selectionId, Long studentId) {
        CourseSelection courseSelection = courseSelectionMapper.selectById(selectionId);
        if (courseSelection == null || !courseSelection.getStudentId().equals(studentId) || courseSelection.getIsDeleted() == 1) {
            throw new BusinessException("选课记录不存在或无权操作");
        }

        // 检查是否已退课
        if (courseSelection.getStatus() == 0) {
            throw new BusinessException("已退选该课程，请勿重复操作");
        }

        CourseSchedule courseSchedule = courseScheduleService.getById(courseSelection.getScheduleId());
        if (courseSchedule == null) {
            throw new BusinessException(ResultCode.COURSE_SCHEDULE_NOT_FOUND);
        }

        // 逻辑删除选课记录
        courseSelection.setIsDeleted(1);
        courseSelection.setStatus(0); // 设置为已退课
        courseSelection.setUpdateTime(LocalDateTime.now());
        courseSelectionMapper.updateById(courseSelection);

        // 更新开课计划已选人数
        if (courseSchedule.getSelectedCount() > 0) {
            courseSchedule.setSelectedCount(courseSchedule.getSelectedCount() - 1);
            courseScheduleService.updateById(courseSchedule);
        }
    }
}
