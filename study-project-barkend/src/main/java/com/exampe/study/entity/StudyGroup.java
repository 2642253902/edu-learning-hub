package com.exampe.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 学习小组实体类
 * 对应数据库表：study_group
 */
@Data
@TableName("study_group")
public class StudyGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 小组ID（主键，使用雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 小组名称
     */
    private String name;

    /**
     * 小组描述
     */
    private String description;

    /**
     * 创建者用户ID
     */
    private String ownerId;

    /**
     * 创建者用户名
     */
    private String ownerName;

    /**
     * 创建时间
     * JSON格式化：GMT+8时区，格式为 yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
