package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.CloudComputingStudentLearningRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description: 学生学习记录表
 * @Author: jeecg-boot
 * @Date: 2025-09-22
 * @Version: V1.0
 */
@Mapper
public interface CloudComputingStudentLearningRecordMapper extends BaseMapper<CloudComputingStudentLearningRecord> {
    int getSumstudytime(String UserName);

    int getSumstudyday(String UserName);
}
