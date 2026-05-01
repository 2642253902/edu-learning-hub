package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.PostComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 帖子评论Mapper接口
 * 提供帖子评论相关的数据库操作
 */
@Mapper
public interface PostCommentMapper extends BaseMapper<PostComment> {
    
    /**
     * 根据帖子ID查询评论列表
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<PostComment> listByPostId(@Param("postId") String postId);

    @Select("SELECT COUNT(*) FROM post_comment WHERE user_id = #{userId}")
    Long countCommentsByUserId(@Param("userId") String userId);
}
