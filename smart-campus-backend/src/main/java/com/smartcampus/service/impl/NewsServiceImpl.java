package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcampus.common.PageResult;
import com.smartcampus.common.ResultCode;
import com.smartcampus.dto.NewsPublishDTO;
import com.smartcampus.entity.News;
import com.smartcampus.entity.User;
import com.smartcampus.exception.BusinessException;
import com.smartcampus.mapper.NewsMapper;
import com.smartcampus.service.NewsService;
import com.smartcampus.service.UserService;
import com.smartcampus.vo.NewsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 新闻服务实现类
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private UserService userService;

    @Override
    public PageResult<NewsVO> pageNews(Integer current, Integer size, String category, String keyword) {
        Page<News> page = new Page<>(current, size);
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();

        // 只查询已发布的新闻
        wrapper.eq(News::getStatus, 1);

        if (StringUtils.hasText(category)) {
            wrapper.eq(News::getCategory, category);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(News::getTitle, keyword).or().like(News::getContent, keyword);
        }
        wrapper.orderByDesc(News::getPublishTime);

        Page<News> newsPage = newsMapper.selectPage(page, wrapper);

        List<NewsVO> newsVOList = newsPage.getRecords().stream()
                .map(news -> {
                    NewsVO vo = new NewsVO();
                    BeanUtils.copyProperties(news, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(newsVOList, newsPage.getTotal(), newsPage.getSize(), newsPage.getCurrent(), newsPage.getPages());
    }

    @Override
    @Transactional
    public NewsVO getNewsDetail(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null || news.getIsDeleted() == 1 || news.getStatus() == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 增加浏览次数
        news.setViewCount(news.getViewCount() + 1);
        newsMapper.updateById(news);

        NewsVO vo = new NewsVO();
        BeanUtils.copyProperties(news, vo);
        return vo;
    }

    @Override
    public void publishNews(Long publisherId, String publisherName, NewsPublishDTO newsPublishDTO) {
        News news = new News();
        BeanUtils.copyProperties(newsPublishDTO, news);
        news.setPublisherId(publisherId);
        news.setPublisherName(publisherName);
        news.setPublishTime(LocalDateTime.now());
        news.setStatus(1); // 直接发布
        news.setViewCount(0);
        news.setCreateTime(LocalDateTime.now());
        news.setUpdateTime(LocalDateTime.now());
        newsMapper.insert(news);
    }

    @Override
    public void deleteNews(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null || news.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        news.setIsDeleted(1);
        news.setUpdateTime(LocalDateTime.now());
        newsMapper.updateById(news);
    }
}
