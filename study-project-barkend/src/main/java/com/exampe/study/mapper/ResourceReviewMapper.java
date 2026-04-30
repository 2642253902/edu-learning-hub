package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.ResourceReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 资源评价Mapper接口
 * 提供资源评价相关的数据库操作
 */
@Mapper
public interface ResourceReviewMapper extends BaseMapper<ResourceReview> {
    
    /**
     * 根据资源ID查询评价列表
     * @param resourceId 资源ID
     * @return 评价列表
     */
    @Select("SELECT id, resource_id, course_id, user_id, username, rating, content, likes, create_time " +
            "FROM resource_review " +
            "WHERE resource_id = #{resourceId} " +
            "ORDER BY create_time DESC")
    List<ResourceReview> listByResourceId(@Param("resourceId") String resourceId);

    /**
     * 增加评价点赞数
     * @param id 评价ID
     * @return 影响行数
     */
    @Update("UPDATE resource_review SET likes = IFNULL(likes, 0) + 1 WHERE id = #{id}")
    int incrementLike(@Param("id") String id);
}
