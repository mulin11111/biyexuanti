package com.graduation.topic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.topic.entity.Topic;
import com.graduation.topic.entity.UserTopic;
import com.graduation.topic.mapper.UserTopicMapper;
import com.graduation.topic.service.TopicService;
import com.graduation.topic.service.UserTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户选题关联表 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-01-01
 */
@Service
public class UserTopicServiceImpl extends ServiceImpl<UserTopicMapper, UserTopic> implements UserTopicService {

    @Autowired
    private TopicService topicService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean selectTopic(Long userId, Long topicId) {
        // 检查用户是否已选择该课题
        if (checkUserTopic(userId, topicId)) {
            throw new RuntimeException("您已选择该课题");
        }

        // 检查课题是否存在
        Topic topic = topicService.getById(topicId);
        if (topic == null) {
            throw new RuntimeException("课题不存在");
        }

        // 检查课题是否可选题
        if (topic.getStatus() == 0) {
            throw new RuntimeException("该课题不可选");
        }

        // 检查课题是否已满
        if (topic.getCurrentStudents() >= topic.getMaxStudents()) {
            throw new RuntimeException("该课题已选满");
        }

        // 创建用户选题关联
        UserTopic userTopic = new UserTopic();
        userTopic.setUserId(userId);
        userTopic.setTopicId(topicId);
        boolean result = save(userTopic);
        if (result) {
            // 增加课题的已选学生数
            topicService.incrementCurrentStudents(topicId);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelTopic(Long userId, Long topicId) {
        // 检查用户是否已选择该课题
        if (!checkUserTopic(userId, topicId)) {
            throw new RuntimeException("您未选择该课题");
        }

        // 删除用户选题关联
        QueryWrapper<UserTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("topic_id", topicId);
        boolean result = remove(queryWrapper);
        if (result) {
            // 减少课题的已选学生数
            topicService.decrementCurrentStudents(topicId);
        }
        return result;
    }

    @Override
    public boolean checkUserTopic(Long userId, Long topicId) {
        QueryWrapper<UserTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("topic_id", topicId);
        return count(queryWrapper) > 0;
    }

    @Override
    public Long getTopicIdByUserId(Long userId) {
        QueryWrapper<UserTopic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserTopic userTopic = getOne(queryWrapper);
        return userTopic != null ? userTopic.getTopicId() : null;
    }

}
