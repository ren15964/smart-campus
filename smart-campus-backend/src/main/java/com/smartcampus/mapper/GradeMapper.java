package com.smartcampus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcampus.entity.Grade;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成绩Mapper接口
 */
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {
}
