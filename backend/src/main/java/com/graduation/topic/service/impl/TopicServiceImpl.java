package com.graduation.topic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.topic.entity.Topic;
import com.graduation.topic.mapper.TopicMapper;
import com.graduation.topic.service.TopicService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 选题表 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-01-01
 */
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Override
    public boolean incrementCurrentStudents(Long topicId) {
        // 使用数据库原子更新操作，避免并发问题
        return baseMapper.checkAndIncrement(topicId) > 0;
    }

    @Override
    public boolean decrementCurrentStudents(Long topicId) {
        // 使用数据库原子更新操作，避免并发问题
        return baseMapper.checkAndDecrement(topicId) > 0;
    }

}
