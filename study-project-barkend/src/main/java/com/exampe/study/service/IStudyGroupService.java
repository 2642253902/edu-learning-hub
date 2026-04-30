package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.StudyGroup;

import java.util.List;

/**
 * 学习小组服务接口
 * 提供学习小组相关的业务逻辑处理
 */
public interface IStudyGroupService extends IService<StudyGroup> {
    
    /**
     * 查询所有学习小组列表
     * @return 学习小组列表
     */
    List<StudyGroup> listGroups();

    /**
     * 用户加入学习小组
     * @param groupId 小组ID
     * @param userId 用户ID
     * @param username 用户名
     * @return 是否加入成功
     */
    boolean joinGroup(String groupId, String userId, String username);
}
