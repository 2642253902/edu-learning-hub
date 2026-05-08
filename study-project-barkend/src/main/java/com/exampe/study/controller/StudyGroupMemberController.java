package com.exampe.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exampe.common.RestBean;
import com.exampe.study.entity.StudyGroupMember;
import com.exampe.study.service.IStudyGroupMemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 学习小组成员管理控制器，提供前端管理视角的 CRUD 接口以及按小组查询成员。
 */
@RestController
@RequestMapping("/study/studyGroupMember")
@Slf4j
public class StudyGroupMemberController {

    @Autowired
    private IStudyGroupMemberService studyGroupMemberService;

    @GetMapping("/list")
    public RestBean<IPage<StudyGroupMember>> list(StudyGroupMember query,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest req) {
        QueryWrapper<StudyGroupMember> qw = new QueryWrapper<>(query);
        Page<StudyGroupMember> page = new Page<>(pageNo, pageSize);
        IPage<StudyGroupMember> pageList = studyGroupMemberService.page(page, qw);
        return RestBean.success(pageList);
    }

    /**
     * 返回指定小组的所有成员（无分页），供详情页使用。
     */
    @GetMapping("/listByGroupId")
    public RestBean<List<StudyGroupMember>> listByGroupId(@RequestParam(name = "groupId", required = true) String groupId) {
        List<StudyGroupMember> members = studyGroupMemberService.listByGroupId(groupId);
        return RestBean.success(members);
    }

    @PostMapping("/add")
    public RestBean<String> add(@RequestBody StudyGroupMember member) {
        member.setCreateTime(new Date());
        studyGroupMemberService.save(member);
        return RestBean.success("添加成功！");
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public RestBean<String> edit(@RequestBody StudyGroupMember member) {
        studyGroupMemberService.updateById(member);
        return RestBean.success("编辑成功!");
    }

    @DeleteMapping("/delete")
    public RestBean<String> delete(@RequestParam(name = "id", required = true) String id) {
        studyGroupMemberService.removeById(id);
        return RestBean.success("删除成功!");
    }

    @DeleteMapping("/deleteBatch")
    public RestBean<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.studyGroupMemberService.removeByIds(Arrays.asList(ids.split(",")));
        return RestBean.success("批量删除成功!");
    }

    @GetMapping("/queryById")
    public RestBean<StudyGroupMember> queryById(@RequestParam(name = "id", required = true) String id) {
        StudyGroupMember member = studyGroupMemberService.getById(id);
        if (member == null) {
            return RestBean.failure(404, "成员不存在");
        }
        return RestBean.success(member);
    }

}
