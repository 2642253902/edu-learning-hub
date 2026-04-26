package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.CloudComputingCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 课程表
 * @Author: jeecg-boot
 * @Date: 2025-09-20
 * @Version: V1.0
 */
@Mapper
public interface CloudComputingCourseMapper extends BaseMapper<CloudComputingCourse> {
    List<Map> getTeacher();

    int deleteCourseById(@Param("courseId") String courseId);

    int getSumcourse(String UserName);

    List<String> getCompletedcourse(String UserName);

    List<Map> getTeachercourse(String UserId);

    int getTeacherNumber(@Param("UserId") String UserId, @Param("CourseId") String CourseId);

    int getTeacherTime(@Param("UserId") String UserId, @Param("CourseId") String CourseId);

    int getTeachersum(@Param("UserId") String UserId, @Param("CourseId") String CourseId);

    List<String> getTeacherComplete(@Param("UserId") String UserId, @Param("CourseId") String CourseId);

    String getrole(@Param("UserId") String UserName);
}
