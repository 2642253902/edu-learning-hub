package com.exampe.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exampe.common.RestBean;
import lombok.extern.slf4j.Slf4j;
import com.exampe.study.entity.CloudComputingStudentLearningRecord;
import com.exampe.study.service.ICloudComputingStudentLearningRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

/**
 * 学生学习记录管理控制器
 * <p>提供学生学习记录的增删改查、学习统计等接口</p>
 *
 * @author jeecg-boot
 * @since 2025-09-22
 */
@RestController
@RequestMapping("/study/cloudComputingStudentLearningRecord")
@Slf4j
public class CloudComputingStudentLearningRecordController {
	
	@Autowired
	private ICloudComputingStudentLearningRecordService cloudComputingStudentLearningRecordService;

	/**
	 * 分页查询指定用户的学习记录
	 *
	 * @param cloudComputingStudentLearningRecord 查询条件对象
	 * @param userId 用户ID（必填）
	 * @param req HTTP请求对象
	 * @return 分页结果
	 */
	@GetMapping(value = "/userlist")
	public RestBean<IPage<CloudComputingStudentLearningRecord>> queryPageList(CloudComputingStudentLearningRecord cloudComputingStudentLearningRecord, 
																			  @RequestParam(name = "userId", required = true) String userId,
																			  HttpServletRequest req) {
		// 使用传入的 userId 参数
		if (userId != null && !userId.isEmpty()) {
			cloudComputingStudentLearningRecord.setUserId(userId);
		}
		QueryWrapper<CloudComputingStudentLearningRecord> queryWrapper = new QueryWrapper<>(cloudComputingStudentLearningRecord);
		Page<CloudComputingStudentLearningRecord> page = new Page<>();
		IPage<CloudComputingStudentLearningRecord> pageList = cloudComputingStudentLearningRecordService.page(page, queryWrapper);
		return RestBean.success(pageList);
	}

	/**
	 * 分页查询学生学习记录列表
	 *
	 * @param cloudComputingStudentLearningRecord 查询条件对象
	 * @param pageNo 页码，默认1
	 * @param pageSize 每页数量，默认10
	 * @param req HTTP请求对象
	 * @return 分页结果
	 */
	@GetMapping(value = "/list")
	public RestBean<IPage<CloudComputingStudentLearningRecord>> queryPageList(CloudComputingStudentLearningRecord cloudComputingStudentLearningRecord,
						@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
						@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
						HttpServletRequest req) {
		QueryWrapper<CloudComputingStudentLearningRecord> queryWrapper = new QueryWrapper<>(cloudComputingStudentLearningRecord);
		Page<CloudComputingStudentLearningRecord> page = new Page<CloudComputingStudentLearningRecord>(pageNo, pageSize);
		IPage<CloudComputingStudentLearningRecord> pageList = cloudComputingStudentLearningRecordService.page(page, queryWrapper);
		return RestBean.success(pageList);
	}

	/**
	 * 新增学生学习记录
	 *
	 * @param cloudComputingStudentLearningRecord 学习记录信息
	 * @param userId 用户ID（必填）
	 * @return 操作结果
	 */
	@PostMapping(value = "/add")
	public RestBean<String> add(@RequestBody CloudComputingStudentLearningRecord cloudComputingStudentLearningRecord, 
								@RequestParam(name = "userId", required = true) String userId) {
		cloudComputingStudentLearningRecord.setUserId(userId);
		cloudComputingStudentLearningRecordService.save(cloudComputingStudentLearningRecord);
		return RestBean.success("添加成功！");
	}

	/**
	 * 编辑学生学习记录
	 *
	 * @param cloudComputingStudentLearningRecord 学习记录信息
	 * @return 操作结果
	 */
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
	public RestBean<String> edit(@RequestBody CloudComputingStudentLearningRecord cloudComputingStudentLearningRecord) {
		cloudComputingStudentLearningRecordService.updateById(cloudComputingStudentLearningRecord);
		return RestBean.success("编辑成功!");
	}

	/**
	 * 删除学生学习记录
	 *
	 * @param id 记录ID
	 * @return 操作结果
	 */
	@DeleteMapping(value = "/delete")
	public RestBean<String> delete(@RequestParam(name = "id", required = true) String id) {
		cloudComputingStudentLearningRecordService.removeById(id);
		return RestBean.success("删除成功!");
	}

	/**
	 * 批量删除学生学习记录
	 *
	 * @param ids 记录ID列表，逗号分隔
	 * @return 操作结果
	 */
	@DeleteMapping(value = "/deleteBatch")
	public RestBean<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.cloudComputingStudentLearningRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return RestBean.success("批量删除成功!");
	}

	/**
	 * 根据ID查询学生学习记录详情
	 *
	 * @param id 记录ID
	 * @return 学习记录信息
	 */
	@GetMapping(value = "/queryById")
	public RestBean<CloudComputingStudentLearningRecord> queryById(@RequestParam(name = "id", required = true) String id) {
		CloudComputingStudentLearningRecord cloudComputingStudentLearningRecord = cloudComputingStudentLearningRecordService.getById(id);
		if (cloudComputingStudentLearningRecord == null) {
			return RestBean.failure(404, "学习记录不存在");
		}
		return RestBean.success(cloudComputingStudentLearningRecord);
	}

}
