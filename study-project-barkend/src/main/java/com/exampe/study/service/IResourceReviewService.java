package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.ResourceReview;

import java.util.List;

public interface IResourceReviewService extends IService<ResourceReview> {
    List<ResourceReview> listByResourceId(String resourceId);

    boolean likeReview(String id);
}
