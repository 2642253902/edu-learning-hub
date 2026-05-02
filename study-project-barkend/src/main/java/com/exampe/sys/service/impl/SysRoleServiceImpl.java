package com.exampe.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.sys.entity.RoleRoute;
import com.exampe.sys.entity.SysRole;
import com.exampe.sys.mapper.RoleRouteMapper;
import com.exampe.sys.mapper.SysRoleMapper;
import com.exampe.sys.service.IRoleRouteService;
import com.exampe.sys.service.ISysRoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统角色服务实现类，负责前端角色授权页面与后端角色路由关系维护协同。
 * <p>实现 ISysRoleService 接口，提供角色管理和角色路由关联的业务逻辑。</p>
 * <p>主要功能：</p>
 * <ul>
 *   <li>查询角色关联的路由ID列表</li>
 *   <li>保存角色与路由的关联关系（支持事务）</li>
 * </ul>
 *
 * @author admin
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Resource
    private RoleRouteMapper roleRouteMapper;

    @Resource
    private IRoleRouteService roleRouteService;

    /**
     * 根据角色ID查询该角色关联的所有路由ID列表
     * <p>查询流程：</p>
     * <ol>
     *   <li>校验角色ID是否为空，为空则返回空列表</li>
     *   <li>从 sys_role_route 表中查询该角色的所有关联记录</li>
     *   <li>提取路由ID并过滤掉空值</li>
     * </ol>
     *
     * @param roleId 角色ID
     * @return 路由ID列表，如果角色ID为空或没有关联路由则返回空列表
     */
    @Override
    public List<String> listRouteIdsByRoleId(String roleId) {
        if (roleId == null || roleId.isBlank()) {
            return Collections.emptyList();
        }
        return roleRouteMapper.selectList(new QueryWrapper<RoleRoute>().eq("role_id", roleId))
                .stream()
                .map(RoleRoute::getRouteId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 保存角色与路由的关联关系
     * <p>处理流程：</p>
     * <ol>
     *   <li>校验角色ID是否为空，为空则返回 false</li>
     *   <li>删除该角色原有的所有路由关联记录</li>
     *   <li>如果新的路由ID列表为空，则直接返回 true（已完成删除）</li>
     *   <li>过滤并去重路由ID列表</li>
     *   <li>批量插入新的角色路由关联记录</li>
     * </ol>
     * <p>使用 @Transactional 注解保证事务一致性，任何异常都会导致回滚</p>
     *
     * @param roleId 角色ID
     * @param routeIds 要关联的路由ID列表
     * @return 是否保存成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRoleRoutes(String roleId, List<String> routeIds) {
        if (roleId == null || roleId.isBlank()) {
            return false;
        }
        // 删除该角色原有的所有路由关联
        roleRouteMapper.delete(new QueryWrapper<RoleRoute>().eq("role_id", roleId));

        // 如果新的路由列表为空，直接返回（已完成删除操作）
        if (routeIds == null || routeIds.isEmpty()) {
            return true;
        }

        // 构建新的角色路由关联实体列表
        List<RoleRoute> entities = routeIds.stream()
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .map(routeId -> {
                    RoleRoute entity = new RoleRoute();
                    entity.setRoleId(roleId);
                    entity.setRouteId(routeId);
                    return entity;
                })
                .collect(Collectors.toList());

        // 批量保存新的关联关系
        return !entities.isEmpty() && roleRouteService.saveBatch(entities);
    }
}
