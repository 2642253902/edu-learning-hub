<template>
    <div class="dashboard-page">
        <el-card shadow="never" class="hero-card">
            <div class="hero-content">
                <div>
                    <div class="hero-kicker">{{ roleLabel }}</div>
                    <h2 class="hero-title">{{ title }}</h2>
                    <p class="hero-subtitle">{{ subtitle }}</p>
                </div>

                <div class="hero-actions">
                    <el-button @click="router.push(homePath)">返回首页</el-button>
                    <el-button type="primary" plain @click="router.push(switchPath)">{{ switchLabel }}</el-button>
                </div>
            </div>
        </el-card>

        <el-row :gutter="16" class="summary-grid">
            <el-col v-for="card in summaryCards" :key="card.label" :xs="12" :sm="12" :md="8" :lg="6">
                <el-card shadow="hover" class="summary-card">
                    <div class="summary-label">{{ card.label }}</div>
                    <div class="summary-value">
                        <span class="value-number">{{ formatValue(card.value) }}</span>
                        <span v-if="card.suffix" class="value-suffix">{{ card.suffix }}</span>
                    </div>
                    <div class="summary-desc">{{ card.description }}</div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="16" class="chart-grid">
            <el-col :xs="24" :lg="14">
                <el-card shadow="hover" class="chart-card">
                    <template #header>
                        <div class="card-header">
                            <span>{{ lineChartData.title || '学习趋势' }}</span>
                            <el-tag type="info" effect="plain">近 7 天</el-tag>
                        </div>
                    </template>
                    <div ref="lineChartRef" class="chart-box"></div>
                </el-card>
            </el-col>

            <el-col :xs="24" :lg="10">
                <el-card shadow="hover" class="chart-card">
                    <template #header>
                        <div class="card-header">
                            <span>{{ pieChartData.title || '占比分析' }}</span>
                            <el-tag type="warning" effect="plain">模块聚合</el-tag>
                        </div>
                    </template>
                    <div ref="pieChartRef" class="chart-box"></div>
                </el-card>
            </el-col>
        </el-row>

        <el-card shadow="hover" class="highlight-card">
            <template #header>
                <div class="card-header">
                    <span>重点关注</span>
                    <el-tag type="success" effect="plain">实时统计</el-tag>
                </div>
            </template>
            <div class="highlight-list">
                <div v-for="item in highlights" :key="item.label" class="highlight-item">
                    <div class="highlight-label">{{ item.label }}</div>
                    <div class="highlight-value">{{ item.value }}</div>
                </div>
                <el-empty v-if="highlights.length === 0" description="暂无统计数据" />
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
// 角色统计小组件：用于在主控台展示不同角色下的关键统计指标。
// 与后端接口协作点：此组件接收父组件传入的 `endpoint`，调用后端统计接口以支持不同视图的数据回显。
/**
 * 前后端协同注释（角色统计）
 * - 设计：父组件传入 `endpoint`（例如 `/sys/stats/teacher`）以复用该组件展示不同角色视图；
 * - 接口约定：返回结构建议为 `{ summaryCards: [], lineChart: { labels:[], values:[] }, pieChart: { labels:[], values:[] }, highlights: [] }`，组件按该结构渲染卡片与图表；
 * - 错误与空数据：返回空或错误时组件应展示空态并允许父组件决定重试策略。
 */
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { get } from '@/net'

type SummaryCard = {
    label: string
    value: number | string
    suffix?: string
    description?: string
}

type HighlightItem = {
    label: string
    value: string
}

type ChartData = {
    title?: string
    seriesName?: string
    labels?: string[]
    values?: number[]
    unit?: string
}

const props = defineProps<{
    title: string
    subtitle: string
    roleLabel: string
    endpoint: string
    homePath: string
    switchPath: string
    switchLabel: string
}>()

const router = useRouter()
const lineChartRef = ref<HTMLElement | null>(null)
const pieChartRef = ref<HTMLElement | null>(null)
const dashboard = ref<{
    summaryCards: SummaryCard[]
    lineChart: ChartData
    pieChart: ChartData
    highlights: HighlightItem[]
}>({
    summaryCards: [],
    lineChart: {},
    pieChart: {},
    highlights: [],
})

let lineChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null

const summaryCards = computed(() => dashboard.value.summaryCards || [])
const lineChartData = computed(() => dashboard.value.lineChart || {})
const pieChartData = computed(() => dashboard.value.pieChart || {})
const highlights = computed(() => dashboard.value.highlights || [])

