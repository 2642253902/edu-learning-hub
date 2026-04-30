package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.StudyGroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 学习小组成员Mapper接口
 * 提供学习小组成员相关的数据库操作
 */
@Mapper
public interface StudyGroupMemberMapper extends BaseMapper<StudyGroupMember> {
    
    /**
     * 统计用户在小组中的成员数量（用于判断是否已加入）
     * @param groupId 小组ID
     * @param userId 用户ID
     * @return 成员数量
     */
    @Select("SELECT COUNT(1) FROM study_group_member WHERE group_id = #{groupId} AND user_id = #{userId}")
    Integer countMembership(@Param("groupId") String groupId, @Param("userId") String userId);
}
