<template>
  <BaseLayout :show-menu="true" :show-footer="false" current-page="admin-dashboard">
    <view class="admin-page">
      <view class="admin-shell">
        <view class="admin-hero">
          <view class="hero-copy">
            <text class="hero-eyebrow">Operations Control</text>
            <text class="hero-title">Admin dashboard for fleet, revenue, issues, and walk-in bookings.</text>
            <text class="hero-subtitle">
              Built for operations managers and maintenance staff: quick analytics, faster dispatching, and less tab-hopping.
            </text>
            <view class="hero-actions">
              <button class="hero-btn hero-btn-primary" :disabled="loading" @tap="loadDashboard">
                {{ loading ? 'Refreshing...' : 'Refresh dashboard' }}
              </button>
              <button class="hero-btn hero-btn-secondary" @tap="exportDashboardCsv">Export CSV</button>
              <button class="hero-btn hero-btn-ghost" @tap="exportDashboardPdf">Export PDF</button>
            </view>
          </view>

          <view class="hero-aside">
            <view class="ops-badge">
              <text class="ops-badge-label">Generated</text>
              <text class="ops-badge-value">{{ formatDateTime(snapshot.generatedAt) }}</text>
            </view>
            <view class="ops-badge">
              <text class="ops-badge-label">Access</text>
              <text class="ops-badge-value">{{ adminAccessLabel }}</text>
            </view>
            <view class="ops-badge">
              <text class="ops-badge-label">Hot Zone</text>
              <text class="ops-badge-value">{{ hottestZone }}</text>
            </view>
          </view>
        </view>

        <view class="kpi-grid">
          <view class="kpi-card">
            <text class="kpi-label">Weekly Revenue</text>
            <text class="kpi-value">{{ formatCurrency(financialSummary.weeklyRevenue) }}</text>
            <text class="kpi-foot">Last 7 days of completed bookings</text>
          </view>
          <view class="kpi-card">
            <text class="kpi-label">Daily Revenue</text>
            <text class="kpi-value">{{ formatCurrency(financialSummary.dailyRevenue) }}</text>
            <text class="kpi-foot">Today's realised booking revenue</text>
          </view>
          <view class="kpi-card">
            <text class="kpi-label">Average Order Value</text>
            <text class="kpi-value">{{ formatCurrency(financialSummary.averageOrderValue) }}</text>
            <text class="kpi-foot">{{ financialSummary.completedRides || 0 }} completed rides this week</text>
          </view>
          <view class="kpi-card">
            <text class="kpi-label">Most Popular Period</text>
            <text class="kpi-value">{{ financialSummary.popularPeriod || 'No data' }}</text>
            <text class="kpi-foot">Based on recent completed-booking volume</text>
          </view>
        </view>

        <view class="recommendation-strip">
          <view
            v-for="item in snapshot.recommendations"
            :key="item.title"
            :class="['recommendation-card', `recommendation-${item.level || 'info'}`]"
          >
            <text class="recommendation-title">{{ item.title }}</text>
            <text class="recommendation-copy">{{ item.detail }}</text>
          </view>
        </view>

        <view class="section-grid">
          <view class="panel panel-analytics">
            <view class="panel-head">
              <view>
                <text class="panel-title">Analytics Dashboard</text>
                <text class="panel-subtitle">Financial tables, graphical charts, and popular rental periods.</text>
              </view>
            </view>

            <view class="chart-grid">
              <view class="chart-card">
                <view class="chart-head">
                  <text class="chart-title">Daily revenue over the last 7 days</text>
                  <text class="chart-meta">{{ formatCurrency(maxDailyRevenue) }} peak day</text>
                </view>
                <svg class="chart-svg" :viewBox="`0 0 ${lineChart.width} ${lineChart.height}`">
                  <line
                    v-for="(lineY, index) in lineChart.gridLines"
                    :key="`grid-${index}`"
                    x1="24"
                    :y1="lineY"
                    :x2="lineChart.width - 24"
                    :y2="lineY"
                    class="chart-grid-line"
                  />
                  <path :d="lineChart.areaPath" class="chart-area"></path>
                  <path :d="lineChart.linePath" class="chart-line"></path>
                  <circle
                    v-for="(point, index) in lineChart.points"
                    :key="`point-${index}`"
                    :cx="point.x"
                    :cy="point.y"
                    r="4"
                    class="chart-point"
                  />
                </svg>
                <view class="chart-label-row">
                  <text v-for="label in dailyLabels" :key="label" class="chart-label">{{ label }}</text>
                </view>
              </view>

              <view class="chart-card">
                <view class="chart-head">
                  <text class="chart-title">Weekly revenue by rental period</text>
                  <text class="chart-meta">{{ analytics.weeklyRevenueTable.length }} period buckets</text>
                </view>
                <svg class="chart-svg" :viewBox="`0 0 ${barChart.width} ${barChart.height}`">
                  <rect
                    v-for="bar in barChart.bars"
                    :key="`bar-${bar.index}`"
                    :x="bar.x"
                    :y="bar.y"
                    :width="bar.width"
                    :height="bar.height"
                    rx="12"
                    class="chart-bar"
                  />
                </svg>
                <view class="chart-label-row chart-label-row-bars">
                  <text v-for="label in weeklyLabels" :key="label" class="chart-label chart-label-tight">{{ label }}</text>
                </view>
              </view>
            </view>

            <view class="analytics-bottom">
              <view class="table-card">
                <view class="subpanel-head">
                  <text class="subpanel-title">Weekly Revenue Table</text>
                  <text class="subpanel-copy">Compare revenue and booking count by hire period.</text>
                </view>
                <view class="table-shell">
                  <view class="table-head-row">
                    <text>Hire Period</text>
                    <text>Revenue</text>
                    <text>Bookings</text>
                  </view>
                  <view
                    v-for="row in analytics.weeklyRevenueTable"
                    :key="row.hirePeriod"
                    class="table-row"
                  >
                    <text>{{ row.hirePeriod }}</text>
                    <text>{{ formatCurrency(row.revenue) }}</text>
                    <text>{{ row.bookings }}</text>
                  </view>
                </view>
              </view>

              <view class="table-card">
                <view class="subpanel-head">
                  <text class="subpanel-title">Popular Choice</text>
                  <text class="subpanel-copy">Fast read on which rental periods riders prefer.</text>
                </view>
                <view class="popular-list">
                  <view
                    v-for="item in analytics.popularPeriods"
                    :key="item.hirePeriod"
                    class="popular-row"
                  >
                    <view class="popular-row-main">
                      <text class="popular-name">{{ item.hirePeriod }}</text>
                      <text class="popular-count">{{ item.count }} bookings</text>
                    </view>
                    <view class="popular-bar-track">
                      <view class="popular-bar-fill" :style="{ width: `${resolvePopularityWidth(item.count)}%` }"></view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>

          <view class="panel panel-customer">
            <view class="panel-head">
              <view>
                <text class="panel-title">Customer Sync & Pricing Controls</text>
                <text class="panel-subtitle">Live user sync, package discounts, and a quick backlog audit for remaining rollout work.</text>
              </view>
            </view>

            <view class="customer-grid">
              <view class="user-sync-card">
                <view class="subpanel-head">
                  <view>
                    <text class="subpanel-title">User Sync Center</text>
                    <text class="subpanel-copy">Customer profiles, activity, loyalty signals, and active rides mirrored into operations.</text>
                  </view>
                  <text class="sync-pill">{{ userSummary.totalUsers || 0 }} synced</text>
                </view>

                <view class="user-summary-grid">
                  <view class="mini-card">
                    <text class="mini-title">Active Riders</text>
                    <text class="mini-value">{{ userSummary.activeRiders || 0 }}</text>
                  </view>
                  <view class="mini-card">
                    <text class="mini-title">New This Week</text>
                    <text class="mini-value">{{ userSummary.newThisWeek || 0 }}</text>
                  </view>
                  <view class="mini-card">
                    <text class="mini-title">Loyalty Members</text>
                    <text class="mini-value">{{ userSummary.loyaltyMembers || 0 }}</text>
                  </view>
                  <view class="mini-card">
                    <text class="mini-title">Top City</text>
                    <text class="mini-value mini-value-compact">{{ userSummary.topCity || 'Unknown' }}</text>
                  </view>
                </view>

                <view v-if="pagedUsers.length" class="user-list">
                  <view
                    v-for="user in pagedUsers"
                    :key="user.userId"
                    class="user-row"
                  >
                    <view class="user-row-main">
                      <view v-if="user.avatarUrl" class="user-avatar-shell">
                        <image class="user-avatar-image" :src="user.avatarUrl" mode="aspectFill" />
                      </view>
                      <view v-else class="user-avatar-shell user-avatar-shell-fallback">
                        <text class="user-avatar-initial">{{ deriveUserInitial(user.username) }}</text>
                      </view>
                      <view class="user-copy">
                        <view class="user-copy-row">
                          <text class="user-name">#{{ user.userId }} {{ user.username }}</text>
                          <text :class="['sync-status-pill', user.activeRide ? 'sync-status-pill-active' : '']">
                            {{ user.activeRide ? `Ride #${user.activeBookingId}` : 'Idle' }}
                          </text>
                        </view>
                        <text class="user-meta">{{ user.email }} · {{ user.city }} · {{ user.role }}</text>
                        <text class="user-meta">Bookings {{ user.totalBookings }} · Spend {{ formatCurrency(user.lifetimeSpend) }} · Last ride {{ formatDateTime(user.lastRideAt) }}</text>
                        <view v-if="user.achievements.length" class="achievement-row">
                          <text
                            v-for="achievement in user.achievements"
                            :key="`${user.userId}-${achievement}`"
                            class="achievement-pill"
                          >
                            {{ achievement }}
                          </text>
                        </view>
                      </view>
                    </view>
                  </view>
                </view>
                <text v-else class="empty-copy">No synced customer records are available yet.</text>

                <view v-if="syncedUsers.length" class="pagination-row">
                  <text class="pagination-copy">{{ customerPaginationLabel }}</text>
                  <view class="pagination-actions">
                    <button
                      class="hero-btn hero-btn-ghost hero-btn-small"
                      :disabled="customerPage <= 1"
                      @tap="goToCustomerPage(customerPage - 1)"
                    >
                      Previous
                    </button>
                    <text class="pagination-page">Page {{ customerPage }} / {{ customerTotalPages }}</text>
                    <button
                      class="hero-btn hero-btn-ghost hero-btn-small"
                      :disabled="customerPage >= customerTotalPages"
                      @tap="goToCustomerPage(customerPage + 1)"
                    >
                      Next
                    </button>
                  </view>
                </view>
              </view>

              <view class="customer-side-grid">
                <view class="discount-card">
                  <view class="subpanel-head">
                    <view>
                      <text class="subpanel-title">Discount Rules</text>
                      <text class="subpanel-copy">Package discounts now stay aligned between admin pricing guidance and the booking flow.</text>
                    </view>
                    <text class="sync-pill">{{ discountSummary.activeRules || 0 }} active</text>
                  </view>

                  <view class="discount-list">
                    <view
                      v-for="rule in discountRuleRows"
                      :key="rule.code"
                      class="discount-row"
                    >
                      <view class="discount-row-main">
                        <view class="discount-title-row">
                          <text class="discount-name">{{ rule.label }}</text>
                          <text class="discount-status">{{ rule.status }}</text>
                        </view>
                        <text class="discount-meta">
                          {{ formatCurrency(rule.packagePrice) }} vs {{ formatCurrency(rule.referencePrice) }} list price
                        </text>
                        <text class="discount-copy">{{ rule.recommendedFor }}</text>
                      </view>
                      <view class="discount-metrics">
                        <text class="discount-savings">Save {{ formatCurrency(rule.savings) }}</text>
                        <text class="discount-usage">{{ formatPercent(rule.savingsRate) }} off · {{ rule.usageCount }} uses</text>
                      </view>
                    </view>
                  </view>
                </view>

                <view class="audit-card">
                  <view class="subpanel-head">
                    <view>
                      <text class="subpanel-title">Backlog Check</text>
                      <text class="subpanel-copy">A quick pass over the delivery checklist and the remaining caveats still worth watching.</text>
                    </view>
                    <text class="sync-pill">{{ releaseAuditSummary.ready || 0 }} ready</text>
                  </view>

                  <view class="audit-list">
                    <view
                      v-for="item in releaseAuditItems"
                      :key="item.title"
                      class="audit-row"
                    >
                      <view class="audit-row-head">
                        <text class="audit-title">{{ item.title }}</text>
                        <text :class="['audit-status', item.status === 'READY' ? 'audit-status-ready' : 'audit-status-watch']">
                          {{ item.status }}
                        </text>
                      </view>
                      <text class="audit-copy">{{ item.detail }}</text>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>

          <view class="panel panel-fleet">
            <view class="panel-head">
              <view>
                <text class="panel-title">Fleet Management</text>
                <text class="panel-subtitle">Configuration, status override, charging queue, maintenance logs, and a heatmap.</text>
              </view>
            </view>

            <view class="fleet-top-grid">
              <view class="mini-card">
                <text class="mini-title">Status Summary</text>
                <view class="status-stack">
                  <view
                    v-for="item in fleetStatusList"
                    :key="item.label"
                    class="status-pill"
                  >
                    <text class="status-pill-label">{{ item.label }}</text>
                    <text class="status-pill-value">{{ item.count }}</text>
                  </view>
                </view>
              </view>

              <view class="mini-card">
                <text class="mini-title">Needs Charging List</text>
                <view v-if="snapshot.fleet.chargingQueue.length" class="charging-list">
                  <view
                    v-for="item in snapshot.fleet.chargingQueue"
                    :key="item.scooterId"
                    class="charging-row"
                  >
                    <text>#{{ item.scooterId }} {{ item.model }}</text>
                    <text>{{ item.batteryLevel }}%</text>
                  </view>
                </view>
                <text v-else class="empty-copy">No scooter is below 20% battery right now.</text>
              </view>
            </view>

            <view class="fleet-main-grid">
              <view class="heatmap-card">
                <view class="subpanel-head">
                  <text class="subpanel-title">Rental / Return Heatmap</text>
                  <text class="subpanel-copy">High-frequency zones are based on recent bookings and live fleet positions.</text>
                </view>
                <view class="heatmap-board">
                  <view class="heatmap-glow heatmap-glow-a"></view>
                  <view class="heatmap-glow heatmap-glow-b"></view>
                  <view
                    v-for="hotspot in snapshot.fleet.hotspots"
                    :key="`${hotspot.scooterId}-${hotspot.city}`"
                    class="heat-dot"
                    :style="{
                      left: `${hotspot.x}%`,
                      top: `${hotspot.y}%`,
                      width: `${Math.max(18, hotspot.intensity)}rpx`,
                      height: `${Math.max(18, hotspot.intensity)}rpx`
                    }"
                  >
                    <text class="heat-dot-label">{{ hotspot.city }}</text>
                  </view>
                </view>
                <view class="heatmap-legend">
                  <text>Low traffic</text>
                  <view class="legend-bar"></view>
                  <text>High traffic</text>
                </view>
              </view>

              <view class="fleet-side-panel">
                <view class="subpanel-head">
                  <text class="subpanel-title">3D Scooter Preview</text>
                  <text class="subpanel-copy">Select a scooter to inspect model visuals, pricing, and location.</text>
                </view>

                <view v-if="selectedScooter" class="preview-stage">
                  <view class="preview-card-3d">
                    <view class="preview-card-3d-inner">
                      <image class="preview-main-image" :src="activePreviewImage" mode="aspectFit" />
                    </view>
                  </view>
                  <view class="preview-copy-block">
                    <text class="preview-title">#{{ selectedScooter.id }} {{ selectedScooter.displayName }}</text>
                    <text class="preview-summary">{{ selectedScooter.performanceSummary }}</text>
                    <view class="preview-gallery-strip">
                      <view
                        v-for="image in selectedScooterPreviewGallery"
                        :key="image"
                        :class="['preview-thumb', image === activePreviewImage ? 'preview-thumb-active' : '']"
                        @tap="selectPreviewImage(image)"
                      >
                        <image class="preview-thumb-image" :src="image" mode="aspectFill" />
                      </view>
                    </view>
                    <view class="preview-spec-grid">
                      <view class="preview-spec">
                        <text class="preview-spec-label">Battery</text>
                        <text class="preview-spec-value">{{ selectedScooter.batteryLevel }}%</text>
                      </view>
                      <view class="preview-spec">
                        <text class="preview-spec-label">Range</text>
                        <text class="preview-spec-value">{{ selectedScooter.specs.remainingRangeKm }} km</text>
                      </view>
                      <view class="preview-spec">
                        <text class="preview-spec-label">Base Rate</text>
                        <text class="preview-spec-value">{{ formatCurrency(selectedScooter.basePrice) }}</text>
                      </view>
                      <view class="preview-spec">
                        <text class="preview-spec-label">Per Minute</text>
                        <text class="preview-spec-value">{{ formatCurrency(selectedScooter.pricePerMin) }}</text>
                      </view>
                    </view>
                  </view>
                </view>
                <text v-else class="empty-copy">Select a scooter card below to load a 3D preview and maintenance tools.</text>
              </view>
            </view>

            <view class="fleet-main-grid">
              <view class="fleet-list-card">
                <view class="subpanel-head">
                  <text class="subpanel-title">Scooter Configuration</text>
                  <text class="subpanel-copy">Add new fleet items or update base pricing and coordinates.</text>
                </view>

                <scroll-view class="fleet-scroll" scroll-y enable-flex :show-scrollbar="false">
                  <view
                    v-for="scooter in fleetScooters"
                    :key="scooter.id"
                    :class="['fleet-row', scooter.id === selectedScooterId ? 'fleet-row-active' : '']"
                    @tap="selectScooter(scooter)"
                  >
                    <view class="fleet-row-main">
                      <text class="fleet-row-title">#{{ scooter.id }} {{ scooter.displayName }}</text>
                      <text class="fleet-row-meta">{{ scooter.city }} · {{ scooter.batteryLevel }}% · {{ scooter.status }}</text>
                    </view>
                    <button class="status-btn" @tap.stop="cycleStatus(scooter)">{{ nextStatusLabel(scooter.status) }}</button>
                  </view>
                </scroll-view>
              </view>

              <view class="fleet-form-card">
                <view class="subpanel-head">
                  <text class="subpanel-title">{{ editingExisting ? 'Edit Scooter' : 'Add Scooter' }}</text>
                  <text class="subpanel-copy">Use this panel to configure the selected vehicle or register a new one.</text>
                </view>
                <view class="form-grid">
                  <view class="field">
                    <text class="field-label">Model</text>
                    <input v-model="scooterForm.model" class="field-input" placeholder="Ninebot Max G2" />
                  </view>
                  <view class="field">
                    <text class="field-label">Battery %</text>
                    <input v-model="scooterForm.batteryLevel" class="field-input" type="number" placeholder="88" />
                  </view>
                  <view class="field">
                    <text class="field-label">Base Rate</text>
                    <input v-model="scooterForm.basePrice" class="field-input" type="digit" placeholder="1.00" />
                  </view>
                  <view class="field">
                    <text class="field-label">Per Minute</text>
                    <input v-model="scooterForm.pricePerMin" class="field-input" type="digit" placeholder="0.20" />
                  </view>
                  <view class="field">
                    <text class="field-label">Latitude</text>
                    <input v-model="scooterForm.latitude" class="field-input" type="digit" placeholder="30.76" />
                  </view>
                  <view class="field">
                    <text class="field-label">Longitude</text>
                    <input v-model="scooterForm.longitude" class="field-input" type="digit" placeholder="103.97" />
                  </view>
                  <view class="field field-wide">
                    <text class="field-label">Status</text>
                    <picker :range="statusOptions" @change="handleScooterStatusChange">
                      <view class="field-picker">{{ scooterForm.status || 'AVAILABLE' }}</view>
                    </picker>
                  </view>
                </view>
                <view class="form-actions">
                  <button class="hero-btn hero-btn-primary" :disabled="submittingScooter" @tap="submitScooterForm">
                    {{ submittingScooter ? 'Saving...' : editingExisting ? 'Save changes' : 'Add scooter' }}
                  </button>
                  <button class="hero-btn hero-btn-secondary" @tap="resetScooterForm">Clear</button>
                </view>

                <view class="maintenance-block">
                  <view class="subpanel-head subpanel-tight">
                    <text class="subpanel-title">Maintenance Log</text>
                    <text class="subpanel-copy">Track service history, diagnostics, and charging follow-up for the selected vehicle.</text>
                  </view>
                  <view class="form-grid">
                    <view class="field">
                      <text class="field-label">Technician</text>
                      <input v-model="maintenanceForm.technicianName" class="field-input" placeholder="Ops Workshop" />
                    </view>
                    <view class="field">
                      <text class="field-label">Action</text>
                      <input v-model="maintenanceForm.actionTaken" class="field-input" placeholder="Battery swap" />
                    </view>
                    <view class="field">
                      <text class="field-label">Battery After Service</text>
                      <input v-model="maintenanceForm.batteryLevel" class="field-input" type="number" placeholder="100" />
                    </view>
                    <view class="field field-wide">
                      <text class="field-label">Notes</text>
                      <textarea v-model="maintenanceForm.notes" class="field-textarea" placeholder="Record what was fixed and any follow-up needed." />
                    </view>
                  </view>
                  <view class="form-actions">
                    <button class="hero-btn hero-btn-secondary" :disabled="!selectedScooterId || submittingMaintenance" @tap="submitMaintenanceLog">
                      {{ submittingMaintenance ? 'Logging...' : 'Add maintenance log' }}
                    </button>
                  </view>

                  <view class="maintenance-log-list">
                    <view
                      v-for="log in maintenanceLogs"
                      :key="log.logId"
                      class="maintenance-log-row"
                    >
                      <text class="maintenance-log-title">#{{ log.scooterId }} · {{ log.actionTaken }}</text>
                      <text class="maintenance-log-meta">{{ log.technicianName }} · {{ log.batteryLevel }}% · {{ formatDateTime(log.createdAt) }}</text>
                      <text class="maintenance-log-notes">{{ log.notes || 'No notes' }}</text>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>

          <view class="panel panel-issues">
            <view class="panel-head">
              <view>
                <text class="panel-title">Issue Tracker</text>
                <text class="panel-subtitle">Prioritise faults, track workflow, and auto-assign the right maintenance team.</text>
              </view>
            </view>

            <view class="issues-summary-row">
              <view class="mini-card">
                <text class="mini-title">High Priority</text>
                <text class="mini-value">{{ issueSummary.highPriority || 0 }}</text>
              </view>
              <view class="mini-card">
                <text class="mini-title">Workflow States</text>
                <text class="mini-copy">{{ workflowSummaryText }}</text>
              </view>
              <view class="mini-card">
                <text class="mini-title">Quick Filter</text>
                <button class="hero-btn hero-btn-secondary hero-btn-small" @tap="toggleHighPriorityOnly">
                  {{ highPriorityOnly ? 'Show all issues' : 'Show high priority' }}
                </button>
              </view>
            </view>

            <view v-if="pagedIssues.length" class="issue-list">
              <view
                v-for="issue in pagedIssues"
                :key="issue.issueId"
                class="issue-card"
              >
                <view class="issue-card-head">
                  <view>
                    <text class="issue-title">Issue #{{ issue.issueId }} · Scooter #{{ issue.scooterId }}</text>
                    <text class="issue-meta">{{ issue.category }} · {{ formatDateTime(issue.createdAt) }}</text>
                  </view>
                  <view :class="['priority-pill', `priority-${(issue.priority || 'MEDIUM').toLowerCase()}`]">
                    {{ issue.priority }}
                  </view>
                </view>
                <text class="issue-description">{{ issue.description }}</text>
                <view class="issue-controls">
                  <picker :range="priorityOptions" @change="updateIssueField(issue, 'priority', priorityOptions[$event.detail.value])">
                    <view class="field-picker">Priority: {{ issue.priority }}</view>
                  </picker>
                  <picker :range="workflowOptions" @change="updateIssueField(issue, 'workflowStatus', workflowOptions[$event.detail.value])">
                    <view class="field-picker">Workflow: {{ issue.workflowStatus }}</view>
                  </picker>
                  <button class="hero-btn hero-btn-ghost hero-btn-small" @tap="autoAssignIssue(issue)">Auto-assign</button>
                </view>
                <view class="issue-footer">
                  <text class="issue-assignee">Assigned to {{ issue.assignedStaff || 'Unassigned' }}</text>
                  <text class="issue-status">{{ issue.status }}</text>
                </view>
              </view>
            </view>
            <text v-else class="empty-copy">No issues match the current filter.</text>

            <view v-if="displayedIssues.length" class="pagination-row">
              <text class="pagination-copy">{{ issuePaginationLabel }}</text>
              <view class="pagination-actions">
                <button
                  class="hero-btn hero-btn-ghost hero-btn-small"
                  :disabled="issuePage <= 1"
                  @tap="goToIssuePage(issuePage - 1)"
                >
                  Previous
                </button>
                <text class="pagination-page">Page {{ issuePage }} / {{ issueTotalPages }}</text>
                <button
                  class="hero-btn hero-btn-ghost hero-btn-small"
                  :disabled="issuePage >= issueTotalPages"
                  @tap="goToIssuePage(issuePage + 1)"
                >
                  Next
                </button>
              </view>
            </view>
          </view>

          <view class="panel panel-pos">
            <view class="panel-head">
              <view>
                <text class="panel-title">Staff POS Mode</text>
                <text class="panel-subtitle">Create walk-in bookings for unregistered users and send confirmation manually.</text>
              </view>
            </view>

            <view class="pos-grid">
              <view class="pos-form-card">
                <view class="subpanel-head">
                  <text class="subpanel-title">Walk-in Booking</text>
                  <text class="subpanel-copy">Front-desk mode for guest riders without a registered account.</text>
                </view>
                <view class="form-grid">
                  <view class="field">
                    <text class="field-label">Guest Name</text>
                    <input v-model="posForm.guestName" class="field-input" placeholder="Visitor name" />
                  </view>
                  <view class="field">
                    <text class="field-label">Guest Email</text>
                    <input v-model="posForm.guestEmail" class="field-input" placeholder="visitor@example.com" />
                  </view>
                  <view class="field">
                    <text class="field-label">Scooter</text>
                    <picker :range="posScooterOptions" range-key="label" @change="handlePosScooterChange">
                      <view class="field-picker">{{ posScooterLabel }}</view>
                    </picker>
                  </view>
                  <view class="field">
                    <text class="field-label">Hire Period</text>
                    <picker :range="hirePeriodOptions" @change="handlePosHirePeriodChange">
                      <view class="field-picker">{{ posForm.hirePeriod }}</view>
                    </picker>
                  </view>
                  <view class="field field-wide">
                    <text class="field-label">Notes</text>
                    <textarea v-model="posForm.notes" class="field-textarea" placeholder="Store pickup, helmet handover, or special reminder." />
                  </view>
                </view>
                <view class="checkbox-row" @tap="toggleSendConfirmation">
                  <view :class="['checkbox-faux', posForm.sendConfirmation ? 'checkbox-faux-active' : '']"></view>
                  <text>Send confirmation immediately after creating the booking</text>
                </view>
                <view class="form-actions">
                  <button class="hero-btn hero-btn-primary" :disabled="submittingPos" @tap="submitPosBooking">
                    {{ submittingPos ? 'Creating...' : 'Create walk-in booking' }}
                  </button>
                </view>
              </view>

              <view class="pos-list-card">
                <view class="subpanel-head">
                  <text class="subpanel-title">Recent POS Bookings</text>
                  <text class="subpanel-copy">{{ snapshot.pos.summary.count || 0 }} total staff-side bookings recorded.</text>
                </view>
                <view v-if="snapshot.pos.recentBookings.length" class="pos-list">
                  <view
                    v-for="booking in snapshot.pos.recentBookings"
                    :key="booking.bookingId"
                    class="pos-row"
                  >
                    <view class="pos-row-main">
                      <text class="pos-row-title">#{{ booking.bookingId }} · {{ booking.guestName }}</text>
                      <text class="pos-row-meta">
                        {{ booking.scooterModel }} · {{ booking.hirePeriod }} · {{ formatCurrency(booking.estimatedCost) }}
                      </text>
                      <text class="pos-row-meta">
                        {{ booking.bookingStatus }} · {{ formatDateTime(booking.desiredStartTime) }}
                      </text>
                    </view>
                    <button class="hero-btn hero-btn-ghost hero-btn-small" @tap="sendConfirmation(booking)">
                      Send confirmation
                    </button>
                  </view>
                </view>
                <text v-else class="empty-copy">No staff bookings yet.</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </BaseLayout>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import BaseLayout from './BaseLayout.vue'
