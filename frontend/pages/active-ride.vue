<template>
  <BaseLayout nav-type="default" :show-menu="true" :show-footer="false" :current-page="currentNavPage">

    <!-- ═══════════════ MY RIDE PAGE (trip) ═══════════════ -->
    <view v-if="isTripPage" class="trip-page">

      <!-- Dark immersive hero with timer -->
      <view class="trip-hero">
        <view class="trip-hero-bg">
          <view class="trip-hero-ring ring-1"></view>
          <view class="trip-hero-ring ring-2"></view>
          <view class="trip-hero-ring ring-3"></view>
        </view>
        <view class="trip-hero-top">
          <view class="trip-status-pill" :class="activeRide ? 'trip-status-active' : 'trip-status-idle'">
            <view class="trip-status-dot"></view>
            <text class="trip-status-text">{{ activeRide ? 'Ride in Progress' : 'No Active Ride' }}</text>
          </view>
          <view v-if="syncing" class="trip-sync-badge">
            <text class="trip-sync-text">Syncing...</text>
          </view>
        </view>

        <!-- Big countdown timer display -->
        <view v-if="activeRide" class="trip-timer-section">
          <text class="trip-timer-label">Time Remaining</text>
          <view class="trip-timer-display" :class="countdownExpired ? 'timer-expired' : ''">
            <text class="trip-timer-value">{{ countdownText }}</text>
          </view>
          <text class="trip-timer-hint">Ends at {{ plannedEndTime }}</text>
        </view>
        <view v-else class="trip-timer-section">
          <text class="trip-timer-label">Ready to Ride</text>
          <view class="trip-timer-display">
            <text class="trip-timer-value">--:--:--</text>
          </view>
          <text class="trip-timer-hint">Start a booking to begin your countdown</text>
        </view>

        <!-- Alert banner inside hero -->
        <view v-if="activeRide && countdownExpired" class="trip-alert-banner">
          <text class="trip-alert-icon">⚠</text>
          <text class="trip-alert-text">Time's up — extend or end your ride now</text>
        </view>
      </view>

      <!-- Scooter info strip -->
      <view v-if="activeRide" class="trip-scooter-strip">
        <view class="trip-scooter-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="5.5" cy="17.5" r="2.5"/><circle cx="18.5" cy="17.5" r="2.5"/>
            <path d="M8 17.5h7M15 17.5l-2-5h-2l-1-3H8"/><path d="M15 12.5l3-5h-2"/>
          </svg>
        </view>
        <view class="trip-scooter-info">
          <text class="trip-scooter-id">#{{ activeRide.scooterId }} · {{ activeRide.scooterModel }}</text>
          <text class="trip-scooter-since">Started {{ formatDateTime(activeRide.startTime) }}</text>
        </view>
        <view class="trip-scooter-cost">
          <text class="trip-scooter-cost-value">{{ formatMoney(activeRide.totalCost) }}</text>
          <text class="trip-scooter-cost-label">Reserved</text>
        </view>
      </view>

      <!-- Main content stack -->
      <view class="trip-content">

        <!-- Active ride: extend + actions card -->
        <view v-if="activeRide" class="trip-card">
          <view class="trip-card-header">
            <view class="trip-card-icon-wrap">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
              </svg>
            </view>
            <text class="trip-card-title">Extend Ride</text>
          </view>
          <text class="trip-section-label">Add more time</text>
          <view class="trip-extend-grid">
            <view
              v-for="(label, index) in extensionLabels"
              :key="label"
              class="trip-extend-chip"
              :class="selectedExtensionIndex === index ? 'trip-extend-chip-active' : ''"
              @tap="selectedExtensionIndex = index"
            >
              <text class="trip-extend-chip-time">{{ extensionOptions[index] }}</text>
              <text class="trip-extend-chip-unit">min</text>
            </view>
          </view>
          <view class="trip-extend-preview">
            <view class="trip-preview-row">
              <text class="trip-preview-label">New end time</text>
              <text class="trip-preview-value">{{ projectedEndTime }}</text>
            </view>
            <view class="trip-preview-divider"></view>
            <view class="trip-preview-row">
              <text class="trip-preview-label">Updated total</text>
              <text class="trip-preview-value trip-preview-cost">{{ projectedTotalCost }}</text>
            </view>
          </view>
          <button class="trip-btn-extend" :disabled="busyAction === 'extend'" @tap="handleExtendRide">
            <text>{{ busyAction === 'extend' ? 'Adding time...' : `+ Add ${extensionMinutes} min` }}</text>
          </button>
        </view>

        <!-- Active ride action buttons -->
        <view v-if="activeRide" class="trip-action-row">
          <button class="trip-action-btn trip-action-report" @tap="openIssuePopup">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
            <text>Report Issue</text>
          </button>
          <button class="trip-action-btn trip-action-end" :disabled="busyAction === 'end'" @tap="handleEndRide">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
            </svg>
            <text>{{ busyAction === 'end' ? 'Ending...' : 'End Ride' }}</text>
          </button>
        </view>

        <!-- Empty state for trip page -->
        <view v-else class="trip-empty-state">
          <view class="trip-empty-illustration">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="#93C5FD" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="5.5" cy="17.5" r="2.5"/><circle cx="18.5" cy="17.5" r="2.5"/>
              <path d="M8 17.5h7M15 17.5l-2-5h-2l-1-3H8"/><path d="M15 12.5l3-5h-2"/>
            </svg>
          </view>
          <text class="trip-empty-title">Your ride hub is ready</text>
          <text class="trip-empty-desc">Once you unlock a scooter, your live timer, controls, and stats will all appear right here.</text>
          <button class="trip-btn-find" @tap="goToFindScooter">
            <text>Find a Scooter</text>
          </button>
          <view class="trip-preview-tiles">
            <view class="trip-preview-tile">
              <text class="trip-ptile-icon">⏱</text>
              <text class="trip-ptile-label">Live countdown</text>
            </view>
            <view class="trip-preview-tile">
              <text class="trip-ptile-icon">🛴</text>
              <text class="trip-ptile-label">Scooter info</text>
            </view>
            <view class="trip-preview-tile">
              <text class="trip-ptile-icon">⚡</text>
              <text class="trip-ptile-label">Extend ride</text>
            </view>
            <view class="trip-preview-tile">
              <text class="trip-ptile-icon">🔧</text>
              <text class="trip-ptile-label">Report issues</text>
            </view>
          </view>
        </view>

      </view>
    </view>

    <!-- ═══════════════ BOOK & RIDING PAGE (booking) ═══════════════ -->
    <view v-else class="booking-page">

      <!-- Light top header bar -->
      <view class="booking-header">
        <view class="booking-header-left">
          <text class="booking-header-title">Book & Riding</text>
          <text class="booking-header-sub">{{ activeRide ? 'Booking confirmed' : 'No active booking' }}</text>
        </view>
        <view class="booking-status-badge" :class="activeRide ? 'booking-badge-active' : 'booking-badge-idle'">
          <view class="booking-badge-dot"></view>
          <text class="booking-badge-text">{{ activeRide ? 'ACTIVE' : 'IDLE' }}</text>
        </view>
      </view>

      <view v-if="syncing" class="booking-sync-bar">
        <text class="booking-sync-text">🔄 Syncing booking details...</text>
      </view>

      <!-- Active booking content -->
      <view v-if="activeRide" class="booking-content">

        <!-- Order card (Didi/Meituan style) -->
        <view class="booking-order-card">
          <view class="booking-order-top">
            <view class="booking-order-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="5.5" cy="17.5" r="2.5"/><circle cx="18.5" cy="17.5" r="2.5"/>
                <path d="M8 17.5h7M15 17.5l-2-5h-2l-1-3H8"/><path d="M15 12.5l3-5h-2"/>
              </svg>
            </view>
            <view class="booking-order-title-wrap">
              <text class="booking-order-model">{{ activeRide.scooterModel || 'Electric Scooter' }}</text>
              <text class="booking-order-id">Order #{{ activeRide.bookingId || 'Pending' }} · Scooter #{{ activeRide.scooterId }}</text>
            </view>
            <view class="booking-order-status-chip">
              <text class="booking-order-status-text">{{ activeRide.status || 'ACTIVE' }}</text>
            </view>
          </view>

          <!-- Timeline-style trip info -->
          <view class="booking-timeline">
            <view class="booking-timeline-row">
              <view class="booking-tl-dot booking-tl-dot-start"></view>
              <view class="booking-tl-line"></view>
              <view class="booking-tl-dot booking-tl-dot-end"></view>
              <view class="booking-tl-labels">
                <view class="booking-tl-label-item">
                  <text class="booking-tl-time">{{ formatDateTime(activeRide.startTime) }}</text>
                  <text class="booking-tl-desc">Ride started</text>
                </view>
                <view class="booking-tl-label-item">
                  <text class="booking-tl-time">{{ plannedEndTime }}</text>
                  <text class="booking-tl-desc">Planned finish</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- Info grid cards -->
        <view class="booking-info-section">
          <text class="booking-section-title">Trip Details</text>
          <view class="booking-info-grid">
            <view class="booking-info-cell">
              <text class="booking-info-icon">🕐</text>
              <text class="booking-info-label">Duration</text>
              <text class="booking-info-value">{{ durationLabel }}</text>
            </view>
            <view class="booking-info-cell">
              <text class="booking-info-icon">💳</text>
              <text class="booking-info-label">Reserved</text>
              <text class="booking-info-value">{{ formatMoney(activeRide.totalCost) }}</text>
            </view>
            <view class="booking-info-cell" v-if="activeRide.cardLast4">
              <text class="booking-info-icon">🏦</text>
              <text class="booking-info-label">Payment</text>
              <text class="booking-info-value">···· {{ activeRide.cardLast4 }}</text>
            </view>
            <view class="booking-info-cell">
              <text class="booking-info-icon">📍</text>
              <text class="booking-info-label">Status</text>
              <text class="booking-info-value">{{ activeRide.status || 'Active' }}</text>
            </view>
          </view>
        </view>

        <!-- Quick extend section -->
        <view class="booking-extend-section">
          <text class="booking-section-title">Extend Booking</text>
          <view class="booking-extend-chips">
            <view
              v-for="(label, index) in extensionLabels"
              :key="label"
              class="booking-extend-chip"
              :class="selectedExtensionIndex === index ? 'booking-chip-active' : ''"
              @tap="selectedExtensionIndex = index"
            >
              <text class="booking-chip-text">{{ label }}</text>
            </view>
          </view>
          <view class="booking-extend-preview">
            <text class="booking-ext-preview-text">New end: <text class="booking-ext-highlight">{{ projectedEndTime }}</text> · Total: <text class="booking-ext-highlight">{{ projectedTotalCost }}</text></text>
          </view>
        </view>

        <!-- Action buttons -->
        <view class="booking-actions">
          <button class="booking-btn-extend" :disabled="busyAction === 'extend'" @tap="handleExtendRide">
            <text>{{ busyAction === 'extend' ? 'Extending...' : `Extend +${extensionMinutes} min` }}</text>
          </button>
          <view class="booking-btn-row">
            <button class="booking-btn-secondary" @tap="goToRideDashboard">
              <text>Open My Ride</text>
            </button>
            <button class="booking-btn-danger" :disabled="busyAction === 'end'" @tap="handleEndRide">
              <text>{{ busyAction === 'end' ? 'Ending...' : 'End Ride' }}</text>
            </button>
          </view>
        </view>
      </view>

      <!-- Empty booking state -->
      <view v-else class="booking-empty">
        <view class="booking-empty-card">
          <view class="booking-empty-art">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#BFDBFE" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="4" width="18" height="16" rx="2"/><path d="M3 9h18M9 4v5M15 4v5"/>
            </svg>
          </view>
          <text class="booking-empty-title">No Active Booking</text>
          <text class="booking-empty-desc">Choose a scooter on the map to confirm your plan and payment. All details will appear here once booked.</text>
          <view class="booking-empty-checklist">
            <view class="booking-checklist-item">
              <text class="booking-check-icon">○</text>
              <text class="booking-check-text">Select scooter on map</text>
            </view>
            <view class="booking-checklist-item">
              <text class="booking-check-icon">○</text>
              <text class="booking-check-text">Choose your plan & duration</text>
            </view>
            <view class="booking-checklist-item">
              <text class="booking-check-icon">○</text>
              <text class="booking-check-text">Confirm payment & ride</text>
            </view>
          </view>
          <view class="booking-empty-actions">
            <button class="booking-btn-primary-cta" @tap="goToFindScooter">
              <text>Book a Scooter</text>
            </button>
            <button class="booking-btn-ghost" @tap="goToRideDashboard">
              <text>Open My Ride</text>
            </button>
          </view>
        </view>
        <view class="booking-empty-info-grid">
          <view class="booking-info-preview-cell">
            <text class="booking-info-preview-icon">🛴</text>
            <text class="booking-info-preview-label">Scooter</text>
            <text class="booking-info-preview-value">Assigned after map selection</text>
          </view>
          <view class="booking-info-preview-cell">
            <text class="booking-info-preview-icon">⏱</text>
            <text class="booking-info-preview-label">Plan</text>
            <text class="booking-info-preview-value">Duration & rate you choose</text>
          </view>
          <view class="booking-info-preview-cell">
            <text class="booking-info-preview-icon">💳</text>
            <text class="booking-info-preview-label">Payment</text>
            <text class="booking-info-preview-value">Card or saved method</text>
          </view>
          <view class="booking-info-preview-cell">
            <text class="booking-info-preview-icon">📋</text>
            <text class="booking-info-preview-label">Status</text>
            <text class="booking-info-preview-value">Booked, active, or completed</text>
          </view>
        </view>
      </view>
    </view>

    <!-- ═══════════════ ISSUE REPORT PANEL (shared) ═══════════════ -->
    <view v-if="issuePanelOpen" class="overlay" :style="ui.overlay" @tap="closeIssuePopup">
      <view class="sheet" :style="ui.sheet" @tap.stop>
        <view class="card-head" :style="ui.cardHead">
          <view>
            <text class="card-title" :style="ui.cardTitle">Report an issue</text>
            <text class="card-sub" :style="ui.cardSub">Tell us what went wrong so we can help quickly.</text>
          </view>
        </view>
        <text class="section-label" :style="ui.tileLabel">Fault category</text>
        <view class="chips" :style="ui.chips">
          <view
            v-for="category in issueCategories"
            :key="category.value"
            :class="['chip', issueForm.category === category.value ? 'chip-active' : '']"
            :style="[ui.chip, issueForm.category === category.value ? ui.chipActive : null]"
            @tap="issueForm.category = category.value"
          >
            <text :class="['chip-text', issueForm.category === category.value ? 'chip-text-active' : '']" :style="[ui.chipText, issueForm.category === category.value ? ui.chipTextActive : null]">{{ category.label }}</text>
          </view>
        </view>
        <text class="section-label" :style="ui.tileLabel">Description</text>
        <textarea
          v-model="issueForm.description"
          class="textarea"
          :style="ui.textarea"
          maxlength="300"
          placeholder="Describe the fault, unusual noise, brake issue, battery issue, or anything else we should know."
        />
        <view class="actions" :style="ui.actions">
          <button class="btn btn-secondary" :style="[ui.button, ui.buttonSecondary]" @tap="closeIssuePopup">
            <text>Cancel</text>
          </button>
          <button class="btn btn-primary" :style="[ui.button, ui.buttonPrimary]" :disabled="busyAction === 'issue'" @tap="submitIssue">
            <text>{{ busyAction === 'issue' ? 'Submitting...' : 'Submit Report' }}</text>
          </button>
        </view>
      </view>
    </view>

  </BaseLayout>
