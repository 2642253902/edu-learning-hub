package com.exampe.study.controller;

import com.exampe.auth.entity.user.AccountUser;
import com.exampe.common.RestBean;
import com.exampe.study.entity.GroupPost;
import com.exampe.study.entity.PostComment;
import com.exampe.study.entity.ResourceReview;
import com.exampe.study.entity.StudyGroup;
import com.exampe.study.service.IGroupPostService;
import com.exampe.study.service.IPostCommentService;
import com.exampe.study.service.IResourceReviewService;
import com.exampe.study.service.IStudyGroupService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Resource
    private IStudyGroupService studyGroupService;
    @Resource
    private IGroupPostService groupPostService;
    @Resource
    private IPostCommentService postCommentService;
    @Resource
    private IResourceReviewService resourceReviewService;

    @GetMapping("/groups")
    public RestBean<List<StudyGroup>> listGroups() {
        return RestBean.success(studyGroupService.listGroups());
    }

    @PostMapping("/groups")
    public RestBean<StudyGroup> createGroup(@RequestBody StudyGroup group,
                                            @SessionAttribute("account") AccountUser accountUser) {
        group.setOwnerId(accountUser.getId());
        group.setOwnerName(accountUser.getUsername());
        group.setCreateTime(new Date());
        studyGroupService.save(group);
        // 创建者自动加入小组
        studyGroupService.joinGroup(group.getId(), accountUser.getId(), accountUser.getUsername());
        return RestBean.success(group);
    }

    @PostMapping("/groups/{groupId}/join")
    public RestBean<String> joinGroup(@PathVariable String groupId,
                                      @SessionAttribute("account") AccountUser accountUser) {
        boolean ok = studyGroupService.joinGroup(groupId, accountUser.getId(), accountUser.getUsername());
        return ok ? RestBean.success("加入成功") : RestBean.failure(500, "加入失败");
    }

    @PostMapping("/posts")
    public RestBean<GroupPost> createPost(@RequestBody GroupPost post,
                                          @SessionAttribute("account") AccountUser accountUser) {
        post.setUserId(accountUser.getId());
        post.setUsername(accountUser.getUsername());
        post.setCommentCount(0);
        post.setCreateTime(new Date());
        groupPostService.save(post);
        return RestBean.success(post);
    }

    @GetMapping("/posts")
    public RestBean<List<GroupPost>> listPosts(@RequestParam(required = false) String groupId) {
        return RestBean.success(groupPostService.listByGroupId(groupId));
    }

    @PostMapping("/posts/{postId}/comments")
    public RestBean<PostComment> addComment(@PathVariable String postId,
                                            @RequestBody PostComment comment,
                                            @SessionAttribute("account") AccountUser accountUser) {
        comment.setPostId(postId);
        comment.setUserId(accountUser.getId());
        comment.setUsername(accountUser.getUsername());
        comment.setCreateTime(new Date());
        postCommentService.save(comment);
        groupPostService.increaseCommentCount(postId);
        return RestBean.success(comment);
    }

    @GetMapping("/posts/{postId}/comments")
    public RestBean<List<PostComment>> getComments(@PathVariable String postId) {
        return RestBean.success(postCommentService.listByPostId(postId));
    }

    // 评价相关接口
    @PostMapping("/reviews")
    public RestBean<ResourceReview> addReview(@RequestBody ResourceReview review,
                                              @SessionAttribute("account") AccountUser accountUser) {
        review.setUserId(accountUser.getId());
        review.setUsername(accountUser.getUsername());
        review.setCreateTime(new Date());
        if (review.getLikes() == null) {
            review.setLikes(0);
        }
        resourceReviewService.save(review);
        return RestBean.success(review);
    }

    @GetMapping("/reviews")
    public RestBean<List<ResourceReview>> listReviews(@RequestParam String resourceId) {
        return RestBean.success(resourceReviewService.listByResourceId(resourceId));
    }

    @PostMapping("/reviews/{id}/like")
    public RestBean<String> likeReview(@PathVariable String id) {
        return resourceReviewService.likeReview(id) ? RestBean.success("点赞成功") : RestBean.failure(404, "未找到评论");
    }
}
