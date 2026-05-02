package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.CloudComputingCourse;
import com.exampe.study.dto.CloudComputingCourseResourceVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 云计算课程服务接口，供前端课程中心页面与后端课程业务流程共用。
 * <p>提供课程管理、教师信息查询、学习统计等业务逻辑</p>
 *
 * @author admin
 */
public interface ICloudComputingCourseService extends IService<CloudComputingCourse> {

    /**
        * 查询课程资源列表（含学生学习状态），供前端学习中心资源区直接渲染。
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 资源列表（包含学习状态）
     */
    List<CloudComputingCourseResourceVO> listWithLearningStatus(String courseId, String studentId);

    /**
        * 获取教师列表，供前端课程编辑页教师下拉选择。
     *
     * @return 教师信息列表
     */
    List<Map> getTeacher();

    /**
        * 删除课程及其关联数据，供前端课程管理页删除操作调用。
     * <p>事务控制，同时删除课程相关的资源和学习记录</p>
     *
     * @param id 课程ID
     * @return 是否删除成功
     */
    @Transactional
    boolean deleteCourseById(String id);

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
    List<Map> getTeachercourse(String UserId);

    /**
     * 获取教师在指定课程中的学生数量
     *
     * @param UserId 教师ID
     * @param CourseId 课程ID
     * @return 学生数量
     */
    int getTeacherNumber(String UserId, String CourseId);

    /**
     * 获取教师在指定课程中的总学习时长
     *
     * @param UserId 教师ID
     * @param CourseId 课程ID
     * @return 总学习时长（秒）
     */
    int getTeacherTime(String UserId, String CourseId);

    /**
     * 获取教师在指定课程中的总学习次数
     *
     * @param UserId 教师ID
     * @param CourseId 课程ID
     * @return 总学习次数
     */
    int getTeachersum(String UserId, String CourseId);

    /**
     * 获取教师在指定课程中完成的资源列表
     *
     * @param UserId 教师ID
     * @param CourseId 课程ID
     * @return 已完成资源ID列表
     */
    List<String> getTeacherComplete(String UserId, String CourseId);

    /**
     * 获取用户角色
     *
     * @param UserName 用户名
     * @return 角色标识
     */
    String getrole(String UserName);
}
