package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.StudyGroupMember;

/**
 * 学习小组成员服务接口，供前端加入状态判断与后端成员关系校验共用。
 */
public interface IStudyGroupMemberService extends IService<StudyGroupMember> {
    
    /**
        * 检查用户是否已加入指定小组，供前端按钮状态与后端防重复入组逻辑复用。
     * @param groupId 小组ID
     * @param userId 用户ID
     * @return 是否已加入
     */
    boolean existsMembership(String groupId, String userId);
}
