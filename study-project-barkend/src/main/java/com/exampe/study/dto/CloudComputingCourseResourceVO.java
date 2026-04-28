package com.exampe.study.dto;

import lombok.Data;

import java.util.Date;

/**
 * 课程资源视图对象（VO）
 * <p>用于展示课程资源及其学生学习状态的组合信息</p>
 * 
 * @author admin
 */
@Data
public class CloudComputingCourseResourceVO {
    
    // ==================== 资源表字段 ====================
    
    /** 资源ID */
    private String id;
    
    /** 创建人 */
    private String createBy;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 更新人 */
    private String updateBy;
    
    /** 更新时间 */
    private Date updateTime;
    
    /** 所属部门编码 */
    private String sysOrgCode;
    
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

    // ==================== 学习记录表字段（可能为 null）====================
    
    /** 学习记录ID */
    private String recordId;
    
    /** 学生ID */
    private String studentId;
    
    /** 学习内容ID */
    private String contentId;
    
    /** 学习状态（0-未开始，1-学习中，2-已完成） */
    private String learningStatus;
    
    /** 学习时长（秒） */
    private Integer learningTime;
    
    /** 最后学习时间 */
    private Date lastLearnTime;
    
    /** 记录创建时间 */
    private Date recordCreateTime;
}
