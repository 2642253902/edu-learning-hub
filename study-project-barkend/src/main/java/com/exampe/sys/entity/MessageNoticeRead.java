package com.exampe.sys.entity;

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
 * 消息通知已读记录实体类，供前后端字段对齐和后端持久化共用。
 * 对应数据库表：message_notice_read
 * 用于记录用户阅读消息的状态
 */
@Data
@TableName("message_notice_read")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class MessageNoticeRead implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID（主键，使用雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 阅读时间
     * JSON格式化：GMT+8时区，格式为 yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;
}
