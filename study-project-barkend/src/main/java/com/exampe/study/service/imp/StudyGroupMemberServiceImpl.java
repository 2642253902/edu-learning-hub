package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.StudyGroupMember;
import com.exampe.study.mapper.StudyGroupMemberMapper;
import com.exampe.study.service.IStudyGroupMemberService;
import org.springframework.stereotype.Service;

/**
 * 学习小组成员服务实现类，负责前端入组状态判断与后端成员关系查询协同。
 */
@Service
public class StudyGroupMemberServiceImpl extends ServiceImpl<StudyGroupMemberMapper, StudyGroupMember> implements IStudyGroupMemberService {

    /**
     * 校验用户是否已加入目标小组，供前端按钮状态与后端幂等处理复用。
     */
    @Override
    public boolean existsMembership(String groupId, String userId) {
        Integer count = baseMapper.countMembership(groupId, userId);
        return count != null && count > 0;
    }

    /**
     * 按加入时间升序返回小组成员，方便详情页按自然顺序展示。
     */
    @Override
    public java.util.List<StudyGroupMember> listByGroupId(String groupId) {
        return baseMapper.listByGroupId(groupId);
    }
}
