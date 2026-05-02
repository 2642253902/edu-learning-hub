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
 * 用户信息控制器，供前端首页与业务页面获取当前登录态和教师列表。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    AuthorizeService authorizeService;

    /**
     * 获取当前登录用户信息，供前端初始化登录态。
     *
     * @param accountUser 从Session中获取的当前登录用户对象
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public RestBean<AccountUser> me(@SessionAttribute("account") AccountUser accountUser) {
        return RestBean.success(accountUser);
    }

    /**
     * 查询教师列表，供前端课程管理页面选择授课教师。
     *
     * @return 所有教师用户列表
     */
    @GetMapping("/list/teachers")
    public RestBean<List<AccountUser>> LIst() {
        List<AccountUser> teachers = authorizeService.getTeachers();
        return RestBean.success(teachers);

    }


}
