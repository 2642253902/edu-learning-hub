package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.entity.CloudComputingStudentLearningRecord;
import com.exampe.study.mapper.CloudComputingStudentLearningRecordMapper;
import com.exampe.study.service.ICloudComputingStudentLearningRecordService;
import org.springframework.stereotype.Service;

/**
 * @Description: 学生学习记录表
 * @Author: jeecg-boot
 * @Date:   2025-09-22
 * @Version: V1.0
 */
@Service
public class CloudComputingStudentLearningRecordServiceImpl extends ServiceImpl<CloudComputingStudentLearningRecordMapper, CloudComputingStudentLearningRecord> implements ICloudComputingStudentLearningRecordService {
    public int getSumstudytime(String UserName){
        return  getBaseMapper().getSumstudytime(UserName);
    };

    public int getSumstudyday(String UserName){
        return  getBaseMapper().getSumstudyday(UserName);
    };
}
