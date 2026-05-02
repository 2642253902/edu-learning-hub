package com.exampe.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.sys.entity.RoleRoute;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色路由关联 Mapper 接口，供前端查询需求与后端持久层共用。
 * <p>继承 MyBatis-Plus 的 BaseMapper，提供基础的 CRUD 操作</p>
 * <p>用于操作 sys_role_route 表，管理角色与路由的多对多关联关系</p>
 *
 * @author admin
 */
@Mapper
public interface RoleRouteMapper extends BaseMapper<RoleRoute> {

}
