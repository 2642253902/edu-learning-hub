package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.GroupPost;
import com.exampe.study.mapper.GroupPostMapper;
import com.exampe.study.service.IGroupPostService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupPostServiceImpl extends ServiceImpl<GroupPostMapper, GroupPost> implements IGroupPostService {

    @Override
    public List<GroupPost> listByGroupId(String groupId) {
        return baseMapper.listByGroupId(groupId);
    }

    @Override
    public boolean increaseCommentCount(String postId) {
        return baseMapper.increaseCommentCount(postId) > 0;
    }
}
