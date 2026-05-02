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
 * 云计算课程资源实体类，供前后端字段对齐和后端持久化共用。
 * <p>对应数据库表：cloud_computing_course_resource</p>
 *
 * @author admin
 */
@Data
@TableName("cloud_computing_course_resource")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CloudComputingCourseResource implements Serializable {
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

    /** 资源类型（1-视频，2-讲义，3-实验，4-其他） */
    private Integer resourceType;

    /** 课程ID */
    private String courseId;

    /** 资源名称 */
    private String resourceName;

    /** 资源存储地址 */
    private String resourceUrl;

    /** 资源排序号 */
    private Integer resourceSort;
}
