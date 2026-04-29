package com.exampe.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.study.dto.MessageNoticeUserVO;
import com.exampe.study.entity.MessageNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageNoticeMapper extends BaseMapper<MessageNotice> {

    List<MessageNoticeUserVO> listForUser(@Param("userId") String userId,
                                          @Param("role") String role,
                                          @Param("limit") Integer limit);
}
