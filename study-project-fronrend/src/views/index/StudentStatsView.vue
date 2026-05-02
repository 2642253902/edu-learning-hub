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

/**
 * 前后端协同注释（学生统计页壳）
 * - 这个页面本身不计算统计数据，只负责把学生视角的标题、路径和 endpoint 传给通用统计面板。
 * - endpoint 指向后端学生统计接口，统一由 RoleStatsDashboard 拉取并渲染卡片、趋势图和占比图。
 * - 与教师统计页共享同一个渲染组件，保证前后端数据契约一致，减少重复维护。
 */
</script>