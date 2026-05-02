package com.exampe.auth.entity.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 账户实体类，供后端认证持久化与前端管理页面字段对齐共用。
 */
@Data
@TableName("sys_account")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 邮箱地址。
     */
    private String email;

    /**
     * 用户名。
     */
    private String username;

    /**
     * 密码（加密存储）。
     */
    private String password;

    /**
     * 角色ID。
     */
    private String role;
}
