<template>
    <RoleStatsDashboard
        title="教师数据统计"
        subtitle="课程建设、资源分布、学生学习和评价反馈都在这里汇总。"
        role-label="教师视图"
        endpoint="/api/dashboard/teacher"
        home-path="/index/home"
        switch-path="/index/student-dashboard"
        switch-label="切换到学生统计"
    />
</template>

<script setup lang="ts">
import RoleStatsDashboard from './components/RoleStatsDashboard.vue'
</script>