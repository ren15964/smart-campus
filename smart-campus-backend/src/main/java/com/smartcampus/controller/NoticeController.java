package com.smartcampus.controller;

import com.smartcampus.common.PageResult;
import com.smartcampus.common.Result;
import com.smartcampus.dto.NoticePublishDTO;
import com.smartcampus.service.NoticeService;
import com.smartcampus.utils.JwtUtil;
import com.smartcampus.vo.NoticeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 通知公告控制器
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 通知列表
     */
    @GetMapping("/list")
    public Result<PageResult<NoticeVO>> pageNotice(@RequestParam(defaultValue = "1") Integer current,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestParam(required = false) Integer priority,
                                                   HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = null;
        String userRole = null;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            userId = jwtUtil.getUserIdFromToken(token);
            userRole = jwtUtil.getUserRoleFromToken(token);
        }
        PageResult<NoticeVO> pageResult = noticeService.pageNotice(current, size, priority, userId, userRole);
        return Result.success(pageResult);
    }

    /**
     * 通知详情
     */
    @GetMapping("/{id}")
    public Result<NoticeVO> getNoticeDetail(@PathVariable Long id,
                                            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            userId = jwtUtil.getUserIdFromToken(token);
        }
        NoticeVO noticeVO = noticeService.getNoticeDetail(id, userId);
        return Result.success(noticeVO);
    }

    /**
     * 发布通知（管理员）
     */
    @PostMapping("/publish")
    public Result<String> publishNotice(@Validated @RequestBody NoticePublishDTO noticePublishDTO,
                                        HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long publisherId = jwtUtil.getUserIdFromToken(token);
        String publisherName = jwtUtil.getUserRealNameFromToken(token); // 假设JWT中存储了用户真实姓名
        noticeService.publishNotice(publisherId, publisherName, noticePublishDTO);
        return Result.success("发布成功");
    }

    /**
     * 删除通知（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success("删除成功");
    }
}
