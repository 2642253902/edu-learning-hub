package com.exampe.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exampe.auth.entity.user.AccountUser;
import com.exampe.study.entity.CloudComputingCourseResource;
import com.exampe.study.service.ICloudComputingCourseResourceService;
import com.exampe.sys.controller.FileUploadController;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.exampe.common.RestBean;
import com.exampe.study.entity.CloudComputingCourse;
import com.exampe.study.service.ICloudComputingCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 云计算课程管理控制器
 * <p>提供课程的增删改查、教师信息查询、课程状态管理等接口</p>
 *
 * @author admin
 */
@RestController
@RequestMapping("/study/cloudComputingCourse")
@Slf4j
public class CloudComputingCourseController {

    @Autowired
    private ICloudComputingCourseService cloudComputingCourseService;


    /**
     * 根据用户ID查询教师信息
     *
     * @param userId 用户ID
     * @return 教师信息
     */
    @GetMapping(value = "/addByUser")
    public RestBean<Map<String, Object>> addByUser(@RequestParam(name = "userId", required = true) String userId) {
        List<Map> teacher = cloudComputingCourseService.getTeacher();
        for (Map i : teacher) {
            if (i.get("value").equals(userId)) {
                return RestBean.success((Map<String, Object>) i);
            }
        }
        return RestBean.success(null);

    }

    /**
     * 更新课程状态
     *
     * @param courseId 课程ID
     * @param status   课程状态
     * @return 操作结果
     */
    @PostMapping(value = "/editCourseStatus/{courseId}/{status}")
    public RestBean<String> editCourseStatus(@PathVariable String courseId, @PathVariable int status) {
        CloudComputingCourse byId = cloudComputingCourseService.getById(courseId);
        byId.setCourseStatus(status);
        cloudComputingCourseService.updateById(byId);
        return RestBean.success("更新成功!");
    }


    /**
     * 查询所有教师列表
     *
     * @return 教师列表
     */
    @GetMapping(value = "/queryById/teacher")
    public RestBean<?> getTeacher() {
        List<?> teacherList = cloudComputingCourseService.getTeacher();
        if (teacherList == null) {
            return RestBean.failure(500, "查询教师列表失败");
        }
        return RestBean.success(teacherList);
    }

    /**
     * 根据教师ID分页查询课程列表
     *
     * @param cloudComputingCourse 查询条件对象
     * @param teacherId            教师ID（可选）
     * @param pageNo               页码，默认1
     * @param pageSize             每页数量，默认10
     * @param req                  HTTP请求对象
     * @return 分页结果
     */
    @GetMapping(value = "/listByTeacherId")
    public RestBean<IPage<CloudComputingCourse>> queryPageListByTeacherId(CloudComputingCourse cloudComputingCourse,
                                                                          @RequestParam(name = "teacherId", required = false) String teacherId,
                                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                          HttpServletRequest req) {
        QueryWrapper<CloudComputingCourse> queryWrapper = new QueryWrapper<>(cloudComputingCourse);
        if (teacherId != null && !teacherId.isEmpty()) {
            queryWrapper.eq("teacher_id", teacherId);
        }
        Page<CloudComputingCourse> page = new Page<>(pageNo, pageSize);
        IPage<CloudComputingCourse> pageList = cloudComputingCourseService.page(page, queryWrapper);
        return RestBean.success(pageList);
    }

    @Resource
    FileUploadController fileUploadController;

    @Resource
    ICloudComputingCourseResourceService cloudComputingCourseResourceService;

    /**
     * 删除课程及其关联数据
     * <p>会同时删除课程相关的资源和学习记录</p>
     *
     * @param courseId 课程ID
     * @return 操作结果
     */
    @DeleteMapping(value = "/deleteCourse")
    public RestBean<String> deleteCourse(@RequestParam(name = "courseId", required = true) String courseId) {
        // 查询该课程的所有资源
        List<CloudComputingCourseResource> resourceList =
                cloudComputingCourseResourceService.list(
                        new QueryWrapper<CloudComputingCourseResource>().eq("course_id", courseId)
                );

        // 提取所有需要删除的文件路径
        List<String> fileNames = resourceList.stream()
                .map(CloudComputingCourseResource::getResourceUrl)
                .filter(url -> url != null && !url.isBlank())
                .collect(Collectors.toList());

        // 如果有文件需要删除，调用批量删除接口
        if (!fileNames.isEmpty()) {
            try {
                RestBean<Object> batchDeleteResult = fileUploadController.batchDeleteFiles(fileNames);

                // 检查批量删除是否全部成功
                if (!batchDeleteResult.isSuccess()) {
                    log.warn("课程ID: {} 的部分资源文件删除失败: {}", courseId, batchDeleteResult.getMessage());
                    // 不阻断课程删除流程，继续执行
                } else {
                    log.info("课程ID: {} 的 {} 个资源文件删除成功", courseId, fileNames.size());
                }
            } catch (Exception e) {
                log.error("课程ID: {} 的资源文件批量删除异常", courseId, e);
                // 不阻断课程删除流程，继续执行
            }
        }

        // 删除课程及其关联的学习记录（在 Service 层处理）
        cloudComputingCourseService.deleteCourseById(courseId);
        return RestBean.success("删除成功!");
    }


