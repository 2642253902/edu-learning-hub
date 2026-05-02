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

/**
 * 前后端协同注释（教师统计页壳）
 * - 该页不单独持有统计逻辑，只把教师端 endpoint 与展示文案交给共享统计组件处理。
 * - 教师统计接口应返回课程、资源、学生和评价等聚合数据，前端按统一结构渲染，避免页面间分叉。
 * - 与学生统计页共享同一组件，确保两种角色的统计口径一致。
 */
</script>