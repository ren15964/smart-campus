package com.smartcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartcampus.dto.HomeworkSubmitDTO;
import com.smartcampus.entity.HomeworkSubmit;
import com.smartcampus.vo.HomeworkSubmissionVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 作业提交服务接口
 */
public interface HomeworkSubmitService extends IService<HomeworkSubmit> {

    /**
     * 学生提交作业
     */
    Long submitHomework(Long studentId, MultipartFile file, HomeworkSubmitDTO submitDTO);

    /**
     * 教师查看作业提交情况
     */
    List<HomeworkSubmissionVO> getHomeworkSubmissions(Long homeworkId);
}
