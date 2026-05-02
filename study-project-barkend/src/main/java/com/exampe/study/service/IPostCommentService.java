package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.PostComment;

import java.util.List;

/**
 * 帖子评论服务接口，统一封装前端评论面板与后端持久层之间的业务逻辑。
 */
public interface IPostCommentService extends IService<PostComment> {
    
    /**
     * 根据帖子ID查询评论列表，供前端详情页和管理页共用。
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<PostComment> listByPostId(String postId);
}