</template>



<script setup>
import { computed, ref } from 'vue'
import { onLoad, onShow, onHide, onUnload } from '@dcloudio/uni-app'
import BaseLayout from '@/pages/BaseLayout.vue'
import { endRide, extendRide, getUserBookings } from '@/api/booking.js'
import { reportIssue } from '@/api/issue.js'
import { getScooterById } from '@/api/scooter.js'
import { clearStoredActiveRide, findActiveRide, getRideEndTime, getStoredActiveRide, getStoredUserId, normalizeActiveRide, setStoredActiveRide } from '@/utils/activeRide.js'

const extensionOptions = [15, 30, 60]
const extensionLabels = extensionOptions.map(minutes => `${minutes} min`)
const issueCategories = [
  { label: 'Mechanical', value: 'MECHANICAL' },
  { label: 'Electrical', value: 'ELECTRICAL' },
  { label: 'Battery', value: 'BATTERY' },
  { label: 'Other', value: 'OTHER' }
]

const currentNavPage = ref('trip')
const syncing = ref(false)
const busyAction = ref('')
const countdownReached = ref(false)
const activeRide = ref(getStoredActiveRide())
const selectedExtensionIndex = ref(0)
const issuePanelOpen = ref(false)
const issueForm = ref({ category: 'OTHER', description: '' })
const nowTick = ref(Date.now())
let timer = null

