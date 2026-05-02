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

/**
 * 学习小组服务实现类，负责前端小组广场与后端小组成员关系维护协同。
 */
@Service
public class StudyGroupServiceImpl extends ServiceImpl<StudyGroupMapper, StudyGroup> implements IStudyGroupService {

    @Resource
    private IStudyGroupMemberService studyGroupMemberService;

    /**
     * 查询全部学习小组，供前端小组列表页加载。
     */
    @Override
    public List<StudyGroup> listGroups() {
        return baseMapper.listGroups();
    }

    /**
     * 用户加入学习小组，供前端加入操作写入成员关系。
     */
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
