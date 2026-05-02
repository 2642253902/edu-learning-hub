package com.exampe.auth.dto;

import lombok.Data;

/**
 * 用户管理 DTO，供前端用户管理页面与后端用户服务传输统一字段。
 */
@Data
public class AccountDTO {
    /**
     * 用户ID。
     */
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

    /**
     * 角色中文描述，供前端表格直接展示。
     */
    private String roleDescription;
}
