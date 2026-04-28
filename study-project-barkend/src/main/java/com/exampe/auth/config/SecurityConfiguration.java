package com.exampe.auth.config;

import com.alibaba.fastjson2.JSONObject;
import com.exampe.auth.service.AuthorizeService;
import com.exampe.common.RestBean;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Spring Security 安全配置类
 * <p>
 * 配置认证、授权、CORS、记住我等安全相关功能
 *
 * @author admin
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Resource
    AuthorizeService authorizeService;

    @Resource
    DataSource dataSource;


    /**
     * 配置安全过滤链，定义安全规则和行为
     *
     * @param httpSecurity HTTP 安全配置对象
     * @return 安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // 配置请求授权规则
                .authorizeHttpRequests(auth -> {
                    // 允许访问根路径和认证相关接口
                    auth.requestMatchers("/", "/api/auth/**").permitAll();
                    // 其他请求需要认证
                    auth.anyRequest().authenticated();
                })
                // 配置表单登录
                .formLogin(form -> form
                        // 指定登录处理 URL
                        .loginProcessingUrl("/api/auth/login")
                        // 认证成功处理器
                        .successHandler(this::onAuthenticationSuccess)
                        // 认证失败处理器
                        .failureHandler(this::onAuthenticationFailure))
                // 配置注销
                .logout(logout -> logout
                        // 指定注销 URL
                        .logoutUrl("/api/auth/logout")
                        // 注销成功处理器
                        .logoutSuccessHandler(this::onAuthenticationSuccess))
                // 配置记住我功能
                .rememberMe(remember -> remember
                        // 指定 remember-me 参数名称
                        .rememberMeParameter("remember")
                        // 设置 token 有效期（3天）
                        .tokenValiditySeconds(3 * 24 * 60 * 60)
                        .tokenRepository(this.tokenRepository()))
                // 禁用 CSRF 保护（后端 API 通常不需要）
                .csrf(AbstractHttpConfigurer::disable)
                // 配置 CORS 跨域规则
                .cors(cors -> cors
                        .configurationSource(this.corsConfigurationSource()))
                // 配置异常处理
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(this::onAuthenticationFailure))
                .build();
    }

    /**
     * 配置记住我功能的 Token 存储库
     *
     * @return PersistentTokenRepository 实例
     */
    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // 生产环境建议手动创建表，设置为 false
        tokenRepository.setCreateTableOnStartup(false);
        return tokenRepository;
    }

    /**
     * 配置 CORS 跨域资源共享规则
     *
     * @return CORS 配置源
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有来源（生产环境建议指定具体域名）
        config.addAllowedOriginPattern("*");
        // 允许所有 HTTP 方法
        config.addAllowedMethod("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许携带凭证（Cookies）
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    /**
     * 配置认证管理器
     *
     * @param httpSecurity HTTP 安全配置对象
     * @return AuthenticationManager 实例
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder builder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(authorizeService);
        return builder.build();
    }


    /**
     * 配置密码编码器，使用 BCrypt 算法加密密码
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证成功处理器，返回 JSON 格式响应
     *
     * @param request        HTTP 请求
     * @param response       HTTP 响应
     * @param authentication 认证信息
     * @throws IOException IO 异常
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setCharacterEncoding("UTF-8");
        if (request.getRequestURI().equals("/api/auth/logout")) {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("注销成功")));
        } else {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功")));
        }
    }

    /**
     * 认证失败处理器，返回 JSON 格式错误响应
     *
     * @param request   HTTP 请求
     * @param response  HTTP 响应
     * @param exception 认证异常
     * @throws IOException IO 异常
     */
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, exception.getMessage())));
    }

}
