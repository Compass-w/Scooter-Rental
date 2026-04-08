<template>
  <BaseLayout nav-type="default" :show-menu="true" :show-footer="false" :current-page="currentNavPage">
    <view class="ride-shell">
      <view class="ride-container">
        <view class="ride-page-hero" :class="isBookingPage ? 'ride-page-hero-booking' : 'ride-page-hero-trip'">
          <view class="ride-page-hero-main">
            <view class="ride-page-kicker-row">
              <text class="ride-page-kicker">{{ isBookingPage ? 'Book & Details' : 'My Ride' }}</text>
              <view class="ride-page-status" :class="activeRide ? 'ride-page-status-live' : 'ride-page-status-idle'">
                <view class="ride-page-status-dot"></view>
                <text class="ride-page-status-text">{{ heroChip }}</text>
              </view>
            </view>
            <text class="ride-page-title">{{ heroTitle }}</text>
            <text class="ride-page-subtitle">{{ heroSubtitle }}</text>
          </view>

          <view class="ride-page-hero-side">
            <view class="ride-page-summary-card">
              <text class="ride-page-summary-label">
                {{ activeRide ? (isBookingPage ? 'Planned finish' : 'Time remaining') : (isBookingPage ? 'Next step' : 'Ride status') }}
              </text>
              <text class="ride-page-summary-value">
                {{ activeRide ? (isBookingPage ? plannedEndTime : countdownText) : (isBookingPage ? 'Pick a scooter from the map' : 'Start a booking to unlock controls') }}
              </text>
            </view>

            <view v-if="activeRide" class="ride-page-mini-grid">
              <view class="ride-page-mini-item">
                <text class="ride-page-mini-label">Scooter</text>
                <text class="ride-page-mini-value">#{{ activeRide.scooterId }}</text>
              </view>
              <view class="ride-page-mini-item">
                <text class="ride-page-mini-label">{{ isBookingPage ? 'Reserved' : 'Current total' }}</text>
                <text class="ride-page-mini-value">{{ formatMoney(activeRide.totalCost) }}</text>
              </view>
            </view>
          </view>
        </view>

        <view v-if="syncing" class="ride-sync-banner">
          <view class="ride-sync-dot"></view>
          <text class="ride-sync-text">{{ isBookingPage ? 'Syncing booking details...' : 'Refreshing ride status...' }}</text>
        </view>

        <view v-if="isTripPage" class="ride-view">
          <template v-if="activeRide">
            <view class="ride-card trip-focus-card">
              <view class="ride-card-head">
                <view class="ride-card-head-main">
                  <view class="ride-card-icon ride-card-icon-primary">
                    <svg viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="5.5" cy="17.5" r="2.5" />
                      <circle cx="18.5" cy="17.5" r="2.5" />
                      <path d="M8 17.5h7M15 17.5l-2-5h-2l-1-3H8" />
                      <path d="M15 12.5l3-5h-2" />
                    </svg>
                  </view>
                  <view class="ride-card-head-copy">
                    <text class="ride-card-title">{{ activeRide.scooterModel || 'Electric Scooter' }}</text>
                    <text class="ride-card-subtitle">Scooter #{{ activeRide.scooterId }} started {{ formatDateTime(activeRide.startTime) }}</text>
                  </view>
                </view>
                <view class="ride-status-tag ride-status-tag-live">
                  <text>{{ rideStatusText }}</text>
                </view>
              </view>

              <view class="trip-focus-layout">
                <view class="trip-countdown-card" :class="countdownExpired ? 'trip-countdown-card-alert' : ''">
                  <text class="trip-countdown-label">{{ countdownExpired ? 'Reserved time reached' : 'Time remaining' }}</text>
                  <text class="trip-countdown-value">{{ countdownText }}</text>
                  <text class="trip-countdown-note">Planned finish {{ plannedEndTime }}</text>
                </view>

                <view class="ride-info-grid trip-summary-grid">
                  <view class="ride-info-tile">
                    <text class="ride-info-label">Reserved duration</text>
                    <text class="ride-info-value">{{ durationLabel }}</text>
                  </view>
                  <view class="ride-info-tile">
                    <text class="ride-info-label">Reserved total</text>
                    <text class="ride-info-value">{{ formatMoney(activeRide.totalCost) }}</text>
                  </view>
                  <view class="ride-info-tile">
                    <text class="ride-info-label">Started at</text>
                    <text class="ride-info-value">{{ formatDateTime(activeRide.startTime) }}</text>
                  </view>
                  <view class="ride-info-tile">
                    <text class="ride-info-label">Planned finish</text>
                    <text class="ride-info-value">{{ plannedEndTime }}</text>
                  </view>
                </view>
              </view>

              <view class="ride-inline-banner" :class="countdownExpired ? 'ride-inline-banner-warn' : ''">
                <text class="ride-inline-banner-text">
                  {{ countdownExpired ? 'This ride has reached the reserved duration. Extend the booking or end the ride now.' : countdownHint }}
                </text>
              </view>
            </view>

            <view class="trip-main-grid">
              <view class="ride-card ride-elevated-card">
                <view class="ride-card-head">
                  <view class="ride-card-head-main">
                    <view class="ride-card-icon ride-card-icon-soft">
                      <svg viewBox="0 0 24 24" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="9" />
                        <path d="M12 7v5l3 2" />
                      </svg>
                    </view>
                    <view class="ride-card-head-copy">
                      <text class="ride-card-title">Add more time</text>
                      <text class="ride-card-subtitle">Keep your current scooter and extend the reservation in one tap.</text>
                    </view>
                  </view>
                  <view class="ride-chip-badge">
                    <text>+{{ extensionMinutes }} min</text>
                  </view>
                </view>

                <view class="ride-choice-row">
                  <view
                    v-for="(minutes, index) in extensionOptions"
                    :key="minutes"
                    class="ride-choice-card"
                    :class="selectedExtensionIndex === index ? 'ride-choice-card-active' : ''"
                    @tap="selectedExtensionIndex = index"
                  >
                    <text class="ride-choice-time">{{ minutes }} min</text>
                    <text class="ride-choice-price">{{ formatMoney(Number(activeRide.pricePerMinute || 0) * minutes) }}</text>
                  </view>
                </view>

                <view class="ride-preview-panel">
                  <view class="ride-preview-row">
                    <text class="ride-preview-label">New finish time</text>
                    <text class="ride-preview-value">{{ projectedEndTime }}</text>
                  </view>
                  <view class="ride-preview-row">
                    <text class="ride-preview-label">Updated reserved total</text>
                    <text class="ride-preview-value ride-preview-value-primary">{{ projectedTotalCost }}</text>
                  </view>
                </view>

                <button class="ride-btn ride-btn-primary" :disabled="busyAction === 'extend'" @tap="handleExtendRide">
                  <text>{{ busyAction === 'extend' ? 'Adding time...' : 'Extend +' + extensionMinutes + ' min' }}</text>
                </button>
              </view>

              <view class="ride-card">
                <view class="ride-card-head">
                  <view class="ride-card-head-main">
                    <view class="ride-card-icon ride-card-icon-warning">
                      <svg viewBox="0 0 24 24" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0Z" />
                        <path d="M12 9v4" />
                        <path d="M12 17h.01" />
                      </svg>
                    </view>
                    <view class="ride-card-head-copy">
                      <text class="ride-card-title">Ride actions</text>
                      <text class="ride-card-subtitle">Use the controls below if you need support or want to finish the booking.</text>
                    </view>
                  </view>
                </view>

                <view class="ride-action-stack">
                  <button class="ride-btn ride-btn-secondary" @tap="openIssuePopup">
                    <text>Report Issue</text>
                  </button>
                  <button class="ride-btn ride-btn-danger" :disabled="busyAction === 'end'" @tap="handleEndRide">
                    <text>{{ busyAction === 'end' ? 'Ending...' : 'End Ride' }}</text>
                  </button>
                </view>

                <text class="ride-card-footnote">Need the order summary? Switch to Book & Details anytime without losing the live timer here.</text>
              </view>
            </view>
          </template>

          <template v-else>
            <view class="ride-card ride-empty-card">
              <view class="ride-empty-mark">R</view>
              <text class="ride-empty-title">Your live ride hub is ready</text>
              <text class="ride-empty-desc">Once a scooter is unlocked, this page will show the countdown, reservation details, extension options, and safety actions in one clean dashboard.</text>
              <button class="ride-btn ride-btn-primary ride-empty-btn" @tap="goToFindScooter">
                <text>Find a Scooter</text>
              </button>
            </view>

            <view class="ride-info-grid ride-empty-grid">
              <view class="ride-info-tile">
                <text class="ride-info-label">Live countdown</text>
                <text class="ride-info-value">See remaining time and the planned finish at a glance.</text>
              </view>
              <view class="ride-info-tile">
                <text class="ride-info-label">Scooter snapshot</text>
                <text class="ride-info-value">Track the scooter model, start time, and reserved amount.</text>
              </view>
              <view class="ride-info-tile">
                <text class="ride-info-label">Instant extension</text>
                <text class="ride-info-value">Add 15, 30, or 60 minutes without leaving the page.</text>
              </view>
              <view class="ride-info-tile">
                <text class="ride-info-label">Support actions</text>
                <text class="ride-info-value">Report a fault or end the ride with clear next-step controls.</text>
              </view>
            </view>
          </template>
        </view>

        <view v-else class="ride-view">
          <template v-if="activeRide">
            <view class="ride-card booking-order-card">
              <view class="booking-order-banner">
                <view>
                  <text class="booking-order-kicker">Current order</text>
                  <text class="booking-order-title">{{ activeRide.scooterModel || 'Electric Scooter' }}</text>
                </view>
                <view class="ride-status-tag ride-status-tag-live">
                  <text>{{ rideStatusText }}</text>
                </view>
              </view>

              <view class="booking-order-meta">
                <view class="booking-meta-pill">
                  <text class="booking-meta-label">Order</text>
                  <text class="booking-meta-value">#{{ activeRide.bookingId || 'Pending' }}</text>
                </view>
                <view class="booking-meta-pill">
                  <text class="booking-meta-label">Scooter</text>
                  <text class="booking-meta-value">#{{ activeRide.scooterId }}</text>
                </view>
                <view class="booking-meta-pill">
                  <text class="booking-meta-label">Reserved</text>
                  <text class="booking-meta-value">{{ formatMoney(activeRide.totalCost) }}</text>
                </view>
              </view>

              <view class="booking-track-card">
                <view class="booking-track-item">
                  <view class="booking-track-marker booking-track-marker-live"></view>
                  <view class="booking-track-copy">
                    <text class="booking-track-title">Ride started</text>
                    <text class="booking-track-value">{{ formatDateTime(activeRide.startTime) }}</text>
                  </view>
                </view>
                <view class="booking-track-line"></view>
                <view class="booking-track-item">
                  <view class="booking-track-marker"></view>
                  <view class="booking-track-copy">
                    <text class="booking-track-title">Planned finish</text>
                    <text class="booking-track-value">{{ plannedEndTime }}</text>
                  </view>
                </view>
              </view>

              <view v-if="countdownExpired" class="ride-inline-banner ride-inline-banner-warn">
                <text class="ride-inline-banner-text">The reserved duration has been reached. Extend the booking or end the ride when you are ready.</text>
              </view>
            </view>

            <view class="booking-main-grid">
              <view class="ride-card">
                <view class="ride-card-head">
                  <view class="ride-card-head-main">
                    <view class="ride-card-icon ride-card-icon-soft">
                      <svg viewBox="0 0 24 24" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="4" y="4" width="16" height="16" rx="3" />
                        <path d="M8 9h8" />
                        <path d="M8 13h8" />
                      </svg>
                    </view>
                    <view class="ride-card-head-copy">
                      <text class="ride-card-title">Trip details</text>
                      <text class="ride-card-subtitle">Everything you need for this order, arranged like a quick ride summary.</text>
                    </view>
                  </view>
                </view>

                <view class="ride-info-grid booking-info-grid">
                  <view class="ride-info-tile">
                    <text class="ride-info-label">Duration</text>
                    <text class="ride-info-value">{{ durationLabel }}</text>
                  </view>
                  <view class="ride-info-tile">
                    <text class="ride-info-label">Status</text>
                    <text class="ride-info-value">{{ rideStatusText }}</text>
                  </view>
                  <view class="ride-info-tile">
                    <text class="ride-info-label">Reserved total</text>
                    <text class="ride-info-value">{{ formatMoney(activeRide.totalCost) }}</text>
                  </view>
                  <view class="ride-info-tile">
                    <text class="ride-info-label">Payment</text>
                    <text class="ride-info-value">{{ activeRide.cardLast4 ? '**** ' + activeRide.cardLast4 : 'Saved card on file' }}</text>
                  </view>
                </view>
              </view>

              <view class="ride-card ride-elevated-card booking-extend-card">
                <view class="ride-card-head">
                  <view class="ride-card-head-main">
                    <view class="ride-card-icon ride-card-icon-primary">
                      <svg viewBox="0 0 24 24" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="9" />
                        <path d="M12 7v5l3 2" />
                      </svg>
                    </view>
                    <view class="ride-card-head-copy">
                      <text class="ride-card-title">Extend booking</text>
                      <text class="ride-card-subtitle">Adjust the finish time before the reservation runs out.</text>
                    </view>
                  </view>
                </view>

                <view class="ride-choice-row">
                  <view
                    v-for="(minutes, index) in extensionOptions"
                    :key="'booking-' + minutes"
                    class="ride-choice-card ride-choice-card-compact"
                    :class="selectedExtensionIndex === index ? 'ride-choice-card-active' : ''"
                    @tap="selectedExtensionIndex = index"
                  >
                    <text class="ride-choice-time">{{ minutes }} min</text>
                    <text class="ride-choice-price">{{ formatMoney(Number(activeRide.pricePerMinute || 0) * minutes) }}</text>
                  </view>
                </view>

                <view class="ride-preview-panel">
                  <view class="ride-preview-row">
                    <text class="ride-preview-label">New finish time</text>
                    <text class="ride-preview-value">{{ projectedEndTime }}</text>
                  </view>
                  <view class="ride-preview-row">
                    <text class="ride-preview-label">Updated total</text>
                    <text class="ride-preview-value ride-preview-value-primary">{{ projectedTotalCost }}</text>
                  </view>
                </view>

                <button class="ride-btn ride-btn-primary" :disabled="busyAction === 'extend'" @tap="handleExtendRide">
                  <text>{{ busyAction === 'extend' ? 'Extending...' : 'Extend +' + extensionMinutes + ' min' }}</text>
                </button>
              </view>

              <view class="ride-card booking-action-card">
                <view class="ride-card-head">
                  <view class="ride-card-head-main">
                    <view class="ride-card-icon ride-card-icon-warning">
                      <svg viewBox="0 0 24 24" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M4 12h16" />
                        <path d="M12 4v16" />
                        <circle cx="12" cy="12" r="9" />
                      </svg>
                    </view>
                    <view class="ride-card-head-copy">
                      <text class="ride-card-title">Next actions</text>
                      <text class="ride-card-subtitle">Open the live dashboard or finish the ride from here.</text>
                    </view>
                  </view>
                </view>

                <view class="ride-action-stack">
                  <button class="ride-btn ride-btn-secondary" @tap="goToRideDashboard">
                    <text>Open My Ride</text>
                  </button>
                  <button class="ride-btn ride-btn-danger" :disabled="busyAction === 'end'" @tap="handleEndRide">
                    <text>{{ busyAction === 'end' ? 'Ending...' : 'End Ride' }}</text>
                  </button>
                </view>
              </view>
            </view>
          </template>

          <template v-else>
            <view class="ride-card ride-empty-card booking-empty-card">
              <view class="ride-empty-mark ride-empty-mark-warm">B</view>
              <text class="ride-empty-title">No active booking yet</text>
              <text class="ride-empty-desc">Choose a scooter on the map to confirm your plan and payment. This page will then show the order summary, timeline, pricing, and quick actions.</text>

              <view class="booking-step-list">
                <view class="booking-step-item">
                  <view class="booking-step-index">1</view>
                  <text class="booking-step-text">Select a nearby scooter on the map.</text>
                </view>
                <view class="booking-step-item">
                  <view class="booking-step-index">2</view>
                  <text class="booking-step-text">Choose your duration and payment method.</text>
                </view>
                <view class="booking-step-item">
                  <view class="booking-step-index">3</view>
                  <text class="booking-step-text">Review the booking here and start riding.</text>
                </view>
              </view>

              <view class="booking-empty-actions">
                <button class="ride-btn ride-btn-primary" @tap="goToFindScooter">
                  <text>Book a Scooter</text>
                </button>
                <button class="ride-btn ride-btn-secondary" @tap="goToRideDashboard">
                  <text>Open My Ride</text>
                </button>
              </view>
            </view>

            <view class="ride-info-grid ride-empty-grid">
              <view class="ride-info-tile">
                <text class="ride-info-label">Scooter</text>
                <text class="ride-info-value">Assigned as soon as you pick one from the map.</text>
              </view>
              <view class="ride-info-tile">
                <text class="ride-info-label">Plan</text>
                <text class="ride-info-value">Duration, finish time, and pricing in one summary card.</text>
              </view>
              <view class="ride-info-tile">
                <text class="ride-info-label">Payment</text>
                <text class="ride-info-value">Use your saved card and keep the reserved total visible.</text>
              </view>
              <view class="ride-info-tile">
                <text class="ride-info-label">Status</text>
                <text class="ride-info-value">Track whether the order is booked, active, or completed.</text>
              </view>
            </view>
          </template>
        </view>
      </view>

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
  tileLabel: {
    display: 'block',
    fontSize: '12px',
    color: '#64748B',
    marginBottom: '6px',
    fontWeight: '700',
    textTransform: 'uppercase',
    letterSpacing: '0.04em'
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
const rideStatusText = computed(() => String(activeRide.value?.status || 'ACTIVE').replace(/_/g, ' '))
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
.ride-shell {
  position: relative;
  min-height: calc(100vh - 160rpx);
  padding: 28rpx 24rpx 72rpx;
  background: linear-gradient(180deg, #f8fbff 0%, #f3f7fd 46%, #fcfdff 100%);
  overflow: hidden;
}

.ride-shell::before,
.ride-shell::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.ride-shell::before {
  width: 440rpx;
  height: 440rpx;
  top: -140rpx;
  right: -160rpx;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.2), rgba(96, 165, 250, 0));
}

