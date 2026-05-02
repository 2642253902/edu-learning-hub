package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.ResourceReview;
import com.exampe.study.mapper.ResourceReviewMapper;
import com.exampe.study.service.IResourceReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资源评价服务实现类，负责前端评价区与后端评价数据维护协同。
 */
@Service
public class ResourceReviewServiceImpl extends ServiceImpl<ResourceReviewMapper, ResourceReview> implements IResourceReviewService {

    /**
     * 查询资源评价列表，供前端评价模块展示与刷新。
     */
    @Override
    public List<ResourceReview> listByResourceId(String resourceId) {
        return baseMapper.listByResourceId(resourceId);
    }

    /**
     * 点赞评价并更新点赞数，供前端点赞操作同步回显。
     */
    @Override
    public boolean likeReview(String id) {
        return baseMapper.incrementLike(id) > 0;
    }
}
