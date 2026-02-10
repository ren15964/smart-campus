package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcampus.common.ResultCode;
import com.smartcampus.dto.HomeworkPublishDTO;
import com.smartcampus.entity.Course;
import com.smartcampus.entity.CourseSchedule;
import com.smartcampus.entity.Homework;
import com.smartcampus.entity.HomeworkGrade;
import com.smartcampus.entity.HomeworkSubmit;
import com.smartcampus.entity.User;
import com.smartcampus.exception.BusinessException;
import com.smartcampus.mapper.HomeworkMapper;
import com.smartcampus.service.CourseScheduleService;
import com.smartcampus.service.CourseService;
import com.smartcampus.service.HomeworkGradeService;
import com.smartcampus.service.HomeworkService;
import com.smartcampus.service.HomeworkSubmitService;
import com.smartcampus.service.UserService;
import com.smartcampus.vo.HomeworkDetailVO;
import com.smartcampus.vo.MyHomeworkVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作业服务实现类
 */
@Service
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework> implements HomeworkService {

    @Autowired
    private HomeworkMapper homeworkMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseScheduleService courseScheduleService;

    @Autowired
    private UserService userService;

    @Autowired
    private com.smartcampus.mapper.HomeworkSubmitMapper homeworkSubmitMapper;


    @Autowired
    private HomeworkGradeService homeworkGradeService;

    @Override
    public List<MyHomeworkVO> getMyHomeworks(Long studentId, Long scheduleId, Integer status) {
        LambdaQueryWrapper<Homework> wrapper = new LambdaQueryWrapper<>();
        if (scheduleId != null) {
            wrapper.eq(Homework::getScheduleId, scheduleId);
        }
        wrapper.eq(Homework::getIsDeleted, 0);
        wrapper.orderByDesc(Homework::getDeadline);

        List<Homework> homeworks = homeworkMapper.selectList(wrapper);

        return homeworks.stream()
                .map(homework -> {
                    MyHomeworkVO vo = new MyHomeworkVO();
                    BeanUtils.copyProperties(homework, vo);
                    vo.setHomeworkId(homework.getId());

                    CourseSchedule schedule = courseScheduleService.getById(homework.getScheduleId());
                    if (schedule != null) {
                        Course course = courseService.getById(schedule.getCourseId());
                        if (course != null) {
                            vo.setCourseName(course.getCourseName());
                        }
                    }

                    User teacher = userService.getById(homework.getTeacherId());
                    if (teacher != null) {
                        vo.setTeacherName(teacher.getRealName());
                    }

                    // 获取学生提交状态和分数
                    LambdaQueryWrapper<HomeworkSubmit> submitWrapper = new LambdaQueryWrapper<>();
                    submitWrapper.eq(HomeworkSubmit::getHomeworkId, homework.getId());
                    submitWrapper.eq(HomeworkSubmit::getStudentId, studentId);
                    HomeworkSubmit submit = homeworkSubmitMapper.selectOne(submitWrapper);

                    if (submit != null) {
                        vo.setSubmitStatus(submit.getStatus());
                        vo.setIsLate(submit.getIsLate() == 1);

                        if (submit.getStatus() == 2) { // 已批改
                            LambdaQueryWrapper<HomeworkGrade> gradeWrapper = new LambdaQueryWrapper<>();
                            gradeWrapper.eq(HomeworkGrade::getSubmitId, submit.getId());
                            HomeworkGrade grade = homeworkGradeService.getOne(gradeWrapper);
                            if (grade != null) {
                                vo.setScore(grade.getScore());
                            }
                        }
                    } else {
                        vo.setSubmitStatus(0); // 未提交
                        vo.setIsLate(homework.getDeadline().isBefore(LocalDateTime.now())); // 判断是否迟交
                    }

                    return vo;
                })
                .filter(vo -> status == null || vo.getSubmitStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public HomeworkDetailVO getHomeworkDetail(Long homeworkId, Long studentId) {
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null || homework.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.HOMEWORK_NOT_FOUND);
        }

        HomeworkDetailVO vo = new HomeworkDetailVO();
        BeanUtils.copyProperties(homework, vo);

        CourseSchedule schedule = courseScheduleService.getById(homework.getScheduleId());
        if (schedule != null) {
            Course course = courseService.getById(schedule.getCourseId());
            if (course != null) {
                vo.setCourseName(course.getCourseName());
            }
        }

        User teacher = userService.getById(homework.getTeacherId());
        if (teacher != null) {
            vo.setTeacherName(teacher.getRealName());
        }

        // 获取学生提交信息
        LambdaQueryWrapper<HomeworkSubmit> submitWrapper = new LambdaQueryWrapper<>();
        submitWrapper.eq(HomeworkSubmit::getHomeworkId, homeworkId);
        submitWrapper.eq(HomeworkSubmit::getStudentId, studentId);
        HomeworkSubmit submit = homeworkSubmitMapper.selectOne(submitWrapper);

        if (submit != null) {
            HomeworkDetailVO.HomeworkSubmitInfoVO submitInfoVO = new HomeworkDetailVO.HomeworkSubmitInfoVO();
            BeanUtils.copyProperties(submit, submitInfoVO);
            submitInfoVO.setSubmitId(submit.getId());
            submitInfoVO.setIsLate(submit.getIsLate() == 1);

            if (submit.getStatus() == 2) { // 已批改
                LambdaQueryWrapper<HomeworkGrade> gradeWrapper = new LambdaQueryWrapper<>();
                gradeWrapper.eq(HomeworkGrade::getSubmitId, submit.getId());
                HomeworkGrade grade = homeworkGradeService.getOne(gradeWrapper);
                if (grade != null) {
                    submitInfoVO.setScore(grade.getScore());
                    submitInfoVO.setComment(grade.getComment());
                }
            }
            vo.setSubmitInfo(submitInfoVO);
        }
        return vo;
    }

    @Override
    @Transactional
    public void publishHomework(Long teacherId, HomeworkPublishDTO homeworkPublishDTO) {
        CourseSchedule schedule = courseScheduleService.getById(homeworkPublishDTO.getScheduleId());
        if (schedule == null || schedule.getIsDeleted() == 1 || !schedule.getTeacherId().equals(teacherId)) {
            throw new BusinessException("无权在该课程发布作业");
        }

        Homework homework = new Homework();
        BeanUtils.copyProperties(homeworkPublishDTO, homework);
        homework.setTeacherId(teacherId);
        homework.setSubmitCount(0);
        homework.setCreateTime(LocalDateTime.now());
        homework.setUpdateTime(LocalDateTime.now());
        homework.setIsDeleted(0);
        homeworkMapper.insert(homework);
    }

    @Override
    @Transactional
    public void deleteHomework(Long id) {
        Homework homework = homeworkMapper.selectById(id);
        if (homework == null || homework.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.HOMEWORK_NOT_FOUND);
        }
        homework.setIsDeleted(1);
        homework.setUpdateTime(LocalDateTime.now());
        homeworkMapper.updateById(homework);

        // 逻辑删除相关提交记录
        LambdaQueryWrapper<HomeworkSubmit> submitWrapper = new LambdaQueryWrapper<>();
        submitWrapper.eq(HomeworkSubmit::getHomeworkId, id);
        List<HomeworkSubmit> submits = homeworkSubmitMapper.selectList(submitWrapper);
        for (HomeworkSubmit submit : submits) {
            submit.setIsDeleted(1);
            submit.setUpdateTime(LocalDateTime.now());
            homeworkSubmitMapper.updateById(submit);
        }
    }
}
