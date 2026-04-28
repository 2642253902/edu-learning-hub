package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.CloudComputingStudentLearningRecord;
import com.exampe.study.mapper.CloudComputingStudentLearningRecordMapper;
import com.exampe.study.service.ICloudComputingStudentLearningRecordService;
import org.springframework.stereotype.Service;

/**
 * 学生学习记录服务实现类
 * <p>实现学生学习记录管理、学习统计等业务逻辑</p>
 *
 * @author admin
 */
@Service
public class CloudComputingStudentLearningRecordServiceImpl extends ServiceImpl<CloudComputingStudentLearningRecordMapper, CloudComputingStudentLearningRecord> implements ICloudComputingStudentLearningRecordService {
    
    @Override
    public int getSumstudytime(String UserName){
        return getBaseMapper().getSumstudytime(UserName);
    }

    @Override
    public int getSumstudyday(String UserName){
        return getBaseMapper().getSumstudyday(UserName);
    }
}
