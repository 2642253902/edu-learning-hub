package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.CloudComputingCourseResource;
import com.exampe.study.dto.CloudComputingCourseResourceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 云计算课程资源 Mapper 接口，供前端查询需求与后端持久层共用。
 * <p>提供课程资源的数据库操作及统计查询</p>
 *
 * @author admin
 */
@Mapper
public interface CloudComputingCourseResourceMapper extends BaseMapper<CloudComputingCourseResource> {
    
    /**
     * 根据课程ID分组统计各资源类型数量
     * 
     * @param courseId 课程ID
     * @return 资源类型统计结果
     */
    @Select("SELECT resource_type AS type, COUNT(*) AS cnt " +
            "FROM cloud_computing_course_resource " +
            "WHERE course_id = #{courseId} " +
            "GROUP BY resource_type")
    List<Map<String, Object>> countByCourseIdGroupByType(@Param("courseId") String courseId);

        @Select("SELECT COUNT(*) " +
            "FROM cloud_computing_course_resource r " +
            "INNER JOIN cloud_computing_course c ON c.id = r.course_id " +
            "WHERE c.teacher_id = #{teacherId}")
        Long countResourcesByTeacherId(@Param("teacherId") String teacherId);

        @Select("SELECT r.resource_type AS type, COUNT(*) AS value " +
            "FROM cloud_computing_course_resource r " +
            "INNER JOIN cloud_computing_course c ON c.id = r.course_id " +
            "WHERE c.teacher_id = #{teacherId} " +
            "GROUP BY r.resource_type ORDER BY r.resource_type")
        List<Map<String, Object>> countResourceTypesByTeacherId(@Param("teacherId") String teacherId);

    /**
     * 查询课程资源列表（含学生学习状态）
     * 
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 资源列表（包含学习状态）
     */
    List<CloudComputingCourseResourceVO> listWithLearningStatus(@Param("courseId") String courseId, @Param("studentId") String studentId);
}
