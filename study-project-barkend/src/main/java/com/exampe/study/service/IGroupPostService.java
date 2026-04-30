package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.GroupPost;

import java.util.List;

/**
 * 小组帖子服务接口
 * 提供小组帖子相关的业务逻辑处理
 */
public interface IGroupPostService extends IService<GroupPost> {
    
    /**
     * 根据小组ID查询帖子列表
     * @param groupId 小组ID
     * @return 帖子列表
     */
    List<GroupPost> listByGroupId(String groupId);

    /**
     * 增加帖子评论数
     * @param postId 帖子ID
     * @return 是否更新成功
     */
    boolean increaseCommentCount(String postId);
}
