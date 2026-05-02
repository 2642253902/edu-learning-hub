package com.exampe.auth.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户实体类，供后端 Session 存储与前端登录态展示共用。
 */
@Data
@TableName("sys_account")
public class AccountUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户名。
     */
    private String username;

    /**
     * 邮箱地址。
     */
    private String email;

    /**
     * 角色ID。
     */
    private String role;
}
