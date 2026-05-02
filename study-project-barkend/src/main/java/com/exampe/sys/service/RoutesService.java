package com.exampe.sys.service;

import com.exampe.sys.dto.RouteTreeDTO;
import com.exampe.sys.entity.Routes;

import java.util.List;

/**
 * 路由服务接口，供前端动态菜单加载与后端权限数据组织共用。
 */
public interface RoutesService {

    /**
     * 按角色查询路由并返回树形结构，供前端动态注入菜单。
     */
    List<RouteTreeDTO> getRoutesTree(Integer role);

    /**
     * 查询所有菜单并构建树，供前端后台菜单管理和授权页面使用。
     */
    List<RouteTreeDTO> getAllRoutesTree();

    List<Routes> listAllRoutes();

    boolean addRoute(Routes routes);

    boolean editRoute(Routes routes);

    boolean deleteRoute(String id);
}
