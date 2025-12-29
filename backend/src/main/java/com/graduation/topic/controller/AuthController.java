package com.graduation.topic.controller;

import com.graduation.topic.common.Result;
import com.graduation.topic.dto.LoginDTO;
import com.graduation.topic.dto.LoginResultDTO;
import com.graduation.topic.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginResultDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoginResultDTO result = userService.login(loginDTO);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

}