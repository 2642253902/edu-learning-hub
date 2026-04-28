package com.exampe.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.auth.entity.auth.Account;
import com.exampe.auth.entity.user.AccountUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户 Mapper 接口
 * <p>
 * 提供用户数据的数据库操作
 *
 * @author admin
 */
@Mapper
public interface UserMapper extends BaseMapper<Account> {

    /**
     * 根据用户名或邮箱查询账户信息
     *
     * @param usernameOrEmail 用户名或邮箱
     * @return 账户实体
     */
    @Select("SELECT * FROM sys_account WHERE username = #{usernameOrEmail} OR email = #{usernameOrEmail}")
    Account findAccountByNameOrEmail(String usernameOrEmail);

    /**
     * 根据邮箱重置密码
     *
     * @param email    邮箱地址
     * @param password 新密码（已加密）
     * @return 更新行数
     */
    @Update("UPDATE sys_account SET password = #{password} WHERE email = #{email}")
    int restPasswordByEmail(String email, String password);

    /**
     * 根据用户名或邮箱查询用户信息（不含密码）
     *
     * @param usernameOrEmail 用户名或邮箱
     * @return 用户实体
     */
    @Select("SELECT * FROM sys_account WHERE username = #{usernameOrEmail} OR email = #{usernameOrEmail}")
    AccountUser findAccountUserByNameOrEmail(String usernameOrEmail);

    /**
     * 查询所有教师用户（role = 2）
     *
     * @return 教师用户列表
     */
    @Select("SELECT * FROM sys_account WHERE role = 2")
    List<AccountUser> selectTeachers();

}
