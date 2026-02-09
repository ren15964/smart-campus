package com.smartcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartcampus.dto.CourseResourceUploadDTO;
import com.smartcampus.entity.CourseResource;
import com.smartcampus.vo.CourseResourceVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 课程资源服务接口
 */
public interface CourseResourceService extends IService<CourseResource> {

    /**
     * 获取课程资源列表
     */
    List<CourseResourceVO> getCourseResources(Long scheduleId, String chapter);

    /**
     * 教师上传课程资源
     */
    CourseResourceVO uploadResource(Long uploaderId, MultipartFile file, CourseResourceUploadDTO uploadDTO);

    /**
     * 下载课程资源
     */
    void downloadResource(Long id, HttpServletResponse response);

    /**
     * 删除课程资源
     */
    void deleteResource(Long id);
}
