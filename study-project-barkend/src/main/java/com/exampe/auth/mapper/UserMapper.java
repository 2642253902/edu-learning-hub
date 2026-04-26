package com.exampe.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.auth.entity.auth.Account;
import com.exampe.auth.entity.user.AccountUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<Account> {
    @Select("SELECT * FROM sys_account WHERE username = #{usernameOrEmail} OR email = #{usernameOrEmail}")
    Account findAccountByNameOrEmail(String usernameOrEmail);

    @Update("UPDATE sys_account SET password = #{password} WHERE email = #{email}")
    int restPasswordByEmail(String email, String password);

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
