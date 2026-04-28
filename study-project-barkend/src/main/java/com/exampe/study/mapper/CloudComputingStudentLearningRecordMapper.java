package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.CloudComputingStudentLearningRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生学习记录 Mapper 接口
 * <p>提供学生学习记录的数据库操作及统计查询</p>
 *
 * @author admin
 */
@Mapper
public interface CloudComputingStudentLearningRecordMapper extends BaseMapper<CloudComputingStudentLearningRecord> {
    
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
