package com.exampe.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.sys.entity.SysRole;

import java.util.List;

public interface ISysRoleService extends IService<SysRole> {

    List<String> listRouteIdsByRoleId(String roleId);

    boolean saveRoleRoutes(String roleId, List<String> routeIds);
}
