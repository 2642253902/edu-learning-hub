package com.exampe.auth.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 账户用户实体类
 * <p>
 * 对应数据库表 sys_account，用于在 Session 中存储登录用户信息
 * 不包含敏感字段（如密码）
 *
 * @author admin
 */
@Data
@TableName("sys_account")
public class AccountUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 角色ID
     */
    private String role;
}
