package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.CloudComputingCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 云计算课程 Mapper 接口
 * <p>提供课程数据的数据库操作及统计查询</p>
 *
 * @author admin
 */
@Mapper
public interface CloudComputingCourseMapper extends BaseMapper<CloudComputingCourse> {
    
    /**
     * 获取教师列表
     * 
     * @return 教师信息列表
     */
    @Select("SELECT su.id AS value, su.realname AS text, su.realname AS title, su.realname AS label " +
            "FROM sys_user_depart sud " +
            "INNER JOIN sys_depart sd ON sud.dep_id = sd.id " +
            "INNER JOIN sys_account su ON sud.user_id = su.id " +
            "WHERE sd.depart_name = '教师'")
    List<Map> getTeacher();

    /**
     * 删除课程及其关联数据
     * 
     * @param courseId 课程ID
     * @return 删除行数
     */
    int deleteCourseById(@Param("courseId") String courseId);

    /**
     * 获取用户完成的课程总数
     * 
     * @param UserName 用户名
     * @return 完成课程数
     */
    int getSumcourse(String UserName);

    /**
     * 获取用户已完成的课程列表
     * 
     * @param UserName 用户名
     * @return 已完成课程ID列表
     */
    List<String> getCompletedcourse(String UserName);

    /**
     * 获取教师教授的课程列表
     * 
     * @param UserId 教师ID
     * @return 课程信息列表
     */
    @Select("SELECT c.id, c.course_name " +
            "FROM cloud_computing_course c " +
            "LEFT JOIN sys_account u ON c.teacher_id = u.ID " +
            "WHERE c.teacher_id = #{UserId}")
    List<Map> getTeachercourse(String UserId);

    /**
     * 获取教师在指定课程中的学生数量
     * 
     * @param UserId 教师ID
     * @param CourseId 课程ID
     * @return 学生数量
     */
    int getTeacherNumber(@Param("UserId") String UserId, @Param("CourseId") String CourseId);

    /**
     * 获取教师在指定课程中的总学习时长
     * 
     * @param UserId 教师ID
     * @param CourseId 课程ID
     * @return 总学习时长（秒）
     */
    int getTeacherTime(@Param("UserId") String UserId, @Param("CourseId") String CourseId);

    /**
     * 获取教师在指定课程中的总学习次数
     * 
     * @param UserId 教师ID
     * @param CourseId 课程ID
     * @return 总学习次数
     */
    int getTeachersum(@Param("UserId") String UserId, @Param("CourseId") String CourseId);

        @Select("SELECT COUNT(*) FROM cloud_computing_course WHERE teacher_id = #{teacherId}")
        Long countCoursesByTeacherId(@Param("teacherId") String teacherId);

        @Select("SELECT COUNT(*) FROM cloud_computing_course WHERE teacher_id = #{teacherId} AND course_status = 1")
        Long countPublishedCoursesByTeacherId(@Param("teacherId") String teacherId);

        @Select("SELECT course_status AS status, COUNT(*) AS value FROM cloud_computing_course WHERE teacher_id = #{teacherId} GROUP BY course_status ORDER BY course_status")
        List<Map<String, Object>> countCourseStatusByTeacherId(@Param("teacherId") String teacherId);

    /**
     * 获取教师在指定课程中完成的资源列表
     * 
     * @param UserId 教师ID
     * @param CourseId 课程ID
     * @return 已完成资源ID列表
     */
    List<String> getTeacherComplete(@Param("UserId") String UserId, @Param("CourseId") String CourseId);

    /**
     * 获取用户角色
     * 
     * @param UserName 用户名
     * @return 角色标识
     */
    @Select("SELECT role_name FROM sys_role " +
            "WHERE id = (SELECT role_id FROM sys_user_role " +
            "WHERE user_id = (SELECT id FROM sys_account WHERE username = #{UserId}))")
    String getrole(@Param("UserId") String UserName);
}
