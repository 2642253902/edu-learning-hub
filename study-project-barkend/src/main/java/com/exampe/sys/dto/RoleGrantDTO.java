package com.exampe.sys.dto;

import lombok.Data;

import java.util.List;

/**
 * 角色授权数据传输对象
 * <p>用于角色路由授权时传输数据</p>
 *
 * @author admin
 */
@Data
public class RoleGrantDTO {
    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 路由ID列表
     */
    private List<String> routeIds;
}
