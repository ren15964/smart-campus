package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcampus.common.PageResult;
import com.smartcampus.common.ResultCode;
import com.smartcampus.dto.CourseAddDTO;
import com.smartcampus.dto.CourseUpdateDTO;
import com.smartcampus.entity.Course;
import com.smartcampus.exception.BusinessException;
import com.smartcampus.mapper.CourseMapper;
import com.smartcampus.service.CourseService;
import com.smartcampus.vo.CourseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public PageResult<CourseVO> pageCourse(Integer current, Integer size, String keyword, String courseType) {
        Page<Course> page = new Page<>(current, size);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Course::getCourseName, keyword).or().like(Course::getCourseCode, keyword);
        }
        if (StringUtils.hasText(courseType)) {
            wrapper.eq(Course::getCourseType, courseType);
        }
        wrapper.orderByDesc(Course::getCreateTime);

        Page<Course> coursePage = courseMapper.selectPage(page, wrapper);

        List<CourseVO> courseVOList = coursePage.getRecords().stream()
                .map(course -> {
                    CourseVO courseVO = new CourseVO();
                    BeanUtils.copyProperties(course, courseVO);
                    return courseVO;
                })
                .collect(Collectors.toList());

        return new PageResult<>(courseVOList, coursePage.getTotal(), coursePage.getSize(), coursePage.getCurrent(), coursePage.getPages());
    }

    @Override
    public CourseVO getCourseDetail(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }

        CourseVO courseVO = new CourseVO();
        BeanUtils.copyProperties(course, courseVO);
        return courseVO;
    }

    @Override
    public void addCourse(CourseAddDTO courseAddDTO) {
        // 检查课程编码是否已存在
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseCode, courseAddDTO.getCourseCode());
        if (courseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("课程编码已存在");
        }

        Course course = new Course();
        BeanUtils.copyProperties(courseAddDTO, course);
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.insert(course);
    }

    @Override
    public void updateCourse(CourseUpdateDTO courseUpdateDTO) {
        Course course = courseMapper.selectById(courseUpdateDTO.getId());
        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }

        BeanUtils.copyProperties(courseUpdateDTO, course);
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(course);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        // 逻辑删除
        course.setIsDeleted(1);
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(course);
    }
}
