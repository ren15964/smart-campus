package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcampus.common.ResultCode;
import com.smartcampus.dto.CourseResourceUploadDTO;
import com.smartcampus.entity.Course;
import com.smartcampus.entity.CourseResource;
import com.smartcampus.entity.CourseSchedule;
import com.smartcampus.entity.User;
import com.smartcampus.exception.BusinessException;
import com.smartcampus.mapper.CourseResourceMapper;
import com.smartcampus.service.CourseResourceService;
import com.smartcampus.service.CourseScheduleService;
import com.smartcampus.service.CourseService;
import com.smartcampus.service.UserService;
import com.smartcampus.utils.FileUtil;
import com.smartcampus.vo.CourseResourceVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程资源服务实现类
 */
@Service
public class CourseResourceServiceImpl extends ServiceImpl<CourseResourceMapper, CourseResource> implements CourseResourceService {

    @Autowired
    private CourseResourceMapper courseResourceMapper;

    @Autowired
    private CourseScheduleService courseScheduleService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Override
    public List<CourseResourceVO> getCourseResources(Long scheduleId, String chapter) {
        LambdaQueryWrapper<CourseResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseResource::getScheduleId, scheduleId);
        if (chapter != null && !chapter.isEmpty()) {
            wrapper.eq(CourseResource::getChapter, chapter);
        }
        wrapper.eq(CourseResource::getIsDeleted, 0);
        wrapper.orderByAsc(CourseResource::getChapter).orderByDesc(CourseResource::getCreateTime);

        List<CourseResource> resources = courseResourceMapper.selectList(wrapper);

        return resources.stream()
                .map(resource -> {
                    CourseResourceVO vo = new CourseResourceVO();
                    BeanUtils.copyProperties(resource, vo);

                    CourseSchedule schedule = courseScheduleService.getById(resource.getScheduleId());
                    if (schedule != null) {
                        Course course = courseService.getById(schedule.getCourseId());
                        if (course != null) {
                            vo.setCourseName(course.getCourseName());
                        }
                    }

                    User uploader = userService.getById(resource.getUploaderId());
                    if (uploader != null) {
                        vo.setUploaderName(uploader.getRealName());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseResourceVO uploadResource(Long uploaderId, MultipartFile file, CourseResourceUploadDTO uploadDTO) {
        CourseSchedule schedule = courseScheduleService.getById(uploadDTO.getScheduleId());
        if (schedule == null || schedule.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.COURSE_SCHEDULE_NOT_FOUND);
        }

        try {
            String filePath = FileUtil.uploadFile(file, "resource");

            CourseResource resource = new CourseResource();
            BeanUtils.copyProperties(uploadDTO, resource);
            resource.setUploaderId(uploaderId);
            resource.setFilePath(filePath);
            resource.setFileSize(file.getSize());
            resource.setDownloadCount(0);
            resource.setCreateTime(LocalDateTime.now());
            resource.setUpdateTime(LocalDateTime.now());
            courseResourceMapper.insert(resource);

            CourseResourceVO vo = new CourseResourceVO();
            BeanUtils.copyProperties(resource, vo);
            vo.setCourseName(courseService.getById(schedule.getCourseId()).getCourseName());
            vo.setUploaderName(userService.getById(uploaderId).getRealName());
            return vo;

        } catch (IOException e) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR);
        }
    }

    @Override
    @Transactional
    public void downloadResource(Long id, HttpServletResponse response) {
        CourseResource resource = courseResourceMapper.selectById(id);
        if (resource == null || resource.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.COURSE_RESOURCE_NOT_FOUND);
        }

        try {
            // 增加下载次数
            resource.setDownloadCount(resource.getDownloadCount() + 1);
            courseResourceMapper.updateById(resource);

            FileUtil.downloadFile(resource.getFilePath(), response);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteResource(Long id) {
        CourseResource resource = courseResourceMapper.selectById(id);
        if (resource == null || resource.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.COURSE_RESOURCE_NOT_FOUND);
        }

        // 逻辑删除数据库记录
        resource.setIsDeleted(1);
        resource.setUpdateTime(LocalDateTime.now());
        courseResourceMapper.updateById(resource);

        // 物理删除文件（可选，根据需求决定）
        FileUtil.deleteFile(resource.getFilePath());
    }
}
