package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcampus.entity.NoticeRead;
import com.smartcampus.mapper.NoticeReadMapper;
import com.smartcampus.service.NoticeReadService;
import org.springframework.stereotype.Service;

/**
 * 通知阅读记录服务实现类
 */
@Service
public class NoticeReadServiceImpl extends ServiceImpl<NoticeReadMapper, NoticeRead> implements NoticeReadService {

}
