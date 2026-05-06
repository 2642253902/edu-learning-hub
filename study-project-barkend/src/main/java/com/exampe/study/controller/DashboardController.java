package com.exampe.study.controller;

import com.exampe.auth.entity.user.AccountUser;
import com.exampe.common.RestBean;
import com.exampe.study.mapper.CloudComputingCourseMapper;
import com.exampe.study.mapper.CloudComputingCourseResourceMapper;
import com.exampe.study.mapper.CloudComputingStudentLearningRecordMapper;
import com.exampe.study.mapper.GroupPostMapper;
import com.exampe.study.mapper.PostCommentMapper;
import com.exampe.study.mapper.ResourceReviewMapper;
import com.exampe.study.mapper.StudyGroupMemberMapper;
import com.exampe.sys.dto.MessageNoticeUserVO;
import com.exampe.sys.service.IMessageNoticeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据看板控制器，向前端学生/教师统计页面提供统一图表数据。
 * <p>
 * 将后端多张业务表的聚合结果组织为前端可直接渲染的卡片、折线图和饼图结构。
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final DateTimeFormatter DAY_LABEL_FORMAT = DateTimeFormatter.ofPattern("MM-dd");

    @Resource
    private CloudComputingStudentLearningRecordMapper learningRecordMapper;

    @Resource
    private CloudComputingCourseMapper courseMapper;

    @Resource
    private CloudComputingCourseResourceMapper courseResourceMapper;

    @Resource
    private StudyGroupMemberMapper studyGroupMemberMapper;

    @Resource
    private GroupPostMapper groupPostMapper;

    @Resource
    private PostCommentMapper postCommentMapper;

    @Resource
    private ResourceReviewMapper resourceReviewMapper;

    @Resource
    private IMessageNoticeService messageNoticeService;

    /**
     * 学生看板接口，供前端学生首页加载学习与互动数据。
     */
    @GetMapping("/student")
    public RestBean<Map<String, Object>> studentDashboard(@SessionAttribute("account") AccountUser accountUser) {
        return RestBean.success(buildStudentDashboard(accountUser));
    }

    /**
     * 教师看板接口，供前端教师首页加载课程与学生学习统计。
     */
    @GetMapping("/teacher")
    public RestBean<Map<String, Object>> teacherDashboard(@SessionAttribute("account") AccountUser accountUser) {
        return RestBean.success(buildTeacherDashboard(accountUser));
    }

    private Map<String, Object> buildStudentDashboard(AccountUser accountUser) {
        String userId = accountUser.getId();
        String role = accountUser.getRole();

        long learningCourses = toLong(learningRecordMapper.countCoursesByUserId(userId));
        long completedCourses = toLong(learningRecordMapper.countCompletedCoursesByUserId(userId));
        double studyHours = toDouble(learningRecordMapper.sumStudyHoursByUserId(userId));
        long studyDays = toLong(learningRecordMapper.countStudyDaysByUserId(userId));
        long resourceCount = toLong(learningRecordMapper.countResourcesByUserId(userId));
        long groupCount = toLong(studyGroupMemberMapper.countGroupsByUserId(userId));
        long postCount = toLong(groupPostMapper.countPostsByUserId(userId));
        long commentCount = toLong(postCommentMapper.countCommentsByUserId(userId));
        long reviewCount = toLong(resourceReviewMapper.countReviewsByUserId(userId));
        long unreadCount = countUnreadMessages(userId, role);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("roleLabel", "学生数据统计");
        summary.put("greeting", "聚焦课程、学习记录和社区参与情况。");
        summary.put("summaryCards", List.of(
                card("学习课程", learningCourses, "门", "已进入学习流程的课程数量"),
                card("已完成课程", completedCourses, "门", "学习状态为已完成的课程数量"),
                card("学习时长", studyHours, "小时", "累计学习时长"),
                card("学习天数", studyDays, "天", "有学习记录的日期数"),
                card("学习资源", resourceCount, "个", "学习过的资源数量"),
                card("加入小组", groupCount, "个", "参与的小组数量"),
                card("发表帖子", postCount, "条", "在小组内发表的帖子数量"),
                card("课程评价", reviewCount, "条", "对课程发表的评价数量")
        ));

        List<Map<String, Object>> activityPie = List.of(
                pieItem("课程学习", learningCourses),
                pieItem("小组参与", groupCount),
                pieItem("帖子发布", postCount),
                pieItem("评论互动", commentCount),
                pieItem("课程评价", reviewCount),
                pieItem("未读消息", unreadCount)
        );

        summary.put("lineChart", buildLineChart(
                "近 7 天学习时长",
                "学习时长（小时）",
                fillTrendLabels(learningRecordMapper.listStudyTrendByUserId(userId)),
                fillTrendValues(learningRecordMapper.listStudyTrendByUserId(userId)),
                "小时"
        ));
        summary.put("pieChart", buildPieChart("学习参与占比", activityPie));
        summary.put("highlights", List.of(
                highlight("未读消息", unreadCount + " 条"),
                highlight("互动评论", commentCount + " 条"),
                highlight("课程评价", reviewCount + " 条")
        ));
        return summary;
    }

    private Map<String, Object> buildTeacherDashboard(AccountUser accountUser) {
        String teacherId = accountUser.getId();
        String role = accountUser.getRole();

        long courseCount = toLong(courseMapper.countCoursesByTeacherId(teacherId));
        long publishedCourseCount = toLong(courseMapper.countPublishedCoursesByTeacherId(teacherId));
        long resourceCount = toLong(courseResourceMapper.countResourcesByTeacherId(teacherId));
        long studentCount = toLong(learningRecordMapper.countStudentsByTeacherId(teacherId));
        long completedStudentCount = toLong(learningRecordMapper.countCompletedStudentsByTeacherId(teacherId));
        double studyHours = toDouble(learningRecordMapper.sumStudyHoursByTeacherId(teacherId));
        long reviewCount = toLong(resourceReviewMapper.countReviewsByTeacherId(teacherId));
        long unreadCount = countUnreadMessages(teacherId, role);

        List<Map<String, Object>> statusDistribution = courseMapper.countCourseStatusByTeacherId(teacherId);
        List<Map<String, Object>> resourceDistribution = courseResourceMapper.countResourceTypesByTeacherId(teacherId);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("roleLabel", "教师数据统计");
        summary.put("greeting", "聚焦课程建设、资源沉淀和学生学习效果。");
        summary.put("summaryCards", List.of(
                card("负责课程", courseCount, "门", "当前负责的课程数量"),
                card("已发布课程", publishedCourseCount, "门", "状态为已发布的课程数量"),
                card("资源总数", resourceCount, "个", "课程下的资源总量"),
                card("学习学生", studentCount, "人", "参与学习的学生数量"),
                card("完成学生", completedStudentCount, "人", "学习状态为已完成的学生数量"),
                card("学习时长", studyHours, "小时", "名下课程累计学习时长"),
                card("课程评价", reviewCount, "条", "课程获得的评价数量"),
                card("未读消息", unreadCount, "条", "当前账号未读消息数量")
        ));

        summary.put("lineChart", buildLineChart(
                "近 7 天学习时长",
                "学习时长（小时）",
                fillTrendLabels(learningRecordMapper.listStudyTrendByTeacherId(teacherId)),
                fillTrendValues(learningRecordMapper.listStudyTrendByTeacherId(teacherId)),
                "小时"
        ));
        summary.put("pieChart", buildPieChart("课程与资源分布", mergeTeacherDistribution(statusDistribution, resourceDistribution)));
        summary.put("highlights", List.of(
                highlight("已发布课程", publishedCourseCount + " 门"),
                highlight("完成学生", completedStudentCount + " 人"),
                highlight("课程评价", reviewCount + " 条")
        ));
        return summary;
    }

    /**
     * 统一计算当前账号未读消息数量，供前端消息提醒角标展示。
     */
    private long countUnreadMessages(String userId, String role) {
        List<MessageNoticeUserVO> list = messageNoticeService.listForUser(userId, role, 500);
        return list.stream().filter(item -> Number.class.isInstance(item.getUnread()) ? item.getUnread().intValue() == 1 : Objects.equals(item.getUnread(), 1)).count();
    }

    private Map<String, Object> card(String label, Number value, String suffix, String description) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("label", label);
        map.put("value", value);
        map.put("suffix", suffix);
        map.put("description", description);
        return map;
    }

    private Map<String, Object> highlight(String label, String value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("label", label);
        map.put("value", value);
        return map;
    }

    private Map<String, Object> pieItem(String label, Number value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("label", label);
        map.put("value", value);
        return map;
    }

    private Map<String, Object> buildLineChart(String title, String seriesName, List<String> labels, List<Number> values, String unit) {
        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("title", title);
        chart.put("seriesName", seriesName);
        chart.put("labels", labels);
        chart.put("values", values);
        chart.put("unit", unit);
        return chart;
    }

    private Map<String, Object> buildPieChart(String title, List<Map<String, Object>> items) {
        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("title", title);
        chart.put("labels", items.stream().map(item -> String.valueOf(item.get("label"))).collect(Collectors.toList()));
        chart.put("values", items.stream().map(item -> toDouble(item.get("value"))).collect(Collectors.toList()));
        return chart;
    }

    private List<Map<String, Object>> mergeTeacherDistribution(List<Map<String, Object>> statusDistribution,
                                                               List<Map<String, Object>> resourceDistribution) {
        List<Map<String, Object>> items = new ArrayList<>();
        items.addAll(statusDistribution.stream().map(item -> {
            Integer status = Integer.valueOf(String.valueOf(item.get("status")));
            String label = switch (status) {
                case 0 -> "未发布";
                case 1 -> "已发布";
                case 2 -> "已下架";
                default -> "其他状态";
            };
            return pieItem(label, toNumber(item.get("value")));
        }).toList());
        items.addAll(resourceDistribution.stream().map(item -> {
            Integer type = Integer.valueOf(String.valueOf(item.get("type")));
            String label = switch (type) {
                case 1 -> "视频资源";
                case 2 -> "讲义资源";
                case 3 -> "实验资源";
                default -> "其他资源";
            };
            return pieItem(label, toNumber(item.get("value")));
        }).toList());
        if (items.isEmpty()) {
            items.add(pieItem("暂无数据", 1));
        }
        return items;
    }

    private List<String> fillTrendLabels(List<Map<String, Object>> raw) {
        List<String> labels = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            labels.add(LocalDate.now().minusDays(i).format(DAY_LABEL_FORMAT));
        }
        return labels;
    }

    private List<Number> fillTrendValues(List<Map<String, Object>> raw) {
        Map<String, Double> valueMap = raw.stream().collect(Collectors.toMap(
                item -> normalizeDayKey(item.get("day")),
                item -> toDouble(item.get("value")),
                (left, right) -> right,
                LinkedHashMap::new
        ));
        List<Number> values = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String key = LocalDate.now().minusDays(i).toString();
            values.add(valueMap.getOrDefault(key, 0D));
        }
        return values;
    }

    private String normalizeDayKey(Object dayValue) {
        if (dayValue == null) {
            return "";
        }
        String value = String.valueOf(dayValue);
        if (value.length() >= 10) {
            return value.substring(0, 10);
        }
        return value;
    }

    private long toLong(Number number) {
        return number == null ? 0L : number.longValue();
    }

    private Number toNumber(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number;
        }
        try {
            if (String.valueOf(value).contains(".")) {
                return Double.parseDouble(String.valueOf(value));
            }
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0D;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return 0D;
        }
    }
}