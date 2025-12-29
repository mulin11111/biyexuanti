package com.graduation.topic.mapper;

import com.graduation.topic.entity.Topic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 选题表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-01-01
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

    /**
     * 检查并递增当前学生人数
     * @param id 课题ID
     * @return 影响行数，如果为0则表示条件不满足（已满或不存在）
     */
    @Update("UPDATE topic SET current_students = current_students + 1 WHERE id = #{id} AND current_students < max_students")
    int checkAndIncrement(Long id);
    
    /**
     * 检查并递减当前学生人数
     * @param id 课题ID
     * @return 影响行数，如果为0则表示条件不满足（已为0或不存在）
     */
    @Update("UPDATE topic SET current_students = current_students - 1 WHERE id = #{id} AND current_students > 0")
    int checkAndDecrement(Long id);

}