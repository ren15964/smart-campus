package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcampus.common.PageResult;
import com.smartcampus.common.ResultCode;
import com.smartcampus.dto.NoticePublishDTO;
import com.smartcampus.entity.Notice;
import com.smartcampus.entity.NoticeRead;
import com.smartcampus.exception.BusinessException;
import com.smartcampus.mapper.NoticeMapper;
import com.smartcampus.service.NoticeReadService;
import com.smartcampus.service.NoticeService;
import com.smartcampus.vo.NoticeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知公告服务实现类
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private NoticeReadService noticeReadService;

    @Override
    public PageResult<NoticeVO> pageNotice(Integer current, Integer size, Integer priority, Long userId, String userRole) {
        Page<Notice> page = new Page<>(current, size);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();

        // 只查询已发布的通知
        wrapper.eq(Notice::getStatus, 1);

        if (priority != null) {
            wrapper.eq(Notice::getPriority, priority);
        }

        // 根据用户角色筛选可见通知
        if (userRole != null) {
            wrapper.and(w -> w.eq(Notice::getTargetRole, "all").or().eq(Notice::getTargetRole, userRole));
        }

        wrapper.orderByDesc(Notice::getPublishTime);

        Page<Notice> noticePage = noticeMapper.selectPage(page, wrapper);

        List<NoticeVO> noticeVOList = noticePage.getRecords().stream()
                .map(notice -> {
                    NoticeVO vo = new NoticeVO();
                    BeanUtils.copyProperties(notice, vo);

                    // 判断当前用户是否已读
                    if (userId != null) {
                        LambdaQueryWrapper<NoticeRead> readWrapper = new LambdaQueryWrapper<>();
                        readWrapper.eq(NoticeRead::getNoticeId, notice.getId());
                        readWrapper.eq(NoticeRead::getUserId, userId);
                        vo.setIsRead(noticeReadService.count(readWrapper) > 0);
                    } else {
                        vo.setIsRead(false);
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(noticeVOList, noticePage.getTotal(), noticePage.getSize(), noticePage.getCurrent(), noticePage.getPages());
    }

    @Override
    @Transactional
    public NoticeVO getNoticeDetail(Long id, Long userId) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null || notice.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 增加阅读次数
        notice.setReadCount(notice.getReadCount() + 1);
        noticeMapper.updateById(notice);

        // 标记已读
        if (userId != null) {
            markNoticeRead(id, userId);
        }

        NoticeVO vo = new NoticeVO();
        BeanUtils.copyProperties(notice, vo);
        if (userId != null) {
            LambdaQueryWrapper<NoticeRead> readWrapper = new LambdaQueryWrapper<>();
            readWrapper.eq(NoticeRead::getNoticeId, notice.getId());
            readWrapper.eq(NoticeRead::getUserId, userId);
            vo.setIsRead(noticeReadService.count(readWrapper) > 0);
        } else {
            vo.setIsRead(false);
        }
        return vo;
    }

    @Override
    public void publishNotice(Long publisherId, String publisherName, NoticePublishDTO noticePublishDTO) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticePublishDTO, notice);
        notice.setPublisherId(publisherId);
        notice.setPublisherName(publisherName);
        notice.setPublishTime(LocalDateTime.now());
        notice.setStatus(1); // 直接发布
        notice.setReadCount(0);
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.insert(notice);
    }

    @Override
    public void deleteNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null || notice.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        notice.setIsDeleted(1);
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
    }

    @Override
    public void markNoticeRead(Long noticeId, Long userId) {
        LambdaQueryWrapper<NoticeRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NoticeRead::getNoticeId, noticeId);
        wrapper.eq(NoticeRead::getUserId, userId);
        if (noticeReadService.count(wrapper) == 0) {
            NoticeRead noticeRead = new NoticeRead();
            noticeRead.setNoticeId(noticeId);
            noticeRead.setUserId(userId);
            noticeRead.setReadTime(LocalDateTime.now());
            noticeReadService.save(noticeRead);
        }
    }
}
