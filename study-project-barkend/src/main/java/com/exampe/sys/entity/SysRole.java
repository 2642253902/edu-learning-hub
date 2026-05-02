package com.exampe.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统角色实体类，供前端权限页面和后端角色持久化共用。
 * <p>对应数据库表：sys_roles，存储系统角色信息</p>
 * <p>用于权限管理，定义不同用户角色的基本信息</p>
 *
 * @author admin
 */
@TableName("sys_roles")
@Data
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID（主键）
     * <p>使用雪花算法自动生成唯一ID</p>
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 角色名称
     * <p>用于标识角色的唯一名称，如：管理员、教师、学生等</p>
     */
    private String name;

    /**
     * 角色描述
     * <p>对角色功能和权限的详细说明</p>
     */
    private String description;
}
