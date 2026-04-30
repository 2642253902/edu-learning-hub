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
 * 帖子评论实体类
 * 对应数据库表：post_comment
 */
@Data
@TableName("post_comment")
public class PostComment implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 评论ID（主键，使用雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 所属帖子ID
     */
    private String postId;

    /**
     * 评论用户ID
     */
    private String userId;

    /**
     * 评论用户名
     */
    private String username;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论时间
     * JSON格式化：GMT+8时区，格式为 yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
