package com.exampe.auth.entity.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 账户实体类
 * <p>
 * 对应数据库表 sys_account，用于认证和授权操作
 * 包含完整的账户信息（包括密码等敏感字段）
 *
 * @author admin
 */
@Data
@TableName("sys_account")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 角色ID
     */
    private String role;
}
