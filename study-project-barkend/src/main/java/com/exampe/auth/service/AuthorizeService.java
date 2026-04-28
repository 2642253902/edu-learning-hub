package com.exampe.auth.service;

import com.exampe.auth.entity.user.AccountUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * 认证服务接口
 * <p>
 * 提供用户认证、注册、密码重置等功能
 *
 * @author admin
 */
public interface AuthorizeService extends UserDetailsService {

    /**
     * 发送验证邮件
     *
     * @param email      邮箱地址
     * @param sessionId  会话ID
     * @param hasAccount 是否已有账户（true:找回密码, false:注册）
     * @return 错误消息，成功返回 null
     */
    String sendValidateEmail(String email, String sessionId, boolean hasAccount);

    /**
     * 验证并注册用户
     *
     * @param username  用户名
     * @param password  密码
     * @param email     邮箱
     * @param code      验证码
     * @param sessionId 会话ID
     * @return 错误消息，成功返回 null
     */
    String validateAndRegister(String username, String password, String email, String code, String sessionId);

    /**
     * 仅验证邮箱（用于密码重置流程）
     *
     * @param email     邮箱地址
     * @param code      验证码
     * @param sessionId 会话ID
     * @return 错误消息，成功返回 null
     */
    String validateOnly(String email, String code, String sessionId);

    /**
     * 重置密码
     *
     * @param email       邮箱地址
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean resetPassword(String email, String newPassword);

    /**
     * 查询所有教师用户
     *
     * @return 教师用户列表
     */
    List<AccountUser> getTeachers();

}
