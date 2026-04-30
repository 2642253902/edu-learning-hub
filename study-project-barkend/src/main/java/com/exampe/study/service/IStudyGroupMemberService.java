package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.StudyGroupMember;

/**
 * 学习小组成员服务接口
 * 提供学习小组成员相关的业务逻辑处理
 */
public interface IStudyGroupMemberService extends IService<StudyGroupMember> {
    
    /**
     * 检查用户是否已加入指定小组
     * @param groupId 小组ID
     * @param userId 用户ID
     * @return 是否已加入
     */
    boolean existsMembership(String groupId, String userId);
}
