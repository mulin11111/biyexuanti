package com.graduation.topic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graduation.topic.common.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT认证失败处理类
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Result<String> result = Result.fail(401, "认证失败，请重新登录");
        out.write(new ObjectMapper().writeValueAsString(result));
        out.flush();
        out.close();
    }

}