const ui = Object.freeze({
  page: {
    minHeight: 'calc(100vh - 160px)',
    padding: '24px 18px 32px',
    background: 'radial-gradient(circle at top right, rgba(59, 130, 246, 0.18), transparent 36%), linear-gradient(180deg, #F8FBFF 0%, #EEF5FF 48%, #F8FBFF 100%)'
  },
  hero: {
    padding: '22px',
    borderRadius: '22px',
    background: 'linear-gradient(135deg, #1D4ED8 0%, #2563EB 55%, #60A5FA 100%)',
    color: '#FFFFFF',
    marginBottom: '18px',
    boxShadow: '0 18px 40px rgba(37, 99, 235, 0.18)'
  },
  eyebrow: {
    display: 'inline-block',
    padding: '6px 10px',
    borderRadius: '999px',
    background: 'rgba(255, 255, 255, 0.16)',
    fontSize: '12px',
    fontWeight: '700',
    letterSpacing: '0.08em',
    textTransform: 'uppercase'
  },
  title: {
    display: 'block',
    marginTop: '12px',
    fontSize: '28px',
    lineHeight: '1.2',
    fontWeight: '800'
  },
  subtitle: {
    display: 'block',
    marginTop: '10px',
    fontSize: '15px',
    lineHeight: '1.65',
    color: 'rgba(255, 255, 255, 0.86)'
  },
  heroChip: {
    display: 'inline-flex',
    alignItems: 'center',
    gap: '8px',
    marginTop: '16px',
    padding: '10px 14px',
    borderRadius: '999px',
    background: 'rgba(255, 255, 255, 0.92)'
  },
  heroChipDot: {
    width: '10px',
    height: '10px',
    borderRadius: '50%',
    background: '#2563EB'
  },
  heroChipText: {
    fontSize: '13px',
    fontWeight: '700',
    color: '#1D4ED8'
  },
  banner: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    marginBottom: '14px',
    padding: '12px 14px',
    borderRadius: '14px',
    background: 'rgba(255, 255, 255, 0.92)',
    border: '1px solid rgba(37, 99, 235, 0.12)',
    boxShadow: '0 10px 22px rgba(37, 99, 235, 0.08)'
  },
  bannerAlert: {
    background: '#DBEAFE'
  },
  bannerText: {
    fontSize: '14px',
    fontWeight: '700',
    color: '#1D4ED8',
    lineHeight: '1.6'
  },
  stack: {
    display: 'flex',
    flexDirection: 'column',
    gap: '14px'
  },
  card: {
    background: 'rgba(255, 255, 255, 0.97)',
    border: '1px solid rgba(148, 163, 184, 0.14)',
    boxShadow: '0 16px 38px rgba(15, 23, 42, 0.06)',
    padding: '20px',
    borderRadius: '20px'
  },
  cardHead: {
    display: 'flex',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    gap: '12px',
    marginBottom: '14px'
  },
  cardTitle: {
    display: 'block',
    fontSize: '22px',
    fontWeight: '800',
    color: '#0F172A'
  },
  cardSub: {
    display: 'block',
    marginTop: '6px',
    fontSize: '14px',
    lineHeight: '1.6',
    color: '#64748B'
  },
  badge: {
    flexShrink: '0',
    padding: '8px 12px',
    borderRadius: '999px',
    background: '#EFF6FF',
    color: '#1D4ED8',
    fontSize: '12px',
    fontWeight: '700'
  },
  timerBox: {
    display: 'flex',
    justifyContent: 'center',
    padding: '18px 14px',
    marginBottom: '14px',
    borderRadius: '16px',
    background: 'linear-gradient(180deg, #F8FBFF 0%, #EFF6FF 100%)',
    border: '1px solid rgba(59, 130, 246, 0.16)'
  },
  timerText: {
    fontSize: '30px',
    fontWeight: '800',
    color: '#1D4ED8',
    letterSpacing: '0.06em'
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(2, minmax(0, 1fr))',
    gap: '12px'
  },
  tile: {
    padding: '14px',
    borderRadius: '14px',
    background: 'linear-gradient(180deg, #F8FBFF 0%, #EEF6FF 100%)',
    border: '1px solid rgba(37, 99, 235, 0.10)'
  },
  tileLabel: {
    display: 'block',
    fontSize: '12px',
    color: '#64748B',
    marginBottom: '6px',
    fontWeight: '700',
    textTransform: 'uppercase',
    letterSpacing: '0.04em'
  },
  tileValue: {
    display: 'block',
    fontSize: '16px',
    lineHeight: '1.5',
    color: '#0F172A',
    fontWeight: '700'
  },
  chips: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: '10px'
  },
  chip: {
    padding: '10px 14px',
    borderRadius: '999px',
    background: '#F1F5F9',
    border: '1px solid #E2E8F0'
  },
  chipActive: {
    background: 'linear-gradient(135deg, #DBEAFE, #BFDBFE)',
    border: '1px solid rgba(37, 99, 235, 0.18)'
  },
  chipText: {
    fontSize: '14px',
    fontWeight: '700',
    color: '#64748B'
  },
  chipTextActive: {
    color: '#1D4ED8'
  },
  detailList: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: '10px',
    marginBottom: '14px'
  },
  detailItem: {
    padding: '10px 14px',
    borderRadius: '999px',
    background: '#EFF6FF',
    border: '1px solid rgba(37, 99, 235, 0.12)',
    fontSize: '13px',
    fontWeight: '600',
    color: '#1E3A8A'
  },
  actions: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: '12px'
  },
  button: {
    minHeight: '48px',
    borderRadius: '999px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '15px',
    fontWeight: '700',
    flex: '1 1 180px',
    border: 'none'
  },
  buttonPrimary: {
    background: 'linear-gradient(135deg, #2563EB, #1D4ED8)',
    color: '#FFFFFF',
    boxShadow: '0 10px 20px rgba(37, 99, 235, 0.18)'
  },
  buttonSecondary: {
    background: '#EFF6FF',
    color: '#1D4ED8',
    border: '1px solid rgba(37, 99, 235, 0.14)'
  },
  buttonDanger: {
    background: 'linear-gradient(135deg, #1E3A8A, #1D4ED8)',
    color: '#FFFFFF'
  },
  emptyCard: {
    textAlign: 'center'
  },
  emptyMark: {
    display: 'inline-flex',
    width: '64px',
    height: '64px',
    borderRadius: '50%',
    alignItems: 'center',
    justifyContent: 'center',
    margin: '0 auto 16px',
    background: 'radial-gradient(circle, rgba(59, 130, 246, 0.16), rgba(191, 219, 254, 0.5))',
    color: '#1D4ED8',
    fontSize: '22px',
    fontWeight: '800'
  },
  centered: {
    textAlign: 'center'
  },
  overlay: {
    position: 'fixed',
    inset: '0',
    background: 'rgba(15, 23, 42, 0.48)',
    zIndex: '1200',
    display: 'flex',
    alignItems: 'flex-end'
  },
  sheet: {
    width: '100%',
    padding: '24px 18px 30px',
    borderRadius: '24px 24px 0 0',
    background: 'linear-gradient(180deg, #F8FBFF 0%, #FFFFFF 28%)',
    boxShadow: '0 -12px 30px rgba(15, 23, 42, 0.18)'
  },
  textarea: {
    width: '100%',
    minHeight: '140px',
    padding: '14px',
    boxSizing: 'border-box',
    borderRadius: '14px',
    background: '#FFFFFF',
    border: '1px solid #D7E3F9',
    fontSize: '14px',
    lineHeight: '1.6',
    color: '#0F172A',
    marginBottom: '14px'
  }
})

