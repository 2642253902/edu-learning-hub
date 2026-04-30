package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.GroupPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 小组帖子Mapper接口
 * 提供小组帖子相关的数据库操作
 */
@Mapper
public interface GroupPostMapper extends BaseMapper<GroupPost> {

    /**
     * 根据小组ID查询帖子列表
     *
     * @param groupId 小组ID
     * @return 帖子列表
     */
    List<GroupPost> listByGroupId(@Param("groupId") String groupId);

    /**
     * 增加帖子评论数
     *
     * @param postId 帖子ID
     * @return 影响行数
     */
    @Update("UPDATE group_post SET comment_count = IFNULL(comment_count, 0) + 1 WHERE id = #{postId}")
    int increaseCommentCount(@Param("postId") String postId);
}
