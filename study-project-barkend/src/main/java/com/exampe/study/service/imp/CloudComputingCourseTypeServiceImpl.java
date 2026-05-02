package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.CloudComputingCourseType;
import com.exampe.study.mapper.CloudComputingCourseTypeMapper;
import com.exampe.study.service.ICloudComputingCourseTypeService;
import org.springframework.stereotype.Service;

/**
 * 云计算课程分类服务实现类，负责前端课程分类选择与后端分类数据维护协同。
 * <p>实现课程分类的基础 CRUD 操作。</p>
 *
 * @author admin
 */
@Service
public class CloudComputingCourseTypeServiceImpl extends ServiceImpl<CloudComputingCourseTypeMapper, CloudComputingCourseType> implements ICloudComputingCourseTypeService {

}
