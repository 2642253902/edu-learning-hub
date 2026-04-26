package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.CloudComputingCourseResource;
import com.exampe.study.dto.CloudComputingCourseResourceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 学习资源表
 * @Author: jeecg-boot
 * @Date: 2025-09-20
 * @Version: V1.0
 */
@Mapper
public interface CloudComputingCourseResourceMapper extends BaseMapper<CloudComputingCourseResource> {
    List<Map<String, Object>> countByCourseIdGroupByType(@Param("courseId") String courseId);

    List<CloudComputingCourseResourceVO> listWithLearningStatus(@Param("courseId") String courseId, @Param("studentId") String studentId);
}
