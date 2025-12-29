package com.graduation.topic.service;

import com.graduation.topic.entity.Progress;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 进度表 服务类
 * </p>
 *
 * @author 
 * @since 2024-01-01
 */
public interface ProgressService extends IService<Progress> {

    /**
     * 获取用户的课题进度列表
     * @param userId 用户ID
     * @return 进度列表
     */
    List<Progress> getProgressByUserId(Long userId);

    /**
     * 获取用户的特定课题进度列表
     * @param userId 用户ID
     * @param topicId 课题ID
     * @return 进度列表
     */
    List<Progress> getProgressByUserIdAndTopicId(Long userId, Long topicId);

    /**
     * 提交课题进度
     * @param progress 进度信息
     * @return 是否成功
     */
    boolean submitProgress(Progress progress);

    /**
     * 更新进度状态
     * @param progressId 进度ID
     * @param status 状态
     * @param teacherComment 教师评语
     * @return 是否成功
     */
    boolean updateProgressStatus(Long progressId, String status, String teacherComment);

}