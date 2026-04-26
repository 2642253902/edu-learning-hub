package com.exampe.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.exampe.common.RestBean;
import com.exampe.study.entity.CloudComputingCourseType;
import com.exampe.study.service.ICloudComputingCourseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

/**
 * 云计算课程分类管理控制器
 * <p>提供课程分类的增删改查接口，支持模糊查询</p>
 *
 * @author jeecg-boot
 * @since 2025-09-20
 */
@RestController
@RequestMapping("/study/cloudComputingCourseType")
@Slf4j
public class CloudComputingCourseTypeController {
    
    @Autowired
    private ICloudComputingCourseTypeService cloudComputingCourseTypeService;

    /**
     * 分页查询课程分类列表
     * <p>支持按分类名称模糊查询（自动去除*号），以及其他字段的精确查询</p>
     *
     * @param cloudComputingCourseType 查询条件对象
     * @param pageNo 页码，默认1
     * @param pageSize 每页数量，默认10
     * @param req HTTP请求对象
     * @return 分页结果
     */
    @GetMapping(value = "/list")
    public RestBean<IPage<CloudComputingCourseType>> queryPageList(CloudComputingCourseType cloudComputingCourseType,
                                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                   HttpServletRequest req) {
        QueryWrapper<CloudComputingCourseType> queryWrapper = new QueryWrapper<>();
        
        // 保留原有"有值才等值查询"的行为，对课程分类名称进行模糊查询
        String courseTypeName = cloudComputingCourseType.getCourseTypeName();
        if (courseTypeName != null && !courseTypeName.isEmpty()) {
            queryWrapper.like("course_type_name", courseTypeName.replace("*", ""));
            cloudComputingCourseType.setCourseTypeName(null); // 清除实体中的值，避免重复查询条件
        }
        queryWrapper.setEntity(cloudComputingCourseType);
        
        Page<CloudComputingCourseType> page = new Page<>(pageNo, pageSize);
        IPage<CloudComputingCourseType> pageList = cloudComputingCourseTypeService.page(page, queryWrapper);
        return RestBean.success(pageList);
    }

    /**
     * 新增课程分类
     *
     * @param cloudComputingCourseType 课程分类信息
     * @return 操作结果
     */
    @PostMapping(value = "/add")
    public RestBean<String> add(@RequestBody CloudComputingCourseType cloudComputingCourseType) {
        cloudComputingCourseTypeService.save(cloudComputingCourseType);
        return RestBean.success("添加成功！");
    }

    /**
     * 编辑课程分类
     *
     * @param cloudComputingCourseType 课程分类信息
     * @return 操作结果
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public RestBean<String> edit(@RequestBody CloudComputingCourseType cloudComputingCourseType) {
        cloudComputingCourseTypeService.updateById(cloudComputingCourseType);
        return RestBean.success("编辑成功!");
    }

    /**
     * 删除课程分类
     *
     * @param id 分类ID
     * @return 操作结果
     */
    @DeleteMapping(value = "/delete")
    public RestBean<String> delete(@RequestParam(name = "id", required = true) String id) {
        cloudComputingCourseTypeService.removeById(id);
        return RestBean.success("删除成功!");
    }

    /**
     * 批量删除课程分类
     *
     * @param ids 分类ID列表，逗号分隔
     * @return 操作结果
     */
    @DeleteMapping(value = "/deleteBatch")
    public RestBean<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.cloudComputingCourseTypeService.removeByIds(Arrays.asList(ids.split(",")));
        return RestBean.success("批量删除成功!");
    }

    /**
     * 根据ID查询课程分类详情
     *
     * @param id 分类ID
     * @return 课程分类信息
     */
    @GetMapping(value = "/queryById")
    public RestBean<CloudComputingCourseType> queryById(@RequestParam(name = "id", required = true) String id) {
        CloudComputingCourseType cloudComputingCourseType = cloudComputingCourseTypeService.getById(id);
        if (cloudComputingCourseType == null) {
            return RestBean.failure(404, "课程分类不存在");
        }
        return RestBean.success(cloudComputingCourseType);
    }

}
