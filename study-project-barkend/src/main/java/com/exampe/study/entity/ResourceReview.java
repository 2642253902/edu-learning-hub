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
 * 课程评价实体类，供前后端字段对齐和后端持久化共用。
 * 对应数据库表：resource_review
 */
@Data
@TableName("resource_review")
public class ResourceReview implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 评价ID（主键，使用雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 评价用户ID
     */
    private String userId;

    /**
     * 评价用户名
     */
    private String username;

    /**
     * 评分（通常为1-5分）
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 评价时间
     * JSON格式化：GMT+8时区，格式为 yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
