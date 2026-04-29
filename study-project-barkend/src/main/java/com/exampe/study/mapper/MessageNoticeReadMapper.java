package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.entity.MessageNoticeRead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageNoticeReadMapper extends BaseMapper<MessageNoticeRead> {

    Integer countRead(@Param("messageId") String messageId, @Param("userId") String userId);
}
