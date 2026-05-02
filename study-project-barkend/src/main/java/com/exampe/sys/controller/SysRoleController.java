package com.exampe.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exampe.auth.entity.auth.Account;
import com.exampe.auth.mapper.UserMapper;
import com.exampe.common.RestBean;
import com.exampe.sys.dto.RoleGrantDTO;
import com.exampe.sys.entity.SysRole;
import com.exampe.sys.service.ISysRoleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统角色管理控制器，负责对接前端页面请求和后端业务逻辑。
 * <p>提供角色的增删改查、路由授权等功能</p>
 *
 * @author admin
 */
@RestController
@RequestMapping("/api/role")
public class SysRoleController {

    @Resource
    private ISysRoleService roleService;

    @Resource
    private UserMapper userMapper;

    /**
     * 查询角色列表
     *
     * @return 角色列表（按ID升序）
     */
    @GetMapping("/list")
    public RestBean<List<SysRole>> list() {
        return RestBean.success(roleService.list(new QueryWrapper<SysRole>().orderByAsc("id")));
    }

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public RestBean<String> add(@RequestBody SysRole role) {
        if (role.getName() == null || role.getName().isBlank()) {
            return RestBean.failure(400, "角色名称不能为空");
        }
        return roleService.save(role) ? RestBean.success("新增角色成功") : RestBean.failure(500, "新增角色失败");
    }

    /**
     * 编辑角色
     *
     * @param role 角色信息
     * @return 操作结果
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.POST, RequestMethod.PUT})
    public RestBean<String> edit(@RequestBody SysRole role) {
        if (role.getId() == null || role.getId().isBlank()) {
            return RestBean.failure(400, "角色ID不能为空");
        }
        if (role.getName() == null || role.getName().isBlank()) {
            return RestBean.failure(400, "角色名称不能为空");
        }
        return roleService.updateById(role) ? RestBean.success("编辑角色成功") : RestBean.failure(500, "编辑角色失败");
    }

    /**
     * 删除角色
     * <p>检查是否有用户绑定该角色，有则不允许删除</p>
     *
     * @param id 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public RestBean<String> delete(@RequestParam("id") String id) {
        if ("1".equals(id)) {
            return RestBean.failure(400, "内置管理员角色不允许删除");
        }

        Integer roleCode;
        try {
            roleCode = Integer.valueOf(id);
        } catch (NumberFormatException ex) {
            roleCode = null;
        }

        if (roleCode != null) {
            Long bindCount = userMapper.selectCount(new QueryWrapper<Account>().eq("role", roleCode));
            if (bindCount != null && bindCount > 0) {
                return RestBean.failure(400, "该角色已分配用户，不能删除");
            }
        }

        roleService.saveRoleRoutes(id, List.of());
        return roleService.removeById(id) ? RestBean.success("删除角色成功") : RestBean.failure(500, "删除角色失败");
    }

    /**
     * 查询角色的路由权限
     *
     * @param roleId 角色ID
     * @return 路由ID列表
     */
    @GetMapping("/routes")
    public RestBean<List<String>> listRoleRoutes(@RequestParam("roleId") String roleId) {
        return RestBean.success(roleService.listRouteIdsByRoleId(roleId));
    }

    /**
     * 为角色授权路由
     *
     * @param dto 角色授权数据传输对象
     * @return 操作结果
     */
    @PostMapping("/grant")
    public RestBean<String> grantRoutes(@RequestBody RoleGrantDTO dto) {
        if (dto.getRoleId() == null || dto.getRoleId().isBlank()) {
            return RestBean.failure(400, "角色ID不能为空");
        }
        boolean saved = roleService.saveRoleRoutes(dto.getRoleId(), dto.getRouteIds());
        return saved ? RestBean.success("角色授权成功") : RestBean.failure(500, "角色授权失败");
    }
}
