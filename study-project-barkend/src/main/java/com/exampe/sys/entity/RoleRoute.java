package com.exampe.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色路由关联实体类，供前后端字段对齐和后端持久化共用。
 * <p>对应数据库表：sys_role_route，表示角色与路由的多对多关系</p>
 *
 * @author admin
 */
@TableName("sys_role_route")
@Data
public class RoleRoute implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 路由ID
     */
    private String routeId;
}
