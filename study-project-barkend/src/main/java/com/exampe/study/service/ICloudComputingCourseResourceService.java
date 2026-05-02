package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.CloudComputingCourseResource;
import com.exampe.study.dto.CloudComputingCourseResourceVO;

import java.util.List;
import java.util.Map;

/**
 * 云计算课程资源服务接口，供前端资源列表页面与后端资源统计查询共用。
 * <p>提供课程资源管理、资源统计、学习状态查询等业务逻辑</p>
 *
 * @author admin
 */
public interface ICloudComputingCourseResourceService extends IService<CloudComputingCourseResource> {

    /**
        * 根据课程ID分组统计各资源类型数量，供前端课程分析区渲染。
     *
     * @param courseId 课程ID
     * @return 资源类型统计结果，key=资源类型，value=数量
     */
    Map<String, Long> countByCourseIdGrouped(String courseId);

    /**
        * 查询课程资源列表（含学生学习状态），供前端学习中心展示进度标签。
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 资源列表（包含学习状态）
     */
    List<CloudComputingCourseResourceVO> listWithLearningStatus(String courseId, String studentId);
}
