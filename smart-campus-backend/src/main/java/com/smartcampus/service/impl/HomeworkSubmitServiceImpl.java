package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcampus.common.ResultCode;
import com.smartcampus.dto.HomeworkSubmitDTO;
import com.smartcampus.entity.Homework;
import com.smartcampus.entity.HomeworkGrade;
import com.smartcampus.entity.HomeworkSubmit;
import com.smartcampus.entity.User;
import com.smartcampus.exception.BusinessException;
import com.smartcampus.mapper.HomeworkSubmitMapper;
import com.smartcampus.service.HomeworkGradeService;
import com.smartcampus.service.HomeworkService;
import com.smartcampus.service.HomeworkSubmitService;
import com.smartcampus.service.UserService;
import com.smartcampus.utils.FileUtil;
import com.smartcampus.vo.HomeworkSubmissionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作业提交服务实现类
 */
@Service
public class HomeworkSubmitServiceImpl extends ServiceImpl<HomeworkSubmitMapper, HomeworkSubmit> implements HomeworkSubmitService {

    @Autowired
    private HomeworkSubmitMapper homeworkSubmitMapper;

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private UserService userService;

    @Autowired
    private HomeworkGradeService homeworkGradeService;

    @Override
    @Transactional
    public Long submitHomework(Long studentId, MultipartFile file, HomeworkSubmitDTO submitDTO) {
        Homework homework = homeworkService.getById(submitDTO.getHomeworkId());
        if (homework == null || homework.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.HOMEWORK_NOT_FOUND);
        }

        // 检查是否已过截止时间
        boolean isLate = homework.getDeadline().isBefore(LocalDateTime.now());

        // 检查是否已提交
        LambdaQueryWrapper<HomeworkSubmit> existingSubmitWrapper = new LambdaQueryWrapper<>();
        existingSubmitWrapper.eq(HomeworkSubmit::getHomeworkId, submitDTO.getHomeworkId());
        existingSubmitWrapper.eq(HomeworkSubmit::getStudentId, studentId);
        if (homeworkSubmitMapper.selectCount(existingSubmitWrapper) > 0) {
            throw new BusinessException(ResultCode.HOMEWORK_ALREADY_SUBMITTED);
        }

        HomeworkSubmit homeworkSubmit = new HomeworkSubmit();
        BeanUtils.copyProperties(submitDTO, homeworkSubmit);
        homeworkSubmit.setStudentId(studentId);
        homeworkSubmit.setSubmitTime(LocalDateTime.now());
        homeworkSubmit.setStatus(1); // 已提交
        homeworkSubmit.setIsLate(isLate ? 1 : 0);
        homeworkSubmit.setCreateTime(LocalDateTime.now());
        homeworkSubmit.setUpdateTime(LocalDateTime.now());

        // 处理文件上传
        if (file != null && !file.isEmpty()) {
            try {
                String filePath = FileUtil.uploadFile(file, "homework-submit");
                homeworkSubmit.setAttachment(filePath);
            } catch (IOException e) {
                throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR.getCode(), ResultCode.FILE_UPLOAD_ERROR.getMessage() + ": " + e.getMessage());
            }
        }
        homeworkSubmitMapper.insert(homeworkSubmit);

        // 更新作业提交人数
        homework.setSubmitCount(homework.getSubmitCount() + 1);
        homeworkService.updateById(homework);

        return homeworkSubmit.getId();
    }

    @Override
    public List<HomeworkSubmissionVO> getHomeworkSubmissions(Long homeworkId) {
        Homework homework = homeworkService.getById(homeworkId);
        if (homework == null || homework.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.HOMEWORK_NOT_FOUND);
        }

        LambdaQueryWrapper<HomeworkSubmit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkSubmit::getHomeworkId, homeworkId);
        wrapper.eq(HomeworkSubmit::getIsDeleted, 0);
        wrapper.orderByDesc(HomeworkSubmit::getSubmitTime);

        List<HomeworkSubmit> submissions = homeworkSubmitMapper.selectList(wrapper);

        return submissions.stream()
                .map(submit -> {
                    HomeworkSubmissionVO vo = new HomeworkSubmissionVO();
                    BeanUtils.copyProperties(submit, vo);
                    vo.setSubmitId(submit.getId());
                    vo.setIsLate(submit.getIsLate() == 1);

                    User student = userService.getById(submit.getStudentId());
                    if (student != null) {
                        vo.setStudentName(student.getRealName());
                        vo.setStudentNo(student.getUsername()); // 假设username是学号
                    }

                    // 获取批改成绩
                    if (submit.getStatus() == 2) { // 已批改
                        LambdaQueryWrapper<HomeworkGrade> gradeWrapper = new LambdaQueryWrapper<>();
                        gradeWrapper.eq(HomeworkGrade::getSubmitId, submit.getId());
                        HomeworkGrade grade = homeworkGradeService.getOne(gradeWrapper);
                        if (grade != null) {
                            vo.setScore(grade.getScore());
                        }
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
