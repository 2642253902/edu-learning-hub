package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.ResourceReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResourceReviewMapper extends BaseMapper<ResourceReview> {
    List<ResourceReview> listByResourceId(@Param("resourceId") String resourceId);

    int incrementLike(@Param("id") String id);
}
