package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.StudyGroupMember;
import com.exampe.study.mapper.StudyGroupMemberMapper;
import com.exampe.study.service.IStudyGroupMemberService;
import org.springframework.stereotype.Service;

@Service
public class StudyGroupMemberServiceImpl extends ServiceImpl<StudyGroupMemberMapper, StudyGroupMember> implements IStudyGroupMemberService {
    @Override
    public boolean existsMembership(String groupId, String userId) {
        Integer count = baseMapper.countMembership(groupId, userId);
        return count != null && count > 0;
    }
}
