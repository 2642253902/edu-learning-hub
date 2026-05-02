package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.PostComment;
import com.exampe.study.mapper.PostCommentMapper;
import com.exampe.study.service.IPostCommentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 帖子评论服务实现类，负责前端帖子评论区与后端评论查询能力协同。
 */
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment> implements IPostCommentService {

    /**
     * 查询帖子评论列表，供前端帖子详情评论区加载。
     */
    @Override
    public List<PostComment> listByPostId(String postId) {
        return baseMapper.listByPostId(postId);
    }
}
