package com.smartcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartcampus.dto.HomeworkPublishDTO;
import com.smartcampus.entity.Homework;
import com.smartcampus.vo.HomeworkDetailVO;
import com.smartcampus.vo.MyHomeworkVO;

import java.util.List;

/**
 * 作业服务接口
 */
public interface HomeworkService extends IService<Homework> {

    /**
     * 学生查看作业列表
     */
    List<MyHomeworkVO> getMyHomeworks(Long studentId, Long scheduleId, Integer status);

    /**
     * 查看作业详情
     */
    HomeworkDetailVO getHomeworkDetail(Long homeworkId, Long studentId);

    /**
     * 教师发布作业
     */
    void publishHomework(Long teacherId, HomeworkPublishDTO homeworkPublishDTO);

    /**
     * 删除作业（逻辑删除）
     */
    void deleteHomework(Long id);
}
