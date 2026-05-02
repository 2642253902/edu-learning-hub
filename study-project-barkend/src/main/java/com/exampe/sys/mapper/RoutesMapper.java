package com.exampe.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.sys.entity.Routes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统路由菜单 Mapper 接口，供前端查询需求与后端持久层共用。
 * <p>继承 MyBatis-Plus 的 BaseMapper，提供基础的 CRUD 操作</p>
 * <p>用于操作 sys_routes 表，管理系统菜单路由信息</p>
 * <p>提供自定义查询方法以支持按角色查询和排序功能</p>
 *
 * @author admin
 */
@Mapper
public interface RoutesMapper extends BaseMapper<Routes> {

    /**
     * 根据角色ID查询该角色可见的所有路由，并按排序字段排序
     * <p>通过连接 sys_routes 和 sys_role_route 表，获取指定角色有权访问的路由列表</p>
     * <p>结果按照 sort 字段和 id 字段升序排列，确保菜单显示顺序稳定</p>
     *
     * @param roleId 角色ID
     * @return 按排序字段排列的路由列表
     */
    @Select("SELECT r.* FROM sys_routes r JOIN sys_role_route rr " +
            "ON r.id COLLATE utf8_general_ci = rr.route_id COLLATE utf8_general_ci WHERE rr.role_id = #{roleId} " +
            "ORDER BY r.sort, r.id")
    List<Routes> selectAllOrderedByRole(@Param("roleId") String roleId);

    /**
     * 查询所有路由并按排序字段排序
     * <p>返回系统中的全部路由信息，不进行角色过滤</p>
     * <p>结果按照 sort 字段和 id 字段升序排列，用于后台管理等场景</p>
     *
     * @return 按排序字段排列的所有路由列表
     */
    @Select("SELECT * FROM sys_routes ORDER BY sort, id")
    List<Routes> selectAllOrdered();

}
