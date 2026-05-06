package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.ResourceReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 课程评价Mapper 接口，供前端查询需求与后端持久层共用。
 * 提供课程评价相关的数据库操作
 */
@Mapper
public interface ResourceReviewMapper extends BaseMapper<ResourceReview> {
    
    /**
     * 根据课程ID查询评价列表
     * @param courseId 课程ID
     * @return 评价列表
     */
        @Select("SELECT id, course_id, user_id, username, rating, content, likes, create_time " +
            "FROM resource_review " +
            "WHERE course_id = #{courseId} " +
            "ORDER BY create_time DESC")
    List<ResourceReview> listByCourseId(@Param("courseId") String courseId);

    /**
     * 增加评价点赞数
     * @param id 评价ID
     * @return 影响行数
     */
    @Update("UPDATE resource_review SET likes = IFNULL(likes, 0) + 1 WHERE id = #{id}")
    int incrementLike(@Param("id") String id);

    @Select("SELECT COUNT(*) FROM resource_review WHERE user_id = #{userId}")
    Long countReviewsByUserId(@Param("userId") String userId);

    @Select("SELECT COUNT(*) " +
            "FROM resource_review rr " +
            "INNER JOIN cloud_computing_course c ON c.id = rr.course_id " +
            "WHERE c.teacher_id = #{teacherId}")
    Long countReviewsByTeacherId(@Param("teacherId") String teacherId);
}
