package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.CloudComputingCourse;
import com.exampe.study.mapper.CloudComputingCourseMapper;
import com.exampe.study.mapper.CloudComputingCourseResourceMapper;
import com.exampe.study.service.ICloudComputingCourseService;
import com.exampe.study.dto.CloudComputingCourseResourceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 云计算课程服务实现类
 * <p>实现课程管理、教师信息查询、学习统计等业务逻辑</p>
 *
 * @author jeecg-boot
 * @since 2025-09-20
 * @version V1.0
 */
@Service
public class CloudComputingCourseServiceImpl extends ServiceImpl<CloudComputingCourseMapper, CloudComputingCourse> implements ICloudComputingCourseService {

    @Autowired
    private CloudComputingCourseResourceMapper resourceMapper;

    @Autowired
    private CloudComputingCourseMapper courseMapper;

    @Override
    public List<CloudComputingCourseResourceVO> listWithLearningStatus(String courseId, String studentId) {
        return resourceMapper.listWithLearningStatus(courseId, studentId);
    }

    @Override
    public List<Map> getTeacher() {
        return getBaseMapper().getTeacher();
    }

    @Override
    @Transactional
    public boolean deleteCourseById(String courseId) {
        // 删除课程关联的全部信息
        int departResult = courseMapper.deleteCourseById(courseId);
        return true;
    }

    @Override
    public int getSumcourse(String UserName) {
        return getBaseMapper().getSumcourse(UserName);
    }

    @Override
    public List<String> getCompletedcourse(String UserName) {
        return getBaseMapper().getCompletedcourse(UserName);
    }

    @Override
    public List<Map> getTeachercourse(String UserId) {
        return getBaseMapper().getTeachercourse(UserId);
    }

    @Override
    public int getTeacherNumber(String UserId, String CourseId) {
        return getBaseMapper().getTeacherNumber(UserId, CourseId);
    }

    @Override
    public int getTeacherTime(String UserId, String CourseId) {
        return getBaseMapper().getTeacherTime(UserId, CourseId);
    }

    @Override
    public int getTeachersum(String UserId, String CourseId) {
        return getBaseMapper().getTeachersum(UserId, CourseId);
    }

    @Override
    public List<String> getTeacherComplete(String UserId, String CourseId) {
        return getBaseMapper().getTeacherComplete(UserId, CourseId);
    }

    @Override
    public String getrole(String UserName) {
        return getBaseMapper().getrole(UserName);
    }

}
