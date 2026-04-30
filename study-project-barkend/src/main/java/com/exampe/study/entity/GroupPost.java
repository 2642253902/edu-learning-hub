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
 * 小组帖子实体类
 * 对应数据库表：group_post
 */
@Data
@TableName("group_post")
public class GroupPost implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 帖子ID（主键，使用雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 所属小组ID
     */
    private String groupId;
    
    /**
     * 发帖用户ID
     */
    private String userId;
    
    /**
     * 发帖用户名
     */
    private String username;
    
    /**
     * 帖子标题
     */
    private String title;
    
    /**
     * 帖子内容
     */
    private String content;
    
    /**
     * 评论数量
     */
    private Integer commentCount;
    
    /**
     * 发帖时间
     * JSON格式化：GMT+8时区，格式为 yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
