package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.exampe.study.entity.CloudComputingCourseType;

/**
 * 云计算课程分类 Mapper 接口
 * <p>提供课程分类的基础数据库操作</p>
 *
 * @author admin
 */
@Mapper
public interface CloudComputingCourseTypeMapper extends BaseMapper<CloudComputingCourseType> {

}
