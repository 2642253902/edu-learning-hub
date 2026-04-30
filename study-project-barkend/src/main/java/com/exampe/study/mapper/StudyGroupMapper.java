package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.StudyGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学习小组Mapper接口
 * 提供学习小组相关的数据库操作
 */
@Mapper
public interface StudyGroupMapper extends BaseMapper<StudyGroup> {
    
    /**
     * 查询所有学习小组列表
     * @return 学习小组列表
     */
    @Select("SELECT id, name, description, owner_id, owner_name, create_time " +
            "FROM study_group " +
            "ORDER BY create_time DESC")
    List<StudyGroup> listGroups();
}
