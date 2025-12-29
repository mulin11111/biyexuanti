package com.graduation.topic.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT工具类
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    /**
     * 生成JWT token
     * @param username 用户名
     * @param role 角色
     * @return token
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                .compact();
    }

    /**
     * 从token中获取用户名
     * @param token token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * 从token中获取角色
     * @param token token
     * @return 角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.get("role");
    }

    /**
     * 验证token是否有效
     * @param token token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}