package com.exampe.sys.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户端消息通知视图对象（VO），供前端消息中心与后端消息查询结果字段对齐。
 * 用于向用户展示消息信息及已读状态。
 */
@Data
public class MessageNoticeUserVO {

    /**
     * 消息ID
     */
    private String id;

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
     * 未读状态标识
     * 1表示未读，0表示已读
     */
    private Integer unread;
}
