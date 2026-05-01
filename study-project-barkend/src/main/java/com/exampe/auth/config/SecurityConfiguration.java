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
 * Spring Security 安全配置类。
 * <p>
 * 统一定义接口放行规则、登录与注销处理、CORS、记住我和异常响应，避免安全行为散落在各处。
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
            // 先明确白名单，再要求其余请求必须经过认证
                .authorizeHttpRequests(auth -> {
                // 首页和认证接口直接放行，避免登录前就被拦截
                    auth.requestMatchers("/", "/api/auth/**").permitAll();
                // 业务接口默认都需要登录态
                    auth.anyRequest().authenticated();
                })
            // 认证成功/失败都改为 JSON 输出，前后端分离场景更容易处理
                .formLogin(form -> form
                // 登录请求由后端统一处理
                        .loginProcessingUrl("/api/auth/login")
                // 登录成功时返回统一响应体
                        .successHandler(this::onAuthenticationSuccess)
                // 登录失败时返回统一错误响应
                        .failureHandler(this::onAuthenticationFailure))
            // 注销也复用同一套成功响应，保持接口返回风格一致
                .logout(logout -> logout
                // 注销请求路径
                        .logoutUrl("/api/auth/logout")
                // 注销后仍返回 JSON，便于前端直接提示并清理状态
                        .logoutSuccessHandler(this::onAuthenticationSuccess))
            // 记住我功能用于延长登录态，适合教学平台这类长时间浏览场景
                .rememberMe(remember -> remember
                // 前端提交的勾选参数名
                        .rememberMeParameter("remember")
                // 记住我 token 有效期：3 天
                        .tokenValiditySeconds(3 * 24 * 60 * 60)
                        .tokenRepository(this.tokenRepository()))
            // 当前接口以 JSON API 为主，先关闭 CSRF，减少前后端联调阻力
                .csrf(AbstractHttpConfigurer::disable)
            // 允许前端跨域访问登录态接口
                .cors(cors -> cors
                        .configurationSource(this.corsConfigurationSource()))
            // 未认证访问时也返回统一 JSON，而不是默认跳转页面
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
        // 生产环境建议提前建表，避免应用启动时自动改库结构
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
        // 这里保持宽松配置，便于本地开发；上线时应收紧到具体前端域名
        config.addAllowedOriginPattern("*");
        // 开发阶段直接放开常见方法，避免因为预检请求影响联调
        config.addAllowedMethod("*");
        // 允许携带自定义请求头和认证相关头部
        config.addAllowedHeader("*");
        // 需要携带 Cookie 才能支持登录态和 remember-me
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
        // 将自定义用户查询服务注册到认证管理器中，登录时会走这里校验账号密码
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
        // 使用 BCrypt 存储密码摘要，避免明文或弱散列
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
        // 登录和注销共用同一个成功处理器，通过请求路径区分响应文案
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
        // 统一返回 401，前端可直接据此判断为未登录或认证失败
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, exception.getMessage())));
    }

}
