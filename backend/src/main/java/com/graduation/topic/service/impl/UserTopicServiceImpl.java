package com.graduation.topic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.topic.entity.Topic;
import com.graduation.topic.entity.UserTopic;
import com.graduation.topic.mapper.TopicMapper;
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
    
    @Autowired
    private TopicMapper topicMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean selectTopic(Long userId, Long topicId) {
        // 1. 检查用户是否已选择任何课题（防止一人选多题）
        QueryWrapper<UserTopic> userWrapper = new QueryWrapper<>();
        userWrapper.eq("user_id", userId);
        if (count(userWrapper) > 0) {
            throw new RuntimeException("您已经选择过课题了，不可重复选择");
        }
        
        // 2. 检查用户是否已选择该课题
        if (checkUserTopic(userId, topicId)) {
            throw new RuntimeException("您已选择该课题");
        }

        // 3. 检查课题是否存在
        Topic topic = topicService.getById(topicId);
        if (topic == null) {
            throw new RuntimeException("课题不存在");
        }

        // 4. 检查课题是否可选题
        if (topic.getStatus() == 0) {
            throw new RuntimeException("该课题不可选");
        }

        // 5. 利用数据库原子更新抢占名额（这一步同时完成了"检查是否已满"和"人数+1"）
        // 如果返回 0，说明条件 (current < max) 不满足，即已满
        int updateCount = topicMapper.checkAndIncrement(topicId);
        if (updateCount == 0) {
            throw new RuntimeException("该课题已选满或不存在");
        }

        // 6. 抢到名额后，再插入选题记录
        UserTopic userTopic = new UserTopic();
        userTopic.setUserId(userId);
        userTopic.setTopicId(topicId);
        return save(userTopic);
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
