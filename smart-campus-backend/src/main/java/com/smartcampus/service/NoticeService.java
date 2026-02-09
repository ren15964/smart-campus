package com.smartcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartcampus.common.PageResult;
import com.smartcampus.dto.NoticePublishDTO;
import com.smartcampus.entity.Notice;
import com.smartcampus.vo.NoticeVO;

/**
 * 通知公告服务接口
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 分页查询通知列表
     */
    PageResult<NoticeVO> pageNotice(Integer current, Integer size, Integer priority, Long userId, String userRole);

    /**
     * 获取通知详情
     */
    NoticeVO getNoticeDetail(Long id, Long userId);

    /**
     * 发布通知
     */
    void publishNotice(Long publisherId, String publisherName, NoticePublishDTO noticePublishDTO);

    /**
     * 删除通知
     */
    void deleteNotice(Long id);

    /**
     * 标记通知已读
     */
    void markNoticeRead(Long noticeId, Long userId);
}
