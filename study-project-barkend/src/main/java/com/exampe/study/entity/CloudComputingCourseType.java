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
 * 云计算课程分类实体类
 * <p>对应数据库表：cloud_computing_course_type</p>
 *
 * @author study-project
 * @since 2025-09-20
 * @version V1.0
 */
@Data
@TableName("cloud_computing_course_type")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CloudComputingCourseType implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID（UUID） */
    @TableId(type = IdType.ASSIGN_UUID)
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

    /** 所属部门编码 */
    private String sysOrgCode;

    /** 课程分类名称 */
    private String courseTypeName;
}
