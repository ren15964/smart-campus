package com.smartcampus.controller;

import com.smartcampus.common.PageResult;
import com.smartcampus.common.Result;
import com.smartcampus.dto.NewsPublishDTO;
import com.smartcampus.service.NewsService;
import com.smartcampus.utils.JwtUtil;
import com.smartcampus.vo.NewsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 新闻控制器
 */
@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 新闻列表
     */
    @GetMapping("/list")
    public Result<PageResult<NewsVO>> pageNews(@RequestParam(defaultValue = "1") Integer current,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(required = false) String category,
                                               @RequestParam(required = false) String keyword) {
        PageResult<NewsVO> pageResult = newsService.pageNews(current, size, category, keyword);
        return Result.success(pageResult);
    }

    /**
     * 新闻详情
     */
    @GetMapping("/{id}")
    public Result<NewsVO> getNewsDetail(@PathVariable Long id) {
        NewsVO newsVO = newsService.getNewsDetail(id);
        return Result.success(newsVO);
    }

    /**
     * 发布新闻（管理员）
     */
    @PostMapping("/publish")
    public Result<String> publishNews(@Validated @RequestBody NewsPublishDTO newsPublishDTO,
                                      HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long publisherId = jwtUtil.getUserIdFromToken(token);
        String publisherName = jwtUtil.getUserRealNameFromToken(token);
        newsService.publishNews(publisherId, publisherName, newsPublishDTO);
        return Result.success("发布成功");
    }

    /**
     * 删除新闻（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return Result.success("删除成功");
    }
}
