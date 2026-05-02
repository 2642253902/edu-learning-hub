package com.exampe.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.sys.dto.MessageNoticeUserVO;
import com.exampe.sys.entity.MessageNotice;
import com.exampe.sys.entity.MessageNoticeRead;
import com.exampe.sys.mapper.MessageNoticeMapper;
import com.exampe.sys.mapper.MessageNoticeReadMapper;
import com.exampe.sys.service.IMessageNoticeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 消息通知服务实现，负责前端消息中心与后端已读记录表之间的状态同步。
 */
@Service
public class MessageNoticeServiceImpl extends ServiceImpl<MessageNoticeMapper, MessageNotice> implements IMessageNoticeService {

    @Resource
    private MessageNoticeReadMapper messageNoticeReadMapper;

    /**
     * 查询用户消息列表并附带未读状态，供前端列表展示与角标统计。
     */
    @Override
    public List<MessageNoticeUserVO> listForUser(String userId, String role, Integer limit) {
        return baseMapper.listForUser(userId, role, limit);
    }

    /**
     * 标记单条消息已读，避免前端重复提交产生重复记录。
     */
    @Override
    public boolean markRead(String messageId, String userId) {
        Integer count = messageNoticeReadMapper.countRead(messageId, userId);
        if (count != null && count > 0) {
            return true;
        }
        MessageNoticeRead read = new MessageNoticeRead();
        read.setMessageId(messageId);
        read.setUserId(userId);
        read.setReadTime(new Date());
        return messageNoticeReadMapper.insert(read) > 0;
    }

    /**
     * 批量标记当前用户全部未读消息为已读，供前端一键已读调用。
     */
    @Override
    public int markAllRead(String userId, String role) {
        List<MessageNoticeUserVO> list = baseMapper.listForUser(userId, role, 500);
        int total = 0;
        for (MessageNoticeUserVO item : list) {
            if (item.getUnread() != null && item.getUnread() == 1) {
                if (markRead(item.getId(), userId)) {
                    total++;
                }
            }
        }
        return total;
    }
}
