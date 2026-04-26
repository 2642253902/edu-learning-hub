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
 * @Description: 学习资源表
 * @Author: jeecg-boot
 * @Date:   2025-09-20
 * @Version: V1.0
 */
@Service
public class CloudComputingCourseResourceServiceImpl extends ServiceImpl<CloudComputingCourseResourceMapper, CloudComputingCourseResource> implements ICloudComputingCourseResourceService {


    @Autowired
    private CloudComputingCourseResourceMapper cloudComputingCourseResourceMapper;

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

    @Override
    public List<CloudComputingCourseResourceVO> listWithLearningStatus(String courseId, String studentId) {
        return cloudComputingCourseResourceMapper.listWithLearningStatus(courseId, studentId);
    }


}
