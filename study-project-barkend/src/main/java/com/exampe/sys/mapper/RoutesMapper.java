package com.exampe.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.sys.entity.Routes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 路由 Mapper 接口
 * </p>
 *
 * @author 26422
 * @since 2026-04-26
 */
@Mapper
public interface RoutesMapper extends BaseMapper<Routes> {

    // 新增：返回实体类型的有序路由列表，便于 service 层使用实体进行树形构建 — 仅返回指定角色可见的路由
    @Select("SELECT r.* FROM sys_routes r JOIN sys_role_route rr " +
            "ON r.id COLLATE utf8_general_ci = rr.route_id COLLATE utf8_general_ci WHERE rr.role_id = #{role} " +
            "ORDER BY r.sort, r.id")
    List<Routes> selectAllOrdered(@Param("role") int role);

}
