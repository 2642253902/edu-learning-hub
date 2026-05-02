package com.exampe.sys.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路由树 DTO，供前端动态菜单渲染与后端路由树构建结果字段对齐。
 * <p>
 * 用于表示菜单路由的树形结构，支持父子层级关系。
 *
 * @author admin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteTreeDTO {
    /**
     * 路由ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 父路由ID
     */
    private String parentId;

    /**
     * 路由层级
     */
    private Integer level;

    /**
     * 路由备注
     */
    private String remark;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 菜单是否可见（0:不可见, 1:可见）
     */
    private Integer menuVisible;

    /**
     * 子路由列表，供前端递归渲染菜单树。
     */
    private List<RouteTreeDTO> children;
}
