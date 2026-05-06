package com.exampe.study.controller;

import com.exampe.auth.entity.user.AccountUser;
import com.exampe.common.RestBean;
import com.exampe.study.entity.ResourceReview;
import com.exampe.study.service.IResourceReviewService;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Date;
import java.util.List;

/**
 * 学习模块课程评价控制器，统一为课程详情、备课中心和评价管理页提供接口。
 */
@RestController
@RequestMapping("/api/study/reviews")
public class CourseReviewController {

    @Resource
    private IResourceReviewService resourceReviewService;

    @PostMapping
    public RestBean<ResourceReview> addReview(@RequestBody ResourceReview review,
                                              @SessionAttribute("account") AccountUser accountUser) {
        if (!StringUtils.hasText(review.getContent())) {
            return RestBean.failure(400, "评价内容不能为空");
        }
        review.setUserId(accountUser.getId());
        review.setUsername(accountUser.getUsername());
        review.setCreateTime(new Date());
        if (review.getLikes() == null) {
            review.setLikes(0);
        }
        resourceReviewService.save(review);
        return RestBean.success(review);
    }

    @GetMapping
    public RestBean<List<ResourceReview>> listReviews(@RequestParam String courseId) {
        return RestBean.success(resourceReviewService.listByCourseId(courseId));
    }

    @GetMapping("/all")
    public RestBean<List<ResourceReview>> listAllReviews() {
        return RestBean.success(resourceReviewService.list());
    }

    @PostMapping("/{id}/like")
    public RestBean<String> likeReview(@PathVariable String id) {
        return resourceReviewService.likeReview(id) ? RestBean.success("点赞成功") : RestBean.failure(404, "未找到评论");
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.POST, RequestMethod.PUT})
    public RestBean<ResourceReview> updateReview(@PathVariable String id,
                                                 @RequestBody ResourceReview review,
                                                 @SessionAttribute("account") AccountUser accountUser) {
        review.setId(id);
        boolean ok = resourceReviewService.updateById(review);
        return ok ? RestBean.success(review) : RestBean.failure(500, "更新失败");
    }

    @DeleteMapping("/{id}")
    public RestBean<String> deleteReview(@PathVariable String id) {
        boolean ok = resourceReviewService.removeById(id);
        return ok ? RestBean.success("删除成功") : RestBean.failure(500, "删除失败");
    }
}