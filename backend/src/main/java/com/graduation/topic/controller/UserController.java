package com.graduation.topic.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.topic.common.Result;
import com.graduation.topic.entity.User;
import com.graduation.topic.entity.Topic;
import com.graduation.topic.entity.Progress;
import com.graduation.topic.service.UserService;
import com.graduation.topic.service.TopicService;
import com.graduation.topic.service.UserTopicService;
import com.graduation.topic.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserTopicService userTopicService;

    @Autowired
    private ProgressService progressService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<User> getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(user);
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/info")
    public Result<?> updateCurrentUser(@RequestBody User userUpdate) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        currentUser.setRealName(userUpdate.getRealName());
        currentUser.setEmail(userUpdate.getEmail());
        boolean result = userService.updateById(currentUser);
        return result ? Result.success() : Result.fail("更新失败");
    }

    // 选题管理

    /**
     * 获取可选课题列表
     */
    @GetMapping("/topics")
    public Result<Page<Topic>> getTopicList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Page<Topic> topicPage = new Page<>(page, size);
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1) // 只显示可选题
                .orderByDesc("create_time");
        topicService.page(topicPage, queryWrapper);
        return Result.success(topicPage);
    }

    /**
     * 选择课题
     */
    @PostMapping("/topics/{topicId}/select")
    public Result<?> selectTopic(@PathVariable Long topicId) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            boolean result = userTopicService.selectTopic(currentUser.getId(), topicId);
            return result ? Result.success() : Result.fail("选题失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 取消选择课题
     */
    @PostMapping("/topics/{topicId}/cancel")
    public Result<?> cancelTopic(@PathVariable Long topicId) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            boolean result = userTopicService.cancelTopic(currentUser.getId(), topicId);
            return result ? Result.success() : Result.fail("取消选题失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取用户已选课题
     */
    @GetMapping("/topics/selected")
    public Result<Topic> getSelectedTopic() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long topicId = userTopicService.getTopicIdByUserId(currentUser.getId());
        if (topicId == null) {
            return Result.success(null);
        }
        Topic topic = topicService.getById(topicId);
        return Result.success(topic);
    }

    // 进度管理

    /**
     * 获取用户的课题进度列表
     */
    @GetMapping("/progress")
    public Result<List<Progress>> getProgressList() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Progress> progressList = progressService.getProgressByUserId(currentUser.getId());
        return Result.success(progressList);
    }

    /**
     * 提交课题进度
     */
    @PostMapping("/progress")
    public Result<?> submitProgress(@RequestBody Progress progress) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 检查用户是否已选课题
        Long topicId = userTopicService.getTopicIdByUserId(currentUser.getId());
        if (topicId == null) {
            return Result.fail("请先选择课题");
        }
        progress.setUserId(currentUser.getId());
        progress.setTopicId(topicId);
        boolean result = progressService.submitProgress(progress);
        return result ? Result.success() : Result.fail("提交失败");
    }

    /**
     * 更新进度
     */
    @PutMapping("/progress/{id}")
    public Result<?> updateProgress(@PathVariable Long id, @RequestBody Progress progressUpdate) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Progress progress = progressService.getById(id);
        if (progress == null) {
            return Result.fail("进度不存在");
        }
        // 检查是否是当前用户的进度
        if (!progress.getUserId().equals(currentUser.getId())) {
            return Result.fail("无权操作");
        }
        // 只有待审核的进度可以修改
        if (!"pending".equals(progress.getStatus())) {
            return Result.fail("只有待审核的进度可以修改");
        }
        progress.setProgressStage(progressUpdate.getProgressStage());
        progress.setProgressContent(progressUpdate.getProgressContent());
        boolean result = progressService.updateById(progress);
        return result ? Result.success() : Result.fail("更新失败");
    }

    /**
     * 删除进度
     */
    @DeleteMapping("/progress/{id}")
    public Result<?> deleteProgress(@PathVariable Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Progress progress = progressService.getById(id);
        if (progress == null) {
            return Result.fail("进度不存在");
        }
        // 检查是否是当前用户的进度
        if (!progress.getUserId().equals(currentUser.getId())) {
            return Result.fail("无权操作");
        }
        // 只有待审核的进度可以删除
        if (!"pending".equals(progress.getStatus())) {
            return Result.fail("只有待审核的进度可以删除");
        }
        boolean result = progressService.removeById(id);
        return result ? Result.success() : Result.fail("删除失败");
    }

}