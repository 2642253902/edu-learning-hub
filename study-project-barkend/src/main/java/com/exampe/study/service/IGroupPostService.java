package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.GroupPost;

import java.util.List;

public interface IGroupPostService extends IService<GroupPost> {
    List<GroupPost> listByGroupId(String groupId);

    boolean increaseCommentCount(String postId);
}
