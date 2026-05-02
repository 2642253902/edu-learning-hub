package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.CloudComputingCourseResource;
import com.exampe.study.mapper.CloudComputingCourseResourceMapper;
import com.exampe.study.service.ICloudComputingCourseResourceService;
import com.exampe.study.dto.CloudComputingCourseResourceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 云计算课程资源服务实现类，负责前端资源展示与后端资源统计查询协同。
 * <p>实现课程资源管理、资源统计、学习状态查询等业务逻辑</p>
 *
 * @author admin
 */
@Service
public class CloudComputingCourseResourceServiceImpl extends ServiceImpl<CloudComputingCourseResourceMapper, CloudComputingCourseResource> implements ICloudComputingCourseResourceService {


    @Autowired
    private CloudComputingCourseResourceMapper cloudComputingCourseResourceMapper;

    /**
     * 统计课程下不同资源类型数量，供前端统计视图展示。
     */
    @Override
    public Map<String, Long> countByCourseIdGrouped(String courseId) {
        List<Map<String, Object>> rows = cloudComputingCourseResourceMapper.countByCourseIdGroupByType(courseId);
        Map<String, Long> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            String type = row.get("type") == null ? "" : String.valueOf(row.get("type"));
            Number cnt = (Number) row.get("cnt");
            result.put(type, cnt == null ? 0L : cnt.longValue());
        }
        return result;
    }

    /**
     * 查询资源并拼接学习状态，供前端学习进度列表回显。
     */
    @Override
    public List<CloudComputingCourseResourceVO> listWithLearningStatus(String courseId, String studentId) {
        return cloudComputingCourseResourceMapper.listWithLearningStatus(courseId, studentId);
    }


}
