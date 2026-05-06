package com.exampe.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exampe.auth.entity.user.AccountUser;
import com.exampe.common.RestBean;
import com.exampe.sys.controller.FileUploadController;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.exampe.study.entity.CloudComputingCourseResource;
import com.exampe.study.service.ICloudComputingCourseResourceService;
import com.exampe.study.dto.CloudComputingCourseResourceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 云计算课程资源管理控制器，负责对接前端页面请求和后端业务逻辑。
 * <p>提供课程资源的增删改查、资源统计、学习状态查询等接口</p>
 *
 * @author admin
 */
@RestController
@RequestMapping("/study/cloudComputingCourseResource")
@Slf4j
public class CloudComputingCourseResourceController {

    @Autowired
    private ICloudComputingCourseResourceService
            cloudComputingCourseResourceService;

    /**
     * 根据课程ID统计各资源类型数量
     * <p>返回视频、讲义、实验等各类资源的数量统计</p>
     *
     * @param id 课程ID
     * @return 资源类型统计结果，key为资源类型，value为数量
     */
    @GetMapping(value = "/counts")
    public RestBean<Map<String, Long>> getResourceCounts(
            @RequestParam(name = "id", required = true) String id) {
        Map<String, Long> counts =
                cloudComputingCourseResourceService.countByCourseIdGrouped(id);
        return RestBean.success(counts);
    }

    /**
     * 查询课程资源列表（含学生学习状态）
     * <p>返回指定课程的资源列表及当前学生的学习记录状态</p>
     *
     * @param courseId    课程ID
     * @param accountUser 当前登录用户（从Session获取）
     * @return 资源列表（包含学习状态）
     */
    @GetMapping(value = "/listWithStatus")
    public RestBean<List<CloudComputingCourseResourceVO>> listWithStatus(@RequestParam(name = "courseId", required = true) String courseId,
                                                                         @SessionAttribute("account") AccountUser accountUser) {
        List<CloudComputingCourseResourceVO> list =
                cloudComputingCourseResourceService.listWithLearningStatus(courseId, accountUser.getId());
        return RestBean.success(list);
    }

    /**
     * 根据ID查询课程资源详情
     *
     * @param id 资源ID
     * @return 资源信息
     */
    @GetMapping(value = "/queryById")
    public RestBean<CloudComputingCourseResource> queryById(@RequestParam(name = "id", required = true) String id) {
        CloudComputingCourseResource cloudComputingCourseResource = cloudComputingCourseResourceService.getById(id);
        if (cloudComputingCourseResource == null) {
            return RestBean.failure(404, "课程资源不存在");
        }
        return RestBean.success(cloudComputingCourseResource);
    }


    /**
     * 分页查询课程资源列表
     *
     * @param cloudComputingCourseResource 查询条件对象
     * @param pageNo                       页码，默认1
     * @param pageSize                     每页数量，默认10
     * @return 分页结果
     */
    @GetMapping(value = "/list")
    public RestBean<IPage<CloudComputingCourseResource>> queryPageList(
            CloudComputingCourseResource cloudComputingCourseResource,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<CloudComputingCourseResource> page = new Page<>(pageNo, pageSize);
        QueryWrapper<CloudComputingCourseResource> queryWrapper = new QueryWrapper<>(cloudComputingCourseResource);
        IPage<CloudComputingCourseResource> pageList = cloudComputingCourseResourceService.page(page, queryWrapper);
        return RestBean.success(pageList);
    }

    /**
     * 新增课程资源
     *
     * @param cloudComputingCourseResource 资源信息
     * @return 操作结果
     */
    @PostMapping(value = "/add")
    public RestBean<String> add(@RequestBody CloudComputingCourseResource cloudComputingCourseResource, @SessionAttribute("account") AccountUser accountUser) {
        cloudComputingCourseResource.setCreateBy(accountUser.getUsername());
        cloudComputingCourseResource.setCreateTime(new Date());
        cloudComputingCourseResourceService.save(cloudComputingCourseResource);
        return RestBean.success("添加成功！");
    }

