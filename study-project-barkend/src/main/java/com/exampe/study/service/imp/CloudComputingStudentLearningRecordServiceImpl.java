package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.CloudComputingStudentLearningRecord;
import com.exampe.study.mapper.CloudComputingStudentLearningRecordMapper;
import com.exampe.study.service.ICloudComputingStudentLearningRecordService;
import org.springframework.stereotype.Service;

/**
 * 学生学习记录服务实现类，负责前端学习看板与后端学习记录统计查询协同。
 * <p>实现学生学习记录管理、学习统计等业务逻辑</p>
 *
 * @author admin
 */
@Service
public class CloudComputingStudentLearningRecordServiceImpl extends ServiceImpl<CloudComputingStudentLearningRecordMapper, CloudComputingStudentLearningRecord> implements ICloudComputingStudentLearningRecordService {

    /**
     * 汇总用户学习总时长，供前端统计卡片展示。
     */
    @Override
    public int getSumstudytime(String UserName){
        return getBaseMapper().getSumstudytime(UserName);
    }

    /**
     * 汇总用户学习总天数，供前端学习天数指标展示。
     */
    @Override
    public int getSumstudyday(String UserName){
        return getBaseMapper().getSumstudyday(UserName);
    }
}
