package com.exampe.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.sys.dto.MessageNoticeUserVO;
import com.exampe.sys.entity.MessageNotice;

import java.util.List;

/**
 * 消息通知服务接口，供前端消息中心和后端消息读写流程共用。
 */
public interface IMessageNoticeService extends IService<MessageNotice> {

    /**
     * 查询指定用户可见的消息列表，供前端消息中心渲染。
     */
    List<MessageNoticeUserVO> listForUser(String userId, String role, Integer limit);

    /**
     * 标记单条消息已读，供前端消息点击后同步状态。
     */
    boolean markRead(String messageId, String userId);

    /**
     * 批量标记全部消息已读，供前端一键已读操作使用。
     */
    int markAllRead(String userId, String role);
}
