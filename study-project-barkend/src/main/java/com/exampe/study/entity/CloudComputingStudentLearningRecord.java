package com.exampe.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 学生学习记录实体类，供前后端字段对齐和后端持久化共用。
 * <p>对应数据库表：cloud_computing_student_learning_record</p>
 *
 * @author admin
 */
@Data
@TableName("cloud_computing_student_learning_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CloudComputingStudentLearningRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 创建人 */
    private String createBy;

    /** 更新人 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;



    /** 课程ID */
    private String courseId;

    /** 学生ID */
    private String userId;

    /** 学习资源ID */
    private String contentId;

    /** 学习状态（0-未开始，1-学习中，2-已完成） */
    private String learningStatus;

    /** 累计学习时长（秒） */
    private Integer learningTime;

    /** 最后一次学习时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLearnTime;

    /** 记录创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
