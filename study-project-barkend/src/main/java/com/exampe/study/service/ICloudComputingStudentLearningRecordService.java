package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.CloudComputingStudentLearningRecord;

/**
 * 学生学习记录服务接口
 * <p>提供学生学习记录管理、学习统计等业务逻辑</p>
 *
 * @author admin
 */
public interface ICloudComputingStudentLearningRecordService extends IService<CloudComputingStudentLearningRecord> {
    
    /**
     * 获取用户总学习时长
     *
     * @param UserName 用户名
     * @return 总学习时长（秒）
     */
    int getSumstudytime(String UserName);

    /**
     * 获取用户总学习天数
     *
     * @param UserName 用户名
     * @return 总学习天数
     */
    int getSumstudyday(String UserName);

}
