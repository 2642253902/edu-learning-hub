package com.exampe.auth.controller;

import com.exampe.auth.service.AuthorizeService;
import com.exampe.common.RestBean;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器，统一为前端登录、注册和找回密码页面提供接口。
 */
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    private final String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$";
    private final String usernameRegex = "^[A-Za-z0-9\\p{IsHan}]+$";


    @Resource
    AuthorizeService authorizeService;


    /**
     * 发送注册邮箱验证码，供前端注册页使用。
     *
     * @param email   用户邮箱地址
     * @param session HTTP会话
     * @return 发送结果提示
     */
    @PostMapping("/validate-register-email")
    public RestBean<String> validateRegisterEmail(@Pattern(regexp = emailRegex) @RequestParam("email") String email, HttpSession session) {
        String string = authorizeService.sendValidateEmail(email, session.getId(), false);
        if (string == null) {
            return RestBean.success("邮件发送成功，请查收");
        } else {
            return RestBean.failure(400, string);
        }
    }


    /**
     * 发送密码重置邮箱验证码，供前端找回密码页使用。
     *
     * @param email   用户邮箱地址
     * @param session HTTP会话
     * @return 发送结果提示
     */
    @PostMapping("/validate-reset-email")
    public RestBean<String> validateRestEmail(@Pattern(regexp = emailRegex) @RequestParam("email") String email, HttpSession session) {

        String string = authorizeService.sendValidateEmail(email, session.getId(), true);
        if (string == null) {
            return RestBean.success("邮件发送成功，请查收");
        } else {
            return RestBean.failure(400, string);
        }
    }


    /**
     * 用户注册，供前端注册页提交账号信息。
     *
     * @param username 用户名（3-8位，支持字母、数字、中文）
     * @param password 密码（6-16位）
     * @param email    邮箱地址
     * @param code     邮箱验证码（6位）
     * @param session  HTTP会话
     * @return 注册结果提示
     */
    @PostMapping("/register")
    public RestBean<String> register(
            @Pattern(regexp = usernameRegex) @Length(min = 3, max = 8) @RequestParam("username") String username,
            @Length(min = 6, max = 16) @RequestParam("password") String password,
            @Pattern(regexp = emailRegex) @RequestParam("email") String email,
            @Length(min = 6, max = 6) @RequestParam("code") String code,
            HttpSession session) {

        String string = authorizeService.validateAndRegister(username, password, email, code, session.getId());
        if (string == null) {
            return RestBean.success("注册成功，请登录");
        } else {
            return RestBean.failure(400, string);
        }
    }

    /**
     * 开始密码重置流程，先校验邮箱验证码再允许前端进入重置步骤。
     *
     * @param email   用户邮箱地址
     * @param code    邮箱验证码（6位）
     * @param session HTTP会话
     * @return 验证结果提示
     */
    @PostMapping("/start-reset")
    public RestBean<String> startReset(@Pattern(regexp = emailRegex)
                                       @RequestParam("email") String email, @Length(min = 6, max = 6) @RequestParam("code") String code,
                                       HttpSession session) {

        String string = authorizeService.validateOnly(email, code, session.getId());
        if (string == null) {
            session.setAttribute("rest-password", email);
            return RestBean.success("验证成功，请继续重置密码");
        } else {
            return RestBean.failure(400, string);
        }
    }

    /**
     * 执行密码重置，供前端找回密码页提交新密码。
     *
     * @param password 新密码（6-16位）
     * @param session  HTTP会话
     * @return 重置结果提示
     */
    @PostMapping("/do-reset")
    public RestBean<String> resetPassword(@Length(min = 6, max = 16) @RequestParam("password") String password, HttpSession session) {
        String email = (String) session.getAttribute("rest-password");
        if (email == null) {
            return RestBean.failure(400, "请先验证邮箱");
        } else if (authorizeService.resetPassword(email, password)) {
            session.removeAttribute("rest-password");
            return RestBean.success("密码重置成功，请使用新密码登录");
        } else {
            return RestBean.failure(400, "重置密码失败，请稍后再试");
        }
    }

}
