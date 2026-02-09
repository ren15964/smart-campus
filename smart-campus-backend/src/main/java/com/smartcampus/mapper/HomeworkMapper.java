package com.smartcampus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcampus.entity.Homework;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作业Mapper接口
 */
@Mapper
public interface HomeworkMapper extends BaseMapper<Homework> {
}