import {
  addMaintenanceLog,
  createAdminScooter,
  createStaffBooking,
  getAdminDashboard,
  getMaintenanceLogs,
  overrideScooterStatus,
  sendStaffConfirmation,
  updateAdminIssue,
  updateAdminScooter
} from '@/api/admin.js'
import { enrichScooter } from '@/utils/scooterCatalog.js'
import {
  buildBarChartGeometry,
  buildLineChartGeometry,
  exportCsv,
  formatCompactNumber,
  formatCurrency,
  formatDateTime,
  printReport
} from '@/utils/adminDashboard.js'

const createEmptySnapshot = () => ({
  generatedAt: '',
  analytics: {
    financialSummary: {
      weeklyRevenue: 0,
      dailyRevenue: 0,
      averageOrderValue: 0,
      completedRides: 0,
      popularPeriod: ''
    },
    weeklyRevenueTable: [],
    dailyRevenueTable: [],
    popularPeriods: [],
    chart: {
      dailyLabels: [],
      dailyRevenue: [],
      weeklyLabels: [],
      weeklyRevenue: []
    }
  },
  fleet: {
    statusSummary: {},
    chargingQueue: [],
    hotspots: [],
    scooters: [],
    maintenanceLogs: []
  },
  issues: {
    records: [],
    summary: {
      total: 0,
      highPriority: 0,
      workflow: {}
    },
    highPriorityView: []
  },
  pos: {
    summary: {
      count: 0,
      sentConfirmations: 0
    },
    recentBookings: []
  },
  users: {
    summary: {
      totalUsers: 0,
      activeRiders: 0,
      newThisWeek: 0,
      loyaltyMembers: 0,
      syncedCities: 0,
      topCity: ''
    },
    records: []
  },
  discountRules: {
    summary: {
      activeRules: 0,
      mostUsedRule: '',
      averageSavingsRate: 0
    },
    rules: []
  },
  releaseAudit: {
    summary: {
      ready: 0,
      watch: 0,
      openIssues: 0,
      recentPosBookings: 0
    },
    items: []
  },
  recommendations: []
})

