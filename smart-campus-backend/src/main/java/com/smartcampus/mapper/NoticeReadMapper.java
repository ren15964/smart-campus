package com.smartcampus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcampus.entity.NoticeRead;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知阅读记录Mapper接口
 */
@Mapper
public interface NoticeReadMapper extends BaseMapper<NoticeRead> {
}
