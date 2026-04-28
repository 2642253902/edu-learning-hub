package com.exampe.auth.dto;

import lombok.Data;

/**
 * 用户管理 DTO
 * <p>
 * 用于管理员操作用户信息时传输数据，包含角色描述信息
 *
 * @author admin
 */
@Data
public class AccountDTO {
    /**
     * 用户ID
     */
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
    private int role;

    /**
     * 角色中文描述
     */
    private String roleDescription;
}
