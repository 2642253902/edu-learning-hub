package com.exampe.sys.controller;

import com.exampe.auth.dto.RouteTreeDTO;
import com.exampe.common.RestBean;
import com.exampe.sys.service.RoutesService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 26422
 * @since 2026-04-26
 */
@RestController
@RequestMapping("/api/routes")
public class RoutesController {

    @Resource
    private RoutesService routesService;

    //关联路由（如果是其他用户，通过sys_role_route表做区分）
    @PostMapping(value = "/tree")
    public RestBean<List<RouteTreeDTO>> getRoutesTree(@RequestParam("role") int role) {
        List<RouteTreeDTO> routesTree = routesService.getRoutesTree(role);
        return RestBean.success(routesTree);
    }


}
