package com.graduation.topic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.topic.entity.Progress;
import com.graduation.topic.mapper.ProgressMapper;
import com.graduation.topic.service.ProgressService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 进度表 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-01-01
 */
@Service
public class ProgressServiceImpl extends ServiceImpl<ProgressMapper, Progress> implements ProgressService {

    @Override
    public List<Progress> getProgressByUserId(Long userId) {
        QueryWrapper<Progress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("progress_date");
        return list(queryWrapper);
    }

    @Override
    public List<Progress> getProgressByUserIdAndTopicId(Long userId, Long topicId) {
        QueryWrapper<Progress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("topic_id", topicId)
                .orderByDesc("progress_date");
        return list(queryWrapper);
    }

    @Override
    public boolean submitProgress(Progress progress) {
        // 设置默认状态为待审核
        progress.setStatus("pending");
        return save(progress);
    }

    @Override
    public boolean updateProgressStatus(Long progressId, String status, String teacherComment) {
        Progress progress = getById(progressId);
        if (progress == null) {
            return false;
        }
        progress.setStatus(status);
        progress.setTeacherComment(teacherComment);
        return updateById(progress);
    }

}
