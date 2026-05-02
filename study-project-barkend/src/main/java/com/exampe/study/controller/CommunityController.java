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

/**
 * 社区控制器，统一为前端社区页提供学习小组、帖子、评论和资源评价接口。
 */
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

    /**
     * 获取前端小组列表页需要的学习小组数据。
     * @return 学习小组列表
     */
    @GetMapping("/groups")
    public RestBean<List<StudyGroup>> listGroups() {
        return RestBean.success(studyGroupService.listGroups());
    }

    /**
     * 获取前端管理页需要的完整学习小组数据。
     * @return 学习小组列表
     */
    @GetMapping("/groups/all")
    public RestBean<List<StudyGroup>> listAllGroups() {
        return RestBean.success(studyGroupService.list());
    }

    /**
     * 创建新的学习小组，供前端小组管理页和详情页使用。
     * @param group 学习小组信息
     * @param accountUser 当前用户账户信息
     * @return 创建的学习小组
     */
    @PostMapping("/groups")
    public RestBean<StudyGroup> createGroup(@RequestBody StudyGroup group,
                                            @SessionAttribute("account") AccountUser accountUser) {
        group.setOwnerId(accountUser.getId());
        group.setOwnerName(accountUser.getUsername());
        group.setCreateTime(new Date());
        studyGroupService.save(group);
        // 创建者自动加入小组，保证前端进入详情页时立即具备成员身份。
        studyGroupService.joinGroup(group.getId(), accountUser.getId(), accountUser.getUsername());
        return RestBean.success(group);
    }

    /**
     * 更新学习小组信息，供前端管理页同步编辑结果。
     * @param id 小组ID
     * @param group 更新的小组信息
     * @param accountUser 当前用户账户信息
     * @return 更新后的小组信息
     */
    @RequestMapping(value = "/groups/{id}", method = {RequestMethod.POST, RequestMethod.PUT})
    public RestBean<StudyGroup> updateGroup(@PathVariable String id,
                                            @RequestBody StudyGroup group,
                                            @SessionAttribute("account") AccountUser accountUser) {
        group.setId(id);
        boolean ok = studyGroupService.updateById(group);
        return ok ? RestBean.success(group) : RestBean.failure(500, "更新失败");
    }

    /**
     * 删除学习小组，供前端管理页同步删除结果。
     * @param id 小组ID
     * @return 删除结果
     */
    @DeleteMapping("/groups/{id}")
    public RestBean<String> deleteGroup(@PathVariable String id) {
        boolean ok = studyGroupService.removeById(id);
        return ok ? RestBean.success("删除成功") : RestBean.failure(500, "删除失败");
    }

    /**
     * 加入学习小组，供前端详情页发起加入动作。
     * @param groupId 小组ID
     * @param accountUser 当前用户账户信息
     * @return 加入结果
     */
    @PostMapping("/groups/{groupId}/join")
    public RestBean<String> joinGroup(@PathVariable String groupId,
                                      @SessionAttribute("account") AccountUser accountUser) {
        boolean ok = studyGroupService.joinGroup(groupId, accountUser.getId(), accountUser.getUsername());
        return ok ? RestBean.success("加入成功") : RestBean.failure(500, "加入失败");
    }

    /**
     * 创建新帖子，供前端公共讨论区和小组详情页共用。
     * @param post 帖子信息
     * @param accountUser 当前用户账户信息
     * @return 创建的帖子
     */
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

    /**
     * 更新帖子信息，供前端帖子管理页同步编辑结果。
     * @param id 帖子ID
     * @param post 更新的帖子信息
     * @param accountUser 当前用户账户信息
     * @return 更新后的帖子信息
     */
    @RequestMapping(value = "/posts/{id}", method = {RequestMethod.POST, RequestMethod.PUT})
    public RestBean<GroupPost> updatePost(@PathVariable String id,
                                          @RequestBody GroupPost post,
                                          @SessionAttribute("account") AccountUser accountUser) {
        post.setId(id);
        boolean ok = groupPostService.updateById(post);
        return ok ? RestBean.success(post) : RestBean.failure(500, "更新失败");
    }

    /**
     * 删除帖子，供前端帖子管理页同步删除结果。
     * @param id 帖子ID
     * @return 删除结果
     */
    @DeleteMapping("/posts/{id}")
    public RestBean<String> deletePost(@PathVariable String id) {
        boolean ok = groupPostService.removeById(id);
        return ok ? RestBean.success("删除成功") : RestBean.failure(500, "删除失败");
    }

    /**
     * 获取帖子列表（可按小组筛选），供前端公共讨论区和小组详情页使用。
     * @param groupId 小组ID（可选）
     * @return 帖子列表
     */
    @GetMapping("/posts")
    public RestBean<List<GroupPost>> listPosts(@RequestParam(required = false) String groupId) {
        return RestBean.success(groupPostService.listByGroupId(groupId));
    }

    /**
     * 获取所有帖子列表，供前端管理页做全量管理展示。
     * @return 帖子列表
     */
    @GetMapping("/posts/all")
    public RestBean<List<GroupPost>> listAllPosts() {
        return RestBean.success(groupPostService.list());
    }

    /**
     * 为帖子添加评论，供前端评论面板提交后即时回显。
     * @param postId 帖子ID
     * @param comment 评论信息
     * @param accountUser 当前用户账户信息
     * @return 添加的评论
     */
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

    /**
     * 获取帖子的评论列表，供前端评论面板渲染。
     * @param postId 帖子ID
     * @return 评论列表
     */
    @GetMapping("/posts/{postId}/comments")
    public RestBean<List<PostComment>> getComments(@PathVariable String postId) {
        return RestBean.success(postCommentService.listByPostId(postId));
    }

    /**
     * 添加资源评价
     * @param review 评价信息
     * @param accountUser 当前用户账户信息
     * @return 添加的评价
     */
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

    /**
     * 获取指定资源的评论列表
     * @param resourceId 资源ID
     * @return 评价列表
     */
    @GetMapping("/reviews")
    public RestBean<List<ResourceReview>> listReviews(@RequestParam String resourceId) {
        return RestBean.success(resourceReviewService.listByResourceId(resourceId));
    }

    /**
     * 获取所有资源评价列表
     * @return 评价列表
     */
    @GetMapping("/reviews/all")
    public RestBean<List<ResourceReview>> listAllReviews() {
        return RestBean.success(resourceReviewService.list());
    }

    /**
     * 点赞资源评价
     * @param id 评价ID
     * @return 点赞结果
     */
    @PostMapping("/reviews/{id}/like")
    public RestBean<String> likeReview(@PathVariable String id) {
        return resourceReviewService.likeReview(id) ? RestBean.success("点赞成功") : RestBean.failure(404, "未找到评论");
    }

    /**
     * 更新资源评价
     * @param id 评价ID
     * @param review 更新的评价信息
     * @param accountUser 当前用户账户信息
     * @return 更新后的评价信息
     */
    @RequestMapping(value = "/reviews/{id}", method = {RequestMethod.POST, RequestMethod.PUT})
    public RestBean<ResourceReview> updateReview(@PathVariable String id,
                                                 @RequestBody ResourceReview review,
                                                 @SessionAttribute("account") AccountUser accountUser) {
        review.setId(id);
        boolean ok = resourceReviewService.updateById(review);
        return ok ? RestBean.success(review) : RestBean.failure(500, "更新失败");
    }

    /**
     * 删除资源评价
     * @param id 评价ID
     * @return 删除结果
     */
    @DeleteMapping("/reviews/{id}")
    public RestBean<String> deleteReview(@PathVariable String id) {
        boolean ok = resourceReviewService.removeById(id);
        return ok ? RestBean.success("删除成功") : RestBean.failure(500, "删除失败");
    }
}
