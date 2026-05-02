package com.exampe.sys.controller;

import com.exampe.sys.dto.RouteTreeDTO;
import com.exampe.common.RestBean;
import com.exampe.sys.entity.Routes;
import com.exampe.sys.service.RoutesService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 路由菜单管理控制器，统一为前端动态菜单和后端权限树提供接口。
 */
@RestController
@RequestMapping("/api/routes")
public class RoutesController {

    @Resource
    private RoutesService routesService;

    /**
     * 获取指定角色的路由树，供前端动态注入可访问菜单。
     *
     * @param role 角色ID
     * @return 路由树列表
     */
    @PostMapping(value = "/tree")
    public RestBean<List<RouteTreeDTO>> getRoutesTree(@RequestParam("role") Integer role) {
        List<RouteTreeDTO> routesTree = routesService.getRoutesTree(role);
        return RestBean.success(routesTree);
    }

    /**
     * 获取所有路由树，供前端管理页查看完整权限结构。
     *
     * @return 完整路由树列表
     */
    @GetMapping(value = "/allTree")
    public RestBean<List<RouteTreeDTO>> getAllRoutesTree() {
        return RestBean.success(routesService.getAllRoutesTree());
    }

    /**
     * 查询所有路由列表，供前端表格和树形编辑共用。
     *
     * @return 路由列表（扁平结构）
     */
    @GetMapping(value = "/list")
    public RestBean<List<Routes>> list() {
        return RestBean.success(routesService.listAllRoutes());
    }

    /**
     * 新增路由菜单，供前端权限配置页提交。
     *
     * @param routes 路由信息
     * @return 操作结果
     */
    @PostMapping(value = "/add")
    public RestBean<String> add(@RequestBody Routes routes) {
        return routesService.addRoute(routes) ? RestBean.success("新增菜单成功") : RestBean.failure(500, "新增菜单失败");
    }

    /**
     * 编辑路由菜单，供前端权限配置页同步修改。
     *
     * @param routes 路由信息
     * @return 操作结果
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public RestBean<String> edit(@RequestBody Routes routes) {
        return routesService.editRoute(routes) ? RestBean.success("编辑菜单成功") : RestBean.failure(500, "编辑菜单失败");
    }

    /**
     * 删除路由菜单，供前端权限配置页同步删除。
     *
     * @param id 路由ID
     * @return 操作结果
     */
    @DeleteMapping(value = "/delete")
    public RestBean<String> delete(@RequestParam("id") String id) {
        return routesService.deleteRoute(id) ? RestBean.success("删除菜单成功") : RestBean.failure(500, "删除菜单失败");
    }


}
