package com.exampe.auth.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exampe.auth.dto.AccountDTO;
import com.exampe.auth.entity.auth.Account;
import com.exampe.auth.entity.user.AccountUser;
import com.exampe.auth.service.IAccountManageService;
import com.exampe.common.RestBean;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * <p>
 * 所有接口都要求：
 * 1. 用户已登录
 * 2. 用户角色为管理员（role=1）
 *
 * @author admin
 */
@RestController
@RequestMapping("/api/user/manage")
public class AccountManageController {

    @Resource
    private IAccountManageService accountManageService;

    /**
     * 分页查询用户列表
     *
     * @param pageNo   页码
     * @param pageSize 每页数量
     * @param keyword  搜索关键词（用户名或邮箱）
     */
    @GetMapping("/list")
    public RestBean<IPage<AccountDTO>> listUsers(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            HttpSession session) {
        IPage<AccountDTO> page = accountManageService.listAccounts(pageNo, pageSize, keyword);
        return RestBean.success(page);
    }

    /**
     * 新增用户
     */
    @PostMapping("/add")
    public RestBean<String> addUser(@RequestBody Account account, HttpSession session) {

        // 基本验证
        if (account.getUsername() == null || account.getUsername().isBlank()) {
            return RestBean.failure(400, "用户名不能为空");
        }
        if (account.getEmail() == null || account.getEmail().isBlank()) {
            return RestBean.failure(400, "邮箱不能为空");
        }
        if (account.getPassword() == null || account.getPassword().isBlank()) {
            return RestBean.failure(400, "密码不能为空");
        }

        // 检查用户名唯一性
        if (!accountManageService.isUsernameUnique(account.getUsername())) {
            return RestBean.failure(400, "用户名已存在");
        }

        // 检查邮箱唯一性
        if (!accountManageService.isEmailUnique(account.getEmail())) {
            return RestBean.failure(400, "邮箱已被使用");
        }

        boolean success = accountManageService.createAccount(account);
        return success ? RestBean.success("用户创建成功") : RestBean.failure(500, "用户创建失败");
    }

    /**
     * 编辑用户信息
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.POST, RequestMethod.PUT})
    public RestBean<String> editUser(@RequestBody Account account, HttpSession session) {

        if (account.getId() == null || account.getId().isBlank()) {
            return RestBean.failure(400, "用户ID不能为空");
        }

        boolean success = accountManageService.editAccount(account);
        if (!success) {
            return RestBean.failure(400, "用户名或邮箱已存在，或用户不存在");
        }
        return RestBean.success("用户信息更新成功");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete")
    public RestBean<String> deleteUser(@RequestParam String id, HttpSession session) {

        if (id == null || id.isBlank()) {
            return RestBean.failure(400, "用户ID不能为空");
        }

        // 防止删除默认管理员
        if ("1".equals(id)) {
            return RestBean.failure(400, "不能删除默认管理员账户");
        }

        boolean success = accountManageService.deleteAccount(id);
        return success ? RestBean.success("用户删除成功") : RestBean.failure(500, "用户删除失败");
    }

    /**
     * 重置用户密码为默认密码（123456）
     */
    @PostMapping("/resetPassword")
    public RestBean<String> resetPassword(@RequestParam String id, HttpSession session) {

        if (id == null || id.isBlank()) {
            return RestBean.failure(400, "用户ID不能为空");
        }

        boolean success = accountManageService.resetPassword(id, "123456");
        return success ? RestBean.success("密码已重置为：123456") : RestBean.failure(500, "密码重置失败");
    }

    /**
     * 修改用户角色
     */
    @PostMapping("/changeRole")
    public RestBean<String> changeUserRole(@RequestParam String id, @RequestParam String role, HttpSession session) {

        if (role == null || role.isBlank()) {
            return RestBean.failure(400, "角色ID不能为空");
        }

        boolean success = accountManageService.changeUserRole(id, role);
        return success ? RestBean.success("用户角色更新成功") : RestBean.failure(500, "用户角色更新失败");
    }

    /**
     * 检查用户名是否唯一
     */
    @GetMapping("/checkUsername")
    public RestBean<Boolean> checkUsername(@RequestParam String username) {
        if (username == null || username.isBlank()) {
            return RestBean.success(false);
        }
        boolean unique = accountManageService.isUsernameUnique(username);
        return RestBean.success(unique);
    }

    /**
     * 检查邮箱是否唯一
     */
    @GetMapping("/checkEmail")
    public RestBean<Boolean> checkEmail(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            return RestBean.success(false);
        }
        boolean unique = accountManageService.isEmailUnique(email);
        return RestBean.success(unique);
    }
}
