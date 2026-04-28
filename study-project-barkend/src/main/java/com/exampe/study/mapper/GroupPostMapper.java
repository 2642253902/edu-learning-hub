package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.GroupPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupPostMapper extends BaseMapper<GroupPost> {
    List<GroupPost> listByGroupId(@Param("groupId") String groupId);

    int increaseCommentCount(@Param("postId") String postId);
}
