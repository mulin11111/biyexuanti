package com.graduation.topic.service;

import com.graduation.topic.entity.Topic;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 选题表 服务类
 * </p>
 *
 * @author 
 * @since 2024-01-01
 */
public interface TopicService extends IService<Topic> {

    /**
     * 增加选题的已选学生数
     * @param topicId 选题ID
     * @return 是否成功
     */
    boolean incrementCurrentStudents(Long topicId);

    /**
     * 减少选题的已选学生数
     * @param topicId 选题ID
     * @return 是否成功
     */
    boolean decrementCurrentStudents(Long topicId);

}