.ride-shell::after {
  width: 520rpx;
  height: 520rpx;
  bottom: -220rpx;
  left: -200rpx;
  background: radial-gradient(circle, rgba(245, 158, 11, 0.12), rgba(245, 158, 11, 0));
}

.ride-container {
  position: relative;
  z-index: 1;
  max-width: 1180px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.ride-view,
.trip-main-grid,
.booking-main-grid {
  display: grid;
  gap: 20rpx;
}

.ride-page-hero {
  position: relative;
  overflow: hidden;
  display: grid;
  gap: 18rpx;
  padding: 32rpx;
  border-radius: 32rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  box-shadow:
    0 18rpx 44rpx rgba(15, 23, 42, 0.07),
    0 2rpx 10rpx rgba(148, 163, 184, 0.08);
}

.ride-page-hero::before,
.ride-page-hero::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.ride-page-hero::before {
  width: 260rpx;
  height: 260rpx;
  top: -90rpx;
  right: -40rpx;
  background: radial-gradient(circle, rgba(255, 199, 94, 0.34), rgba(255, 199, 94, 0));
}

.ride-page-hero::after {
  width: 300rpx;
  height: 300rpx;
  left: -140rpx;
  bottom: -170rpx;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.18), rgba(59, 130, 246, 0));
}

