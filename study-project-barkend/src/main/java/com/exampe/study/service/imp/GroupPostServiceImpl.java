package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.GroupPost;
import com.exampe.study.mapper.GroupPostMapper;
import com.exampe.study.service.IGroupPostService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 小组帖子服务实现类，负责前端小组讨论页面与后端帖子数据读写协同。
 */
@Service
public class GroupPostServiceImpl extends ServiceImpl<GroupPostMapper, GroupPost> implements IGroupPostService {

    /**
     * 查询指定小组的帖子列表，供前端帖子流渲染。
     */
    @Override
    public List<GroupPost> listByGroupId(String groupId) {
        return baseMapper.listByGroupId(groupId);
    }

    /**
     * 增加帖子评论计数，供前端评论提交成功后同步统计。
     */
    @Override
    public boolean increaseCommentCount(String postId) {
        return baseMapper.increaseCommentCount(postId) > 0;
    }
}
