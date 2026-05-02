package com.exampe.sys.dto;

import lombok.Data;

import java.util.List;

/**
 * 角色授权数据传输对象，供前端角色授权弹窗与后端授权保存接口共用。
 * <p>用于角色路由授权时传输数据。</p>
 *
 * @author admin
 */
@Data
public class RoleGrantDTO {
    /**
     * 角色ID，来自前端当前授权角色。
     */
    private String roleId;

    /**
     * 路由ID列表，来自前端权限树勾选结果。
     */
    private List<String> routeIds;
}