.ride-page-hero-trip {
  background: linear-gradient(135deg, #eef6ff 0%, #ffffff 56%, #fff8ec 100%);
}

.ride-page-hero-booking {
  background: linear-gradient(135deg, #fff9ec 0%, #ffffff 50%, #eef6ff 100%);
}

.ride-page-hero-main,
.ride-page-hero-side {
  position: relative;
  z-index: 1;
}

.ride-page-kicker-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14rpx;
  flex-wrap: wrap;
}

.ride-page-kicker {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  border: 1rpx solid #e5edf6;
  background: rgba(255, 255, 255, 0.88);
  color: #0f172a;
  font-size: 20rpx;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.ride-page-status {
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  border: 1rpx solid #e2e8f0;
  background: rgba(255, 255, 255, 0.82);
}

.ride-page-status-live {
  background: #ecfdf5;
  border-color: #bbf7d0;
}

.ride-page-status-idle {
  background: #f8fafc;
  border-color: #e2e8f0;
}

.ride-page-status-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #94a3b8;
}

.ride-page-status-live .ride-page-status-dot {
  background: #10b981;
  box-shadow: 0 0 0 6rpx rgba(16, 185, 129, 0.12);
}

.ride-page-status-text {
  font-size: 22rpx;
  font-weight: 800;
  color: #334155;
}

.ride-page-title {
  display: block;
  max-width: 12em;
  margin-top: 18rpx;
  font-size: 44rpx;
  line-height: 1.2;
  font-weight: 900;
  color: #0f172a;
}

.ride-page-subtitle {
  display: block;
  max-width: 920rpx;
  margin-top: 14rpx;
  font-size: 26rpx;
  line-height: 1.7;
  color: #475569;
}

.ride-page-hero-side {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.ride-page-summary-card {
  padding: 22rpx 24rpx;
  border-radius: 24rpx;
  border: 1rpx solid #edf2f7;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10rpx);
}

.ride-page-summary-label,
.ride-page-mini-label,
.ride-info-label,
.booking-meta-label,
.booking-track-title,
.section-label {
  display: block;
  font-size: 20rpx;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #94a3b8;
}

.ride-page-summary-value,
.ride-page-mini-value {
  display: block;
  margin-top: 10rpx;
  font-size: 28rpx;
  line-height: 1.4;
  font-weight: 800;
  color: #0f172a;
}

.ride-page-mini-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12rpx;
}

.ride-page-mini-item {
  padding: 18rpx 20rpx;
  border-radius: 20rpx;
  border: 1rpx solid #edf2f7;
  background: rgba(255, 255, 255, 0.74);
}

.ride-sync-banner {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 18rpx 22rpx;
  border-radius: 20rpx;
  border: 1rpx solid #dbeafe;
  background: #eff6ff;
}

.ride-sync-dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: 50%;
  background: #3b82f6;
  animation: ride-sync-pulse 1.4s ease-in-out infinite;
}

