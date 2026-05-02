package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.GroupPost;

import java.util.List;

/**
 * 小组帖子服务接口，供前端小组讨论页与后端帖子持久化流程共用。
 */
public interface IGroupPostService extends IService<GroupPost> {
    
    /**
        * 根据小组ID查询帖子列表，供前端帖子列表区域渲染。
     * @param groupId 小组ID
     * @return 帖子列表
     */
    List<GroupPost> listByGroupId(String groupId);

    /**
     * 增加帖子评论数，供前端评论提交成功后同步互动统计。
     * @param postId 帖子ID
     * @return 是否更新成功
     */
    boolean increaseCommentCount(String postId);
}