const formatValue = (value: number | string) => {
    if (typeof value === 'number') {
        return Number.isInteger(value) ? value.toString() : value.toFixed(1)
    }
    return value
}

const renderCharts = () => {
    const lineTarget = lineChartRef.value
    const pieTarget = pieChartRef.value

    if (lineTarget) {
        lineChart?.dispose()
        lineChart = echarts.init(lineTarget)
        lineChart.setOption({
            tooltip: { trigger: 'axis' },
            grid: { left: 24, right: 20, top: 32, bottom: 24, containLabel: true },
            xAxis: {
                type: 'category',
                data: lineChartData.value.labels || [],
                axisLine: { lineStyle: { color: '#cbd5e1' } },
                axisTick: { show: false },
            },
            yAxis: {
                type: 'value',
                axisLine: { show: false },
                splitLine: { lineStyle: { color: '#eef2f7' } },
            },
            series: [{
                name: lineChartData.value.seriesName || '统计值',
                type: 'line',
                smooth: true,
                data: lineChartData.value.values || [],
                symbolSize: 8,
                lineStyle: { width: 3, color: '#2563eb' },
                itemStyle: { color: '#2563eb' },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(37, 99, 235, 0.28)' },
                        { offset: 1, color: 'rgba(37, 99, 235, 0.04)' },
                    ]),
                },
            }],
        })
    }

    if (pieTarget) {
        pieChart?.dispose()
        pieChart = echarts.init(pieTarget)
        pieChart.setOption({
            tooltip: { trigger: 'item' },
            legend: { bottom: 0, left: 'center' },
            series: [{
                type: 'pie',
                radius: ['38%', '72%'],
                avoidLabelOverlap: false,
                itemStyle: { borderColor: '#fff', borderWidth: 2 },
                label: { show: false },
                data: (pieChartData.value.labels || []).map((label, index) => ({
                    name: label,
                    value: pieChartData.value.values?.[index] ?? 0,
                })),
            }],
        })
    }
}

const loadDashboard = () => {
    get(props.endpoint, (_message: string, data: any) => {
        dashboard.value = data || { summaryCards: [], lineChart: {}, pieChart: {}, highlights: [] }
        nextTick(renderCharts)
    }, () => {
        dashboard.value = { summaryCards: [], lineChart: {}, pieChart: {}, highlights: [] }
        nextTick(renderCharts)
    })
}

const handleResize = () => {
    lineChart?.resize()
    pieChart?.resize()
}

watch(() => props.endpoint, loadDashboard, { immediate: true })

onMounted(() => {
    window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    lineChart?.dispose()
    pieChart?.dispose()
})
</script>

<style scoped>
.dashboard-page {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.hero-card {
    border: 1px solid #e5eefc;
    background: linear-gradient(135deg, #ffffff 0%, #f7fbff 52%, #eef6ff 100%);
}

.hero-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
}

.hero-kicker {
    color: #2563eb;
    font-size: 13px;
    font-weight: 600;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

.hero-title {
    margin: 8px 0 6px;
    font-size: 28px;
    color: #0f172a;
}

.hero-subtitle {
    margin: 0;
    color: #64748b;
    line-height: 1.7;
}

.hero-actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
}

.summary-grid,
.chart-grid {
    width: 100%;
}

.summary-card {
    border-radius: 16px;
    min-height: 132px;
    background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
}

.summary-label {
    color: #64748b;
    font-size: 14px;
}

.summary-value {
    display: flex;
    align-items: baseline;
    gap: 6px;
    margin: 12px 0 8px;
}

.value-number {
    font-size: 30px;
    font-weight: 700;
    color: #0f172a;
}

.value-suffix {
    font-size: 14px;
    color: #94a3b8;
}

.summary-desc {
    color: #94a3b8;
    font-size: 13px;
    line-height: 1.6;
}

.chart-card,
.highlight-card {
    border-radius: 18px;
}

.card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    font-weight: 600;
    color: #0f172a;
}

.chart-box {
    width: 100%;
    height: 340px;
}

.highlight-list {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 12px;
}

.highlight-item {
    padding: 16px;
    border-radius: 14px;
    background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%);
    border: 1px solid #e5eefc;
}

.highlight-label {
    color: #64748b;
    font-size: 13px;
}

.highlight-value {
    margin-top: 10px;
    color: #0f172a;
    font-size: 18px;
    font-weight: 600;
}

@media (max-width: 768px) {
    .hero-content {
        flex-direction: column;
        align-items: flex-start;
    }

    .highlight-list {
        grid-template-columns: 1fr;
    }

    .chart-box {
        height: 280px;
    }
}
</style>