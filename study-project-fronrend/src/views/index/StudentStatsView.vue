<template>
    <RoleStatsDashboard
        title="学生数据统计"
        subtitle="课程学习、资源使用、小组互动和消息提醒都集中在这里。"
        role-label="学生视图"
        endpoint="/api/dashboard/student"
        home-path="/index/home"
        switch-path="/index/teacher-dashboard"
        switch-label="切换到教师统计"
    />
</template>

<script setup lang="ts">
import RoleStatsDashboard from './components/RoleStatsDashboard.vue'
</script>