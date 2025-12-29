package com.graduation.topic.service;

import com.graduation.topic.entity.UserTopic;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户选题关联表 服务类
 * </p>
 *
 * @author 
 * @since 2024-01-01
 */
public interface UserTopicService extends IService<UserTopic> {

    /**
     * 用户选择课题
     * @param userId 用户ID
     * @param topicId 课题ID
     * @return 是否成功
     */
    boolean selectTopic(Long userId, Long topicId);

    /**
     * 用户取消选择课题
     * @param userId 用户ID
     * @param topicId 课题ID
     * @return 是否成功
     */
    boolean cancelTopic(Long userId, Long topicId);

    /**
     * 检查用户是否已选择该课题
     * @param userId 用户ID
     * @param topicId 课题ID
     * @return 是否已选择
     */
    boolean checkUserTopic(Long userId, Long topicId);

    /**
     * 获取用户已选择的课题ID
     * @param userId 用户ID
     * @return 课题ID
     */
    Long getTopicIdByUserId(Long userId);

}
