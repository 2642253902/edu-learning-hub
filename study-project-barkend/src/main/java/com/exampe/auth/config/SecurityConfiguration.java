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
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
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
 * Spring Security 安全配置类，统一约束前端登录态、接口权限和跨域访问规则。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Resource
    AuthorizeService authorizeService;

    @Resource
    DataSource dataSource;


    /**
     * 配置安全过滤链，定义前后端联调用到的认证、注销、跨域和异常返回规则。
     *
     * @param httpSecurity HTTP 安全配置对象
     * @return 安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            // 先放行静态入口和认证接口，再对其余请求统一要求登录态，避免前端初始化就被拦截。
                .authorizeHttpRequests(auth -> {
                    // 首页、登录、聊天流接口直接开放，前端才能在未登录或首次进入时完成初始化。
                    auth.requestMatchers("/", "/api/auth/**", "/api/chat/**").permitAll();
                    // 允许跨域预检请求通过，否则浏览器会在真正请求前就被 CORS/OPTIONS 卡住。
                    auth.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll();
                    // 其余业务接口默认都需要认证，保持安全边界清晰。
                    auth.anyRequest().authenticated();
                })
            // 使用表单登录能力，但把登录处理结果改成 JSON，方便前端按统一响应体处理。
                .formLogin(form -> form
                // 登录请求由 Spring Security 拦截处理，不需要单独写登录 Controller。
                        .loginProcessingUrl("/api/auth/login")
                // 登录成功时返回统一 JSON，前端可直接提示并刷新用户状态。
                        .successHandler(this::onAuthenticationSuccess)
                // 登录失败时也返回 JSON，避免前端还要解析默认跳转页。
                        .failureHandler(this::onAuthenticationFailure))
            // 注销同样走统一 JSON 返回，前端清理状态和提示文案时更简单。
                .logout(logout -> logout
                // 注销请求路径与前端接口保持一致。
                        .logoutUrl("/api/auth/logout")
                // 注销后仍复用成功处理器，统一返回 JSON。
                        .logoutSuccessHandler(this::onAuthenticationSuccess))
            // remember-me 用于延长登录态，适合长时间浏览课程、资源和讨论区的场景。
                .rememberMe(remember -> remember
                // 与前端勾选框字段保持一致，只有勾选后才会签发 remember-me token。
                        .rememberMeParameter("remember")
                // 记住我 token 有效期设为 3 天，兼顾体验和安全性。
                        .tokenValiditySeconds(3 * 24 * 60 * 60)
                        .tokenRepository(this.tokenRepository()))
            // 当前接口以 JSON API 为主，这里先关闭 CSRF，降低前后端联调门槛。
                .csrf(AbstractHttpConfigurer::disable)
            // 允许前端跨域访问，Cookie 登录态和 remember-me 都依赖这里正确放行。
                .cors(cors -> cors
                        .configurationSource(this.corsConfigurationSource()))
            // 未认证时不要返回默认 HTML 登录页，直接返回统一 JSON 给前端处理。
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(this::onAuthenticationFailure))
                .build();
    }

    /**
     * 配置记住我功能的 Token 存储库。
     *
     * @return PersistentTokenRepository 实例
     */
    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
                // 生产环境通常提前建好 remember-me 相关表，避免启动时自动修改数据库结构。
        tokenRepository.setCreateTableOnStartup(false);
        return tokenRepository;
    }

    /**
         * 配置 CORS 跨域资源共享规则。
         *
         * 这里偏向开发期的宽松策略，生产环境应收紧到具体前端域名，并结合实际 Cookie 策略调整。
     *
     * @return CORS 配置源
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
                // 开发阶段先放开来源，便于本地前后端联调；上线后要改为明确域名白名单。
        config.addAllowedOriginPattern("*");
                // 放开常见 HTTP 方法，避免预检请求阻断接口调用。
        config.addAllowedMethod("*");
                // 放开请求头，兼容自定义头部和认证相关头部传递。
        config.addAllowedHeader("*");
                // 允许携带 Cookie，才能支持基于会话的登录态和 remember-me。
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    /**
     * 配置认证管理器。
     *
     * @param httpSecurity HTTP 安全配置对象
     * @return AuthenticationManager 实例
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder builder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
                // 把自定义用户查询服务挂到认证链路上，登录时会通过这里校验账号和密码。
        builder.userDetailsService(authorizeService);
        return builder.build();
    }


    /**
      * 配置密码编码器，使用 BCrypt 算法加密密码。
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
                // 统一使用 BCrypt 存储密码摘要，避免明文或弱散列方案。
        return new BCryptPasswordEncoder();
    }

    /**
      * 认证成功处理器，返回 JSON 格式响应。
     *
     * @param request        HTTP 请求
     * @param response       HTTP 响应
     * @param authentication 认证信息
     * @throws IOException IO 异常
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setCharacterEncoding("UTF-8");
        // 登录和注销共用同一处理器，通过请求路径区分成功文案即可。
        if (request.getRequestURI().equals("/api/auth/logout")) {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("注销成功")));
        } else {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功")));
        }
    }

    /**
     * 认证失败处理器，返回 JSON 格式错误响应。
     *
     * @param request   HTTP 请求
     * @param response  HTTP 响应
     * @param exception 认证异常
     * @throws IOException IO 异常
     */
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setCharacterEncoding("UTF-8");
        // 统一返回 401，前端可以据此区分未登录、密码错误或会话失效等场景。
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, exception.getMessage())));
    }

}
