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
 * 消息通知实体类，供前后端字段对齐和后端持久化共用。
 * 对应数据库表：message_notice
 * 用于存储系统消息通知信息
 */
@Data
@TableName("message_notice")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class MessageNotice implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID（主键，使用雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     * JSON格式化：GMT+8时区，格式为 yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     * JSON格式化：GMT+8时区，格式为 yyyy-MM-dd HH:mm:ss
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息级别
     */
    private Integer level;

    /**
     * 目标角色
     * 指定哪些角色可以接收此消息
     */
    private String targetRole;

    /**
     * 启用状态
     * 1表示启用，0表示禁用
     */
    private Integer enabled;
}
