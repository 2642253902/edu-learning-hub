package com.exampe.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.sys.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口，供前端角色权限页面与后端角色授权流程共用。
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 查询角色已绑定的路由ID列表，供前端权限树回显。
     */
    List<String> listRouteIdsByRoleId(String roleId);

    /**
     * 保存角色与路由关系，支撑前端角色授权提交。
     */
    boolean saveRoleRoutes(String roleId, List<String> routeIds);
}
