package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.CloudComputingStudentLearningRecord;

/**
 * 学生学习记录服务接口，供前端学习统计页面与后端学习行为记录查询共用。
 * <p>提供学生学习记录管理、学习统计等业务逻辑</p>
 *
 * @author admin
 */
public interface ICloudComputingStudentLearningRecordService extends IService<CloudComputingStudentLearningRecord> {
    
    /**
        * 获取用户总学习时长，供前端学习数据看板展示。
     *
     * @param UserName 用户名
     * @return 总学习时长（秒）
     */
    int getSumstudytime(String UserName);

    /**
        * 获取用户总学习天数，供前端学习连续性统计展示。
     *
     * @param UserName 用户名
     * @return 总学习天数
     */
    int getSumstudyday(String UserName);

}