const snapshot = reactive(createEmptySnapshot())
const loading = ref(false)
const highPriorityOnly = ref(false)
const selectedScooterId = ref(null)
const selectedPreviewImage = ref('')
const customerPage = ref(1)
const issuePage = ref(1)
const maintenanceLogs = ref([])
const submittingScooter = ref(false)
const submittingMaintenance = ref(false)
const submittingPos = ref(false)
const redirectingUnauthorized = ref(false)

const CUSTOMER_PAGE_SIZE = 6
const ISSUE_PAGE_SIZE = 5
const ADMIN_ROLES = ['ADMIN', 'MANAGER']

const statusOptions = ['AVAILABLE', 'IN_USE', 'MAINTENANCE', 'LOW_BATTERY', 'REMOTE_LOCKED']
const priorityOptions = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']
const workflowOptions = ['REPORTED', 'IN_PROGRESS', 'FIXED']
const hirePeriodOptions = ['1_HOUR', '4_HOURS', '1_DAY', '1_WEEK']

const scooterForm = reactive({
  id: '',
  model: '',
  batteryLevel: '100',
  basePrice: '1.00',
  pricePerMin: '0.20',
  latitude: '',
  longitude: '',
  status: 'AVAILABLE'
})

const maintenanceForm = reactive({
  technicianName: 'Ops Workshop',
  actionTaken: '',
  batteryLevel: '100',
  notes: ''
})

