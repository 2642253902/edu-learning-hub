package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.CloudComputingStudentLearningRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 学生学习记录 Mapper 接口
 * <p>提供学生学习记录的数据库操作及统计查询</p>
 *
 * @author admin
 */
@Mapper
public interface CloudComputingStudentLearningRecordMapper extends BaseMapper<CloudComputingStudentLearningRecord> {
    
    /**
     * 获取用户总学习时长
     * 
     * @param UserName 用户名
     * @return 总学习时长（秒）
     */
    int getSumstudytime(String UserName);

    /**
     * 获取用户总学习天数
     * 
     * @param UserName 用户名
     * @return 总学习天数
     */
    int getSumstudyday(String UserName);

        @Select("SELECT COUNT(DISTINCT course_id) FROM cloud_computing_student_learning_record WHERE user_id = #{userId}")
        Long countCoursesByUserId(@Param("userId") String userId);

        @Select("SELECT COUNT(DISTINCT course_id) FROM cloud_computing_student_learning_record WHERE user_id = #{userId} AND learning_status = '1'")
        Long countCompletedCoursesByUserId(@Param("userId") String userId);

        @Select("SELECT COALESCE(ROUND(SUM(learning_time) / 3600, 1), 0) FROM cloud_computing_student_learning_record WHERE user_id = #{userId} AND learning_time IS NOT NULL AND learning_time > 0")
        Double sumStudyHoursByUserId(@Param("userId") String userId);

        @Select("SELECT COUNT(DISTINCT learn_day) FROM (" +
            "SELECT DATE(create_time) AS learn_day FROM cloud_computing_student_learning_record WHERE user_id = #{userId} AND create_time IS NOT NULL " +
            "UNION " +
            "SELECT DATE(last_learn_time) AS learn_day FROM cloud_computing_student_learning_record WHERE user_id = #{userId} AND last_learn_time IS NOT NULL" +
            ") t")
        Long countStudyDaysByUserId(@Param("userId") String userId);

        @Select("SELECT COUNT(DISTINCT content_id) FROM cloud_computing_student_learning_record WHERE user_id = #{userId} AND content_id IS NOT NULL AND content_id <> ''")
        Long countResourcesByUserId(@Param("userId") String userId);

        @Select("SELECT COUNT(DISTINCT lr.user_id) " +
            "FROM cloud_computing_student_learning_record lr " +
            "INNER JOIN cloud_computing_course c ON c.id = lr.course_id " +
            "WHERE c.teacher_id = #{teacherId}")
        Long countStudentsByTeacherId(@Param("teacherId") String teacherId);

        @Select("SELECT COUNT(DISTINCT lr.user_id) " +
            "FROM cloud_computing_student_learning_record lr " +
            "INNER JOIN cloud_computing_course c ON c.id = lr.course_id " +
            "WHERE c.teacher_id = #{teacherId} AND lr.learning_status = '1'")
        Long countCompletedStudentsByTeacherId(@Param("teacherId") String teacherId);

        @Select("SELECT COALESCE(ROUND(SUM(lr.learning_time) / 3600, 1), 0) " +
            "FROM cloud_computing_student_learning_record lr " +
            "INNER JOIN cloud_computing_course c ON c.id = lr.course_id " +
            "WHERE c.teacher_id = #{teacherId} AND lr.learning_time IS NOT NULL AND lr.learning_time > 0")
        Double sumStudyHoursByTeacherId(@Param("teacherId") String teacherId);

        @Select("SELECT DATE(create_time) AS day, COALESCE(ROUND(SUM(learning_time) / 3600, 1), 0) AS value " +
            "FROM cloud_computing_student_learning_record " +
            "WHERE user_id = #{userId} AND create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
            "GROUP BY DATE(create_time) ORDER BY day")
        List<Map<String, Object>> listStudyTrendByUserId(@Param("userId") String userId);

        @Select("SELECT DATE(lr.create_time) AS day, COALESCE(ROUND(SUM(lr.learning_time) / 3600, 1), 0) AS value " +
            "FROM cloud_computing_student_learning_record lr " +
            "INNER JOIN cloud_computing_course c ON c.id = lr.course_id " +
            "WHERE c.teacher_id = #{teacherId} AND lr.create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
            "GROUP BY DATE(lr.create_time) ORDER BY day")
        List<Map<String, Object>> listStudyTrendByTeacherId(@Param("teacherId") String teacherId);
}
