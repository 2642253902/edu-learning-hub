package com.exampe.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exampe.auth.dto.AccountDTO;
import com.exampe.auth.entity.auth.Account;

/**
 * 用户管理服务接口，供前端用户管理页面与后端账户维护流程共用。
 */
public interface IAccountManageService {

    /**
     * 分页查询用户列表（管理员专用），供前端表格分页与筛选。
     */
    IPage<AccountDTO> listAccounts(int pageNo, int pageSize, String keyword);

    /**
     * 创建新用户（管理员专用），供前端新增用户弹窗提交。
     */
    boolean createAccount(Account account);

    /**
     * 编辑用户信息（管理员专用），供前端编辑页提交变更。
     */
    boolean editAccount( Account account );

    /**
     * 删除用户（管理员专用），供前端列表删除操作调用。
     */
    boolean deleteAccount(String id);

    /**
     * 重置用户密码（管理员专用），供前端管理页重置动作使用。
     */
    boolean resetPassword(String id, String newPassword);

    /**
     * 修改用户角色（管理员专用），供前端角色分配操作调用。
     */
    boolean changeUserRole(String id, String newRole);

    /**
     * 检查用户名是否唯一，供前端表单即时校验。
     */
    boolean isUsernameUnique(String username);

    /**
     * 检查邮箱是否唯一，供前端表单即时校验。
     */
    boolean isEmailUnique(String email);
}
