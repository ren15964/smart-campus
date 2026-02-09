package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcampus.common.ResultCode;
import com.smartcampus.dto.HomeworkGradeDTO;
import com.smartcampus.entity.HomeworkGrade;
import com.smartcampus.entity.HomeworkSubmit;
import com.smartcampus.exception.BusinessException;
import com.smartcampus.mapper.HomeworkGradeMapper;
import com.smartcampus.mapper.HomeworkSubmitMapper;
import com.smartcampus.service.HomeworkGradeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 作业成绩服务实现类
 */
@Service
public class HomeworkGradeServiceImpl extends ServiceImpl<HomeworkGradeMapper, HomeworkGrade> implements HomeworkGradeService {

    @Autowired
    private HomeworkGradeMapper homeworkGradeMapper;

    @Autowired
    private HomeworkSubmitMapper homeworkSubmitMapper;

    @Override
    @Transactional
    public void gradeHomework(Long graderId, HomeworkGradeDTO homeworkGradeDTO) {
        HomeworkSubmit submit = homeworkSubmitMapper.selectById(homeworkGradeDTO.getSubmitId());
        if (submit == null || submit.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.HOMEWORK_SUBMIT_NOT_FOUND);
        }

        // TODO: 检查 graderId 是否有权限批改此作业 (例如，是否是该作业所属课程的教师)

        LambdaQueryWrapper<HomeworkGrade> existingGradeWrapper = new LambdaQueryWrapper<>();
        existingGradeWrapper.eq(HomeworkGrade::getSubmitId, homeworkGradeDTO.getSubmitId());
        HomeworkGrade existingGrade = homeworkGradeMapper.selectOne(existingGradeWrapper);

        if (existingGrade != null) {
            // 更新成绩
            existingGrade.setScore(homeworkGradeDTO.getScore());
            existingGrade.setComment(homeworkGradeDTO.getComment());
            existingGrade.setGraderId(graderId);
            existingGrade.setGradeTime(LocalDateTime.now());
            existingGrade.setUpdateTime(LocalDateTime.now());
            homeworkGradeMapper.updateById(existingGrade);
        } else {
            // 新增成绩
            HomeworkGrade homeworkGrade = new HomeworkGrade();
            BeanUtils.copyProperties(homeworkGradeDTO, homeworkGrade);
            homeworkGrade.setGraderId(graderId);
            homeworkGrade.setGradeTime(LocalDateTime.now());
            homeworkGrade.setCreateTime(LocalDateTime.now());
            homeworkGrade.setUpdateTime(LocalDateTime.now());
            homeworkGradeMapper.insert(homeworkGrade);
        }

        // 更新提交状态为已批改
        submit.setStatus(2); // 已批改
        submit.setUpdateTime(LocalDateTime.now());
        homeworkSubmitMapper.updateById(submit);
    }
}
