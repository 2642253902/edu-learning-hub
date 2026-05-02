package com.exampe.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.sys.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统角色 Mapper 接口，供前端查询需求与后端持久层共用。
 * <p>继承 MyBatis-Plus 的 BaseMapper，提供基础的 CRUD 操作</p>
 * <p>用于操作 sys_roles 表</p>
 *
 * @author admin
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
