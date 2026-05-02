package com.exampe.auth.config;

import com.exampe.auth.interceptor.Authorizelnterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类，供前端接口请求链路与后端登录态拦截规则协同。
 * <p>
 * 负责注册 MVC 拦截器，让权限校验只作用在需要登录的业务请求上。
 *
 * @author admin
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Resource
    Authorizelnterceptor authorizelnterceptor;

    /**
        * 添加自定义拦截器，统一前端业务接口的会话注入与权限校验。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                // 统一注册权限拦截器
                .addInterceptor(authorizelnterceptor)
                // 默认拦截全部接口请求
                .addPathPatterns("/**")
                // 登录相关接口必须放行，否则无法进入认证流程
                .excludePathPatterns("/", "/api/auth/**");
    }
}
