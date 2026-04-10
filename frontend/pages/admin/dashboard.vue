<template>
  <BaseLayout :show-menu="true" :show-footer="false" :content-padding-top="88">
    <view class="admin-page">
      <view class="hero-card">
        <view class="hero-copy">
          <text class="eyebrow">Admin Console</text>
          <text class="hero-title">Operations dashboard for issues and revenue</text>
          <text class="hero-subtitle">
            Keep urgent scooter faults moving through the workflow and track income performance across the last few days and weeks.
          </text>
        </view>
        <button class="refresh-btn" :disabled="busyRefreshing" @tap="refreshDashboard">
          {{ busyRefreshing ? 'Refreshing...' : 'Refresh Dashboard' }}
        </button>
      </view>

      <view class="summary-grid">
        <view class="summary-card alert">
          <text class="summary-label">High Priority</text>
          <text class="summary-value">{{ highPriorityCount }}</text>
          <text class="summary-note">Issues needing urgent attention</text>
        </view>
        <view class="summary-card">
          <text class="summary-label">Open Workflow</text>
          <text class="summary-value">{{ openWorkflowCount }}</text>
          <text class="summary-note">Reported or in progress</text>
        </view>
        <view class="summary-card">
          <text class="summary-label">Fix Rate</text>
          <text class="summary-value">{{ fixRate }}</text>
          <text class="summary-note">Resolved issues across all records</text>
        </view>
        <view class="summary-card income">
          <text class="summary-label">Revenue Window</text>
          <text class="summary-value">{{ formatCurrency(activeReport.totalRevenue) }}</text>
          <text class="summary-note">{{ activeReport.periodLabel || 'Loading report...' }}</text>
        </view>
      </view>

      <view class="dashboard-grid">
        <view class="panel issue-panel">
          <view class="panel-header">
            <view>
              <text class="panel-title">Issue Tracker</text>
              <text class="panel-subtitle">Highlight urgent scooter faults and move them through the workflow.</text>
            </view>
            <button class="ghost-btn" :disabled="issuesLoading" @tap="loadIssues">
              {{ issuesLoading ? 'Loading...' : 'Reload Issues' }}
            </button>
          </view>

          <view class="filter-row">
            <view
              v-for="option in priorityFilters"
              :key="option.value"
              :class="['filter-chip', selectedPriority === option.value ? 'filter-chip-active' : '']"
              @tap="selectedPriority = option.value"
            >
              <text>{{ option.label }}</text>
            </view>
          </view>

          <view v-if="issuesLoading" class="state-card">
            <text class="state-title">Loading issues...</text>
            <text class="state-text">Pulling the latest admin issue list from the backend.</text>
          </view>

          <view v-else-if="!issues.length" class="state-card">
            <text class="state-title">No issues in this view</text>
            <text class="state-text">Try a different priority filter or submit a new issue from an active ride.</text>
          </view>

          <view v-else class="issue-list">
            <view
              v-for="issue in issues"
              :key="issue.issueId"
              :class="['issue-card', `priority-${normalizePriority(issue.priority).toLowerCase()}`]"
            >
              <view class="issue-head">
                <view class="issue-title-wrap">
                  <text class="issue-id">Issue #{{ issue.issueId }}</text>
                  <view class="badges">
                    <text :class="['badge', `badge-${normalizePriority(issue.priority).toLowerCase()}`]">
                      {{ formatPriority(issue.priority) }} Priority
                    </text>
                    <text class="badge badge-neutral">{{ formatCategory(issue.category) }}</text>
                  </view>
                </view>
                <text class="issue-time">{{ formatDateTime(issue.updatedAt || issue.createdAt) }}</text>
              </view>

              <text class="issue-description">{{ issue.description }}</text>

              <view class="issue-meta">
                <text class="issue-meta-item">Scooter #{{ issue.scooterId }}</text>
                <text class="issue-meta-item">Booking {{ issue.bookingId ? `#${issue.bookingId}` : 'N/A' }}</text>
                <text class="issue-meta-item">Reporter {{ issue.userId ? `#${issue.userId}` : 'Unknown' }}</text>
              </view>

              <view class="workflow-row">
                <text class="workflow-label">Workflow</text>
                <view class="workflow-actions">
                  <button
                    v-for="status in workflowStatuses"
                    :key="status.value"
                    :class="[
                      'workflow-btn',
                      normalizeStatus(issue.status) === status.value ? 'workflow-btn-active' : '',
                      issueUpdatingId === issue.issueId ? 'workflow-btn-busy' : ''
                    ]"
                    :disabled="issueUpdatingId === issue.issueId || normalizeStatus(issue.status) === status.value"
                    @tap="setIssueStatus(issue, status.value)"
                  >
                    {{ status.label }}
                  </button>
                </view>
              </view>
            </view>
          </view>
        </view>

        <view class="panel analytics-panel">
          <view class="panel-header">
            <view>
              <text class="panel-title">Analytics</text>
              <text class="panel-subtitle">Compare daily and weekly revenue using the income report endpoint.</text>
            </view>
            <view class="toggle-row">
              <view
                v-for="option in reportTypes"
                :key="option.value"
                :class="['toggle-pill', reportType === option.value ? 'toggle-pill-active' : '']"
                @tap="reportType = option.value"
              >
                <text>{{ option.label }}</text>
              </view>
            </view>
          </view>

          <view class="analytics-cards">
            <view class="metric-card">
              <text class="metric-label">Daily Income</text>
              <text class="metric-value">{{ formatCurrency(dailyReport.totalRevenue) }}</text>
              <text class="metric-note">{{ dailyReport.totalBookings || 0 }} completed bookings in {{ dailyReport.periodLabel || 'the current window' }}</text>
            </view>
            <view class="metric-card">
              <text class="metric-label">Weekly Income</text>
              <text class="metric-value">{{ formatCurrency(weeklyReport.totalRevenue) }}</text>
              <text class="metric-note">{{ weeklyReport.totalBookings || 0 }} completed bookings in {{ weeklyReport.periodLabel || 'the current window' }}</text>
            </view>
          </view>

          <view class="chart-card">
            <view class="chart-head">
              <view>
                <text class="chart-title">{{ activeReportTitle }}</text>
                <text class="chart-subtitle">{{ activeReport.periodLabel || 'Preparing revenue report...' }}</text>
              </view>
              <text class="chart-total">{{ formatCurrency(activeReport.totalRevenue) }}</text>
            </view>

            <view v-if="analyticsLoading" class="state-card chart-state">
              <text class="state-title">Loading analytics...</text>
              <text class="state-text">Fetching daily and weekly income reports.</text>
            </view>

            <view v-else-if="chartError" class="state-card chart-state">
              <text class="state-title">Chart unavailable</text>
              <text class="state-text">{{ chartError }}</text>
            </view>

            <view v-else :id="chartId" class="chart-host"></view>

            <view class="breakdown-list">
              <view v-for="point in activeReport.points" :key="point.label" class="breakdown-row">
                <view class="breakdown-copy">
                  <text class="breakdown-label">{{ point.label }}</text>
                  <text class="breakdown-meta">{{ point.bookingCount || 0 }} bookings</text>
                </view>
                <text class="breakdown-value">{{ formatCurrency(point.revenue) }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </BaseLayout>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import BaseLayout from '@/pages/BaseLayout.vue'
import { getIssues, updateIssueStatus } from '@/api/issue.js'
import { getIncomeReport } from '@/api/admin.js'

const priorityFilters = [
  { label: 'All Issues', value: 'ALL' },
  { label: 'High Priority', value: 'HIGH' },
  { label: 'Medium Priority', value: 'MEDIUM' },
  { label: 'Low Priority', value: 'LOW' }
]

const workflowStatuses = [
  { label: 'Reported', value: 'REPORTED' },
  { label: 'In Progress', value: 'IN_PROGRESS' },
  { label: 'Fixed', value: 'FIXED' }
]

const reportTypes = [
  { label: 'Daily', value: 'daily' },
  { label: 'Weekly', value: 'weekly' }
]

const chartId = 'admin-income-chart'
const issues = ref([])
const issueOverview = ref([])
const selectedPriority = ref('ALL')
const issuesLoading = ref(false)
const analyticsLoading = ref(false)
const busyRefreshing = ref(false)
const issueUpdatingId = ref(null)
const chartError = ref('')
const reportType = ref('daily')
const dailyReport = ref(createEmptyReport('DAILY'))
const weeklyReport = ref(createEmptyReport('WEEKLY'))

let chartInstance = null
let echartsLib = null

const highPriorityCount = computed(() =>
  issueOverview.value.filter((issue) => normalizePriority(issue.priority) === 'HIGH').length
)

const openWorkflowCount = computed(() =>
  issueOverview.value.filter((issue) => ['REPORTED', 'IN_PROGRESS'].includes(normalizeStatus(issue.status))).length
)

const fixRate = computed(() => {
  if (!issueOverview.value.length) return '0%'
  const fixed = issueOverview.value.filter((issue) => normalizeStatus(issue.status) === 'FIXED').length
  return `${Math.round((fixed / issueOverview.value.length) * 100)}%`
})

const activeReport = computed(() => (reportType.value === 'weekly' ? weeklyReport.value : dailyReport.value))

const activeReportTitle = computed(() => (reportType.value === 'weekly' ? 'Weekly Revenue Report' : 'Daily Revenue Report'))

watch(selectedPriority, () => {
  loadIssues()
})

watch(
  () => [reportType.value, dailyReport.value.totalRevenue, weeklyReport.value.totalRevenue, analyticsLoading.value],
  async () => {
    await nextTick()
    renderChart()
  }
)

onMounted(async () => {
  await refreshDashboard()
  if (canUseDom()) {
    window.addEventListener('resize', handleResize)
  }
})

onBeforeUnmount(() => {
  if (canUseDom()) {
    window.removeEventListener('resize', handleResize)
  }
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})

function createEmptyReport(type) {
  return {
    type,
    periodLabel: '',
    periodCount: 0,
    totalRevenue: 0,
    totalBookings: 0,
    points: []
  }
}

async function refreshDashboard() {
  busyRefreshing.value = true
  try {
    await Promise.all([loadIssueOverview(), loadIssues(), loadIncomeReports()])
  } finally {
    busyRefreshing.value = false
  }
}

async function loadIssueOverview() {
  try {
    issueOverview.value = await getIssues()
  } catch (error) {
    console.error('Failed to load issue overview:', error)
    issueOverview.value = []
  }
}

async function loadIssues() {
  issuesLoading.value = true
  try {
    const params = selectedPriority.value === 'ALL' ? {} : { priority: selectedPriority.value }
    issues.value = await getIssues(params)
  } catch (error) {
    console.error('Failed to load issues:', error)
    issues.value = []
  } finally {
    issuesLoading.value = false
  }
}

async function loadIncomeReports() {
  analyticsLoading.value = true
  try {
    const [daily, weekly] = await Promise.all([
      getIncomeReport({ type: 'daily' }),
      getIncomeReport({ type: 'weekly' })
    ])
    dailyReport.value = daily || createEmptyReport('DAILY')
    weeklyReport.value = weekly || createEmptyReport('WEEKLY')
    chartError.value = ''
  } catch (error) {
    console.error('Failed to load income reports:', error)
    dailyReport.value = createEmptyReport('DAILY')
    weeklyReport.value = createEmptyReport('WEEKLY')
    chartError.value = 'Revenue data could not be loaded from the admin report API.'
  } finally {
    analyticsLoading.value = false
  }
}

async function setIssueStatus(issue, status) {
  if (!issue?.issueId || normalizeStatus(issue.status) === status || issueUpdatingId.value) return

  issueUpdatingId.value = issue.issueId
  try {
    await updateIssueStatus(issue.issueId, status)
    issue.status = status
    if (issueOverview.value.length) {
      const target = issueOverview.value.find((item) => item.issueId === issue.issueId)
      if (target) {
        target.status = status
      }
    }
    uni.showToast({ title: 'Status updated', icon: 'success' })
    await Promise.all([loadIssueOverview(), loadIssues()])
  } catch (error) {
    console.error('Failed to update issue status:', error)
  } finally {
    issueUpdatingId.value = null
  }
}

function normalizePriority(priority) {
  return String(priority || 'LOW').trim().toUpperCase()
}

function normalizeStatus(status) {
  return String(status || 'REPORTED').trim().replace(/\s+/g, '_').toUpperCase()
}

function formatPriority(priority) {
  const normalized = normalizePriority(priority)
  return normalized.charAt(0) + normalized.slice(1).toLowerCase()
}

function formatCategory(category) {
  const normalized = String(category || 'OTHER').trim().toLowerCase()
  return normalized
    .split('_')
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ')
}

function formatCurrency(value) {
  const numeric = Number(value || 0)
  return `RMB ${numeric.toFixed(2)}`
}

function formatDateTime(value) {
  if (!value) return 'Unknown update time'

  const normalized = String(value).replace(' ', 'T')
  const date = new Date(normalized)
  if (Number.isNaN(date.getTime())) {
    return String(value)
  }

  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  const hours = `${date.getHours()}`.padStart(2, '0')
  const minutes = `${date.getMinutes()}`.padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

function canUseDom() {
  return typeof window !== 'undefined' && typeof document !== 'undefined'
}

function handleResize() {
  if (chartInstance) {
    chartInstance.resize()
  }
}

async function renderChart() {
  if (analyticsLoading.value || chartError.value) return
  if (!canUseDom()) {
    chartError.value = 'Chart rendering is available in the H5 build where ECharts can access the browser canvas.'
    return
  }

  const host = document.getElementById(chartId)
  if (!host) return

  try {
    if (!echartsLib) {
      const module = await import('echarts')
      echartsLib = module.default || module
    }

    if (!chartInstance) {
      chartInstance = echartsLib.init(host)
    }

    const points = activeReport.value?.points || []
    chartInstance.setOption(
      {
        animationDuration: 500,
        grid: { left: 16, right: 16, top: 40, bottom: 28, containLabel: true },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter(params) {
            const point = params?.[0]
            if (!point) return ''
            const bookingCount = points[point.dataIndex]?.bookingCount || 0
            return `${point.axisValue}<br/>Revenue: RMB ${Number(point.value || 0).toFixed(2)}<br/>Bookings: ${bookingCount}`
          }
        },
        xAxis: {
          type: 'category',
          data: points.map((point) => point.label),
          axisLine: { lineStyle: { color: '#CBD5E1' } },
          axisLabel: { color: '#475569', interval: 0, rotate: reportType.value === 'weekly' ? 10 : 0 }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#E2E8F0' } },
          axisLabel: {
            color: '#64748B',
            formatter(value) {
              return `RMB ${Number(value).toFixed(0)}`
            }
          }
        },
        series: [
          {
            type: 'bar',
            data: points.map((point) => Number(point.revenue || 0)),
            barWidth: '42%',
            itemStyle: {
              borderRadius: [12, 12, 0, 0],
              color: reportType.value === 'weekly' ? '#0F766E' : '#2563EB'
            }
          }
        ]
      },
      true
    )

    chartError.value = ''
  } catch (error) {
    console.error('Failed to render chart:', error)
    chartError.value = 'ECharts is not installed yet. Run `npm install` in the frontend to enable the bar chart.'
  }
}
</script>

<style scoped>
.admin-page {
  padding: 44rpx 34rpx 72rpx;
  background:
    radial-gradient(circle at top left, rgba(14, 165, 233, 0.16), transparent 30%),
    radial-gradient(circle at top right, rgba(37, 99, 235, 0.14), transparent 28%),
    linear-gradient(180deg, #F7FAFF 0%, #EDF4FF 52%, #F8FBFF 100%);
  min-height: calc(100vh - 120rpx);
}

.hero-card,
.panel,
.summary-card,
.metric-card,
.chart-card,
.issue-card,
.state-card {
  background: rgba(255, 255, 255, 0.94);
  border: 1rpx solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 22rpx 48rpx rgba(15, 23, 42, 0.08);
}

.hero-card {
  border-radius: 36rpx;
  padding: 40rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 28rpx;
  margin-bottom: 28rpx;
}

.hero-copy {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  max-width: 1200rpx;
}

.eyebrow {
  font-size: 22rpx;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  color: #0F766E;
  font-weight: 700;
}

.hero-title {
  font-size: 54rpx;
  line-height: 1.1;
  font-weight: 800;
  color: #0F172A;
}

.hero-subtitle {
  font-size: 28rpx;
  line-height: 1.65;
  color: #475569;
}

.refresh-btn,
.ghost-btn,
.workflow-btn {
  border: none;
}

.refresh-btn::after,
.ghost-btn::after,
.workflow-btn::after {
  border: none;
}

.refresh-btn {
  flex-shrink: 0;
  height: 92rpx;
  line-height: 92rpx;
  padding: 0 34rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #0F766E, #2563EB);
  color: #FFFFFF;
  font-size: 28rpx;
  font-weight: 700;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20rpx;
  margin-bottom: 28rpx;
}

.summary-card {
  border-radius: 30rpx;
  padding: 30rpx 28rpx;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.summary-card.alert {
  background: linear-gradient(180deg, rgba(255, 244, 242, 0.96), rgba(255, 255, 255, 0.95));
  border-color: rgba(248, 113, 113, 0.2);
}

.summary-card.income {
  background: linear-gradient(180deg, rgba(236, 253, 245, 0.96), rgba(255, 255, 255, 0.95));
  border-color: rgba(16, 185, 129, 0.2);
}

.summary-label {
  font-size: 24rpx;
  font-weight: 700;
  color: #64748B;
}

.summary-value {
  font-size: 52rpx;
  font-weight: 800;
  color: #0F172A;
}

.summary-note {
  font-size: 24rpx;
  line-height: 1.5;
  color: #64748B;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(0, 0.92fr);
  gap: 24rpx;
  align-items: start;
}

.panel {
  border-radius: 36rpx;
  padding: 34rpx;
}

.panel-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.panel-title {
  display: block;
  font-size: 38rpx;
  font-weight: 800;
  color: #0F172A;
}

.panel-subtitle {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  line-height: 1.55;
  color: #64748B;
}

.ghost-btn {
  height: 80rpx;
  line-height: 80rpx;
  padding: 0 26rpx;
  border-radius: 999rpx;
  background: #EFF6FF;
  color: #1D4ED8;
  font-size: 24rpx;
  font-weight: 700;
}

.filter-row,
.toggle-row {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
}

.filter-row {
  margin-bottom: 22rpx;
}

.filter-chip,
.toggle-pill {
  padding: 16rpx 24rpx;
  border-radius: 999rpx;
  background: #F1F5F9;
  color: #475569;
  font-size: 24rpx;
  font-weight: 700;
}

.filter-chip-active,
.toggle-pill-active {
  background: linear-gradient(135deg, #2563EB, #0F766E);
  color: #FFFFFF;
}

.issue-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.issue-card {
  border-radius: 28rpx;
  padding: 26rpx;
  position: relative;
  overflow: hidden;
}

.issue-card::before {
  content: '';
  position: absolute;
  inset: 0 auto 0 0;
  width: 10rpx;
  border-radius: 28rpx 0 0 28rpx;
  background: #CBD5E1;
}

.issue-card.priority-high::before {
  background: linear-gradient(180deg, #EF4444, #F97316);
}

.issue-card.priority-medium::before {
  background: linear-gradient(180deg, #F59E0B, #FACC15);
}

.issue-card.priority-low::before {
  background: linear-gradient(180deg, #0EA5E9, #2563EB);
}

.issue-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.issue-title-wrap {
  min-width: 0;
}

.issue-id {
  display: block;
  font-size: 30rpx;
  font-weight: 800;
  color: #0F172A;
}

.issue-time {
  font-size: 22rpx;
  color: #64748B;
  white-space: nowrap;
}

.badges {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 10rpx;
}

.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 46rpx;
  padding: 0 18rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 700;
}

.badge-high {
  background: rgba(239, 68, 68, 0.12);
  color: #B91C1C;
}

.badge-medium {
  background: rgba(245, 158, 11, 0.14);
  color: #B45309;
}

.badge-low {
  background: rgba(14, 165, 233, 0.12);
  color: #0369A1;
}

.badge-neutral {
  background: #E2E8F0;
  color: #334155;
}

.issue-description {
  display: block;
  margin-top: 18rpx;
  font-size: 26rpx;
  line-height: 1.65;
  color: #334155;
}

.issue-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx 18rpx;
  margin-top: 18rpx;
}

.issue-meta-item {
  font-size: 22rpx;
  color: #64748B;
  padding: 10rpx 16rpx;
  border-radius: 999rpx;
  background: #F8FAFC;
}

.workflow-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-top: 22rpx;
  padding-top: 18rpx;
  border-top: 1rpx solid #E2E8F0;
}

.workflow-label {
  font-size: 24rpx;
  font-weight: 700;
  color: #475569;
}

.workflow-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10rpx;
}

.workflow-btn {
  height: 72rpx;
  line-height: 72rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  background: #F1F5F9;
  color: #334155;
  font-size: 22rpx;
  font-weight: 700;
}

.workflow-btn-active {
  background: linear-gradient(135deg, #0F766E, #14B8A6);
  color: #FFFFFF;
}

.workflow-btn-busy {
  opacity: 0.7;
}

.analytics-cards {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  margin-bottom: 18rpx;
}

.metric-card {
  border-radius: 26rpx;
  padding: 26rpx;
}

.metric-label {
  display: block;
  font-size: 22rpx;
  font-weight: 700;
  color: #64748B;
}

.metric-value {
  display: block;
  margin-top: 10rpx;
  font-size: 42rpx;
  font-weight: 800;
  color: #0F172A;
}

.metric-note {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.5;
  color: #64748B;
}

.chart-card {
  border-radius: 28rpx;
  padding: 24rpx;
}

.chart-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.chart-title {
  display: block;
  font-size: 30rpx;
  font-weight: 800;
  color: #0F172A;
}

.chart-subtitle {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #64748B;
}

.chart-total {
  font-size: 30rpx;
  font-weight: 800;
  color: #0F766E;
  white-space: nowrap;
}

.chart-host {
  width: 100%;
  height: 520rpx;
  margin-top: 18rpx;
}

.chart-state {
  margin-top: 18rpx;
}

.state-card {
  border-radius: 24rpx;
  padding: 24rpx;
}

.state-title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: #0F172A;
}

.state-text {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.6;
  color: #64748B;
}

.breakdown-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  margin-top: 18rpx;
}

.breakdown-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 18rpx 0;
  border-top: 1rpx solid #E2E8F0;
}

.breakdown-copy {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.breakdown-label {
  font-size: 24rpx;
  font-weight: 700;
  color: #1E293B;
}

.breakdown-meta {
  font-size: 22rpx;
  color: #64748B;
}

.breakdown-value {
  font-size: 24rpx;
  font-weight: 700;
  color: #0F766E;
}

@media (max-width: 1180px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 820px) {
  .admin-page {
    padding: 32rpx 22rpx 60rpx;
  }

  .hero-card,
  .panel {
    padding: 28rpx;
  }

  .hero-card,
  .panel-header,
  .chart-head,
  .workflow-row {
    flex-direction: column;
  }

  .summary-grid,
  .analytics-cards {
    grid-template-columns: 1fr;
  }

  .workflow-actions {
    justify-content: flex-start;
  }

  .chart-total,
  .issue-time {
    white-space: normal;
  }
}

@media (max-width: 640px) {
  .hero-title {
    font-size: 42rpx;
  }

  .summary-value,
  .metric-value {
    font-size: 38rpx;
  }

  .issue-head {
    flex-direction: column;
  }

  .workflow-btn {
    width: 100%;
  }

  .chart-host {
    height: 460rpx;
  }
}
</style>