const isBookingPage = computed(() => currentNavPage.value === 'booking')
const isTripPage = computed(() => !isBookingPage.value)
const extensionMinutes = computed(() => extensionOptions[selectedExtensionIndex.value] || extensionOptions[0])
const heroTitle = computed(() => {
  if (isBookingPage.value) {
    return activeRide.value ? 'Everything reserved for this ride in one place' : 'Book & Details is prepared for your next scooter order'
  }
  return activeRide.value ? 'Stay in control while your scooter is on the move' : 'Your ride controls will appear here the moment a trip starts'
})
const heroSubtitle = computed(() => {
  if (isBookingPage.value) {
    return activeRide.value
      ? 'Review the reserved scooter, planned finish, payment snapshot, and next actions without switching context.'
      : 'This page becomes your booking summary as soon as you confirm a scooter, keeping the plan, payment, and key ride details together.'
  }
  return activeRide.value
    ? 'Track the remaining rental time, add more minutes, end the ride safely, or report a fault without leaving the dashboard.'
    : 'Use this page as your ride hub for countdowns, live controls, safety actions, and status updates once a scooter is in use.'
})
const heroChip = computed(() => {
  if (isBookingPage.value) return activeRide.value ? 'Booking is active' : 'Waiting for booking'
  return activeRide.value ? 'Ride in progress' : 'Ready when you are'
})
const durationLabel = computed(() => `${Number(activeRide.value?.durationMinutes || 0)} min reserved`)

