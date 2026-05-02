package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.StudyGroup;

import java.util.List;

/**
 * 学习小组服务接口，供前端小组列表页面与后端小组业务流程共用。
 */
public interface IStudyGroupService extends IService<StudyGroup> {
    
    /**
        * 查询所有学习小组列表，供前端小组广场页面渲染。
     * @return 学习小组列表
     */
    List<StudyGroup> listGroups();

    /**
        * 用户加入学习小组，供前端加入按钮提交后落库成员关系。
     * @param groupId 小组ID
     * @param userId 用户ID
     * @param username 用户名
     * @return 是否加入成功
     */
    boolean joinGroup(String groupId, String userId, String username);
}
