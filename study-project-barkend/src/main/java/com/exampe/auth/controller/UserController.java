package com.exampe.auth.controller;

import com.exampe.auth.entity.user.AccountUser;
import com.exampe.auth.service.AuthorizeService;
import com.exampe.common.RestBean;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

/**
 * 用户信息控制器
 * <p>
 * 提供当前登录用户信息查询及教师列表查询功能
 *
 * @author admin
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    AuthorizeService authorizeService;

    /**
     * 获取当前登录用户信息
     *
     * @param accountUser 从Session中获取的当前登录用户对象
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public RestBean<AccountUser> me(@SessionAttribute("account") AccountUser accountUser) {
        return RestBean.success(accountUser);
    }

    /**
     * 查询教师列表
     *
     * @return 所有教师用户列表
     */
    @GetMapping("/list/teachers")
    public RestBean<List<AccountUser>> LIst() {
        List<AccountUser> teachers = authorizeService.getTeachers();
        return RestBean.success(teachers);

    }


}
