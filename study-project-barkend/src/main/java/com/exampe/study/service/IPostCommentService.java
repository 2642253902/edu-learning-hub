package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.PostComment;

import java.util.List;

public interface IPostCommentService extends IService<PostComment> {
    List<PostComment> listByPostId(String postId);
}
