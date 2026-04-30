package com.exampe.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.sys.dto.MessageNoticeUserVO;
import com.exampe.sys.entity.MessageNotice;

import java.util.List;

public interface IMessageNoticeService extends IService<MessageNotice> {

    List<MessageNoticeUserVO> listForUser(String userId, String role, Integer limit);

    boolean markRead(String messageId, String userId);

    int markAllRead(String userId, String role);
}