const posForm = reactive({
  guestName: '',
  guestEmail: '',
  scooterId: '',
  hirePeriod: '1_HOUR',
  notes: '',
  sendConfirmation: true
})

const analytics = computed(() => snapshot.analytics || createEmptySnapshot().analytics)
const financialSummary = computed(() => analytics.value.financialSummary || {})
const dailyLabels = computed(() => analytics.value.chart?.dailyLabels || [])
const weeklyLabels = computed(() => analytics.value.chart?.weeklyLabels || [])
const maxDailyRevenue = computed(() => Math.max(...(analytics.value.chart?.dailyRevenue || [0]), 0))
const lineChart = computed(() => buildLineChartGeometry(analytics.value.chart?.dailyRevenue || []))
const barChart = computed(() => buildBarChartGeometry(analytics.value.chart?.weeklyRevenue || []))

const fleetScooters = computed(() =>
  (snapshot.fleet.scooters || []).map(item => enrichScooter({
    ...item,
    id: item.id,
    scooterId: item.id,
    batteryLevel: item.batteryLevel,
    model: item.model,
    basePrice: item.basePrice,
    pricePerMin: item.pricePerMin
  }))
)

const fleetStatusList = computed(() => {
  const statusSummary = snapshot.fleet.statusSummary || {}
  return [
    { label: 'Available', count: statusSummary.AVAILABLE || 0 },
    { label: 'In Use', count: statusSummary.IN_USE || 0 },
    { label: 'Maintenance', count: statusSummary.MAINTENANCE || 0 },
    { label: 'Low Battery', count: statusSummary.LOW_BATTERY || 0 },
    { label: 'Locked', count: statusSummary.REMOTE_LOCKED || 0 }
  ]
})

const selectedScooter = computed(() =>
  fleetScooters.value.find(item => String(item.id) === String(selectedScooterId.value)) || null
)

