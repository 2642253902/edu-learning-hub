package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.StudyGroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudyGroupMemberMapper extends BaseMapper<StudyGroupMember> {
    Integer countMembership(@Param("groupId") String groupId, @Param("userId") String userId);
}
