package com.graduation.topic.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.topic.common.Result;
import com.graduation.topic.entity.User;
import com.graduation.topic.entity.Topic;
import com.graduation.topic.entity.Progress;
import com.graduation.topic.service.UserService;
import com.graduation.topic.service.TopicService;
import com.graduation.topic.service.ProgressService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 用户管理

    @GetMapping("/users")
    public Result<Page<User>> getUserList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Page<User> userPage = new Page<>(page, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        userService.page(userPage, queryWrapper);
        return Result.success(userPage);
    }

    @PostMapping("/users")
    public Result<?> addUser(@RequestBody User user) {
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        boolean result = userService.save(user);
        return result ? Result.success() : Result.fail("添加失败");
    }

    @PutMapping("/users/{id}")
    public Result<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        // 如果密码不为空，则加密更新
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 否则不更新密码
            user.setPassword(null);
        }
        boolean result = userService.updateById(user);
        return result ? Result.success() : Result.fail("修改失败");
    }

    @DeleteMapping("/users/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        boolean result = userService.removeById(id);
        return result ? Result.success() : Result.fail("删除失败");
    }

    // 选题管理

    @GetMapping("/topics")
    public Result<Page<Topic>> getTopicList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Page<Topic> topicPage = new Page<>(page, size);
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        topicService.page(topicPage, queryWrapper);
        return Result.success(topicPage);
    }

    @PostMapping("/topics")
    public Result<?> addTopic(@RequestBody Topic topic) {
        boolean result = topicService.save(topic);
        return result ? Result.success() : Result.fail("添加失败");
    }

    @PutMapping("/topics/{id}")
    public Result<?> updateTopic(@PathVariable Long id, @RequestBody Topic topic) {
        topic.setId(id);
        boolean result = topicService.updateById(topic);
        return result ? Result.success() : Result.fail("修改失败");
    }

    @DeleteMapping("/topics/{id}")
    public Result<?> deleteTopic(@PathVariable Long id) {
        boolean result = topicService.removeById(id);
        return result ? Result.success() : Result.fail("删除失败");
    }

    // 数据统计

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 用户总数
        long userCount = userService.count();
        statistics.put("userCount", userCount);

        // 选题总数
        long topicCount = topicService.count();
        statistics.put("topicCount", topicCount);

        // 进度总数
        long progressCount = progressService.count();
        statistics.put("progressCount", progressCount);

        return Result.success(statistics);
    }

    // 进度管理

    @GetMapping("/progress")
    public Result<Page<Progress>> getAllProgress(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Page<Progress> progressPage = new Page<>(page, size);
        QueryWrapper<Progress> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("progress_date");
        progressService.page(progressPage, queryWrapper);
        return Result.success(progressPage);
    }

    @PutMapping("/progress/{id}")
    public Result<?> auditProgress(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String teacherComment) {
        boolean result = progressService.updateProgressStatus(id, status, teacherComment);
        return result ? Result.success() : Result.fail("审核失败");
    }

}