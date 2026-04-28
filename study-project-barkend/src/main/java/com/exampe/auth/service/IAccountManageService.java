package com.exampe.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exampe.auth.dto.AccountDTO;
import com.exampe.auth.entity.auth.Account;

/**
 * 用户管理服务接口
 */
public interface IAccountManageService {

    /**
     * 分页查询用户列表（管理员专用）
     */
    IPage<AccountDTO> listAccounts(int pageNo, int pageSize, String keyword);

    /**
     * 创建新用户（管理员专用）
     */
    boolean createAccount(Account account);

    /**
     * 编辑用户信息（管理员专用）
     */
    boolean editAccount( Account account );

    /**
     * 删除用户（管理员专用）
     */
    boolean deleteAccount(String id);

    /**
     * 重置用户密码（管理员专用）
     */
    boolean resetPassword(String id, String newPassword);

    /**
     * 修改用户角色（管理员专用）
     */
    boolean changeUserRole(String id, int newRole);

    /**
     * 检查用户名是否唯一
     */
    boolean isUsernameUnique(String username);

    /**
     * 检查邮箱是否唯一
     */
    boolean isEmailUnique(String email);
}