@keyframes ride-sync-pulse {
  0%,
  100% {
    opacity: 0.55;
    transform: scale(0.9);
  }

  50% {
    opacity: 1;
    transform: scale(1);
  }
}

.ride-sync-text {
  font-size: 24rpx;
  font-weight: 700;
  color: #1d4ed8;
}

.ride-card {
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid #e7eef6;
  border-radius: 28rpx;
  padding: 28rpx;
  box-shadow:
    0 12rpx 36rpx rgba(15, 23, 42, 0.06),
    0 2rpx 8rpx rgba(148, 163, 184, 0.08);
}

.ride-elevated-card {
  background: linear-gradient(180deg, #ffffff 0%, #fafdff 100%);
}

.ride-card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14rpx;
}

.ride-card-head-main {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
}

.ride-card-head-copy {
  flex: 1;
  min-width: 0;
}

.ride-card-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ride-card-icon svg {
  width: 36rpx;
  height: 36rpx;
}

.ride-card-icon-primary {
  background: linear-gradient(135deg, #2563eb, #60a5fa);
  box-shadow: 0 12rpx 24rpx rgba(37, 99, 235, 0.22);
}

.ride-card-icon-primary svg {
  stroke: #ffffff;
}

.ride-card-icon-soft {
  border: 1rpx solid #dbeafe;
  background: #eff6ff;
}

.ride-card-icon-soft svg {
  stroke: #2563eb;
}

.ride-card-icon-warning {
  border: 1rpx solid #fed7aa;
  background: #fff7ed;
}

.ride-card-icon-warning svg {
  stroke: #ea580c;
}

.ride-card-title {
  display: block;
  font-size: 32rpx;
  font-weight: 800;
  color: #0f172a;
}

.ride-card-subtitle {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  line-height: 1.6;
  color: #64748b;
}

.ride-status-tag,
.ride-chip-badge {
  flex-shrink: 0;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  font-size: 20rpx;
  font-weight: 800;
  letter-spacing: 0.06em;
}

.ride-status-tag-live {
  background: #dcfce7;
  color: #15803d;
}

.ride-chip-badge {
  background: #eff6ff;
  color: #2563eb;
}

.trip-focus-layout {
  display: grid;
  gap: 18rpx;
  margin-top: 22rpx;
}

.trip-countdown-card {
  padding: 26rpx;
  border-radius: 24rpx;
  border: 1rpx solid #dbeafe;
  background: linear-gradient(135deg, #eff6ff, #fafdff);
}

.trip-countdown-card-alert {
  border-color: #fdba74;
  background: linear-gradient(135deg, #fff7ed, #fffdf8);
}

.trip-countdown-label {
  display: block;
  font-size: 20rpx;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #64748b;
}

.trip-countdown-value {
  display: block;
  margin-top: 14rpx;
  font-size: 64rpx;
  line-height: 1;
  font-weight: 900;
  color: #0f172a;
  font-variant-numeric: tabular-nums;
}

.trip-countdown-card-alert .trip-countdown-value,
.ride-inline-banner-warn .ride-inline-banner-text {
  color: #9a3412;
}

.trip-countdown-note {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.6;
  color: #64748b;
}

.ride-info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
}

.ride-info-tile {
  min-height: 132rpx;
  padding: 20rpx;
  border-radius: 22rpx;
  border: 1rpx solid #e7eef6;
  background: #f8fbff;
}

.ride-info-value {
  display: block;
  margin-top: 8rpx;
  font-size: 26rpx;
  line-height: 1.55;
  font-weight: 700;
  color: #0f172a;
  word-break: break-word;
}

.ride-inline-banner {
  margin-top: 18rpx;
  padding: 18rpx 20rpx;
  border-radius: 20rpx;
  border: 1rpx solid #dbeafe;
  background: #f0f9ff;
}

.ride-inline-banner-warn {
  border-color: #fed7aa;
  background: #fff7ed;
}

.ride-inline-banner-text {
  display: block;
  font-size: 24rpx;
  line-height: 1.65;
  color: #335381;
}

.ride-choice-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
  margin: 22rpx 0 18rpx;
}

.ride-choice-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
  padding: 20rpx 14rpx;
  border-radius: 22rpx;
  border: 2rpx solid #dbe6f2;
  background: #f8fbff;
}

