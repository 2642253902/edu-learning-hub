package com.exampe.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exampe.sys.dto.MessageNoticeUserVO;
import com.exampe.sys.entity.MessageNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息通知Mapper 接口，供前端查询需求与后端持久层共用。
 * 提供消息通知相关的数据库操作
 */
@Mapper
public interface MessageNoticeMapper extends BaseMapper<MessageNotice> {

    /**
     * 查询用户可见的消息列表（包含已读状态）
     * 根据用户ID和角色筛选适用的消息
     * @param userId 用户ID
     * @param role 用户角色
     * @param limit 返回数量限制
     * @return 消息列表（包含未读标识）
     */
    List<MessageNoticeUserVO> listForUser(@Param("userId") String userId,
                                          @Param("role") String role,
                                          @Param("limit") Integer limit);
}
