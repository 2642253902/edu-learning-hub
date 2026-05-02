package com.exampe.auth.service.impl;

import com.exampe.auth.entity.auth.Account;
import com.exampe.auth.entity.user.AccountUser;
import com.exampe.auth.mapper.UserMapper;
import com.exampe.auth.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类，负责前端登录/注册/找回密码流程与后端认证存储协同。
 * <p>
 * 实现用户认证、注册、邮箱验证、密码重置等功能。
 *
 * @author admin
 */
@Service
public class AuthorizeServiceImpl implements AuthorizeService, UserDetailsService {

    @Resource
    UserMapper userMapper;

    /**
     * 加载用户详情用于认证
     *
     * @param username 用户名或邮箱
     * @return 用户详情对象
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        Account accountByNameOrEmail = userMapper.findAccountByNameOrEmail(username);
        if (accountByNameOrEmail == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return User
                .withUsername(accountByNameOrEmail.getUsername())
                .password(accountByNameOrEmail.getPassword())
                .roles(String.valueOf(accountByNameOrEmail.getRole()))
                .build();
    }

    @Resource
    MailSender mailSender;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    String fromEmail;


    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
        * 发送验证邮件，供前端注册与找回密码页面的验证码步骤调用。
     * <p>
     * 流程：
     * 1. 生成6位随机验证码
     * 2. 将验证码存储到 Redis，有效期3分钟
     * 3. 如果剩余时间低于2分钟可重新发送
     * 4. 发送邮件包含验证码
     * 5. 如果发送失败，删除 Redis 中的验证码
     *
     * @param email      邮箱地址
     * @param sessionId  会话ID
     * @param hasAccount 是否已有账户
     * @return 错误消息，成功返回 null
     */
    @Override
    public String sendValidateEmail(String email, String sessionId, boolean hasAccount) {
        String key = "email" + sessionId + ":" + email + ":" + (hasAccount);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            Long expire = Optional.ofNullable(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if (expire > 120) {
                return "请求过于频繁，请稍后再试";
            }
        }

        Account account = userMapper.findAccountByNameOrEmail(email);

        if (hasAccount && account == null) {
            return "该邮箱未注册";
        }
        if (!hasAccount && account != null) {
            return "该邮箱已被注册";
        }

        String format = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("验证码");
        message.setText("您的验证码是" + format + "，请在三分钟内使用");
        try {
            mailSender.send(message);
            stringRedisTemplate.opsForValue().set(key, format, 3, TimeUnit.MINUTES);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "邮件发送失败，请联系管理员";
        }
    }

    /**
        * 验证并注册用户，完成前端注册表单到后端账户落库的闭环。
     *
     * @param username  用户名
     * @param password  密码
     * @param email     邮箱
     * @param code      验证码
     * @param sessionId 会话ID
     * @return 错误消息，成功返回 null
     */
    @Override
    public String validateAndRegister(String username, String password, String email, String code, String sessionId) {
        String key = "email" + sessionId + ":" + email + ":" + "false";

        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey((key)))) {
            String string = stringRedisTemplate.opsForValue().get(key);

            Account accountByNameOrEmail = userMapper.findAccountByNameOrEmail(username);
            if (accountByNameOrEmail != null) {
                return "用户名已存在，请重新输入";
            }
            if (string == null) {
                stringRedisTemplate.delete(key);
                return "验证码已过期，请重新获取";
            }
            if (string.equals(code)) {
                password = passwordEncoder.encode(password);
                Account newAccount = new Account();
                newAccount.setUsername(username);
                newAccount.setPassword(password);
                newAccount.setEmail(email);
                newAccount.setRole("3");
                int result = userMapper.insert(newAccount);
                newAccount.setEmail(email);
                if (result > 0) {
                    return null;
                } else {
                    return "注册失败，请稍后再试";
                }
            } else {
                return "验证码错误，请重新输入";
            }
        } else {
            return "请先获取验证码";
        }
    }

    /**
        * 仅验证邮箱验证码（用于密码重置），供前端重置流程的验证码校验步骤使用。
     *
     * @param email     邮箱地址
     * @param code      验证码
     * @param sessionId 会话ID
     * @return 错误消息，成功返回 null
     */
    @Override
    public String validateOnly(String email, String code, String sessionId) {
        String key = "email" + sessionId + ":" + email + ":true";
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey((key)))) {
            String string = stringRedisTemplate.opsForValue().get(key);
            if (string == null) {
                return "验证码已过期，请重新获取";
            }
            if (string.equals(code)) {
                stringRedisTemplate.delete(key);
                return null;
            } else {
                return "验证码错误，请重新输入";
            }
        } else {
            return "请先获取验证码";
        }
    }

    /**
     * 重置密码
     *
     * @param email       邮箱地址
     * @param newPassword 新密码
     * @return 是否成功
     */
    @Override
    public boolean resetPassword(String email, String newPassword) {
        newPassword = passwordEncoder.encode(newPassword);
        return userMapper.restPasswordByEmail(email, newPassword) > 0;
    }

    /**
     * 查询所有教师用户
     *
     * @return 教师用户列表
     */
    @Override
    public List<AccountUser> getTeachers() {
        return userMapper.selectTeachers();
    }


}
