package com.exampe.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统路由菜单实体类
 * <p>对应数据库表：sys_routes，存储系统菜单路由信息</p>
 *
 * @author admin
 */
@TableName("sys_routes")
@Data
public class Routes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 菜单名称/显示名称
     */
    private String title;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 父菜单ID，顶级为 "0"
     */
    private String parentId;

    /**
     * 菜单层级（1:一级，2:二级）
     */
    private String level;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 排序号
     */
    private String sort;

    /**
     * 是否在菜单中显示（1:显示，0:隐藏）
     */
    @TableField("menu_visible")
    private Integer menuVisible;
}
