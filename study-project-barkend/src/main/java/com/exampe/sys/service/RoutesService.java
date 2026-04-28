package com.exampe.sys.service;

import com.exampe.sys.dto.RouteTreeDTO;
import com.exampe.sys.entity.Routes;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 26422
 * @since 2026-04-26
 */
public interface RoutesService {

    /**
     * 查询 routes 表并返回树形结构。
     */
    List<RouteTreeDTO> getRoutesTree(Integer role);

    /**
     * 查询所有菜单并构建树（用于后台菜单管理/授权）。
     */
    List<RouteTreeDTO> getAllRoutesTree();

    List<Routes> listAllRoutes();

    boolean addRoute(Routes routes);

    boolean editRoute(Routes routes);

    boolean deleteRoute(String id);
}
