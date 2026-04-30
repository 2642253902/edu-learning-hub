package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.PostComment;

import java.util.List;

/**
 * 帖子评论服务接口
 * 提供帖子评论相关的业务逻辑处理
 */
public interface IPostCommentService extends IService<PostComment> {
    
    /**
     * 根据帖子ID查询评论列表
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<PostComment> listByPostId(String postId);
}
