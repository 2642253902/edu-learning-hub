package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.StudyGroup;
import com.exampe.study.entity.StudyGroupMember;
import com.exampe.study.mapper.StudyGroupMapper;
import com.exampe.study.service.IStudyGroupMemberService;
import com.exampe.study.service.IStudyGroupService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StudyGroupServiceImpl extends ServiceImpl<StudyGroupMapper, StudyGroup> implements IStudyGroupService {

    @Resource
    private IStudyGroupMemberService studyGroupMemberService;

    @Override
    public List<StudyGroup> listGroups() {
        return baseMapper.listGroups();
    }

    @Override
    public boolean joinGroup(String groupId, String userId, String username) {
        if (studyGroupMemberService.existsMembership(groupId, userId)) {
            return true;
        }
        StudyGroupMember member = new StudyGroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setUsername(username);
        member.setCreateTime(new Date());
        return studyGroupMemberService.save(member);
    }
}
