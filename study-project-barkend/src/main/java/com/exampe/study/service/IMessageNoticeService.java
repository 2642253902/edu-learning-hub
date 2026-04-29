package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.dto.MessageNoticeUserVO;
import com.exampe.study.entity.MessageNotice;

import java.util.List;

public interface IMessageNoticeService extends IService<MessageNotice> {

    List<MessageNoticeUserVO> listForUser(String userId, String role, Integer limit);

    boolean markRead(String messageId, String userId);

    int markAllRead(String userId, String role);
}
