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

@Service
public class MessageNoticeServiceImpl extends ServiceImpl<MessageNoticeMapper, MessageNotice> implements IMessageNoticeService {

    @Resource
    private MessageNoticeReadMapper messageNoticeReadMapper;

    @Override
    public List<MessageNoticeUserVO> listForUser(String userId, String role, Integer limit) {
        return baseMapper.listForUser(userId, role, limit);
    }

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
