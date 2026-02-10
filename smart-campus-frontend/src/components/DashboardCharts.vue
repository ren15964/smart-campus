<template>
  <div class="charts">
    <el-row :gutter="16" class="kpis">
      <el-col v-for="k in kpis" :key="k.key" :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="kpi-card">
          <div class="kpi-label">{{ k.label }}</div>
          <div class="kpi-value">
            <span class="kpi-value__main">{{ k.value }}</span>
            <span v-if="k.unit" class="kpi-value__unit">{{ k.unit }}</span>
          </div>
          <div v-if="k.hint" class="kpi-hint">{{ k.hint }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-alert
      v-if="errorMsg"
      :title="errorMsg"
      type="warning"
      show-icon
      :closable="false"
      class="dash-alert"
    />

    <el-skeleton :loading="loading" animated>
      <template #template>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="24" :md="12"><div class="sk sk-card" /></el-col>
          <el-col :xs="24" :sm="24" :md="12"><div class="sk sk-card" /></el-col>
          <el-col :xs="24" :sm="24" :md="24"><div class="sk sk-card sk-card--tall" /></el-col>
        </el-row>
      </template>
      <template #default>
    <el-row :gutter="16">
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card>
          <template #header>
            <div class="card-title">{{ titles.line }}</div>
          </template>
          <VChart class="chart" :option="lineOption" autoresize />
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card>
          <template #header>
            <div class="card-title">{{ titles.bar }}</div>
          </template>
          <VChart class="chart" :option="barOption" autoresize />
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="24" :lg="24">
        <el-card>
          <template #header>
            <div class="card-title">{{ titles.pie }}</div>
          </template>
          <VChart class="chart chart--tall" :option="pieOption" autoresize />
        </el-card>
      </el-col>
    </el-row>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
} from 'echarts/components'
import { getNoticeList } from '@/api/notice'
import { getMyHomeworkList } from '@/api/homework'
import { getMyCourses, getSelectionStatistics } from '@/api/course-selection'
import { getCourseList } from '@/api/course'
import { getCourseScheduleList } from '@/api/course-schedule'
import { getUserList } from '@/api/user'
import { getGradeStatistics } from '@/api/grade'
import { getMySchedule } from '@/api/schedule'
import { useUserStore } from '@/store/user'

use([CanvasRenderer, LineChart, BarChart, PieChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent])

const userStore = useUserStore()
const loading = ref(true)
const errorMsg = ref('')

const titles = ref({
  line: '近 7 日通知发布趋势',
  bar: '本周概览',
  pie: '通知优先级分布'
})

const kpis = ref([
  { key: 'k1', label: '—', value: '—', unit: '', hint: '' },
  { key: 'k2', label: '—', value: '—', unit: '', hint: '' },
  { key: 'k3', label: '—', value: '—', unit: '', hint: '' },
  { key: 'k4', label: '—', value: '—', unit: '', hint: '' },
])

const xDays = computed(() => {
  const arr = []
  for (let i = 6; i >= 0; i--) arr.push(dayjs().subtract(i, 'day').format('MM-DD'))
  return arr
})

const lineSeries = ref([])
const barSeries = ref([])
const barXAxis = ref([])
const pieData = ref([])

const baseText = {
  textStyle: { color: 'var(--app-text-muted)', fontFamily: 'inherit' }
}

const lineOption = computed(() => ({
  ...baseText,
  tooltip: { trigger: 'axis' },
  grid: { left: 10, right: 18, top: 24, bottom: 10, containLabel: true },
  xAxis: {
    type: 'category',
    data: xDays.value,
    boundaryGap: false,
    axisLine: { lineStyle: { color: 'var(--app-border)' } },
    axisTick: { show: false }
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.06)' } },
    axisLabel: { color: 'var(--app-text-muted)' }
  },
  series: lineSeries.value
}))

