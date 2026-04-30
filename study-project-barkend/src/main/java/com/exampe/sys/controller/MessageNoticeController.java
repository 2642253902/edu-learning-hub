package com.exampe.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exampe.auth.entity.user.AccountUser;
import com.exampe.common.RestBean;
import com.exampe.sys.entity.MessageNotice;
import com.exampe.sys.service.IMessageNoticeService;
import com.exampe.sys.dto.MessageNoticeUserVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 消息通知控制器
 * 提供消息通知的管理和用户相关接口
 */
@RestController
@RequestMapping("/api/message")
public class MessageNoticeController {

    @Resource
    private IMessageNoticeService messageNoticeService;

    /**
     * 管理端：分页查询消息列表
     * 支持按标题、级别、启用状态筛选
     *
     * @param query    查询条件
     * @param pageNo   页码，默认1
     * @param pageSize 每页数量，默认10
     * @return 分页消息列表
     */
    @GetMapping("/manage/list")
    public RestBean<IPage<MessageNotice>> manageList(MessageNotice query,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<MessageNotice> wrapper = new QueryWrapper<>();
        if (query.getTitle() != null && !query.getTitle().isBlank()) {
            wrapper.like("title", query.getTitle().replace("*", ""));
        }
        if (query.getLevel() != null) {
            wrapper.eq("level", query.getLevel());
        }
        if (query.getEnabled() != null) {
            wrapper.eq("enabled", query.getEnabled());
        }
        wrapper.orderByDesc("create_time");
        Page<MessageNotice> page = new Page<>(pageNo, pageSize);
        return RestBean.success(messageNoticeService.page(page, wrapper));
    }

    /**
     * 管理端：发布新消息
     *
     * @param notice      消息内容
     * @param accountUser 当前操作用户
     * @return 操作结果
     */
    @PostMapping("/manage/add")
    public RestBean<String> add(@RequestBody MessageNotice notice, @SessionAttribute("account") AccountUser accountUser) {
        notice.setCreateBy(accountUser.getUsername());
        notice.setCreateTime(new Date());
        notice.setEnabled(notice.getEnabled() == null ? 1 : notice.getEnabled());
        notice.setLevel(notice.getLevel() == null ? 1 : notice.getLevel());
        messageNoticeService.save(notice);
        return RestBean.success("发布成功");
    }

    /**
     * 管理端：编辑消息
     *
     * @param notice      更新的消息内容
     * @param accountUser 当前操作用户
     * @return 操作结果
     */
    @RequestMapping(value = "/manage/edit", method = {RequestMethod.POST, RequestMethod.PUT})
    public RestBean<String> edit(@RequestBody MessageNotice notice, @SessionAttribute("account") AccountUser accountUser) {
        notice.setUpdateBy(accountUser.getUsername());
        notice.setUpdateTime(new Date());
        messageNoticeService.updateById(notice);
        return RestBean.success("编辑成功");
    }

    /**
     * 管理端：删除消息
     *
     * @param id 消息ID
     * @return 操作结果
     */
    @DeleteMapping("/manage/delete")
    public RestBean<String> delete(@RequestParam("id") String id) {
        messageNoticeService.removeById(id);
        return RestBean.success("删除成功");
    }

    /**
     * 管理端：批量删除消息
     *
     * @param ids 消息ID列表，以逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/manage/deleteBatch")
    public RestBean<String> deleteBatch(@RequestParam("ids") String ids) {
        messageNoticeService.removeByIds(Arrays.asList(ids.split(",")));
        return RestBean.success("批量删除成功");
    }

    /**
     * 用户端：获取消息列表
     *
     * @param limit       消息数量限制，默认20
     * @param accountUser 当前用户
     * @return 消息列表（包含已读状态）
     */
    @GetMapping("/user/list")
    public RestBean<List<MessageNoticeUserVO>> userList(@RequestParam(name = "limit", defaultValue = "20") Integer limit,
                                                        @SessionAttribute("account") AccountUser accountUser) {
        return RestBean.success(messageNoticeService.listForUser(accountUser.getId(), accountUser.getRole(), limit));
    }

    /**
     * 用户端：标记单条消息为已读
     *
     * @param messageId   消息ID
     * @param accountUser 当前用户
     * @return 操作结果
     */
    @PostMapping("/user/read")
    public RestBean<String> markRead(@RequestParam("messageId") String messageId,
                                     @SessionAttribute("account") AccountUser accountUser) {
        boolean ok = messageNoticeService.markRead(messageId, accountUser.getId());
        return ok ? RestBean.success("已读") : RestBean.failure(500, "标记失败");
    }

    /**
     * 用户端：标记所有消息为已读
     *
     * @param accountUser 当前用户
     * @return 操作结果及已读数量
     */
    @PostMapping("/user/readAll")
    public RestBean<String> markAllRead(@SessionAttribute("account") AccountUser accountUser) {
        int count = messageNoticeService.markAllRead(accountUser.getId(), accountUser.getRole());
        return RestBean.success("已读 " + count + " 条消息");
    }
}
