package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.ResourceReview;

import java.util.List;

/**
 * 课程评价服务接口，供前端课程评价区与后端评价持久化流程共用。
 */
public interface IResourceReviewService extends IService<ResourceReview> {
    
    /**
        * 根据课程ID查询评价列表，供前端评价列表加载与刷新。
        * @param courseId 课程ID
     * @return 评价列表
     */
        List<ResourceReview> listByCourseId(String courseId);

    /**
     * 点赞评价，供前端点赞按钮交互后更新统计。
     * @param id 评价ID
     * @return 是否点赞成功
     */
    boolean likeReview(String id);
}
