package com.smartcampus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcampus.entity.CourseSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 开课计划Mapper接口
 */
@Mapper
public interface CourseScheduleMapper extends BaseMapper<CourseSchedule> {
}
