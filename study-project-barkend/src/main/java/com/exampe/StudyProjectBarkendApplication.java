package com.exampe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 学习平台后端应用启动入口，负责承载前端页面所有 API 请求。
 * <p>
 * 负责触发 Spring Boot 容器初始化，并扫描 {@code com.exampe} 及其子包下的组件、配置和控制器，
 * 为前后端联调提供统一服务上下文。
 */
@SpringBootApplication
public class StudyProjectBarkendApplication {

    /**
     * 启动应用。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(StudyProjectBarkendApplication.class, args);
    }

}
