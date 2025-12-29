package com.graduation.topic.service;

import com.graduation.topic.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.topic.dto.LoginDTO;
import com.graduation.topic.dto.LoginResultDTO;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 
 * @since 2024-01-01
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     * @param loginDTO 登录请求
     * @return 登录结果
     */
    LoginResultDTO login(LoginDTO loginDTO);

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

}