    /**
     * 编辑课程资源
     *
     * @param cloudComputingCourseResource 资源信息
     * @return 操作结果
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public RestBean<String> edit(@RequestBody CloudComputingCourseResource cloudComputingCourseResource, @SessionAttribute("account") AccountUser accountUser) {
        cloudComputingCourseResource.setUpdateBy(accountUser.getUsername());
        cloudComputingCourseResource.setUpdateTime(new Date());
        cloudComputingCourseResourceService.updateById(cloudComputingCourseResource);
        return RestBean.success("编辑成功!");
    }

    @Resource
    FileUploadController fileUploadController;

    /**
     * 删除课程资源
     *
     * @return 操作结果
     */
    @DeleteMapping(value = "/delete")
    public RestBean<String> delete(@RequestParam(name = "id", required = true) String id) {
        CloudComputingCourseResource resource = cloudComputingCourseResourceService.getById(id);
        if (resource == null) {
            return RestBean.failure(404, "课程资源不存在");
        }

        String resourceUrl = resource.getResourceUrl();
        if (resourceUrl != null && !resourceUrl.isBlank()) {
            try {
                RestBean<String> stringRestBean = fileUploadController.deleteFile(resourceUrl);
                if (!stringRestBean.isSuccess()) {
                    if (stringRestBean.getStatus() == 404) {
                        boolean removed = cloudComputingCourseResourceService.removeById(id);
                        return removed
                                ? RestBean.success("文件不存在，已删除资源记录")
                                : RestBean.failure(500, "删除失败");
                    }
                    return RestBean.failure(500, "文件删除失败：" + stringRestBean.getMessage());
                }
            } catch (Exception e) {
                return RestBean.failure(500, "文件删除异常：" + e.getMessage());
            }
        }

        boolean removed = cloudComputingCourseResourceService.removeById(id);
        return removed ? RestBean.success("删除成功!") : RestBean.failure(500, "删除失败");
    }

    /**
     * 批量删除课程资源
     *
     * @param ids 资源ID列表，逗号分隔
     * @return 操作结果
     */
    @DeleteMapping(value = "/deleteBatch")
    public RestBean<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        List<String> idList = Arrays.asList(ids.split(","));

        // 查询所有要删除的资源
        List<CloudComputingCourseResource> resourceList = cloudComputingCourseResourceService.listByIds(idList);

        // 提取所有需要删除的文件路径
        List<String> fileNames = resourceList.stream()
                .map(CloudComputingCourseResource::getResourceUrl)
                .filter(url -> url != null && !url.isBlank())
                .collect(Collectors.toList());

        // 如果有文件需要删除，调用批量删除接口
        if (!fileNames.isEmpty()) {
            try {
                RestBean<Object> batchDeleteResult = fileUploadController.batchDeleteFiles(fileNames);

                if (!batchDeleteResult.isSuccess()) {
                    Object data = batchDeleteResult.getData();
                    boolean hasMissingFiles = false;
                    boolean hasOtherFailures = false;
                    if (data instanceof Map<?, ?> resultMap) {
                        Object failedFiles = resultMap.get("failedFiles");
                        if (failedFiles instanceof List<?> failedList) {
                            for (Object item : failedList) {
                                if (item instanceof Map<?, ?> failedInfo) {
                                    Object reason = failedInfo.get("reason");
                                    String reasonText = reason == null ? "" : reason.toString();
                                    if (reasonText.contains("文件不存在")) {
                                        hasMissingFiles = true;
                                    } else {
                                        hasOtherFailures = true;
                                        break;
                                    }
                                } else {
                                    hasOtherFailures = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (hasOtherFailures) {
                        return RestBean.failure(500, "文件删除失败：" + batchDeleteResult.getMessage());
                    }

                    if (hasMissingFiles) {
                        boolean removed = cloudComputingCourseResourceService.removeByIds(idList);
                        return removed
                                ? RestBean.success("部分文件不存在，已删除资源记录")
                                : RestBean.failure(500, "批量删除失败");
                    }

                    return RestBean.failure(500, "文件删除失败：" + batchDeleteResult.getMessage());
                }
            } catch (Exception e) {
                return RestBean.failure(500, "批量删除资源文件异常：" + e.getMessage());
            }
        }

        // 删除数据库中的资源记录
        boolean removed = cloudComputingCourseResourceService.removeByIds(idList);
        return removed ? RestBean.success("批量删除成功!") : RestBean.failure(500, "批量删除失败");
    }


}
