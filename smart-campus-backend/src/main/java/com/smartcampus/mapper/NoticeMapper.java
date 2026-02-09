package com.smartcampus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcampus.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知公告Mapper接口
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
