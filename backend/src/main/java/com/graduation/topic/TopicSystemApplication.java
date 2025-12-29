package com.graduation.topic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 毕业设计选题系统启动类
 */
@SpringBootApplication
@MapperScan("com.graduation.topic.mapper")
public class TopicSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TopicSystemApplication.class, args);
    }

}
