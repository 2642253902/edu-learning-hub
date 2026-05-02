package com.exampe.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.sys.entity.Routes;
import com.exampe.sys.mapper.RoutesMapper;
import com.exampe.sys.dto.RouteTreeDTO;
import com.exampe.sys.service.RoutesService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 路由服务实现类，负责把后端路由记录整理成前端可直接使用的树形菜单。
 */
@Service
public class RoutesServiceImpl extends ServiceImpl<RoutesMapper, Routes> implements RoutesService {

    @Resource
    RoutesMapper routesMapper;

    /**
     * 查询 routes 表并返回树形结构，供前端动态菜单和权限页直接渲染。
     */
    @Override
    public List<RouteTreeDTO> getRoutesTree(Integer role) {

        List<Routes> routes;
        if (role == null) {
            routes = routesMapper.selectAllOrdered();
        } else {
            routes = routesMapper.selectAllOrderedByRole(String.valueOf(role));
        }
        // 若为空，直接返回空集合，避免前端菜单树渲染出空指针。
        if (routes.isEmpty()) {
            return new ArrayList<>();
        }

        // 按 parentId 分组，null 的 parentId 当作 "0" 处理，方便前端还原层级结构。
        Map<String, List<Routes>> childrenMap = routes.stream()
                .collect(Collectors.groupingBy(route -> route.getParentId() == null ? "0" : route.getParentId()));

        // 递归构建并返回以 "0" 为根的路由树。
        return buildTree(childrenMap, "0");
    }

    @Override
    public List<RouteTreeDTO> getAllRoutesTree() {
        return getRoutesTree(null);
    }

    @Override
    public List<Routes> listAllRoutes() {
        return routesMapper.selectAllOrdered();
    }

    @Override
    public boolean addRoute(Routes routes) {
        normalizeDefaultFields(routes);
        return save(routes);
    }

    @Override
    public boolean editRoute(Routes routes) {
        normalizeDefaultFields(routes);
        return updateById(routes);
    }

    @Override
    public boolean deleteRoute(String id) {
        return removeById(id);
    }

    /**
     * 递归构建树节点，供前端按层级展示菜单和权限树。
     *
     * @param childrenMap 已经按 parentId 分组的原始记录集合
     * @param parentId    当前要构建子节点的父节点 id（String）
     * @return 当前 parentId 下的 RouteTreeDTO 列表（已包含子节点）
     */
    private List<RouteTreeDTO> buildTree(Map<String, List<Routes>> childrenMap, String parentId) {
        return childrenMap.getOrDefault(parentId, new ArrayList<>())
                // 先排序：按照 sort 字段，再按 id，保证前端菜单顺序稳定。
                .stream()
                .sorted(Comparator.comparingInt(this::safeSort).thenComparing(route -> route.getId() == null ? "~" : route.getId()))
                // 映射为 RouteTreeDTO，同时递归构建 children。
                .map(route -> {
                    // 当前节点 id。
                    String currentId = route.getId();
                    // 递归构建 children（如果当前 id 为 null，就传 "-1" 避免无限递归）。
                    List<RouteTreeDTO> children = buildTree(childrenMap, currentId == null ? "-1" : currentId);

                    // 把实体 Routes 映射为 DTO，保持和前端菜单/权限视图字段对齐。
                    return new RouteTreeDTO(
                            route.getId(),
                            route.getTitle(),
                            route.getPath(),
                            route.getParentId() == null ? "0" : route.getParentId(),
                            parseInteger(route.getLevel()),
                            route.getRemark(),
                            parseInteger(route.getSort()),
                            route.getMenuVisible(),
                            children
                    );
                })
                .collect(Collectors.toList());
    }

    private void normalizeDefaultFields(Routes routes) {
        if (!StringUtils.hasText(routes.getParentId())) {
            routes.setParentId("0");
        }
        if (!StringUtils.hasText(routes.getLevel())) {
            routes.setLevel("1");
        }
        if (!StringUtils.hasText(routes.getSort())) {
            routes.setSort("999");
        }
        if (routes.getMenuVisible() == null) {
            routes.setMenuVisible(1);
        }
    }

    /**
     * safeSort 用于排序时把 sort 字段解析为整数，保证前端看到的顺序稳定。
     */
    private Integer safeSort(Routes route) {
        return parseInteger(route.getSort()) == null ? Integer.MAX_VALUE : parseInteger(route.getSort());
    }

    /**
     * 把字符串解析成 Integer，适配后端数据库里字符串存数字的旧字段。
     * 返回 null 表示无法解析或输入为空。
     */
    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
