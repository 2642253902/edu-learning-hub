package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.CloudComputingCourseType;
import com.exampe.study.mapper.CloudComputingCourseTypeMapper;
import com.exampe.study.service.ICloudComputingCourseTypeService;
import org.springframework.stereotype.Service;

/**
 * 云计算课程分类服务实现类
 * <p>实现课程分类的基础CRUD操作</p>
 *
 * @author admin
 */
@Service
public class CloudComputingCourseTypeServiceImpl extends ServiceImpl<CloudComputingCourseTypeMapper, CloudComputingCourseType> implements ICloudComputingCourseTypeService {

}