.ride-choice-card-active {
  border-color: #3b82f6;
  background: linear-gradient(135deg, #eff6ff, #ffffff);
  box-shadow: 0 10rpx 24rpx rgba(59, 130, 246, 0.14);
}

.ride-choice-card-compact {
  padding-top: 18rpx;
  padding-bottom: 18rpx;
}

.ride-choice-time {
  display: block;
  font-size: 30rpx;
  font-weight: 800;
  color: #0f172a;
}

.ride-choice-price {
  display: block;
  font-size: 22rpx;
  color: #64748b;
}

.ride-preview-panel {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  margin-bottom: 20rpx;
  padding: 20rpx 22rpx;
  border-radius: 20rpx;
  border: 1rpx solid #e7eef6;
  background: #f8fbff;
}

.ride-preview-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24rpx;
}

.ride-preview-label {
  font-size: 22rpx;
  line-height: 1.5;
  color: #64748b;
}

.ride-preview-value {
  text-align: right;
  font-size: 24rpx;
  line-height: 1.5;
  font-weight: 700;
  color: #0f172a;
}

.ride-preview-value-primary {
  color: #2563eb;
}

.ride-btn,
.btn {
  min-height: 96rpx;
  border-radius: 999rpx;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 28rpx;
  font-size: 28rpx;
  font-weight: 800;
}

