package com.exampe.sys.service;

import com.exampe.auth.dto.RouteTreeDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 26422
 * @since 2026-04-26
 */
public interface RoutesService {

    /**
     * 查询 routes 表并返回树形结构。
     */
    List<RouteTreeDTO> getRoutesTree(int role);
}
