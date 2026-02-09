package com.smartcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartcampus.dto.HomeworkGradeDTO;
import com.smartcampus.entity.HomeworkGrade;

/**
 * 作业成绩服务接口
 */
public interface HomeworkGradeService extends IService<HomeworkGrade> {

    /**
     * 教师批改作业
     */
    void gradeHomework(Long graderId, HomeworkGradeDTO homeworkGradeDTO);
}
