package com.exampe.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.sys.entity.RoleRoute;
import com.exampe.sys.mapper.RoleRouteMapper;
import com.exampe.sys.service.IRoleRouteService;
import org.springframework.stereotype.Service;

/**
 * 角色路由关联服务实现类，供前端角色授权页面与后端角色路由关系持久化共用。
 * <p>实现 IRoleRouteService 接口，继承 MyBatis-Plus 的 ServiceImpl。</p>
 * <p>提供角色与路由关联关系的基础 CRUD 操作。</p>
 *
 * @author admin
 */
@Service
public class RoleRouteServiceImpl extends ServiceImpl<RoleRouteMapper, RoleRoute> implements IRoleRouteService {

}
