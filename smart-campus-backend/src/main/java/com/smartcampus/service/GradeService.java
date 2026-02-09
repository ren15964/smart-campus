package com.smartcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartcampus.dto.BatchGradeInputDTO;
import com.smartcampus.dto.GradeInputDTO;
import com.smartcampus.entity.Grade;
import com.smartcampus.vo.CourseGradeVO;
import com.smartcampus.vo.GradeStatisticsVO;
import com.smartcampus.vo.MyGradeVO;

import java.util.List;

/**
 * 成绩服务接口
 */
public interface GradeService extends IService<Grade> {

    /**
     * 学生查询个人成绩
     */
    List<MyGradeVO> getMyGrades(Long studentId, String semester);

    /**
     * 获取学生成绩统计信息
     */
    GradeStatisticsVO getGradeStatistics(Long studentId);

    /**
     * 教师查看所授课程的学生成绩列表
     */
    List<CourseGradeVO> getCourseGrades(Long scheduleId, Long teacherId);

    /**
     * 教师录入学生成绩
     */
    void inputGrade(Long teacherId, GradeInputDTO gradeInputDTO);

    /**
     * 教师批量录入成绩
     */
    void batchInputGrade(Long teacherId, BatchGradeInputDTO batchGradeInputDTO);
}