const editingExisting = computed(() => Boolean(scooterForm.id))
const issueSummary = computed(() => snapshot.issues.summary || {})
const displayedIssues = computed(() => {
  const records = snapshot.issues.records || []
  if (!highPriorityOnly.value) return records
  return records.filter(item => ['HIGH', 'CRITICAL'].includes(String(item.priority || '').toUpperCase()))
})
const userSummary = computed(() => snapshot.users?.summary || {})
const syncedUsers = computed(() => snapshot.users?.records || [])
const discountSummary = computed(() => snapshot.discountRules?.summary || {})
const discountRuleRows = computed(() => snapshot.discountRules?.rules || [])
const releaseAuditSummary = computed(() => snapshot.releaseAudit?.summary || {})
const releaseAuditItems = computed(() => snapshot.releaseAudit?.items || [])
const customerTotalPages = computed(() => Math.max(1, Math.ceil(syncedUsers.value.length / CUSTOMER_PAGE_SIZE)))
const issueTotalPages = computed(() => Math.max(1, Math.ceil(displayedIssues.value.length / ISSUE_PAGE_SIZE)))
const pagedUsers = computed(() => {
  const start = (customerPage.value - 1) * CUSTOMER_PAGE_SIZE
  return syncedUsers.value.slice(start, start + CUSTOMER_PAGE_SIZE)
})
const pagedIssues = computed(() => {
  const start = (issuePage.value - 1) * ISSUE_PAGE_SIZE
  return displayedIssues.value.slice(start, start + ISSUE_PAGE_SIZE)
})
const customerPaginationLabel = computed(() =>
  buildPaginationLabel(customerPage.value, CUSTOMER_PAGE_SIZE, syncedUsers.value.length, 'customers')
)
const issuePaginationLabel = computed(() =>
  buildPaginationLabel(issuePage.value, ISSUE_PAGE_SIZE, displayedIssues.value.length, 'issues')
)
const selectedScooterPreviewGallery = computed(() => {
  const gallery = selectedScooter.value?.gallery || []
  return Array.isArray(gallery) && gallery.length
    ? gallery.slice(0, 4)
    : selectedScooter.value?.imageUrl
      ? [selectedScooter.value.imageUrl]
      : []
})
const activePreviewImage = computed(() =>
  selectedPreviewImage.value || selectedScooterPreviewGallery.value[0] || selectedScooter.value?.imageUrl || ''
)

const workflowSummaryText = computed(() => {
  const workflow = issueSummary.value.workflow || {}
  return Object.keys(workflow).length
    ? Object.entries(workflow).map(([key, value]) => `${key}: ${value}`).join(' · ')
    : 'No issue activity yet'
})

const posScooterOptions = computed(() =>
  fleetScooters.value.map(item => ({
    label: `#${item.id} ${item.displayName} (${item.status})`,
    value: item.id
  }))
)

const posScooterLabel = computed(() => {
  const match = posScooterOptions.value.find(item => String(item.value) === String(posForm.scooterId))
  return match?.label || 'Select scooter'
})

const adminAccessLabel = computed(() => {
  try {
    const stored = uni.getStorageSync('userInfo')
    const userInfo = typeof stored === 'string' ? JSON.parse(stored) : stored
    const role = String(userInfo?.role || '').toUpperCase()
    if (ADMIN_ROLES.includes(role)) return `${role} session`
    return 'Demo / shared access'
  } catch {
    return 'Demo / shared access'
  }
})

const hottestZone = computed(() => snapshot.fleet.hotspots?.[0]?.city || 'No hotspot data')

const buildPaginationLabel = (page, pageSize, total, label) => {
  if (!total) return `No ${label} to display`
  const start = (page - 1) * pageSize + 1
  const end = Math.min(total, start + pageSize - 1)
  return `Showing ${start}-${end} of ${total} ${label}`
}

const hasAdminAccess = () => {
  try {
    const stored = uni.getStorageSync('userInfo')
    const userInfo = typeof stored === 'string' ? JSON.parse(stored) : stored
    return ADMIN_ROLES.includes(String(userInfo?.role || '').toUpperCase())
  } catch {
    return false
  }
}

const ensureAdminAccess = () => {
  if (hasAdminAccess()) return true
  if (redirectingUnauthorized.value) return false

  redirectingUnauthorized.value = true
  uni.showToast({
    title: 'Admin access required',
    icon: 'none',
    duration: 1500
  })
  setTimeout(() => {
    uni.reLaunch({
      url: '/pages/index',
      complete: () => {
        redirectingUnauthorized.value = false
      }
    })
  }, 1500)
  return false
}

const applySnapshot = (data) => {
  const fresh = createEmptySnapshot()
  Object.assign(snapshot, fresh, data || {})
}

const resetScooterForm = () => {
  Object.assign(scooterForm, {
    id: '',
    model: '',
    batteryLevel: '100',
    basePrice: '1.00',
    pricePerMin: '0.20',
    latitude: '',
    longitude: '',
    status: 'AVAILABLE'
  })
}

const resetMaintenanceForm = () => {
  Object.assign(maintenanceForm, {
    technicianName: 'Ops Workshop',
    actionTaken: '',
    batteryLevel: selectedScooter.value?.batteryLevel ? String(selectedScooter.value.batteryLevel) : '100',
    notes: ''
  })
}

const syncPreviewGallery = () => {
  selectedPreviewImage.value = selectedScooterPreviewGallery.value[0] || ''
}

const resetPosForm = () => {
  Object.assign(posForm, {
    guestName: '',
    guestEmail: '',
    scooterId: selectedScooterId.value || '',
    hirePeriod: '1_HOUR',
    notes: '',
    sendConfirmation: true
  })
}

const loadMaintenanceLogs = async (scooterId) => {
  if (!scooterId) {
    maintenanceLogs.value = snapshot.fleet.maintenanceLogs || []
    return
  }
  try {
    maintenanceLogs.value = await getMaintenanceLogs(scooterId)
  } catch (error) {
    console.error('Failed to load maintenance logs:', error)
    maintenanceLogs.value = snapshot.fleet.maintenanceLogs || []
  }
}

const loadDashboard = async () => {
  loading.value = true
  try {
    const data = await getAdminDashboard()
    applySnapshot(data)

    if (!selectedScooterId.value && snapshot.fleet.scooters?.length) {
      selectedScooterId.value = snapshot.fleet.scooters[0].id
    }

    if (selectedScooter.value) {
      hydrateScooterForm(selectedScooter.value)
      syncPreviewGallery()
      await loadMaintenanceLogs(selectedScooter.value.id)
    } else {
      selectedPreviewImage.value = ''
      maintenanceLogs.value = snapshot.fleet.maintenanceLogs || []
    }
  } catch (error) {
    console.error('Failed to load admin dashboard:', error)
  } finally {
    loading.value = false
  }
}

const hydrateScooterForm = (scooter) => {
  if (!scooter) return
  Object.assign(scooterForm, {
    id: scooter.id,
    model: scooter.model || scooter.displayName || '',
    batteryLevel: String(scooter.batteryLevel ?? 100),
    basePrice: String(scooter.basePrice ?? 1),
    pricePerMin: String(scooter.pricePerMin ?? 0.2),
    latitude: String(scooter.latitude ?? ''),
    longitude: String(scooter.longitude ?? ''),
    status: scooter.status || 'AVAILABLE'
  })
}

const selectScooter = async (scooter) => {
  selectedScooterId.value = scooter.id
  hydrateScooterForm(scooter)
  resetMaintenanceForm()
  selectedPreviewImage.value = scooter.gallery?.[0] || scooter.imageUrl || ''
  if (!posForm.scooterId) {
    posForm.scooterId = scooter.id
  }
  await loadMaintenanceLogs(scooter.id)
}

const selectPreviewImage = (image) => {
  selectedPreviewImage.value = image
}

const cycleStatus = async (scooter) => {
  const currentIndex = statusOptions.indexOf(scooter.status)
  const next = statusOptions[(currentIndex + 1) % statusOptions.length]
  try {
    await overrideScooterStatus(scooter.id, next)
    uni.showToast({ title: `Status -> ${next}`, icon: 'success' })
    await loadDashboard()
  } catch (error) {
    console.error('Failed to override scooter status:', error)
  }
}

const nextStatusLabel = (status) => {
  const currentIndex = statusOptions.indexOf(status)
  const next = statusOptions[(currentIndex + 1) % statusOptions.length]
  return `Set ${next}`
}

const handleScooterStatusChange = (event) => {
  const index = Number(event?.detail?.value ?? 0)
  scooterForm.status = statusOptions[index] || 'AVAILABLE'
}

const submitScooterForm = async () => {
  const payload = {
    model: scooterForm.model,
    batteryLevel: Number(scooterForm.batteryLevel),
    basePrice: Number(scooterForm.basePrice),
    pricePerMin: Number(scooterForm.pricePerMin),
    latitude: Number(scooterForm.latitude),
    longitude: Number(scooterForm.longitude),
    status: scooterForm.status
  }

  submittingScooter.value = true
  try {
    if (editingExisting.value) {
      await updateAdminScooter(scooterForm.id, payload)
      uni.showToast({ title: 'Scooter updated', icon: 'success' })
    } else {
      await createAdminScooter(payload)
      uni.showToast({ title: 'Scooter added', icon: 'success' })
    }
    resetScooterForm()
    await loadDashboard()
  } catch (error) {
    console.error('Failed to save scooter:', error)
  } finally {
    submittingScooter.value = false
  }
}

