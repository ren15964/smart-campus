package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcampus.common.ResultCode;
import com.smartcampus.dto.BatchGradeInputDTO;
import com.smartcampus.dto.GradeInputDTO;
import com.smartcampus.entity.Course;
import com.smartcampus.entity.CourseSchedule;
import com.smartcampus.entity.Grade;
import com.smartcampus.entity.User;
import com.smartcampus.exception.BusinessException;
import com.smartcampus.mapper.GradeMapper;
import com.smartcampus.service.CourseScheduleService;
import com.smartcampus.service.CourseService;
import com.smartcampus.service.GradeService;
import com.smartcampus.service.UserService;
import com.smartcampus.vo.CourseGradeVO;
import com.smartcampus.vo.GradeStatisticsVO;
import com.smartcampus.vo.MyGradeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 成绩服务实现类
 */
@Service
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseScheduleService courseScheduleService;

    @Autowired
    private UserService userService;

    @Override
    public List<MyGradeVO> getMyGrades(Long studentId, String semester) {
        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Grade::getStudentId, studentId);
        wrapper.eq(Grade::getIsDeleted, 0);

        List<Grade> grades = gradeMapper.selectList(wrapper);

        return grades.stream()
                .filter(grade -> {
                    CourseSchedule schedule = courseScheduleService.getById(grade.getScheduleId());
                    return schedule != null && (semester == null || schedule.getSemester().equals(semester));
                })
                .map(grade -> {
                    MyGradeVO vo = new MyGradeVO();
                    BeanUtils.copyProperties(grade, vo);

                    CourseSchedule schedule = courseScheduleService.getById(grade.getScheduleId());
                    if (schedule != null) {
                        vo.setSemester(schedule.getSemester());
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
    public GradeStatisticsVO getGradeStatistics(Long studentId) {
        List<MyGradeVO> myGrades = getMyGrades(studentId, null);

        BigDecimal totalCredits = BigDecimal.ZERO;
        BigDecimal totalGpaPoints = BigDecimal.ZERO;
        int passedCourses = 0;
        int failedCourses = 0;

        for (MyGradeVO grade : myGrades) {
            if (grade.getTotalScore() != null && grade.getCredit() != null) {
                totalCredits = totalCredits.add(grade.getCredit());
                BigDecimal gpa = calculateGpa(grade.getTotalScore());
                grade.setGpa(gpa); // 设置绩点到VO中
                totalGpaPoints = totalGpaPoints.add(gpa.multiply(grade.getCredit()));

                if (grade.getTotalScore().compareTo(new BigDecimal("60")) >= 0) {
                    passedCourses++;
                } else {
                    failedCourses++;
                }
            }
        }

        GradeStatisticsVO statisticsVO = new GradeStatisticsVO();
        statisticsVO.setTotalCredits(totalCredits);
        statisticsVO.setPassedCourses(passedCourses);
        statisticsVO.setFailedCourses(failedCourses);

        if (totalCredits.compareTo(BigDecimal.ZERO) > 0) {
            statisticsVO.setAverageGpa(totalGpaPoints.divide(totalCredits, 2, RoundingMode.HALF_UP));
        } else {
            statisticsVO.setAverageGpa(BigDecimal.ZERO);
        }
        return statisticsVO;
    }

    @Override
    public List<CourseGradeVO> getCourseGrades(Long scheduleId, Long teacherId) {
        CourseSchedule schedule = courseScheduleService.getById(scheduleId);
        if (schedule == null || !schedule.getTeacherId().equals(teacherId)) {
            throw new BusinessException("无权查看该课程成绩");
        }

        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Grade::getScheduleId, scheduleId);
        wrapper.eq(Grade::getIsDeleted, 0);
        List<Grade> grades = gradeMapper.selectList(wrapper);

        return grades.stream()
                .map(grade -> {
                    CourseGradeVO vo = new CourseGradeVO();
                    BeanUtils.copyProperties(grade, vo);
                    vo.setGradeId(grade.getId());

                    User student = userService.getById(grade.getStudentId());
                    if (student != null) {
                        vo.setStudentName(student.getRealName());
                        // TODO: 获取学生学号，需要根据User表中的username来判断
                        // 暂时用realName代替
                        vo.setStudentNo(student.getUsername());
                    }
                    vo.setGpa(calculateGpa(grade.getTotalScore()));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void inputGrade(Long teacherId, GradeInputDTO gradeInputDTO) {
        CourseSchedule schedule = courseScheduleService.getById(gradeInputDTO.getScheduleId());
        if (schedule == null || !schedule.getTeacherId().equals(teacherId)) {
            throw new BusinessException("无权操作该课程成绩");
        }

        // 计算总成绩和绩点
        BigDecimal usualScore = gradeInputDTO.getUsualScore() != null ? gradeInputDTO.getUsualScore() : BigDecimal.ZERO;
        BigDecimal examScore = gradeInputDTO.getExamScore() != null ? gradeInputDTO.getExamScore() : BigDecimal.ZERO;
        BigDecimal totalScore = usualScore.multiply(new BigDecimal("0.4")).add(examScore.multiply(new BigDecimal("0.6")));
        BigDecimal gpa = calculateGpa(totalScore);

        LambdaQueryWrapper<Grade> existingGradeWrapper = new LambdaQueryWrapper<>();
        existingGradeWrapper.eq(Grade::getStudentId, gradeInputDTO.getStudentId());
        existingGradeWrapper.eq(Grade::getScheduleId, gradeInputDTO.getScheduleId());
        Grade existingGrade = gradeMapper.selectOne(existingGradeWrapper);

        if (existingGrade != null) {
            // 更新成绩
            existingGrade.setUsualScore(usualScore);
            existingGrade.setExamScore(examScore);
            existingGrade.setTotalScore(totalScore);
            existingGrade.setGpa(gpa);
            existingGrade.setUpdateTime(LocalDateTime.now());
            gradeMapper.updateById(existingGrade);
        } else {
            // 新增成绩
            Grade grade = new Grade();
            BeanUtils.copyProperties(gradeInputDTO, grade);
            grade.setUsualScore(usualScore);
            grade.setExamScore(examScore);
            grade.setTotalScore(totalScore);
            grade.setGpa(gpa);
            grade.setCreateTime(LocalDateTime.now());
            grade.setUpdateTime(LocalDateTime.now());
            gradeMapper.insert(grade);
        }
    }

    @Override
    @Transactional
    public void batchInputGrade(Long teacherId, BatchGradeInputDTO batchGradeInputDTO) {
        Long scheduleId = batchGradeInputDTO.getScheduleId();
        CourseSchedule schedule = courseScheduleService.getById(scheduleId);
        if (schedule == null || !schedule.getTeacherId().equals(teacherId)) {
            throw new BusinessException("无权操作该课程成绩");
        }

        for (BatchGradeInputDTO.GradeItemDTO item : batchGradeInputDTO.getGrades()) {
            BigDecimal usualScore = item.getUsualScore() != null ? item.getUsualScore() : BigDecimal.ZERO;
            BigDecimal examScore = item.getExamScore() != null ? item.getExamScore() : BigDecimal.ZERO;
            BigDecimal totalScore = usualScore.multiply(new BigDecimal("0.4")).add(examScore.multiply(new BigDecimal("0.6")));
            BigDecimal gpa = calculateGpa(totalScore);

            LambdaQueryWrapper<Grade> existingGradeWrapper = new LambdaQueryWrapper<>();
            existingGradeWrapper.eq(Grade::getStudentId, item.getStudentId());
            existingGradeWrapper.eq(Grade::getScheduleId, scheduleId);
            Grade existingGrade = gradeMapper.selectOne(existingGradeWrapper);

            if (existingGrade != null) {
                // 更新成绩
                existingGrade.setUsualScore(usualScore);
                existingGrade.setExamScore(examScore);
                existingGrade.setTotalScore(totalScore);
                existingGrade.setGpa(gpa);
                existingGrade.setUpdateTime(LocalDateTime.now());
                gradeMapper.updateById(existingGrade);
            } else {
                // 新增成绩
                Grade grade = new Grade();
                grade.setStudentId(item.getStudentId());
                grade.setScheduleId(scheduleId);
                grade.setUsualScore(usualScore);
                grade.setExamScore(examScore);
                grade.setTotalScore(totalScore);
                grade.setGpa(gpa);
                grade.setCreateTime(LocalDateTime.now());
                grade.setUpdateTime(LocalDateTime.now());
                gradeMapper.insert(grade);
            }
        }
    }

    /**
     * 计算绩点
     */
    private BigDecimal calculateGpa(BigDecimal score) {
        if (score == null) {
            return BigDecimal.ZERO;
        }
        if (score.compareTo(new BigDecimal("90")) >= 0) {
            return new BigDecimal("4.0");
        } else if (score.compareTo(new BigDecimal("85")) >= 0) {
            return new BigDecimal("3.7");
        } else if (score.compareTo(new BigDecimal("80")) >= 0) {
            return new BigDecimal("3.3");
        } else if (score.compareTo(new BigDecimal("75")) >= 0) {
            return new BigDecimal("3.0");
        } else if (score.compareTo(new BigDecimal("70")) >= 0) {
            return new BigDecimal("2.7");
        } else if (score.compareTo(new BigDecimal("65")) >= 0) {
            return new BigDecimal("2.3");
        } else if (score.compareTo(new BigDecimal("60")) >= 0) {
            return new BigDecimal("2.0");
        } else {
            return BigDecimal.ZERO;
        }
    }
}
