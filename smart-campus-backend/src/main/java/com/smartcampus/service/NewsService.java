package com.smartcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartcampus.common.PageResult;
import com.smartcampus.dto.NewsPublishDTO;
import com.smartcampus.entity.News;
import com.smartcampus.vo.NewsVO;

/**
 * 新闻服务接口
 */
public interface NewsService extends IService<News> {

    /**
     * 分页查询新闻列表
     */
    PageResult<NewsVO> pageNews(Integer current, Integer size, String category, String keyword);

    /**
     * 获取新闻详情
     */
    NewsVO getNewsDetail(Long id);

    /**
     * 发布新闻
     */
    void publishNews(Long publisherId, String publisherName, NewsPublishDTO newsPublishDTO);

    /**
     * 删除新闻
     */
    void deleteNews(Long id);
}
