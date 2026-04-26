package com.exampe.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 26422
 * @since 2026-04-26
 */
@TableName("sys_routes")
@Data
public class Routes implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 菜单名称/显示名称
     */
    private String title;

    /**
     * 	路由路径
     */
    private String path;

    /**
     * 父菜单ID，顶级为 0 — 改为 String 类型以与其它模块统一（顶级为 "0"）
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
}
