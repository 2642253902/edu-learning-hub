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
 * 帖子评论实体类，供前端评论区展示和后端持久化共用。
 */
@Data
@TableName("post_comment")
public class PostComment implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 评论ID，前后端通过它关联同一条评论记录。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 所属帖子ID，前端详情页和后端查询接口都按它定位评论。
     */
    private String postId;

    /**
     * 评论用户ID，前端显示作者信息、后端保存归属关系时共用。
     */
    private String userId;

    /**
     * 评论用户名，供前端列表直接展示。
     */
    private String username;

    /**
     * 评论内容，前端输入后由后端保存并回显。
     */
    private String content;

    /**
     * 评论时间，前端列表展示和后端序列化都按同一时间格式处理。
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