.ride-btn {
  width: 100%;
}

.ride-btn::after,
.btn::after {
  border: none;
}

.ride-btn text,
.btn text,
.ride-status-tag text,
.ride-chip-badge text {
  color: inherit;
  font-size: inherit;
  font-weight: inherit;
}

.ride-btn-primary {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #ffffff;
  box-shadow: 0 18rpx 32rpx rgba(37, 99, 235, 0.24);
}

.ride-btn-secondary {
  border: 1rpx solid #dbeafe;
  background: #eff6ff;
  color: #1d4ed8;
}

.ride-btn-danger {
  border: 1rpx solid #fecdd3;
  background: #fff1f2;
  color: #dc2626;
}

.ride-btn[disabled],
.btn[disabled] {
  opacity: 0.55;
}

.ride-action-stack {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  margin-top: 22rpx;
}

.ride-card-footnote {
  display: block;
  margin-top: 16rpx;
  font-size: 22rpx;
  line-height: 1.65;
  color: #94a3b8;
}

.ride-empty-card {
  text-align: center;
  padding-top: 36rpx;
  padding-bottom: 36rpx;
}

.ride-empty-mark {
  width: 112rpx;
  height: 112rpx;
  margin: 0 auto 20rpx;
  border-radius: 36rpx;
  background: linear-gradient(135deg, #dbeafe, #eff6ff);
  color: #1d4ed8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
  font-weight: 800;
  box-shadow: 0 10rpx 24rpx rgba(37, 99, 235, 0.12);
}

.ride-empty-mark-warm {
  background: linear-gradient(135deg, #fff4d6, #fff9ec);
  color: #c27a00;
  box-shadow: 0 10rpx 24rpx rgba(245, 158, 11, 0.14);
}

.ride-empty-title {
  display: block;
  font-size: 36rpx;
  font-weight: 800;
  color: #0f172a;
}

.ride-empty-desc {
  display: block;
  max-width: 920rpx;
  margin: 12rpx auto 0;
  font-size: 26rpx;
  line-height: 1.7;
  color: #64748b;
}

.ride-empty-btn {
  max-width: 360rpx;
  margin: 28rpx auto 0;
}

.ride-empty-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.booking-order-card {
  background: linear-gradient(180deg, #ffffff 0%, #fcfdff 100%);
}

.booking-order-banner {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #edf2f7;
}

.booking-order-kicker {
  display: block;
  font-size: 20rpx;
  font-weight: 800;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #f59e0b;
}

.booking-order-title {
  display: block;
  margin-top: 8rpx;
  font-size: 36rpx;
  font-weight: 800;
  color: #0f172a;
}

.booking-order-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 20rpx;
}

.booking-meta-pill {
  flex: 1 1 180rpx;
  min-width: 180rpx;
  padding: 16rpx 18rpx;
  border-radius: 18rpx;
  border: 1rpx solid #e7eef6;
  background: #f8fbff;
}

.booking-meta-value,
.booking-track-value {
  display: block;
  margin-top: 8rpx;
  font-size: 28rpx;
  line-height: 1.45;
  font-weight: 700;
  color: #0f172a;
}

.booking-track-card {
  margin-top: 20rpx;
  padding: 20rpx 22rpx;
  border-radius: 24rpx;
  border: 1rpx solid #f7e4b6;
  background: linear-gradient(135deg, #fff9ec, #ffffff 42%, #eff6ff 100%);
}

.booking-track-item {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
}

.booking-track-marker {
  width: 18rpx;
  height: 18rpx;
  margin-top: 8rpx;
  border-radius: 50%;
  background: #cbd5e1;
  box-shadow: 0 0 0 8rpx rgba(203, 213, 225, 0.24);
  flex-shrink: 0;
}

.booking-track-marker-live {
  background: #2563eb;
  box-shadow: 0 0 0 8rpx rgba(37, 99, 235, 0.14);
}

.booking-track-line {
  width: 2rpx;
  height: 56rpx;
  margin: 10rpx 0 10rpx 8rpx;
  background: linear-gradient(#93c5fd, #e2e8f0);
}

.booking-track-copy {
  flex: 1;
  min-width: 0;
}

.booking-step-list {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  margin-top: 28rpx;
  text-align: left;
}

.booking-step-item {
  display: flex;
  align-items: flex-start;
  gap: 14rpx;
  padding: 16rpx 18rpx;
  border-radius: 20rpx;
  border: 1rpx solid #f7e4b6;
  background: #fff9ec;
}

.booking-step-index {
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: #f59e0b;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 800;
  flex-shrink: 0;
}

.booking-step-text {
  font-size: 24rpx;
  line-height: 1.6;
  color: #475569;
}

.booking-empty-actions {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  margin-top: 28rpx;
}

.overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.52);
  z-index: 1200;
  display: flex;
  align-items: flex-end;
}

.sheet {
  width: 100%;
  padding: 32rpx 28rpx calc(env(safe-area-inset-bottom) + 32rpx);
  border-radius: 40rpx 40rpx 0 0;
  background: #ffffff;
  box-shadow: 0 -16rpx 48rpx rgba(15, 23, 42, 0.18);
}

.card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 22rpx;
}

.card-title {
  display: block;
  font-size: 34rpx;
  font-weight: 800;
  color: #0f172a;
}

.card-sub {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  line-height: 1.6;
  color: #64748b;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 24rpx;
}

.chip {
  padding: 16rpx 26rpx;
  border-radius: 999rpx;
  background: #f1f5f9;
  border: 2rpx solid #e2e8f0;
}

.chip-active {
  background: #eff6ff;
  border-color: #2563eb;
}

.chip-text {
  font-size: 26rpx;
  font-weight: 600;
  color: #64748b;
}

.chip-text-active {
  color: #2563eb;
}

.textarea {
  width: 100%;
  min-height: 200rpx;
  padding: 24rpx;
  box-sizing: border-box;
  border-radius: 20rpx;
  background: #f8fbff;
  border: 2rpx solid #e2e8f0;
  font-size: 26rpx;
  line-height: 1.6;
  color: #0f172a;
  margin-bottom: 20rpx;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.btn {
  flex: 1 1 220rpx;
}

.btn-primary {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #ffffff;
  box-shadow: 0 12rpx 24rpx rgba(37, 99, 235, 0.22);
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
  border: 2rpx solid #e2e8f0;
}

@media screen and (min-width: 960px) {
  .ride-page-hero {
    grid-template-columns: minmax(0, 1.22fr) minmax(300px, 0.78fr);
    align-items: end;
  }

  .trip-focus-layout {
    grid-template-columns: minmax(320px, 0.92fr) minmax(0, 1.08fr);
    align-items: stretch;
  }

  .trip-main-grid,
  .booking-main-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .ride-empty-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .booking-empty-actions {
    flex-direction: row;
  }
}

@media screen and (max-width: 767px) {
  .ride-shell {
    padding-left: 20rpx;
    padding-right: 20rpx;
  }

  .ride-page-title {
    font-size: 38rpx;
  }

  .trip-countdown-value {
    font-size: 56rpx;
  }

  .ride-page-mini-grid,
  .ride-info-grid,
  .ride-empty-grid,
  .ride-choice-row {
    grid-template-columns: 1fr;
  }
}
</style>