const barOption = computed(() => ({
  ...baseText,
  tooltip: { trigger: 'axis' },
  legend: barSeries.value.length > 1 ? { top: 0, right: 0, itemWidth: 10, textStyle: { color: 'var(--app-text-muted)' } } : undefined,
  grid: { left: 10, right: 18, top: 34, bottom: 10, containLabel: true },
  xAxis: {
    type: 'category',
    data: barXAxis.value,
    axisLine: { lineStyle: { color: 'var(--app-border)' } },
    axisTick: { show: false }
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.06)' } },
    axisLabel: { color: 'var(--app-text-muted)' }
  },
  series: barSeries.value
}))

const pieOption = computed(() => ({
  ...baseText,
  tooltip: { trigger: 'item' },
  legend: { top: 0, left: 0, itemWidth: 10, textStyle: { color: 'var(--app-text-muted)' } },
  series: [
    {
      name: '优先级',
      type: 'pie',
      radius: ['44%', '72%'],
      center: ['50%', '58%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 10, borderColor: 'var(--app-surface-solid)', borderWidth: 2 },
      label: { color: 'var(--app-text)' },
      data: pieData.value
    }
  ]
}))

function safeNumber(n) {
  const v = Number(n)
  return Number.isFinite(v) ? v : 0
}

function inLast7Days(timeStr) {
  if (!timeStr) return false
  const t = dayjs(timeStr)
  if (!t.isValid()) return false
  const start = dayjs().subtract(6, 'day').startOf('day')
  const end = dayjs().endOf('day')
  return t.isAfter(start) && t.isBefore(end)
}

function buildLast7Series(records, timeField, name) {
  const map = new Map()
  for (let i = 6; i >= 0; i--) map.set(dayjs().subtract(i, 'day').format('MM-DD'), 0)
  for (const r of records) {
    const raw = r?.[timeField]
    if (!raw) continue
    const t = dayjs(raw)
    if (!t.isValid()) continue
    if (!inLast7Days(raw)) continue
    const k = t.format('MM-DD')
    if (map.has(k)) map.set(k, map.get(k) + 1)
  }
  return {
    name,
    type: 'line',
    data: Array.from(map.values()),
    smooth: true,
    symbol: 'circle',
    symbolSize: 7,
    lineStyle: { width: 3, color: 'var(--primary-light-color)' }, // 使用主题亮蓝色
    itemStyle: { color: 'var(--primary-light-color)' }, // 使用主题亮蓝色
    areaStyle: { color: 'rgba(0, 123, 255, 0.16)' } // 使用主题蓝色透明度
  }
}

function buildPriorityPie(records) {
  const cnt = { 1: 0, 2: 0, 3: 0 }
  for (const r of records) {
    const p = r?.priority
    if (p === 1 || p === 2 || p === 3) cnt[p] += 1
  }
  return [
    { value: cnt[1], name: '普通', itemStyle: { color: '#909399' } }, // Element Plus info grey
    { value: cnt[2], name: '重要', itemStyle: { color: '#409eff' } }, // Element Plus primary blue
    { value: cnt[3], name: '紧急', itemStyle: { color: '#f56c6c' } }, // Element Plus danger red
  ]
}

async function fetchNoticesForDash() {
  // 获取足够多的数据用于近 7 天趋势 & 优先级分布（后端如果支持分页，会返回 records/total）
  const res = await getNoticeList({ current: 1, size: 200 })
  const records = res?.data?.records ?? res?.data ?? []
  return Array.isArray(records) ? records : []
}

async function initDashboard() {
  loading.value = true
  errorMsg.value = ''
  try {
    const role = userStore?.userInfo?.role || userStore?.role || ''

    const notices = await fetchNoticesForDash()
    const unread = notices.filter(n => n && n.isRead === false).length

    // 默认：用通知发布趋势 + 优先级分布
    titles.value = {
      line: '近 7 日通知发布趋势',
      bar: role === 'admin' ? '热门课程 Top' : '本周概览',
      pie: '通知优先级分布'
    }

    lineSeries.value = [buildLast7Series(notices, 'publishTime', '通知')]
    pieData.value = buildPriorityPie(notices)

    // role-specific
    if (role === 'admin') {
      const [userRes, courseRes, scheduleRes, selRes] = await Promise.all([
        getUserList({ current: 1, size: 1 }),
        getCourseList({ current: 1, size: 1 }),
        getCourseScheduleList({ current: 1, size: 1 }),
        getSelectionStatistics()
      ])

      const totalUsers = safeNumber(userRes?.data?.total)
      const totalCourses = safeNumber(courseRes?.data?.total)
      const totalSchedules = safeNumber(scheduleRes?.data?.total)
      const sel = selRes?.data || {}

      kpis.value = [
        { key: 'users', label: '用户总数', value: totalUsers, unit: '', hint: '' },
        { key: 'courses', label: '课程总数', value: totalCourses, unit: '', hint: '' },
        { key: 'schedules', label: '开课计划', value: totalSchedules, unit: '', hint: '' },
        { key: 'students', label: '选课学生', value: safeNumber(sel.totalStudents), unit: '', hint: `人均选课 ${sel.averageCoursesPerStudent ?? 0}` },
      ]

      const hot = Array.isArray(sel.hotCourses) ? sel.hotCourses.slice(0, 7) : []
      // 兼容字段：courseName / name + selectedCount / count
      const x = hot.map(h => h.courseName || h.name || '课程')
      const y = hot.map(h => safeNumber(h.selectedCount ?? h.count ?? h.total ?? 0))
      barXAxis.value = x
      barSeries.value = [
        { name: '选课人数', type: 'bar', data: y, barWidth: 14, itemStyle: { borderRadius: 8, color: '#409eff' } } // Element Plus primary blue
      ]
      return
    }

    if (role === 'student') {
      const [coursesRes, hwRes, gradeStatRes] = await Promise.allSettled([
        getMyCourses({}),
        getMyHomeworkList({ current: 1, size: 200 }),
        getGradeStatistics()
      ])

      const myCourses = coursesRes.status === 'fulfilled' ? (coursesRes.value?.data ?? []) : []
      const homeworks = hwRes.status === 'fulfilled' ? (hwRes.value?.data ?? []) : []
      const gradeStat = gradeStatRes.status === 'fulfilled' ? (gradeStatRes.value?.data ?? {}) : {}

      const pendingHw = Array.isArray(homeworks) ? homeworks.filter(h => h && h.submitStatus === 0).length : 0

      kpis.value = [
        { key: 'myCourses', label: '已选课程', value: Array.isArray(myCourses) ? myCourses.length : 0, unit: '', hint: '' },
        { key: 'unread', label: '未读通知', value: unread, unit: '', hint: '' },
        { key: 'pendingHw', label: '待提交作业', value: pendingHw, unit: '', hint: '' },
        { key: 'avgGpa', label: '平均绩点', value: gradeStat.averageGpa ?? 0, unit: '', hint: `总学分 ${gradeStat.totalCredits ?? 0}` },
      ]

      // bar: 未来 7 天作业截止数（deadline）
      titles.value.bar = '未来 7 天作业截止数'
      const map = new Map()
      for (let i = 0; i < 7; i++) map.set(dayjs().add(i, 'day').format('MM-DD'), 0)
      for (const h of Array.isArray(homeworks) ? homeworks : []) {
        const d = h?.deadline
        const t = dayjs(d)
        if (!t.isValid()) continue
        const k = t.format('MM-DD')
        if (map.has(k)) map.set(k, map.get(k) + 1)
      }
      barXAxis.value = Array.from(map.keys())
      barSeries.value = [
        { name: '截止作业', type: 'bar', data: Array.from(map.values()), barWidth: 14, itemStyle: { borderRadius: 8, color: '#67c23a' } } // Element Plus success green
      ]
      return
    }

    // teacher（以及其它角色兜底）
    const [scheduleRes] = await Promise.allSettled([getMySchedule({})])
    const schedule = scheduleRes.status === 'fulfilled' ? (scheduleRes.value?.data ?? []) : []

    kpis.value = [
      { key: 'unread', label: '未读通知', value: unread, unit: '', hint: '' },
      { key: 'weekCourses', label: '本周课次', value: Array.isArray(schedule) ? schedule.length : 0, unit: '', hint: '' },
      { key: 'notices7', label: '7日通知', value: safeNumber(lineSeries.value?.[0]?.data?.reduce?.((a, b) => a + b, 0)), unit: '', hint: '' },
      { key: 'today', label: '今日日期', value: dayjs().format('MM-DD'), unit: '', hint: '' },
    ]

    // bar: 课表按周几分布（weekDay: 1-7）
    titles.value.bar = '课表分布（按周几）'
    const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    const wMap = new Map(weekDays.map(d => [d, 0]))
    for (const s of Array.isArray(schedule) ? schedule : []) {
      const wd = Number(s?.weekDay)
      if (wd >= 1 && wd <= 7) wMap.set(weekDays[wd - 1], wMap.get(weekDays[wd - 1]) + 1)
    }
    barXAxis.value = Array.from(wMap.keys())
    barSeries.value = [
      { name: '课次', type: 'bar', data: Array.from(wMap.values()), barWidth: 14, itemStyle: { borderRadius: 8, color: '#409eff' } } // Element Plus primary blue
    ]
  } catch (e) {
    console.error('dashboard init failed:', e)
    errorMsg.value = '仪表盘数据加载失败（将显示最近缓存/默认图表），请稍后刷新重试'
  } finally {
    // 兜底：确保有 bar/pie 数据避免空图
    if (!Array.isArray(barXAxis.value) || barXAxis.value.length === 0) {
      barXAxis.value = xDays.value
      barSeries.value = [
        { name: '通知', type: 'bar', data: xDays.value.map(() => 0), barWidth: 14, itemStyle: { borderRadius: 8, color: '#a5b4fc' } }
      ]
    }
    if (!Array.isArray(pieData.value) || pieData.value.length === 0) {
      pieData.value = [
        { value: 0, name: '普通', itemStyle: { color: '#a5b4fc' } },
        { value: 0, name: '重要', itemStyle: { color: '#22d3ee' } },
        { value: 0, name: '紧急', itemStyle: { color: '#fb7185' } },
      ]
    }
    loading.value = false
  }
}

onMounted(() => {
  initDashboard()
})
</script>

<style scoped lang="scss">
.charts {
  width: 100%;
}

.kpis {
  margin-bottom: 12px;
}

.kpi-card {
  min-height: 100px; // 略微增大高度
  border-radius: 18px; // 略微增大圆角
}

.kpi-label {
  font-size: 12px;
  color: var(--app-text-muted);
}

.kpi-value {
  margin-top: 6px;
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.kpi-value__main {
  font-size: 22px;
  font-weight: 800;
  color: var(--app-text);
  letter-spacing: 0.2px;
}

.kpi-value__unit {
  font-size: 12px;
  color: var(--app-text-muted);
}

.kpi-hint {
  margin-top: 8px;
  font-size: 12px;
  color: var(--app-text-muted);
}

.dash-alert {
  margin: 8px 0 12px;
}

.card-title {
  font-weight: 700;
  color: var(--app-text);
}

.chart {
  width: 100%;
  height: 260px;
}

.chart--tall {
  height: 320px;
}

:deep(.el-card__body) {
  padding-top: 12px;
}

.sk {
  width: 100%;
  border-radius: var(--app-radius);
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  box-shadow: var(--app-shadow);
}

.sk-card {
  height: 340px;
}

.sk-card--tall {
  height: 400px;
}
</style>

