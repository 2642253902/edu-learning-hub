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
 * <p>
 * 服务实现类：负责从数据库加载 routes（路由/菜单）并把它们组织成树形结构返回给前端。
 * 主要职责：
 * - 从 `sys_routes` 表加载路由记录（按 sort、id 排序）
 * - 把扁平列表按 parentId 分组
 * - 递归构建 RouteTreeDTO 的树形结构
 * </p>
 * <p>
 * 注意：本类只修改注释，不改变现有行为。
 *
 * @author 26422
 * @since 2026-04-26
 */
@Service
public class RoutesServiceImpl extends ServiceImpl<RoutesMapper, Routes> implements RoutesService {

    // 注：保留 mapper 引用以便未来需要直接调用自定义 SQL 时使用，因此标注 SuppressWarnings
    @Resource
    RoutesMapper routesMapper;

    /**
     * 查询 routes 表并返回树形结构。
     * <p>
     * 处理步骤说明：
     * 1. 使用 MyBatis-Plus 的 `list(...)` 方法查询所有 Routes，按 sort 和 id 升序（保证顺序稳定）。
     * 2. 如果没有数据，返回空列表。
     * 3. 将路由列表按 parentId 分组，得到一个 Map<parentId, List<Routes>>，方便按层级构建子树。
     * 4. 调用 `buildTree` 从父节点 id=0 开始递归构建树。
     * <p>
     * 返回值：List<RouteTreeDTO> — 顶层节点（parentId 为 0 的节点）组成的列表。
     */
    @Override
    public List<RouteTreeDTO> getRoutesTree(Integer role) {

        List<Routes> routes;
        if (role == null) {
            routes = routesMapper.selectAllOrdered();
        } else {
            routes = routesMapper.selectAllOrderedByRole(String.valueOf(role));
        }
        // 2) 若为空，直接返回空集合
        if (routes.isEmpty()) {
            return new ArrayList<>();
        }

        // 3) 按 parentId 分组，null 的 parentId 当作 "0" 处理
        // childrenMap 的 key 是 parentId（String），value 是以该 parentId 为父的所有 Routes
        Map<String, List<Routes>> childrenMap = routes.stream()
                .collect(Collectors.groupingBy(route -> route.getParentId() == null ? "0" : route.getParentId()));

        // 4) 递归构建并返回以 "0" 为根的路由树
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
     * 递归构建树节点：
     * - 使用 childrenMap 获取当前 parentId 下的所有 Routes
     * - 对这些 Routes 做排序（先根据 sort 字段，其次根据 id）
     * - 把每个 Routes 转换为 RouteTreeDTO，同时递归构建其 children
     * <p>
     * 参数说明：
     *
     * @param childrenMap 已经按 parentId 分组的原始记录集合
     * @param parentId    当前要构建子节点的父节点 id（String）
     * @return 当前 parentId 下的 RouteTreeDTO 列表（已包含子节点）
     */
    private List<RouteTreeDTO> buildTree(Map<String, List<Routes>> childrenMap, String parentId) {
        return childrenMap.getOrDefault(parentId, new ArrayList<>())
                // 先排序：按照 safeSort（解析 sort 字段为整数，空或解析失败放到最后），再按 id（String 比较，null 放最后）
                .stream()
                .sorted(Comparator.comparingInt(this::safeSort).thenComparing(route -> route.getId() == null ? "~" : route.getId()))
                // 映射为 RouteTreeDTO，同时递归构建 children
                .map(route -> {
                    // 当前节点 id
                    String currentId = route.getId();
                    // 递归构建 children（如果当前 id 为 null，就传 "-1" 避免无限递归）
                    List<RouteTreeDTO> children = buildTree(childrenMap, currentId == null ? "-1" : currentId);

                    // 把实体 Routes 映射为 DTO：注意类型转换与字段对齐
                    // - id: String
                    // - title -> name（前端展示名）
                    // - parentId: String
                    // - level, sort 字段原来为字符串，使用 parseInteger 转成 Integer
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
     * safeSort 用于排序时把 sort 字段解析为整数：
     * - 如果 parseInteger 返回 null（表示空或不可解析），则把排序权重设为 Integer.MAX_VALUE（放到最后）
     */
    private Integer safeSort(Routes route) {
        return parseInteger(route.getSort()) == null ? Integer.MAX_VALUE : parseInteger(route.getSort());
    }

    /**
     * 把字符串解析成 Integer。适配数据库中 string 类型保存的数字或空值。
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
