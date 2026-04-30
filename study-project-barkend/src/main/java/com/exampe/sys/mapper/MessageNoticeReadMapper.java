package com.exampe.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.sys.entity.MessageNoticeRead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 消息通知已读记录Mapper接口
 * 提供消息已读记录相关的数据库操作
 */
@Mapper
public interface MessageNoticeReadMapper extends BaseMapper<MessageNoticeRead> {

    /**
     * 统计用户对某条消息的已读记录数量
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 已读记录数量（0表示未读，大于0表示已读）
     */
    Integer countRead(@Param("messageId") String messageId, @Param("userId") String userId);
}
