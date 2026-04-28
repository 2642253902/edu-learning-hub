package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.ResourceReview;
import com.exampe.study.mapper.ResourceReviewMapper;
import com.exampe.study.service.IResourceReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceReviewServiceImpl extends ServiceImpl<ResourceReviewMapper, ResourceReview> implements IResourceReviewService {

    @Override
    public List<ResourceReview> listByResourceId(String resourceId) {
        return baseMapper.listByResourceId(resourceId);
    }

    @Override
    public boolean likeReview(String id) {
        return baseMapper.incrementLike(id) > 0;
    }
}
