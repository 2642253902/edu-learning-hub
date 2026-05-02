package com.exampe.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exampe.auth.dto.AccountDTO;
import com.exampe.auth.entity.auth.Account;
import com.exampe.auth.mapper.UserMapper;
import com.exampe.auth.service.IAccountManageService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户管理服务实现类，负责对接前端用户管理页面与后端账户持久化逻辑。
 * <p>
 * 提供：用户增删改查、密码重置、角色分配等功能。
 *
 * @author admin
 */
@Service
public class AccountManageServiceImpl implements IAccountManageService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表
     *
     * @param pageNo   页码（从1开始）
     * @param pageSize 每页数量
     * @param keyword  搜索关键词（用户名或邮箱）
     * @return 分页用户数据，包含角色描述
     */
    @Override
    public IPage<AccountDTO> listAccounts(int pageNo, int pageSize, String keyword) {
        Page<Account> page = new Page<>(pageNo, pageSize);
        QueryWrapper<Account> wrapper = new QueryWrapper<>();

        // 支持按用户名或邮箱模糊查询
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like("username", keyword).or().like("email", keyword));
        }

        wrapper.orderByDesc("id");
        IPage<Account> accountPage = userMapper.selectPage(page, wrapper);

        return accountPage.convert(account -> {
            AccountDTO dto = new AccountDTO();
            dto.setId(account.getId());
            dto.setUsername(account.getUsername());
            dto.setEmail(account.getEmail());
            dto.setRole(account.getRole());
            dto.setRoleDescription((account.getRole()));
            return dto;
        });
    }

    /**
     * 创建新用户
     *
     * @param account 用户信息
     * @return 是否创建成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createAccount(Account account) {
        // 验证必填字段
        if (account.getUsername() == null || account.getUsername().isBlank()) {
            return false;
        }
        if (account.getEmail() == null || account.getEmail().isBlank()) {
            return false;
        }
        if (account.getPassword() == null || account.getPassword().isBlank()) {
            return false;
        }

        // 检查用户名唯一性
        if (!isUsernameUnique(account.getUsername())) {
            return false;
        }

        // 检查邮箱唯一性
        if (!isEmailUnique(account.getEmail())) {
            return false;
        }

        Account accountAdd = new Account();
        accountAdd.setUsername(account.getUsername());
        accountAdd.setEmail(account.getEmail());
        accountAdd.setPassword(passwordEncoder.encode(account.getPassword()));
        // 默认学生角色
        accountAdd.setRole("3");
        return userMapper.insert(account) > 0;
    }

    /**
     * 编辑用户信息
     *
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editAccount(Account account) {
        if (account.getId() == null || account.getId().isBlank()) {
            return false;
        }

        Account accountUpdate = userMapper.selectById(account.getId());
        if (accountUpdate == null) {
            return false;
        }

        // 修改用户名时检查唯一性
        if (account.getUsername() != null && !account.getUsername().isBlank()
                && !account.getUsername().equals(accountUpdate.getUsername())) {
            if (!isUsernameUnique(account.getUsername())) {
                return false;
            }
            accountUpdate.setUsername(account.getUsername());
        }

        // 修改邮箱时检查唯一性
        if (account.getEmail() != null && !account.getEmail().isBlank()
                && !account.getEmail().equals(accountUpdate.getEmail())) {
            if (!isEmailUnique(account.getEmail())) {
                return false;
            }
            accountUpdate.setEmail(account.getEmail());
        }

        // 密码不为空时更新密码
        if (account.getPassword() != null && !account.getPassword().isBlank()) {
            accountUpdate.setPassword(passwordEncoder.encode(account.getPassword()));
        }

        // 角色不为 0 时更新
        if ((account.getRole().toString()) != " 0") {
            accountUpdate.setRole(account.getRole());
        }

        return userMapper.updateById(accountUpdate) > 0;
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAccount(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }

        // 防止删除管理员账户（假设 id=1 是默认管理员）
        if ("1".equals(id)) {
            return false;
        }

        return userMapper.deleteById(id) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param id          用户ID
     * @param newPassword 新密码
     * @return 是否重置成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(String id, String newPassword) {
        if (id == null || id.isBlank() || newPassword == null || newPassword.isBlank()) {
            return false;
        }

        Account account = userMapper.selectById(id);
        if (account == null) {
            return false;
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        return userMapper.updateById(account) > 0;
    }

    /**
     * 修改用户角色
     *
     * @param id      用户ID
     * @param newRole 新角色ID
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeUserRole(String id, String newRole) {
        if (id == null || id.isBlank() ) {
            return false;
        }

        Account account = userMapper.selectById(id);
        if (account == null) {
            return false;
        }

        account.setRole(newRole);
        return userMapper.updateById(account) > 0;
    }

    /**
     * 检查用户名是否唯一
     *
     * @param username 用户名
     * @return true-唯一，false-已存在
     */
    @Override
    public boolean isUsernameUnique(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }

        Account account = userMapper.selectOne(new QueryWrapper<Account>().eq("username", username));
        return account == null;
    }

    /**
     * 检查邮箱是否唯一
     *
     * @param email 邮箱地址
     * @return true-唯一，false-已存在
     */
    @Override
    public boolean isEmailUnique(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        Account account = userMapper.selectOne(new QueryWrapper<Account>().eq("email", email));
        return account == null;
    }

    /**
     * 获取角色中文描述
     *
     * @param role 角色ID
     * @return 角色中文名称
     */
//    private String getRoleDescription(String role) {
//        return switch (role) {
//            case 1 -> "管理员";
//            case 2 -> "教师";
//            case 3 -> "学生";
//            case 4 -> "班级管理员";
//            default -> "未知角色";
//        };
//    }
}
