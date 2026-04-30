package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.ResourceReview;

import java.util.List;

/**
 * 资源评价服务接口
 * 提供资源评价相关的业务逻辑处理
 */
public interface IResourceReviewService extends IService<ResourceReview> {
    
    /**
     * 根据资源ID查询评价列表
     * @param resourceId 资源ID
     * @return 评价列表
     */
    List<ResourceReview> listByResourceId(String resourceId);

    /**
     * 点赞评价
     * @param id 评价ID
     * @return 是否点赞成功
     */
    boolean likeReview(String id);
}