const submitMaintenanceLog = async () => {
  if (!selectedScooterId.value) {
    uni.showToast({ title: 'Select a scooter first', icon: 'none' })
    return
  }

  submittingMaintenance.value = true
  try {
    await addMaintenanceLog(selectedScooterId.value, {
      technicianName: maintenanceForm.technicianName,
      actionTaken: maintenanceForm.actionTaken,
      batteryLevel: Number(maintenanceForm.batteryLevel),
      notes: maintenanceForm.notes
    })
    uni.showToast({ title: 'Maintenance logged', icon: 'success' })
    resetMaintenanceForm()
    await loadDashboard()
    await loadMaintenanceLogs(selectedScooterId.value)
  } catch (error) {
    console.error('Failed to log maintenance:', error)
  } finally {
    submittingMaintenance.value = false
  }
}

const updateIssueField = async (issue, key, value) => {
  try {
    await updateAdminIssue(issue.issueId, { [key]: value })
    await loadDashboard()
  } catch (error) {
    console.error('Failed to update issue:', error)
  }
}

const autoAssignIssue = async (issue) => {
  try {
    await updateAdminIssue(issue.issueId, { autoAssign: true })
    uni.showToast({ title: 'Issue assigned', icon: 'success' })
    await loadDashboard()
  } catch (error) {
    console.error('Failed to auto assign issue:', error)
  }
}

const toggleHighPriorityOnly = () => {
  highPriorityOnly.value = !highPriorityOnly.value
  issuePage.value = 1
}

const goToCustomerPage = (page) => {
  customerPage.value = Math.min(customerTotalPages.value, Math.max(1, page))
}

const goToIssuePage = (page) => {
  issuePage.value = Math.min(issueTotalPages.value, Math.max(1, page))
}

const handlePosScooterChange = (event) => {
  const option = posScooterOptions.value[Number(event?.detail?.value ?? 0)]
  posForm.scooterId = option?.value || ''
}

const handlePosHirePeriodChange = (event) => {
  posForm.hirePeriod = hirePeriodOptions[Number(event?.detail?.value ?? 0)] || '1_HOUR'
}

const toggleSendConfirmation = () => {
  posForm.sendConfirmation = !posForm.sendConfirmation
}

const submitPosBooking = async () => {
  submittingPos.value = true
  try {
    await createStaffBooking({
      guestName: posForm.guestName,
      guestEmail: posForm.guestEmail,
      scooterId: Number(posForm.scooterId || selectedScooterId.value),
      hirePeriod: posForm.hirePeriod,
      notes: posForm.notes,
      sendConfirmation: posForm.sendConfirmation
    })
    uni.showToast({ title: 'Booking created', icon: 'success' })
    resetPosForm()
    await loadDashboard()
  } catch (error) {
    console.error('Failed to create POS booking:', error)
  } finally {
    submittingPos.value = false
  }
}

const sendConfirmation = async (booking) => {
  try {
    await sendStaffConfirmation(booking.bookingId, booking.guestEmail)
    uni.showToast({ title: 'Confirmation sent', icon: 'success' })
    await loadDashboard()
  } catch (error) {
    console.error('Failed to send confirmation:', error)
  }
}

const resolvePopularityWidth = (count) => {
  const max = Math.max(...(analytics.value.popularPeriods || []).map(item => Number(item.count || 0)), 1)
  return Math.max(8, (Number(count || 0) / max) * 100)
}

const formatPercent = (value) => {
  const parsed = Number(value || 0)
  return `${Math.round(parsed * 100)}%`
}

const deriveUserInitial = (value) => String(value || 'U').trim().charAt(0).toUpperCase() || 'U'

const exportDashboardCsv = () => {
  const analyticsRows = (analytics.value.dailyRevenueTable || []).map(item => ({
    date: item.date,
    revenue: item.revenue,
    bookings: item.bookings
  }))
  const success = exportCsv('admin-dashboard-daily-revenue.csv', analyticsRows)
  if (!success) {
    uni.showToast({ title: 'CSV export is available on web only', icon: 'none' })
  }
}

const renderTableHtml = (headers, rows) => `
  <table>
    <thead>
      <tr>${headers.map(header => `<th>${header}</th>`).join('')}</tr>
    </thead>
    <tbody>
      ${rows.map(row => `<tr>${row.map(cell => `<td>${cell}</td>`).join('')}</tr>`).join('')}
    </tbody>
  </table>
`

const exportDashboardPdf = () => {
  const success = printReport('ScooterGo Admin Dashboard', [
    {
      title: 'Financial Summary',
      html: renderTableHtml(
        ['Metric', 'Value'],
        [
          ['Weekly revenue', formatCurrency(financialSummary.value.weeklyRevenue)],
          ['Daily revenue', formatCurrency(financialSummary.value.dailyRevenue)],
          ['Average order value', formatCurrency(financialSummary.value.averageOrderValue)],
          ['Popular period', financialSummary.value.popularPeriod || 'No data']
        ]
      )
    },
    {
      title: 'Charging Queue',
      html: renderTableHtml(
        ['Scooter', 'Model', 'Battery'],
        (snapshot.fleet.chargingQueue || []).map(item => [`#${item.scooterId}`, item.model, `${item.batteryLevel}%`])
      )
    },
    {
      title: 'Issue Tracker',
      html: renderTableHtml(
        ['Issue', 'Scooter', 'Priority', 'Workflow', 'Assignee'],
        (snapshot.issues.records || []).slice(0, 8).map(item => [
          `#${item.issueId}`,
          `#${item.scooterId}`,
          item.priority,
          item.workflowStatus,
          item.assignedStaff || 'Unassigned'
        ])
      )
    },
    {
      title: 'Customer Sync',
      html: renderTableHtml(
        ['User', 'City', 'Bookings', 'Spend', 'Active Ride'],
        syncedUsers.value.map(user => [
          `#${user.userId} ${user.username}`,
          user.city,
          formatCompactNumber(user.totalBookings),
          formatCurrency(user.lifetimeSpend),
          user.activeRide ? `#${user.activeBookingId}` : 'Idle'
        ])
      )
    }
  ])

  if (!success) {
    uni.showToast({ title: 'PDF export is available on web only', icon: 'none' })
  }
}

onShow(() => {
  if (!ensureAdminAccess()) return
  loadDashboard()
  resetPosForm()
})

watch(
  () => syncedUsers.value.length,
  () => {
    if (customerPage.value > customerTotalPages.value) {
      customerPage.value = customerTotalPages.value
    }
  },
  { immediate: true }
)

watch(
  () => displayedIssues.value.length,
  () => {
    if (issuePage.value > issueTotalPages.value) {
      issuePage.value = issueTotalPages.value
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background: transparent;
}

.admin-shell {
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 36rpx 36rpx 72rpx;
  box-sizing: border-box;
}

.admin-hero,
.panel,
.kpi-card,
.recommendation-card,
.mini-card,
.table-card,
.chart-card,
.fleet-list-card,
.fleet-form-card,
.heatmap-card,
.fleet-side-panel,
.user-sync-card,
.discount-card,
.audit-card,
.pos-form-card,
.pos-list-card {
  background: #ffffff;
  border: 1px solid rgba(37, 99, 235, 0.08);
  box-shadow: 0 18rpx 44rpx rgba(37, 99, 235, 0.08);
}

.admin-hero {
  display: grid;
  grid-template-columns: 1.9fr 1fr;
  gap: 28rpx;
  border-radius: 42rpx;
  padding: 36rpx;
  margin-bottom: 28rpx;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(239, 246, 255, 0.96) 100%);
}

.hero-copy {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.hero-eyebrow {
  font-size: 24rpx;
  text-transform: uppercase;
  letter-spacing: 0.28em;
  color: #2563eb;
}

.hero-title {
  font-size: 56rpx;
  line-height: 1.08;
  color: #0f172a;
  font-weight: 700;
}

.hero-subtitle {
  font-size: 28rpx;
  line-height: 1.7;
  color: #4b5563;
  max-width: 860rpx;
}

.hero-actions,
.form-actions,
.hero-aside,
.panel-chip-row,
.issues-summary-row,
.checkbox-row,
.chart-label-row,
.heatmap-legend,
.status-stack,
.preview-spec-grid,
.issue-controls,
.pagination-row,
.pagination-actions {
  display: flex;
  gap: 16rpx;
  flex-wrap: wrap;
}

.hero-btn {
  min-width: 220rpx;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 600;
  padding: 0 22rpx;
}

.hero-btn::after,
.status-btn::after {
  border: none;
}

.hero-btn-primary {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #ffffff;
}

.hero-btn-secondary {
  background: rgba(37, 99, 235, 0.08);
  color: #1d4ed8;
}

.hero-btn-ghost {
  background: #ffffff;
  color: #0f172a;
  border: 1px solid #d8e2ee;
}

.hero-btn-small {
  min-width: 0;
  padding: 0 18rpx;
  height: 72rpx;
  line-height: 72rpx;
}

.hero-aside {
  align-content: flex-start;
}

.ops-badge {
  flex: 1 1 100%;
  border-radius: 26rpx;
  padding: 22rpx 24rpx;
  background: linear-gradient(135deg, #eff6ff 0%, #f8fbff 100%);
  color: #1e3a8a;
  border: 1px solid rgba(37, 99, 235, 0.1);
}

.ops-badge-label {
  display: block;
  font-size: 22rpx;
  text-transform: uppercase;
  letter-spacing: 0.2em;
  opacity: 0.7;
  margin-bottom: 10rpx;
}

.ops-badge-value {
  font-size: 30rpx;
  line-height: 1.4;
  font-weight: 600;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18rpx;
  margin-bottom: 22rpx;
}

.kpi-card {
  border-radius: 30rpx;
  padding: 24rpx;
}

.kpi-label,
.mini-title,
.subpanel-copy,
.panel-subtitle,
.kpi-foot,
.chart-meta,
.field-label,
.empty-copy,
.maintenance-log-meta,
.pos-row-meta,
.issue-meta,
.issue-assignee,
.issue-status,
.recommendation-copy {
  color: #64748b;
}

.kpi-label,
.mini-title,
.field-label {
  font-size: 24rpx;
}

.kpi-value,
.mini-value {
  display: block;
  margin: 12rpx 0 10rpx;
  font-size: 42rpx;
  line-height: 1.1;
  color: #10233e;
  font-weight: 700;
}

.mini-value-compact {
  font-size: 30rpx;
}

.kpi-foot,
.mini-copy,
.subpanel-copy,
.chart-meta,
.recommendation-copy,
.empty-copy {
  font-size: 24rpx;
  line-height: 1.55;
}

.recommendation-strip {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18rpx;
  margin-bottom: 26rpx;
}

.recommendation-card {
  border-radius: 28rpx;
  padding: 22rpx 24rpx;
}

.recommendation-info {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.06), rgba(147, 197, 253, 0.14));
}

.recommendation-warning {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.12), rgba(251, 191, 36, 0.18));
}

