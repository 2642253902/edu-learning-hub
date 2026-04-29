package com.exampe.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exampe.auth.entity.user.AccountUser;
import com.exampe.common.RestBean;
import com.exampe.study.dto.MessageNoticeUserVO;
import com.exampe.study.entity.MessageNotice;
import com.exampe.study.service.IMessageNoticeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageNoticeController {

    @Resource
    private IMessageNoticeService messageNoticeService;

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

    @PostMapping("/manage/add")
    public RestBean<String> add(@RequestBody MessageNotice notice, @SessionAttribute("account") AccountUser accountUser) {
        notice.setCreateBy(accountUser.getUsername());
        notice.setCreateTime(new Date());
        notice.setEnabled(notice.getEnabled() == null ? 1 : notice.getEnabled());
        notice.setLevel(notice.getLevel() == null ? 1 : notice.getLevel());
        messageNoticeService.save(notice);
        return RestBean.success("发布成功");
    }

    @RequestMapping(value = "/manage/edit", method = {RequestMethod.POST, RequestMethod.PUT})
    public RestBean<String> edit(@RequestBody MessageNotice notice, @SessionAttribute("account") AccountUser accountUser) {
        notice.setUpdateBy(accountUser.getUsername());
        notice.setUpdateTime(new Date());
        messageNoticeService.updateById(notice);
        return RestBean.success("编辑成功");
    }

    @DeleteMapping("/manage/delete")
    public RestBean<String> delete(@RequestParam("id") String id) {
        messageNoticeService.removeById(id);
        return RestBean.success("删除成功");
    }

    @DeleteMapping("/manage/deleteBatch")
    public RestBean<String> deleteBatch(@RequestParam("ids") String ids) {
        messageNoticeService.removeByIds(Arrays.asList(ids.split(",")));
        return RestBean.success("批量删除成功");
    }

    @GetMapping("/user/list")
    public RestBean<List<MessageNoticeUserVO>> userList(@RequestParam(name = "limit", defaultValue = "20") Integer limit,
                                                        @SessionAttribute("account") AccountUser accountUser) {
        return RestBean.success(messageNoticeService.listForUser(accountUser.getId(), accountUser.getRole(), limit));
    }

    @PostMapping("/user/read")
    public RestBean<String> markRead(@RequestParam("messageId") String messageId,
                                     @SessionAttribute("account") AccountUser accountUser) {
        boolean ok = messageNoticeService.markRead(messageId, accountUser.getId());
        return ok ? RestBean.success("已读") : RestBean.failure(500, "标记失败");
    }

    @PostMapping("/user/readAll")
    public RestBean<String> markAllRead(@SessionAttribute("account") AccountUser accountUser) {
        int count = messageNoticeService.markAllRead(accountUser.getId(), accountUser.getRole());
        return RestBean.success("已读 " + count + " 条消息");
    }
}
