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

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    AuthorizeService authorizeService;

    @GetMapping("/me")
    public RestBean<AccountUser> me(@SessionAttribute("account") AccountUser accountUser) {
        return RestBean.success(accountUser);
    }

    //查询教师列表
    @GetMapping("/list/teachers")
    public RestBean<List<AccountUser>> LIst() {
        List<AccountUser> teachers = authorizeService.getTeachers();
        return RestBean.success(teachers);

    }


}
