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

@Component
public class Authorizelnterceptor implements HandlerInterceptor {

    @Resource
    UserMapper userMapper;

    @Override
    // 这里可以获取到当前的用户信息，进行权限校验
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取当前的用户信息
        SecurityContext context = SecurityContextHolder.getContext();
        // 获取当前的认证信息
        Authentication authentication = context.getAuthentication();

        // 类型转换校验
        Object principal = authentication.getPrincipal();   // 获取当前用户信息

        if (principal instanceof User user) {   // 这里使用了 Java 16 的模式匹配特性，直接在 instanceof 中声明一个变量 user，并且自动进行类型转换
            String username = user.getUsername();   // 获取用户名
            AccountUser accountUser = userMapper.findAccountUserByNameOrEmail(username);    // 根据用户名或邮箱查询用户信息
            request.getSession().setAttribute("account", accountUser);  // 将用户信息保存到 session 中
        }

        return true;
    }
}
