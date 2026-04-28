package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.PostComment;
import com.exampe.study.mapper.PostCommentMapper;
import com.exampe.study.service.IPostCommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment> implements IPostCommentService {

    @Override
    public List<PostComment> listByPostId(String postId) {
        return baseMapper.listByPostId(postId);
    }
}
