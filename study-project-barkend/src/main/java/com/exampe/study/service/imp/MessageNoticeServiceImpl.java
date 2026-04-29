package com.exampe.study.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exampe.study.dto.MessageNoticeUserVO;
import com.exampe.study.entity.MessageNotice;
import com.exampe.study.entity.MessageNoticeRead;
import com.exampe.study.mapper.MessageNoticeMapper;
import com.exampe.study.mapper.MessageNoticeReadMapper;
import com.exampe.study.service.IMessageNoticeService;
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