    /**
     * 分页查询课程列表
     * <p>支持按课程名称模糊查询，以及其他字段的精确查询</p>
     *
     * @param cloudComputingCourse 查询条件对象
     * @param pageNo               页码，默认1
     * @param pageSize             每页数量，默认10
     * @param req                  HTTP请求对象
     * @return 分页结果
     */
    @GetMapping(value = "/list")
    public RestBean<IPage<CloudComputingCourse>> queryPageList(CloudComputingCourse cloudComputingCourse,
                                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                               HttpServletRequest req) {
        QueryWrapper<CloudComputingCourse> queryWrapper = new QueryWrapper<>();

        // 处理课程名称模糊查询（忽略空字符串）
        String courseName = cloudComputingCourse.getCourseName();
        if (courseName != null && !courseName.isEmpty()) {
            queryWrapper.like("course_name", courseName.replace("*", ""));
        }

        // 处理课程分类ID精确查询（忽略空字符串）
        String courseTypeId = cloudComputingCourse.getCourseTypeId();
        if (courseTypeId != null && !courseTypeId.isEmpty()) {
            queryWrapper.eq("course_type_id", courseTypeId);
        }

        // 处理教师ID精确查询（忽略空字符串）
        String teacherId = cloudComputingCourse.getTeacherId();
        if (teacherId != null && !teacherId.isEmpty()) {
            queryWrapper.eq("teacher_id", teacherId);
        }

        // 处理课程状态精确查询（忽略 null）
        if (cloudComputingCourse.getCourseStatus() != null) {
            queryWrapper.eq("course_status", cloudComputingCourse.getCourseStatus());
        }

        // 处理课程标签精确查询（忽略空字符串）
        String courseTag = cloudComputingCourse.getCourseTag();
        if (courseTag != null && !courseTag.isEmpty()) {
            queryWrapper.eq("course_tag", courseTag);
        }

        Page<CloudComputingCourse> page = new Page<>(pageNo, pageSize);
        IPage<CloudComputingCourse> pageList = cloudComputingCourseService.page(page, queryWrapper);
        return RestBean.success(pageList);
    }

    /**
     * 新增课程
     *
     * @param cloudComputingCourse 课程信息
     * @return 操作结果
     */
    @PostMapping(value = "/add")
    public RestBean<String> add(@RequestBody CloudComputingCourse cloudComputingCourse, @SessionAttribute("account") AccountUser accountUser) {
        cloudComputingCourse.setCreateBy(accountUser.getUsername());
        cloudComputingCourse.setCreateTime(new Date());
        cloudComputingCourseService.save(cloudComputingCourse);
        return RestBean.success("添加成功！");
    }


    /**
     * 编辑课程信息
     *
     * @param cloudComputingCourse 课程信息
     * @return 操作结果
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public RestBean<String> edit(@RequestBody CloudComputingCourse cloudComputingCourse, @SessionAttribute("account") AccountUser accountUser) {
        cloudComputingCourse.setUpdateBy(accountUser.getUsername());
        cloudComputingCourse.setUpdateTime(new Date());
        cloudComputingCourseService.updateById(cloudComputingCourse);
        return RestBean.success("编辑成功!");
    }

    /**
     * 删除课程
     *
     * @param id 课程ID
     * @return 操作结果
     */
    @DeleteMapping(value = "/delete")
    public RestBean<String> delete(@RequestParam(name = "id", required = true) String id) {
        cloudComputingCourseService.removeById(id);
        return RestBean.success("删除成功!");
    }

    /**
     * 批量删除课程
     *
     * @param ids 课程ID列表，逗号分隔
     * @return 操作结果
     */
    @DeleteMapping(value = "/deleteBatch")
    public RestBean<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.cloudComputingCourseService.removeByIds(Arrays.asList(ids.split(",")));
        return RestBean.success("批量删除成功!");
    }

    /**
     * 根据ID查询课程详情
     *
     * @param id 课程ID
     * @return 课程信息
     */
    @GetMapping(value = "/queryById")
    public RestBean<CloudComputingCourse> queryById(@RequestParam(name = "id", required = true) String id) {
        CloudComputingCourse cloudComputingCourse = cloudComputingCourseService.getById(id);
        if (cloudComputingCourse == null) {
            return RestBean.failure(404, "课程不存在");
        }
        return RestBean.success(cloudComputingCourse);
    }


}
