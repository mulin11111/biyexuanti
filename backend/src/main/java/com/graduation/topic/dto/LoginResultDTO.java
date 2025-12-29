package com.graduation.topic.dto;

import lombok.Data;

/**
 * 登录结果DTO
 */
@Data
public class LoginResultDTO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色
     */
    private String role;

    /**
     * 访问令牌
     */
    private String token;

}