.recommendation-critical {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.12), rgba(248, 113, 113, 0.18));
}

.recommendation-title,
.panel-title,
.subpanel-title,
.chart-title,
.fleet-row-title,
.user-name,
.discount-name,
.audit-title,
.preview-title,
.issue-title,
.pos-row-title,
.maintenance-log-title {
  display: block;
  color: #0f172a;
  font-weight: 700;
}

.recommendation-title {
  font-size: 30rpx;
  margin-bottom: 10rpx;
}

.section-grid {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.panel {
  border-radius: 36rpx;
  padding: 30rpx;
}

.panel-head,
.subpanel-head,
.chart-head,
.issue-card-head,
.fleet-row,
.charging-row,
.pos-row,
.maintenance-log-row,
.popular-row,
.popular-row-main,
.table-head-row,
.table-row,
.preview-copy-block,
.issue-footer {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
}

.panel-head,
.subpanel-head {
  align-items: flex-start;
  margin-bottom: 22rpx;
}

.subpanel-tight {
  margin-bottom: 18rpx;
}

.panel-title {
  font-size: 40rpx;
  margin-bottom: 8rpx;
}

.panel-subtitle {
  display: block;
  font-size: 26rpx;
  line-height: 1.55;
  max-width: 840rpx;
}

.panel-chip {
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 22rpx;
  font-weight: 600;
}

.chart-grid,
.fleet-top-grid,
.fleet-main-grid,
.analytics-bottom,
.customer-grid,
.customer-side-grid,
.user-summary-grid,
.pos-grid {
  display: grid;
  gap: 18rpx;
}

.chart-grid,
.analytics-bottom,
.customer-grid,
.fleet-main-grid,
.pos-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.fleet-top-grid {
  grid-template-columns: 1fr 1fr;
  margin-bottom: 18rpx;
}

.customer-side-grid,
.user-summary-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.customer-side-grid {
  grid-template-columns: 1fr;
}

.mini-card,
.table-card,
.chart-card,
.heatmap-card,
.user-sync-card,
.discount-card,
.audit-card,
.fleet-side-panel,
.fleet-list-card,
.fleet-form-card,
.pos-form-card,
.pos-list-card {
  border-radius: 30rpx;
  padding: 24rpx;
}

.mini-title,
.subpanel-title,
.chart-title {
  font-size: 30rpx;
  margin-bottom: 8rpx;
}

.status-pill {
  flex: 1 1 30%;
  min-width: 180rpx;
  border-radius: 22rpx;
  padding: 18rpx;
  background: linear-gradient(135deg, #eff6ff 0%, #f8fbff 100%);
}

.status-pill-label,
.preview-spec-label {
  display: block;
  font-size: 22rpx;
  color: #64748b;
  margin-bottom: 8rpx;
}

.status-pill-value,
.preview-spec-value {
  font-size: 30rpx;
  color: #10233e;
  font-weight: 700;
}

.charging-list,
.issue-list,
.maintenance-log-list,
.pos-list,
.popular-list {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.charging-row,
.fleet-row,
.maintenance-log-row,
.pos-row,
.table-row,
.popular-row {
  align-items: center;
  border-radius: 22rpx;
  background: #f8fbff;
  padding: 18rpx 20rpx;
}

.heatmap-board {
  position: relative;
  min-height: 420rpx;
  border-radius: 28rpx;
  overflow: hidden;
  background:
    linear-gradient(135deg, rgba(16, 185, 129, 0.07), rgba(14, 165, 233, 0.12)),
    linear-gradient(0deg, transparent 24%, rgba(148, 163, 184, 0.18) 25%, transparent 26%, transparent 49%, rgba(148, 163, 184, 0.18) 50%, transparent 51%, transparent 74%, rgba(148, 163, 184, 0.18) 75%, transparent 76%),
    linear-gradient(90deg, transparent 24%, rgba(148, 163, 184, 0.18) 25%, transparent 26%, transparent 49%, rgba(148, 163, 184, 0.18) 50%, transparent 51%, transparent 74%, rgba(148, 163, 184, 0.18) 75%, transparent 76%),
    #f5fbff;
}

.heatmap-glow {
  position: absolute;
  border-radius: 999rpx;
  filter: blur(28rpx);
  opacity: 0.45;
}

.heatmap-glow-a {
  width: 260rpx;
  height: 260rpx;
  left: 18%;
  top: 18%;
  background: rgba(14, 165, 233, 0.32);
}

.heatmap-glow-b {
  width: 320rpx;
  height: 320rpx;
  right: 10%;
  bottom: 8%;
  background: rgba(249, 115, 22, 0.28);
}

.heat-dot {
  position: absolute;
  transform: translate(-50%, -50%);
  border-radius: 999rpx;
  background: radial-gradient(circle, rgba(249, 115, 22, 0.95) 0%, rgba(239, 68, 68, 0.62) 60%, rgba(239, 68, 68, 0.02) 100%);
  box-shadow: 0 0 0 12rpx rgba(249, 115, 22, 0.08);
}

.heat-dot-label {
  position: absolute;
  left: 50%;
  top: calc(100% + 12rpx);
  transform: translateX(-50%);
  white-space: nowrap;
  font-size: 20rpx;
  color: #10233e;
  font-weight: 600;
}

.legend-bar {
  flex: 1;
  min-width: 180rpx;
  height: 14rpx;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #84cc16, #facc15, #f97316, #ef4444);
}

.preview-stage {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18rpx;
  align-items: center;
}

.preview-card-3d {
  perspective: 1600rpx;
  min-height: 320rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-card-3d-inner {
  width: 100%;
  height: 300rpx;
  border-radius: 28rpx;
  background:
    radial-gradient(circle at 30% 20%, rgba(148, 163, 184, 0.16), transparent 46%),
    linear-gradient(160deg, #0f172a, #1e3a5f);
  display: flex;
  align-items: center;
  justify-content: center;
  transform-style: preserve-3d;
  animation: scooterFloat 6s ease-in-out infinite;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.08), 0 30rpx 60rpx rgba(15, 23, 42, 0.22);
}

.preview-main-image {
  width: 72%;
  height: 72%;
  transform: rotateY(-18deg) rotateX(7deg) translateZ(24rpx);
  filter: drop-shadow(0 26rpx 34rpx rgba(15, 23, 42, 0.42));
}

.preview-copy-block {
  flex-direction: column;
  justify-content: flex-start;
}

.preview-gallery-strip,
.achievement-row {
  display: flex;
  gap: 10rpx;
  flex-wrap: wrap;
}

.preview-thumb {
  width: 88rpx;
  height: 88rpx;
  border-radius: 18rpx;
  overflow: hidden;
  border: 2rpx solid transparent;
  background: #e2e8f0;
}

.preview-thumb-active {
  border-color: #0ea5e9;
  box-shadow: 0 8rpx 18rpx rgba(14, 165, 233, 0.18);
}

.preview-thumb-image {
  width: 100%;
  height: 100%;
}

.preview-title {
  font-size: 34rpx;
  margin-bottom: 12rpx;
}

.preview-summary,
.maintenance-log-notes,
.issue-description {
  font-size: 24rpx;
  line-height: 1.65;
  color: #475569;
}

.preview-spec {
  flex: 1 1 40%;
  min-width: 180rpx;
  border-radius: 22rpx;
  background: linear-gradient(135deg, #eff6ff 0%, #f8fbff 100%);
  padding: 16rpx;
}

.fleet-scroll {
  max-height: 620rpx;
}

.fleet-row-active {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.1), rgba(147, 197, 253, 0.16));
  border: 1px solid rgba(37, 99, 235, 0.18);
}

.fleet-row-main,
.pos-row-main {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.fleet-row-meta {
  font-size: 22rpx;
  color: #64748b;
}

.status-btn {
  min-width: 188rpx;
  border-radius: 999rpx;
  background: rgba(37, 99, 235, 0.08);
  color: #1d4ed8;
  font-size: 22rpx;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.field-wide {
  grid-column: 1 / -1;
}

.field-input,
.field-picker,
.field-textarea {
  width: 100%;
  min-height: 84rpx;
  border-radius: 20rpx;
  background: #f8fbff;
  border: 1px solid rgba(37, 99, 235, 0.1);
  padding: 0 18rpx;
  font-size: 26rpx;
  color: #0f172a;
  box-sizing: border-box;
}

.field-picker {
  display: flex;
  align-items: center;
}

.field-textarea {
  min-height: 180rpx;
  padding: 18rpx;
}

.maintenance-block {
  margin-top: 26rpx;
  padding-top: 22rpx;
  border-top: 1px solid #e5edf5;
}

.sync-pill,
.discount-status,
.audit-status,
.sync-status-pill,
.achievement-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  font-size: 20rpx;
  font-weight: 700;
}

.sync-pill,
.sync-status-pill,
.achievement-pill {
  background: #eff6ff;
  color: #1d4ed8;
}

.sync-status-pill-active {
  background: #ecfdf5;
  color: #047857;
}

.user-list,
.discount-list,
.audit-list,
.pagination-row {
  display: flex;
  gap: 14rpx;
}

.user-list,
.discount-list,
.audit-list {
  flex-direction: column;
}

.user-row,
.discount-row,
.audit-row {
  border-radius: 24rpx;
  background: #f8fbff;
  padding: 18rpx 20rpx;
}

.user-row-main,
.user-copy,
.discount-row-main {
  display: flex;
  gap: 14rpx;
}

.user-row-main,
.user-copy,
.discount-row-main {
  align-items: flex-start;
}

.user-copy,
.discount-row-main {
  flex-direction: column;
}

.user-avatar-shell {
  width: 84rpx;
  height: 84rpx;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.user-avatar-shell-fallback {
  background: linear-gradient(135deg, #0f766e, #0ea5a4);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar-image {
  width: 100%;
  height: 100%;
}

.user-avatar-initial {
  color: #ffffff;
  font-size: 28rpx;
  font-weight: 700;
}

.user-copy {
  flex: 1;
}

.user-copy-row,
.discount-title-row,
.audit-row-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
}

.user-name,
.discount-name,
.audit-title {
  font-size: 28rpx;
}

.user-meta,
.discount-meta,
.discount-copy,
.audit-copy,
.discount-usage {
  font-size: 22rpx;
  line-height: 1.55;
  color: #64748b;
}

.discount-row,
.audit-row {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.discount-status {
  background: rgba(20, 184, 166, 0.12);
  color: #0f766e;
}

.discount-metrics {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
  flex-wrap: wrap;
}

.discount-savings {
  font-size: 26rpx;
  font-weight: 700;
  color: #0f766e;
}

.audit-status-ready {
  background: rgba(34, 197, 94, 0.12);
  color: #15803d;
}

.audit-status-watch {
  background: rgba(245, 158, 11, 0.14);
  color: #b45309;
}

.pagination-row {
  align-items: center;
  justify-content: space-between;
  margin-top: 18rpx;
  padding-top: 8rpx;
}

.pagination-copy,
.pagination-page {
  font-size: 22rpx;
  color: #64748b;
}

.pagination-page {
  font-weight: 700;
  color: #0f172a;
}

.table-shell,
.issue-card {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.table-head-row,
.table-row {
  display: grid;
  grid-template-columns: 1.4fr 1fr 0.7fr;
  gap: 12rpx;
}

.table-head-row {
  padding: 0 4rpx 8rpx;
  font-size: 22rpx;
  color: #64748b;
}

.popular-row {
  flex-direction: column;
  align-items: stretch;
}

.popular-row-main {
  align-items: center;
}

.popular-name,
.popular-count {
  font-size: 24rpx;
}

.popular-bar-track {
  height: 14rpx;
  border-radius: 999rpx;
  background: #e2e8f0;
  overflow: hidden;
}

.popular-bar-fill {
  height: 100%;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #0ea5e9, #14b8a6);
}

.chart-svg {
  width: 100%;
  height: 320rpx;
  margin-top: 10rpx;
}

.chart-grid-line {
  stroke: rgba(148, 163, 184, 0.26);
  stroke-width: 1;
}

.chart-area {
  fill: rgba(14, 165, 233, 0.16);
}

.chart-line {
  fill: none;
  stroke: #0ea5e9;
  stroke-width: 5;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.chart-point {
  fill: #0f766e;
  stroke: #ffffff;
  stroke-width: 3;
}

.chart-bar {
  fill: #14b8a6;
}

.chart-label-row {
  justify-content: space-between;
  margin-top: 6rpx;
}

.chart-label {
  flex: 1;
  font-size: 20rpx;
  color: #64748b;
  text-align: center;
}

.chart-label-tight {
  font-size: 19rpx;
}

.issues-summary-row .mini-card {
  flex: 1;
}

.issue-card {
  border-radius: 28rpx;
  padding: 20rpx;
  background: #f8fbff;
}

.priority-pill {
  align-self: flex-start;
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 700;
}

.priority-low {
  background: #ecfdf5;
  color: #047857;
}

.priority-medium {
  background: #eff6ff;
  color: #1d4ed8;
}

.priority-high {
  background: #fff7ed;
  color: #c2410c;
}

.priority-critical {
  background: #fee2e2;
  color: #b91c1c;
}

.issue-footer {
  align-items: center;
}

.checkbox-row {
  align-items: center;
  margin: 16rpx 0 18rpx;
  font-size: 24rpx;
  color: #334155;
}

.checkbox-faux {
  width: 30rpx;
  height: 30rpx;
  border-radius: 10rpx;
  border: 1px solid #94a3b8;
  background: #ffffff;
}

.checkbox-faux-active {
  background: linear-gradient(135deg, #0ea5e9, #14b8a6);
  border-color: transparent;
}

.maintenance-log-row,
.pos-row {
  flex-direction: column;
  align-items: stretch;
}

.maintenance-log-title,
.pos-row-title {
  font-size: 26rpx;
}

.maintenance-log-meta,
.pos-row-meta,
.issue-meta {
  font-size: 22rpx;
}

@keyframes scooterFloat {
  0%, 100% {
    transform: rotateY(-10deg) rotateX(4deg) translateY(0);
  }
  50% {
    transform: rotateY(10deg) rotateX(-4deg) translateY(-8rpx);
  }
}

@media (max-width: 1180px) {
  .admin-hero,
  .kpi-grid,
  .recommendation-strip,
  .chart-grid,
  .analytics-bottom,
  .customer-grid,
  .fleet-top-grid,
  .fleet-main-grid,
  .pos-grid {
    grid-template-columns: 1fr;
  }

  .preview-stage {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 820px) {
  .admin-shell {
    padding: 24rpx 20rpx 56rpx;
  }

  .admin-hero,
  .panel,
  .kpi-card,
  .mini-card,
  .table-card,
  .chart-card,
  .user-sync-card,
  .discount-card,
  .audit-card,
  .fleet-list-card,
  .fleet-form-card,
  .heatmap-card,
  .fleet-side-panel,
  .pos-form-card,
  .pos-list-card {
    border-radius: 26rpx;
    padding: 22rpx;
  }

  .hero-title {
    font-size: 44rpx;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .table-head-row,
  .table-row {
    grid-template-columns: 1fr;
  }

  .chart-label-row-bars {
    gap: 8rpx;
  }

  .discount-metrics,
  .user-copy-row,
  .audit-row-head,
  .pagination-row {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
