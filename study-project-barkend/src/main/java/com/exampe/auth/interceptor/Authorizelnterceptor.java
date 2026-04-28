package com.exampe.auth.interceptor;


import com.exampe.auth.entity.user.AccountUser;
import com.exampe.auth.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 权限拦截器
 * <p>
 * 在请求处理前获取当前用户信息并保存到 Session 中
 *
 * @author admin
 */
@Component
public class Authorizelnterceptor implements HandlerInterceptor {

    @Resource
    UserMapper userMapper;

    /**
     * 请求预处理，获取用户信息并保存到 Session
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @return true 继续处理，false 中断处理
     * @throws Exception 处理异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取当前安全上下文
        SecurityContext context = SecurityContextHolder.getContext();
        // 获取认证信息
        Authentication authentication = context.getAuthentication();

        // 获取用户主体信息
        Object principal = authentication.getPrincipal();

        // 类型校验并提取用户信息
        if (principal instanceof User user) {
            String username = user.getUsername();
            // 根据用户名或邮箱查询用户详细信息
            AccountUser accountUser = userMapper.findAccountUserByNameOrEmail(username);
            // 将用户信息保存到 Session 中
            request.getSession().setAttribute("account", accountUser);
        }

        return true;
    }
}