const formatDateTime = (value) => {
  if (!value) return 'Not available'
  const date = value instanceof Date ? value : new Date(String(value).replace(' ', 'T'))
  if (Number.isNaN(date.getTime())) return 'Not available'
  return date.toLocaleString('en-GB', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const formatMoney = (value) => `GBP ${Number(value || 0).toFixed(2)}`

const countdownParts = computed(() => {
  const endTime = getRideEndTime(activeRide.value)
  if (!endTime) return { totalSeconds: 0, hour: 0, minute: 0, second: 0, day: 0 }
  const totalSeconds = Math.max(0, Math.floor((endTime.getTime() - nowTick.value) / 1000))
  return {
    totalSeconds,
    day: Math.floor(totalSeconds / 86400),
    hour: Math.floor((totalSeconds % 86400) / 3600),
    minute: Math.floor((totalSeconds % 3600) / 60),
    second: totalSeconds % 60
  }
})

const countdownExpired = computed(() => countdownReached.value || countdownParts.value.totalSeconds <= 0)
const countdownText = computed(() => {
  const part = countdownParts.value
  const pad = (value) => String(value).padStart(2, '0')
  return part.day > 0
    ? `${pad(part.day)}d ${pad(part.hour)}:${pad(part.minute)}:${pad(part.second)}`
    : `${pad(part.hour)}:${pad(part.minute)}:${pad(part.second)}`
})
const plannedEndTime = computed(() => {
  const endTime = getRideEndTime(activeRide.value)
  return endTime ? formatDateTime(endTime) : 'Not available'
})
const projectedEndTime = computed(() => {
  if (!activeRide.value) return 'Not available'
  const nextRide = normalizeActiveRide({
    ...activeRide.value,
    durationMinutes: Number(activeRide.value.durationMinutes || 0) + extensionMinutes.value
  }, activeRide.value)
  const endTime = getRideEndTime(nextRide)
  return endTime ? formatDateTime(endTime) : 'Not available'
})
const projectedTotalCost = computed(() => {
  if (!activeRide.value) return formatMoney(0)
  const extraCost = Number(activeRide.value.pricePerMinute || 0) * extensionMinutes.value
  return formatMoney(Number(activeRide.value.totalCost || 0) + extraCost)
})
const countdownHint = computed(() => {
  if (!activeRide.value) return ''
  return `Scooter #${activeRide.value.scooterId} is active and scheduled to finish at ${plannedEndTime.value}.`
})

const updateActiveRideState = (ride) => {
  if (!ride) {
    countdownReached.value = false
    activeRide.value = null
    clearStoredActiveRide()
    return
  }
  countdownReached.value = false
  activeRide.value = setStoredActiveRide(normalizeActiveRide(ride, activeRide.value || getStoredActiveRide() || {}))
}

const enrichRideDetails = async (ride) => {
  if (!ride?.scooterId) return ride
  const hasPricing = Number(ride.basePrice || 0) > 0 && Number(ride.pricePerMinute || 0) > 0
  const hasModel = Boolean(ride.scooterModel && ride.scooterModel !== 'Scooter')
  if (hasPricing && hasModel) return ride
  try {
    const scooter = await getScooterById(ride.scooterId)
    return normalizeActiveRide({
      ...ride,
      scooterModel: hasModel ? ride.scooterModel : scooter?.model,
      basePrice: hasPricing ? ride.basePrice : scooter?.basePrice,
      pricePerMinute: hasPricing ? ride.pricePerMinute : scooter?.pricePerMin
    }, ride)
  } catch (error) {
    console.error('Failed to enrich ride details:', error)
    return ride
  }
}

const syncActiveRide = async () => {
  const cached = getStoredActiveRide()
  if (cached) activeRide.value = cached
  const userId = getStoredUserId()
  if (!userId) return
  syncing.value = true
  try {
    const bookings = await getUserBookings(userId)
    const liveRide = findActiveRide(bookings, cached || activeRide.value || {})
    if (!liveRide) {
      updateActiveRideState(null)
      return
    }
    updateActiveRideState(await enrichRideDetails(liveRide))
  } catch (error) {
    console.error('Failed to sync active ride:', error)
  } finally {
    syncing.value = false
  }
}

const startTimer = () => {
  if (timer) return
  nowTick.value = Date.now()
  timer = setInterval(() => {
    nowTick.value = Date.now()
  }, 1000)
}

const stopTimer = () => {
  if (!timer) return
  clearInterval(timer)
  timer = null
}

const handleExtendRide = async () => {
  if (!activeRide.value?.bookingId || busyAction.value) return
  busyAction.value = 'extend'
  try {
    const response = await extendRide(activeRide.value.bookingId, extensionMinutes.value)
    updateActiveRideState({ ...activeRide.value, ...response, status: response?.bookingStatus || activeRide.value.status })
    uni.showToast({ title: `Added ${extensionMinutes.value} min`, icon: 'success' })
  } catch (error) {
    console.error('Failed to extend ride:', error)
  } finally {
    busyAction.value = ''
  }
}

const handleEndRide = () => {
  if (!activeRide.value?.bookingId || busyAction.value) return
  uni.showModal({
    title: 'End this ride?',
    content: 'This will finish the current booking and calculate your final total.',
    success: async ({ confirm }) => {
      if (!confirm) return
      busyAction.value = 'end'
      try {
        const result = await endRide(activeRide.value.bookingId)
        updateActiveRideState(null)
        await syncActiveRide()
        const followUpMessage = activeRide.value?.bookingId
          ? ` Another active booking (#${activeRide.value.bookingId}) is still open, so please end it before starting a new scooter.`
          : ''
        uni.showModal({
          title: 'Ride ended',
          content: `Booking #${result?.bookingId || ''} finished. Total charged: ${formatMoney(result?.totalCost)}.${followUpMessage}`,
          showCancel: false
        })
      } catch (error) {
        console.error('Failed to end ride:', error)
      } finally {
        busyAction.value = ''
      }
    }
  })
}

const openIssuePopup = () => {
  if (!activeRide.value || busyAction.value) return
  issuePanelOpen.value = true
}

const closeIssuePopup = () => {
  if (busyAction.value === 'issue') return
  issuePanelOpen.value = false
}

const submitIssue = async () => {
  if (!activeRide.value?.scooterId || busyAction.value) return
  const description = String(issueForm.value.description || '').trim()
  if (description.length < 8) {
    uni.showToast({ title: 'Please add more detail', icon: 'none' })
    return
  }
  busyAction.value = 'issue'
  try {
    await reportIssue({
      userId: getStoredUserId(),
      scooterId: activeRide.value.scooterId,
      description,
      category: issueForm.value.category
    })
    issuePanelOpen.value = false
    issueForm.value = { category: 'OTHER', description: '' }
    uni.showToast({ title: 'Issue reported', icon: 'success' })
  } catch (error) {
    console.error('Failed to report issue:', error)
  } finally {
    busyAction.value = ''
  }
}

const goToFindScooter = () => {
  uni.navigateTo({ url: '/pages/find-scooter' })
}

const goToRideDashboard = () => {
  if (currentNavPage.value === 'trip') return
  uni.redirectTo({ url: '/pages/active-ride?source=trip' })
}

onLoad((options) => {
  currentNavPage.value = options?.source === 'booking' ? 'booking' : 'trip'
})

onShow(() => {
  startTimer()
  syncActiveRide()
})

onHide(() => {
  stopTimer()
  closeIssuePopup()
})

onUnload(() => {
  stopTimer()
  closeIssuePopup()
})
</script>

<style scoped>

/* ═══════════════════════════════════════════════
   SHARED — Issue sheet (used by both pages)
═══════════════════════════════════════════════ */
.overlay { position: fixed; inset: 0; background: rgba(15,23,42,0.52); z-index: 1200; display: flex; align-items: flex-end; }
.sheet { width: 100%; padding: 32rpx 28rpx calc(env(safe-area-inset-bottom) + 32rpx); border-radius: 40rpx 40rpx 0 0; background: #fff; box-shadow: 0 -16rpx 48rpx rgba(15,23,42,0.18); }
.card-head { display: flex; align-items: flex-start; justify-content: space-between; gap: 16rpx; margin-bottom: 22rpx; }
.card-title { display: block; font-size: 34rpx; font-weight: 800; color: #0F172A; }
.card-sub { display: block; margin-top: 8rpx; font-size: 24rpx; line-height: 1.6; color: #64748B; }
.section-label { display: block; font-size: 22rpx; color: #94A3B8; margin-bottom: 16rpx; font-weight: 700; text-transform: uppercase; letter-spacing: 0.06em; }
.chips { display: flex; flex-wrap: wrap; gap: 12rpx; margin-bottom: 24rpx; }
.chip { padding: 16rpx 26rpx; border-radius: 999rpx; background: #F1F5F9; border: 2rpx solid #E2E8F0; }
.chip-active { background: #EFF6FF; border-color: #2563EB; }
.chip-text { font-size: 26rpx; font-weight: 600; color: #64748B; }
.chip-text-active { color: #2563EB; }
.textarea { width: 100%; min-height: 200rpx; padding: 24rpx; box-sizing: border-box; border-radius: 20rpx; background: #F8FBFF; border: 2rpx solid #E2E8F0; font-size: 26rpx; line-height: 1.6; color: #0F172A; margin-bottom: 20rpx; }
.actions { display: flex; flex-wrap: wrap; gap: 16rpx; }
.btn { min-height: 90rpx; border-radius: 999rpx; display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: 700; border: none; flex: 1 1 220rpx; }
.btn::after { border: none; }
.btn-primary { background: linear-gradient(135deg, #2563EB, #1D4ED8); color: #fff; box-shadow: 0 12rpx 24rpx rgba(37,99,235,0.22); }
.btn-secondary { background: #F1F5F9; color: #475569; border: 2rpx solid #E2E8F0; }
.btn[disabled] { opacity: 0.6; }

/* ═══════════════════════════════════════════════
   MY RIDE PAGE — Dark immersive dashboard style
═══════════════════════════════════════════════ */
.trip-page {
  min-height: calc(100vh - 160rpx);
  background: #0F172A;
  padding-bottom: 48rpx;
}

/* Hero: full-bleed dark section with rings */
.trip-hero {
  position: relative;
  background: linear-gradient(160deg, #0F172A 0%, #1E3A8A 60%, #1D4ED8 100%);
  padding: 48rpx 32rpx 56rpx;
  overflow: hidden;
}
.trip-hero-bg { position: absolute; inset: 0; pointer-events: none; }
.trip-hero-ring {
  position: absolute;
  border-radius: 50%;
  border: 1rpx solid rgba(147, 197, 253, 0.12);
}
.ring-1 { width: 500rpx; height: 500rpx; top: -180rpx; right: -120rpx; }
.ring-2 { width: 340rpx; height: 340rpx; top: -80rpx; right: -30rpx; border-color: rgba(147,197,253,0.08); }
.ring-3 { width: 700rpx; height: 700rpx; bottom: -400rpx; left: -200rpx; border-color: rgba(96,165,250,0.06); }

.trip-hero-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 40rpx;
  position: relative;
  z-index: 1;
}
.trip-status-pill {
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  padding: 10rpx 22rpx;
  border-radius: 999rpx;
  border: 1rpx solid rgba(255,255,255,0.18);
  background: rgba(255,255,255,0.1);
  backdrop-filter: blur(8rpx);
}
.trip-status-active { border-color: rgba(74,222,128,0.4); background: rgba(74,222,128,0.12); }
.trip-status-dot {
  width: 14rpx; height: 14rpx; border-radius: 50%;
  background: #94A3B8;
  animation: trip-pulse 2s infinite;
}
.trip-status-active .trip-status-dot { background: #4ADE80; }
.trip-status-idle .trip-status-dot { background: #94A3B8; animation: none; }
@keyframes trip-pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(0.85); }
}
.trip-status-text { font-size: 22rpx; font-weight: 700; color: #fff; letter-spacing: 0.02em; }
.trip-sync-badge { padding: 8rpx 18rpx; border-radius: 999rpx; background: rgba(255,255,255,0.1); }
.trip-sync-text { font-size: 20rpx; color: rgba(255,255,255,0.6); font-weight: 600; }

/* Timer display */
.trip-timer-section { text-align: center; position: relative; z-index: 1; padding: 20rpx 0 28rpx; }
.trip-timer-label { display: block; font-size: 22rpx; font-weight: 700; color: rgba(255,255,255,0.5); text-transform: uppercase; letter-spacing: 0.1em; margin-bottom: 18rpx; }
.trip-timer-display {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 28rpx 52rpx;
  border-radius: 32rpx;
  background: rgba(255,255,255,0.07);
  border: 1rpx solid rgba(255,255,255,0.14);
  backdrop-filter: blur(12rpx);
  margin-bottom: 18rpx;
}
.timer-expired { border-color: rgba(251,113,133,0.4); background: rgba(251,113,133,0.1); }
.trip-timer-value {
  font-size: 72rpx;
  font-weight: 900;
  color: #fff;
  letter-spacing: 0.06em;
  font-variant-numeric: tabular-nums;
}
.timer-expired .trip-timer-value { color: #FB7185; }
.trip-timer-hint { display: block; font-size: 24rpx; color: rgba(255,255,255,0.5); font-weight: 500; }

/* Alert banner inside hero */
.trip-alert-banner {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-top: 24rpx;
  padding: 18rpx 24rpx;
  border-radius: 20rpx;
  background: rgba(251,113,133,0.15);
  border: 1rpx solid rgba(251,113,133,0.3);
  position: relative;
  z-index: 1;
}
.trip-alert-icon { font-size: 28rpx; }
.trip-alert-text { font-size: 24rpx; font-weight: 700; color: #FCA5A5; flex: 1; }

/* Scooter info strip */
.trip-scooter-strip {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx 32rpx;
  background: rgba(255,255,255,0.05);
  border-bottom: 1rpx solid rgba(255,255,255,0.06);
}
.trip-scooter-icon {
  width: 72rpx; height: 72rpx;
  border-radius: 20rpx;
  background: rgba(37,99,235,0.2);
  border: 1rpx solid rgba(96,165,250,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.trip-scooter-info { flex: 1; min-width: 0; }
.trip-scooter-id { display: block; font-size: 26rpx; font-weight: 700; color: #fff; }
.trip-scooter-since { display: block; font-size: 22rpx; color: rgba(255,255,255,0.45); margin-top: 4rpx; }
.trip-scooter-cost { text-align: right; flex-shrink: 0; }
.trip-scooter-cost-value { display: block; font-size: 30rpx; font-weight: 800; color: #93C5FD; }
.trip-scooter-cost-label { display: block; font-size: 20rpx; color: rgba(255,255,255,0.4); margin-top: 2rpx; }

/* Content area */
.trip-content { padding: 28rpx 28rpx 48rpx; display: flex; flex-direction: column; gap: 20rpx; }

/* Card on dark background */
.trip-card {
  background: rgba(255,255,255,0.06);
  border: 1rpx solid rgba(255,255,255,0.1);
  border-radius: 28rpx;
  padding: 28rpx;
  backdrop-filter: blur(8rpx);
}
.trip-card-header { display: flex; align-items: center; gap: 16rpx; margin-bottom: 22rpx; }
.trip-card-icon-wrap {
  width: 56rpx; height: 56rpx;
  border-radius: 16rpx;
  background: rgba(37,99,235,0.2);
  border: 1rpx solid rgba(96,165,250,0.25);
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.trip-card-title { font-size: 30rpx; font-weight: 800; color: #fff; }
.trip-section-label { display: block; font-size: 22rpx; color: rgba(255,255,255,0.45); font-weight: 700; text-transform: uppercase; letter-spacing: 0.06em; margin-bottom: 16rpx; }

/* Extend time chips */
.trip-extend-grid { display: flex; gap: 14rpx; margin-bottom: 22rpx; }
.trip-extend-chip {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24rpx 12rpx;
  border-radius: 20rpx;
  background: rgba(255,255,255,0.05);
  border: 2rpx solid rgba(255,255,255,0.1);
  cursor: pointer;
  transition: all 0.18s ease;
}
.trip-extend-chip-active {
  background: rgba(37,99,235,0.3);
  border-color: #2563EB;
  box-shadow: 0 0 0 4rpx rgba(37,99,235,0.2);
}
.trip-extend-chip-time { display: block; font-size: 38rpx; font-weight: 900; color: #fff; line-height: 1; }
.trip-extend-chip-unit { display: block; font-size: 20rpx; color: rgba(255,255,255,0.45); font-weight: 600; margin-top: 4rpx; text-transform: uppercase; }
.trip-extend-chip-active .trip-extend-chip-time { color: #93C5FD; }
.trip-extend-chip-active .trip-extend-chip-unit { color: rgba(147,197,253,0.7); }

/* Extend preview */
.trip-extend-preview {
  background: rgba(255,255,255,0.04);
  border: 1rpx solid rgba(255,255,255,0.08);
  border-radius: 20rpx;
  padding: 20rpx 24rpx;
  margin-bottom: 22rpx;
}
.trip-preview-row { display: flex; justify-content: space-between; align-items: center; }
.trip-preview-divider { height: 1rpx; background: rgba(255,255,255,0.08); margin: 14rpx 0; }
.trip-preview-label { font-size: 22rpx; color: rgba(255,255,255,0.45); font-weight: 600; }
.trip-preview-value { font-size: 26rpx; color: #fff; font-weight: 700; }
.trip-preview-cost { color: #4ADE80; }

/* Buttons */
.trip-btn-extend {
  width: 100%;
  height: 96rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #2563EB, #1D4ED8);
  color: #fff;
  font-size: 30rpx;
  font-weight: 800;
  border: none;
  box-shadow: 0 16rpx 32rpx rgba(37,99,235,0.35);
  display: flex; align-items: center; justify-content: center;
}
.trip-btn-extend::after { border: none; }
.trip-btn-extend[disabled] { opacity: 0.55; }

/* Action row (report + end) */
.trip-action-row { display: flex; gap: 16rpx; }
.trip-action-btn {
  flex: 1;
  height: 90rpx;
  border-radius: 999rpx;
  border: none;
  display: flex; align-items: center; justify-content: center; gap: 10rpx;
  font-size: 26rpx; font-weight: 700;
}
.trip-action-btn::after { border: none; }
.trip-action-report { background: rgba(255,255,255,0.1); color: #fff; border: 1rpx solid rgba(255,255,255,0.15); }
.trip-action-end { background: rgba(239,68,68,0.15); color: #FCA5A5; border: 1rpx solid rgba(239,68,68,0.3); }
.trip-action-btn[disabled] { opacity: 0.5; }

/* Empty state */
.trip-empty-state { text-align: center; padding: 20rpx 0; }
.trip-empty-illustration {
  width: 160rpx; height: 160rpx;
  border-radius: 50%;
  background: rgba(37,99,235,0.12);
  border: 2rpx solid rgba(96,165,250,0.2);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28rpx;
}
.trip-empty-title { display: block; font-size: 36rpx; font-weight: 800; color: #fff; margin-bottom: 12rpx; }
.trip-empty-desc { display: block; font-size: 26rpx; line-height: 1.65; color: rgba(255,255,255,0.45); margin-bottom: 36rpx; padding: 0 16rpx; }
.trip-btn-find {
  height: 96rpx;
  border-radius: 999rpx;
  background: #fff;
  color: #1D4ED8;
  font-size: 30rpx;
  font-weight: 800;
  border: none;
  padding: 0 64rpx;
  box-shadow: 0 12rpx 28rpx rgba(0,0,0,0.25);
  display: inline-flex; align-items: center; justify-content: center;
  margin-bottom: 40rpx;
}
.trip-btn-find::after { border: none; }
.trip-preview-tiles { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16rpx; }
.trip-preview-tile {
  padding: 22rpx;
  border-radius: 20rpx;
  background: rgba(255,255,255,0.05);
  border: 1rpx solid rgba(255,255,255,0.08);
  text-align: left;
}
.trip-ptile-icon { display: block; font-size: 32rpx; margin-bottom: 10rpx; }
.trip-ptile-label { display: block; font-size: 24rpx; color: rgba(255,255,255,0.6); font-weight: 600; }

/* ═══════════════════════════════════════════════
   BOOK & RIDING PAGE — Light card order style
═══════════════════════════════════════════════ */
.booking-page {
  min-height: calc(100vh - 160rpx);
  background: #F0F4F8;
  padding-bottom: 60rpx;
}

/* Header bar */
.booking-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 36rpx 32rpx 24rpx;
  background: #fff;
  border-bottom: 1rpx solid #E8EDF3;
}
.booking-header-title { display: block; font-size: 38rpx; font-weight: 900; color: #0F172A; }
.booking-header-sub { display: block; font-size: 24rpx; color: #94A3B8; margin-top: 4rpx; }
.booking-status-badge {
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  padding: 12rpx 22rpx;
  border-radius: 999rpx;
}
.booking-badge-active { background: #DCFCE7; }
.booking-badge-idle { background: #F1F5F9; }
.booking-badge-dot { width: 12rpx; height: 12rpx; border-radius: 50%; background: #94A3B8; }
.booking-badge-active .booking-badge-dot { background: #22C55E; }
.booking-badge-text { font-size: 20rpx; font-weight: 800; color: #64748B; letter-spacing: 0.06em; }
.booking-badge-active .booking-badge-text { color: #15803D; }

.booking-sync-bar { padding: 16rpx 32rpx; background: #EFF6FF; border-bottom: 1rpx solid #DBEAFE; }
.booking-sync-text { font-size: 24rpx; color: #2563EB; font-weight: 600; }

/* Content */
.booking-content { padding: 24rpx 24rpx 0; display: flex; flex-direction: column; gap: 18rpx; }

/* Order card — Meituan style white card with accent */
.booking-order-card {
  background: #fff;
  border-radius: 28rpx;
  box-shadow: 0 4rpx 20rpx rgba(15,23,42,0.06);
  overflow: hidden;
}
.booking-order-top {
  display: flex;
  align-items: center;
  gap: 18rpx;
  padding: 28rpx 28rpx 20rpx;
  border-bottom: 1rpx solid #F1F5F9;
}
.booking-order-icon {
  width: 80rpx; height: 80rpx;
  border-radius: 22rpx;
  background: linear-gradient(135deg, #2563EB, #1D4ED8);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 8rpx 20rpx rgba(37,99,235,0.28);
}
.booking-order-title-wrap { flex: 1; min-width: 0; }
.booking-order-model { display: block; font-size: 30rpx; font-weight: 800; color: #0F172A; }
.booking-order-id { display: block; font-size: 22rpx; color: #94A3B8; margin-top: 4rpx; }
.booking-order-status-chip {
  padding: 10rpx 20rpx;
  border-radius: 999rpx;
  background: #DCFCE7;
  flex-shrink: 0;
}
.booking-order-status-text { font-size: 20rpx; font-weight: 800; color: #15803D; letter-spacing: 0.04em; }

/* Timeline */
.booking-timeline { padding: 24rpx 28rpx; }
.booking-timeline-row { display: flex; align-items: stretch; gap: 18rpx; }
.booking-tl-dot {
  width: 18rpx; height: 18rpx;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 6rpx;
}
.booking-tl-dot-start { background: #2563EB; box-shadow: 0 0 0 4rpx rgba(37,99,235,0.18); }
.booking-tl-dot-end { background: #94A3B8; }
.booking-tl-line { width: 2rpx; background: linear-gradient(#2563EB, #E2E8F0); flex-shrink: 0; margin: 20rpx 8rpx; }
.booking-tl-labels { flex: 1; display: flex; flex-direction: column; justify-content: space-between; gap: 28rpx; }
.booking-tl-label-item {}
.booking-tl-time { display: block; font-size: 28rpx; font-weight: 700; color: #0F172A; }
.booking-tl-desc { display: block; font-size: 22rpx; color: #94A3B8; margin-top: 4rpx; }

/* Info section */
.booking-info-section { background: #fff; border-radius: 28rpx; padding: 24rpx 24rpx 28rpx; box-shadow: 0 4rpx 20rpx rgba(15,23,42,0.06); }
.booking-section-title { display: block; font-size: 26rpx; font-weight: 800; color: #0F172A; margin-bottom: 18rpx; }
.booking-info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 14rpx; }
.booking-info-cell {
  padding: 20rpx;
  border-radius: 20rpx;
  background: #F8FBFF;
  border: 1rpx solid #E8EDF3;
}
.booking-info-icon { display: block; font-size: 30rpx; margin-bottom: 8rpx; }
.booking-info-label { display: block; font-size: 20rpx; color: #94A3B8; font-weight: 700; text-transform: uppercase; letter-spacing: 0.04em; margin-bottom: 6rpx; }
.booking-info-value { display: block; font-size: 26rpx; color: #0F172A; font-weight: 700; }

/* Extend section */
.booking-extend-section { background: #fff; border-radius: 28rpx; padding: 24rpx; box-shadow: 0 4rpx 20rpx rgba(15,23,42,0.06); }
.booking-extend-chips { display: flex; gap: 12rpx; margin-bottom: 16rpx; }
.booking-extend-chip {
  flex: 1;
  height: 76rpx;
  border-radius: 18rpx;
  background: #F8FBFF;
  border: 2rpx solid #E2E8F0;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
}
.booking-chip-active { background: #EFF6FF; border-color: #2563EB; }
.booking-chip-text { font-size: 26rpx; font-weight: 700; color: #64748B; }
.booking-chip-active .booking-chip-text { color: #2563EB; }
.booking-extend-preview {
  padding: 16rpx 20rpx;
  border-radius: 16rpx;
  background: #F8FBFF;
  border: 1rpx solid #E8EDF3;
}
.booking-ext-preview-text { font-size: 24rpx; color: #64748B; font-weight: 500; line-height: 1.6; }
.booking-ext-highlight { font-weight: 700; color: #2563EB; }

/* Action buttons */
.booking-actions { padding: 0 0 8rpx; display: flex; flex-direction: column; gap: 14rpx; }
.booking-btn-extend {
  width: 100%; height: 100rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #2563EB, #1D4ED8);
  color: #fff;
  font-size: 30rpx; font-weight: 800;
  border: none;
  box-shadow: 0 14rpx 28rpx rgba(37,99,235,0.28);
  display: flex; align-items: center; justify-content: center;
}
.booking-btn-extend::after { border: none; }
.booking-btn-extend[disabled] { opacity: 0.55; }
.booking-btn-row { display: flex; gap: 14rpx; }
.booking-btn-secondary {
  flex: 1; height: 90rpx;
  border-radius: 999rpx;
  background: #F1F5F9;
  color: #475569;
  font-size: 27rpx; font-weight: 700;
  border: 2rpx solid #E2E8F0;
  display: flex; align-items: center; justify-content: center;
}
.booking-btn-secondary::after { border: none; }
.booking-btn-danger {
  flex: 1; height: 90rpx;
  border-radius: 999rpx;
  background: #FFF1F2;
  color: #E11D48;
  font-size: 27rpx; font-weight: 700;
  border: 2rpx solid #FECDD3;
  display: flex; align-items: center; justify-content: center;
}
.booking-btn-danger::after { border: none; }
.booking-btn-danger[disabled] { opacity: 0.55; }

/* Empty booking state */
.booking-empty { padding: 24rpx; display: flex; flex-direction: column; gap: 18rpx; }
.booking-empty-card {
  background: #fff;
  border-radius: 28rpx;
  padding: 40rpx 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(15,23,42,0.06);
  text-align: center;
}
.booking-empty-art {
  width: 140rpx; height: 140rpx;
  border-radius: 50%;
  background: #EFF6FF;
  border: 2rpx solid #DBEAFE;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28rpx;
}
.booking-empty-title { display: block; font-size: 36rpx; font-weight: 800; color: #0F172A; margin-bottom: 12rpx; }
.booking-empty-desc { display: block; font-size: 26rpx; line-height: 1.65; color: #64748B; margin-bottom: 32rpx; }

/* Checklist */
.booking-empty-checklist { text-align: left; margin-bottom: 36rpx; display: flex; flex-direction: column; gap: 18rpx; }
.booking-checklist-item { display: flex; align-items: center; gap: 18rpx; padding: 16rpx 20rpx; background: #F8FBFF; border-radius: 16rpx; border: 1rpx solid #E8EDF3; }
.booking-check-icon { font-size: 24rpx; color: #CBD5E1; flex-shrink: 0; }
.booking-check-text { font-size: 26rpx; color: #475569; font-weight: 600; }

.booking-empty-actions { display: flex; flex-direction: column; gap: 14rpx; }
.booking-btn-primary-cta {
  width: 100%; height: 100rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #2563EB, #1D4ED8);
  color: #fff;
  font-size: 30rpx; font-weight: 800;
  border: none;
  box-shadow: 0 12rpx 24rpx rgba(37,99,235,0.25);
  display: flex; align-items: center; justify-content: center;
}
.booking-btn-primary-cta::after { border: none; }
.booking-btn-ghost {
  width: 100%; height: 88rpx;
  border-radius: 999rpx;
  background: transparent;
  color: #2563EB;
  font-size: 28rpx; font-weight: 700;
  border: 2rpx solid #DBEAFE;
  display: flex; align-items: center; justify-content: center;
}
.booking-btn-ghost::after { border: none; }

/* Info preview grid */
.booking-empty-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14rpx;
}
.booking-info-preview-cell {
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(15,23,42,0.04);
}
.booking-info-preview-icon { display: block; font-size: 32rpx; margin-bottom: 10rpx; }
.booking-info-preview-label { display: block; font-size: 20rpx; color: #94A3B8; font-weight: 700; text-transform: uppercase; letter-spacing: 0.04em; margin-bottom: 8rpx; }
.booking-info-preview-value { display: block; font-size: 22rpx; color: #475569; font-weight: 600; line-height: 1.4; }

/* Responsive */
@media (max-width: 750px) {
  .trip-timer-value { font-size: 58rpx; }
  .trip-extend-grid { gap: 10rpx; }
  .booking-info-grid { grid-template-columns: 1fr; }
  .booking-empty-info-grid { grid-template-columns: 1fr; }
  .booking-btn-row { flex-direction: column; }
  .trip-preview-tiles { grid-template-columns: repeat(2, 1fr); }
}
</style>
