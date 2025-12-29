package com.graduation.topic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.topic.entity.User;
import com.graduation.topic.mapper.UserMapper;
import com.graduation.topic.service.UserService;
import com.graduation.topic.dto.LoginDTO;
import com.graduation.topic.dto.LoginResultDTO;
import com.graduation.topic.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-01-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public LoginResultDTO login(LoginDTO loginDTO) {
        // 根据用户名查询用户
        User user = getUserByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已禁用");
        }

        // 生成JWT token
        String token = jwtUtils.generateToken(user.getUsername(), user.getRole());

        // 构建登录结果
        LoginResultDTO result = new LoginResultDTO();
        result.setId(user.getId());
        result.setUsername(user.getUsername());
        result.setRealName(user.getRealName());
        result.setEmail(user.getEmail());
        result.setRole(user.getRole());
        result.setToken(token);

        return result;
    }

    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return baseMapper.selectOne(queryWrapper);
    }

}