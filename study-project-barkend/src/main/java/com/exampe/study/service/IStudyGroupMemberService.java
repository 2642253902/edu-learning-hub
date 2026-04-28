package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.StudyGroupMember;

public interface IStudyGroupMemberService extends IService<StudyGroupMember> {
    boolean existsMembership(String groupId, String userId);
}
