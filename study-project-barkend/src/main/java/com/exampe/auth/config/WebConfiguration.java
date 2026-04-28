package com.exampe.auth.config;

import com.exampe.auth.interceptor.Authorizelnterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * <p>
 * 配置拦截器、静态资源等 Web 相关功能
 *
 * @author admin
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Resource
    Authorizelnterceptor authorizelnterceptor;

    /**
     * 添加自定义拦截器
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                // 添加权限拦截器
                .addInterceptor(authorizelnterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除不需要拦截的路径
                .excludePathPatterns("/", "/api/auth/**");
    }
}
