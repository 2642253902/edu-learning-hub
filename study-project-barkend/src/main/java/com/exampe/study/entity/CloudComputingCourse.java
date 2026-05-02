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
 * 云计算课程实体类，供前后端字段对齐和后端持久化共用。
 * <p>对应数据库表：cloud_computing_course</p>
 *
 * @author admin
 */
@Data
@TableName("cloud_computing_course")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CloudComputingCourse implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新人 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    /** 课程名称 */
    private String courseName;

    /** 课程分类ID */
    private String courseTypeId;

    /** 课程标签 */
    private String courseTag;

    /** 负责教师ID */
    private String teacherId;

    /** 总课时（小时） */
    private Integer courseHours;

    /** 课程状态（0-未发布，1-已发布，2-已下架） */
    private Integer courseStatus;
}
