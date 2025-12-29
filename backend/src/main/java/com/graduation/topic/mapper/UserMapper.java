package com.graduation.topic.mapper;

import com.graduation.topic.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-01-01
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}