package com.exampe.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.sys.entity.RoleRoute;

/**
 * 角色路由关联服务接口，供前端权限配置页提交授权结果，并供后端构建权限树时读取。
 */
public interface IRoleRouteService extends IService<RoleRoute> {

}
