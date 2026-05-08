package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.StudyGroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学习小组成员Mapper 接口，供前端查询需求与后端持久层共用。
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

    @Select("SELECT COUNT(DISTINCT group_id) FROM study_group_member WHERE user_id = #{userId}")
    Long countGroupsByUserId(@Param("userId") String userId);

    /**
     * 查询指定小组的成员列表。
     *
     * @param groupId 小组ID
     * @return 成员列表
     */
    @Select("SELECT id, group_id AS groupId, user_id AS userId, username, create_time AS createTime " +
            "FROM study_group_member WHERE group_id = #{groupId} ORDER BY create_time ASC")
    List<StudyGroupMember> listByGroupId(@Param("groupId") String groupId);
}
