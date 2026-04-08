<template>
  <BaseLayout nav-type="default" :show-menu="true" :show-footer="false" :current-page="currentNavPage">
    <view class="active-ride-page">
      <view class="page-hero">
        <view class="hero-copy">
          <text class="hero-eyebrow">{{ heroContent.eyebrow }}</text>
          <text class="hero-title">{{ heroContent.title }}</text>
          <text class="hero-subtitle">{{ heroContent.subtitle }}</text>
        </view>
        <view class="hero-chip">
          <uni-icons :type="heroChipIcon" size="18" color="#1D4ED8"></uni-icons>
          <text class="hero-chip-text">{{ heroContent.chip }}</text>
        </view>
      </view>

      <uni-notice-bar
        v-if="countdownExpired && activeRide"
        show-icon
        text="Your reserved time has ended. Extend the booking or end the ride to avoid confusion."
        color="#1D4ED8"
        background-color="#DBEAFE"
      />

      <uni-notice-bar
        v-else-if="activeRide"
        show-icon
        :text="countdownHint"
        color="#1E3A8A"
        background-color="#EFF6FF"
      />

      <uni-load-more v-if="syncing && !activeRide" status="loading" />

      <view v-else-if="activeRide && isTripPage" class="dashboard-stack">
        <uni-card
          title="Remaining Rental Time"
          sub-title="Live countdown for your current booking"
          extra="ACTIVE"
          is-shadow
        >
          <view class="timer-shell">
            <view class="timer-header">
              <view class="timer-badge">
                <uni-icons type="notification-filled" size="16" color="#2563EB"></uni-icons>
                <text class="timer-badge-text">Booking #{{ activeRide.bookingId }}</text>
              </view>
              <text class="timer-label">{{ countdownExpired ? 'Time reached' : 'Time left' }}</text>
            </view>

            <view class="countdown-wrap">
              <uni-countdown
                :day="countdownParts.day"
                :hour="countdownParts.hour"
                :minute="countdownParts.minute"
                :second="countdownParts.second"
                :show-day="countdownParts.day > 0"
                :show-hour="true"
                :show-minute="true"
                :show-colon="true"
                background-color="#DBEAFE"
                color="#1D4ED8"
                splitor-color="#1D4ED8"
                :font-size="22"
                @timeup="handleCountdownComplete"
              />
            </view>

            <view class="timer-summary-grid">
              <view class="summary-item">
                <text class="summary-label">Scooter</text>
                <text class="summary-value">#{{ activeRide.scooterId }} {{ activeRide.scooterModel }}</text>
              </view>
              <view class="summary-item">
                <text class="summary-label">Started</text>
                <text class="summary-value">{{ formatDateTime(activeRide.startTime) }}</text>
              </view>
              <view class="summary-item">
                <text class="summary-label">Planned finish</text>
                <text class="summary-value">{{ plannedEndTime }}</text>
              </view>
              <view class="summary-item">
                <text class="summary-label">Reserved total</text>
                <text class="summary-value">{{ formatMoney(activeRide.totalCost) }}</text>
              </view>
            </view>
          </view>
        </uni-card>

        <uni-card
          title="Ride Details"
          sub-title="Current booking snapshot"
          :extra="durationLabel"
          is-shadow
        >
          <view class="detail-grid">
            <view class="detail-pill">
              <uni-icons type="wallet" size="18" color="#2563EB"></uni-icons>
              <text class="detail-pill-text">Reserved {{ formatMoney(activeRide.totalCost) }}</text>
            </view>
            <view class="detail-pill">
              <uni-icons type="calendar-filled" size="18" color="#2563EB"></uni-icons>
              <text class="detail-pill-text">{{ durationLabel }}</text>
            </view>
            <view class="detail-pill">
              <uni-icons type="locked-filled" size="18" color="#2563EB"></uni-icons>
              <text class="detail-pill-text">Status {{ activeRide.status }}</text>
            </view>
            <view class="detail-pill" v-if="activeRide.cardLast4">
              <uni-icons type="wallet-filled" size="18" color="#2563EB"></uni-icons>
              <text class="detail-pill-text">Card ending {{ activeRide.cardLast4 }}</text>
            </view>
          </view>
        </uni-card>

        <uni-card
          title="Extend Booking"
          sub-title="Add extra time before the rental expires"
          is-shadow
        >
          <uni-section title="Select extra time" type="line"></uni-section>
          <uni-segmented-control
            :current="selectedExtensionIndex"
            :values="extensionLabels"
            style-type="button"
            active-color="#2563EB"
            in-active-color="#EFF6FF"
            @clickItem="handleExtensionSelection"
          />

          <view class="extend-preview">
            <view class="extend-preview-copy">
              <text class="extend-preview-title">New planned finish</text>
              <text class="extend-preview-value">{{ projectedEndTime }}</text>
            </view>
            <view class="extend-preview-copy">
              <text class="extend-preview-title">Updated reserved total</text>
              <text class="extend-preview-value">{{ projectedTotalCost }}</text>
            </view>
          </view>

          <button
            class="primary-action"
            :disabled="busyAction === 'extend'"
            @tap="handleExtendRide"
          >
            <text>{{ busyAction === 'extend' ? 'Extending booking...' : `Extend by ${extensionMinutes} min` }}</text>
          </button>
        </uni-card>

        <uni-card
          title="Ride Actions"
          sub-title="Finish the rental or send a maintenance report"
          is-shadow
        >
          <view class="action-grid">
            <button class="secondary-action" @tap="openIssuePopup">
              <uni-icons type="chatboxes-filled" size="18" color="#1D4ED8"></uni-icons>
              <text>Report Issue</text>
            </button>

            <button
              class="danger-action"
              :disabled="busyAction === 'end'"
              @tap="handleEndRide"
            >
              <uni-icons type="closeempty" size="18" color="#FFFFFF"></uni-icons>
              <text>{{ busyAction === 'end' ? 'Ending ride...' : 'End Ride' }}</text>
            </button>
          </view>
        </uni-card>
      </view>

      <view v-else-if="activeRide && isBookingPage" class="dashboard-stack booking-dashboard">
        <uni-card
          title="Booking Snapshot"
          sub-title="Current reservation details for this ride"
          extra="ACTIVE"
          is-shadow
        >
          <view class="overview-grid">
            <view
              v-for="item in bookingOverviewItems"
              :key="item.label"
              class="overview-tile"
            >
              <text class="overview-label">{{ item.label }}</text>
              <text class="overview-value">{{ item.value }}</text>
            </view>
          </view>
        </uni-card>

        <uni-card
          title="Extend Reserved Time"
          sub-title="Adjust the booking without leaving the detail page"
          is-shadow
        >
          <uni-section title="Select extra time" type="line"></uni-section>
          <uni-segmented-control
            :current="selectedExtensionIndex"
            :values="extensionLabels"
            style-type="button"
            active-color="#2563EB"
            in-active-color="#EFF6FF"
            @clickItem="handleExtensionSelection"
          />

          <view class="extend-preview">
            <view class="extend-preview-copy">
              <text class="extend-preview-title">New planned finish</text>
              <text class="extend-preview-value">{{ projectedEndTime }}</text>
            </view>
            <view class="extend-preview-copy">
              <text class="extend-preview-title">Updated reserved total</text>
              <text class="extend-preview-value">{{ projectedTotalCost }}</text>
            </view>
          </view>

          <button
            class="primary-action"
            :disabled="busyAction === 'extend'"
            @tap="handleExtendRide"
          >
            <text>{{ busyAction === 'extend' ? 'Extending booking...' : `Extend by ${extensionMinutes} min` }}</text>
          </button>
        </uni-card>

        <uni-card
          title="Booking Essentials"
          sub-title="Everything currently attached to this reservation"
          is-shadow
        >
          <view class="detail-grid">
            <view class="detail-pill">
              <uni-icons type="wallet" size="18" color="#2563EB"></uni-icons>
              <text class="detail-pill-text">Reserved {{ formatMoney(activeRide.totalCost) }}</text>
            </view>
            <view class="detail-pill">
              <uni-icons type="calendar-filled" size="18" color="#2563EB"></uni-icons>
              <text class="detail-pill-text">{{ durationLabel }}</text>
            </view>
            <view class="detail-pill">
              <uni-icons type="locked-filled" size="18" color="#2563EB"></uni-icons>
              <text class="detail-pill-text">Status {{ activeRide.status }}</text>
            </view>
            <view class="detail-pill">
              <uni-icons type="paperplane-filled" size="18" color="#2563EB"></uni-icons>
              <text class="detail-pill-text">Finish {{ plannedEndTime }}</text>
            </view>
            <view class="detail-pill" v-if="activeRide.cardLast4">
              <uni-icons type="wallet-filled" size="18" color="#2563EB"></uni-icons>
              <text class="detail-pill-text">Card ending {{ activeRide.cardLast4 }}</text>
            </view>
          </view>
        </uni-card>

        <view class="support-grid">
          <view class="support-card">
            <text class="support-card-title">Need live ride controls?</text>
            <text class="support-card-copy">
              Open My Ride at any time for the live timer, ride controls, and safety actions.
            </text>
            <button class="secondary-action compact-action" @tap="goToRideDashboard">
              <text>Open My Ride</text>
            </button>
          </view>

          <view class="support-card">
            <text class="support-card-title">Quick actions</text>
            <text class="support-card-copy">
              Extend the reservation, report a scooter issue, or finish the booking here when needed.
            </text>
            <view class="mode-action-row">
              <button class="secondary-action compact-action" @tap="openIssuePopup">
                <text>Report Issue</text>
              </button>
              <button
                class="danger-action compact-action"
                :disabled="busyAction === 'end'"
                @tap="handleEndRide"
              >
                <text>{{ busyAction === 'end' ? 'Ending ride...' : 'End Ride' }}</text>
              </button>
            </view>
          </view>
        </view>
      </view>

      <view v-else-if="isTripPage" class="empty-state-shell">
        <uni-card
          title="No active ride found"
          sub-title="Start a new booking from the scooter map to unlock the live ride dashboard."
          is-shadow
        >
          <view class="empty-illustration">
            <view class="empty-icon-ring">
              <uni-icons type="paperplane-filled" size="34" color="#2563EB"></uni-icons>
            </view>
            <text class="empty-title">My Ride is standing by</text>
            <text class="empty-subtitle">
              Once a ride becomes active, this page turns into your control center for countdowns, extensions, issue reports, and ending the rental safely.
            </text>
          </view>

          <button class="primary-action" @tap="goToFindScooter">
            <text>Find a Scooter</text>
          </button>
        </uni-card>

        <view class="support-grid">
          <view class="support-card scaffold-card">
            <text class="support-card-title">Ride dashboard preview</text>
            <text class="support-card-copy">
              The layout below fills with live data as soon as a scooter starts moving under your account.
            </text>
            <view class="preview-grid">
              <view
                v-for="item in tripPreviewItems"
                :key="item.label"
                class="preview-tile"
              >
                <text class="preview-label">{{ item.label }}</text>
                <text class="preview-value">{{ item.value }}</text>
              </view>
            </view>
          </view>

          <view class="support-card">
            <text class="support-card-title">What this page is for</text>
            <view class="checklist-list">
              <view
                v-for="item in tripChecklist"
                :key="item.title"
                class="checklist-item"
              >
                <view class="item-icon">
                  <uni-icons type="checkmarkempty" size="16" color="#1D4ED8"></uni-icons>
                </view>
                <view class="item-copy">
                  <text class="item-title">{{ item.title }}</text>
                  <text class="item-subtitle">{{ item.copy }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view v-else class="empty-state-shell">
        <uni-card
          title="No booking details yet"
          sub-title="Reserve a scooter to see the order summary, payment snapshot, and next steps here."
          is-shadow
        >
          <view class="empty-illustration">
            <view class="empty-icon-ring">
              <uni-icons type="calendar-filled" size="34" color="#2563EB"></uni-icons>
            </view>
            <text class="empty-title">Book & Details is ready for your next order</text>
            <text class="empty-subtitle">
              Once you choose a scooter and confirm a plan, this page will keep the booking essentials together before and during the ride.
            </text>
          </view>

          <view class="empty-actions">
            <button class="primary-action compact-action" @tap="goToFindScooter">
              <text>Book a Scooter</text>
            </button>
            <button class="secondary-action compact-action" @tap="goToRideDashboard">
              <text>Open My Ride</text>
            </button>
          </view>
        </uni-card>

        <view class="support-grid">
          <view class="support-card scaffold-card">
            <text class="support-card-title">Booking framework</text>
            <text class="support-card-copy">
              These details will be filled automatically once a booking is created from the map.
            </text>
            <view class="placeholder-list">
              <view
                v-for="item in bookingPreviewItems"
                :key="item.label"
                class="placeholder-row"
              >
                <text class="placeholder-label">{{ item.label }}</text>
                <text class="placeholder-value">{{ item.value }}</text>
              </view>
            </view>
          </view>

          <view class="support-card">
            <text class="support-card-title">How booking works</text>
            <view class="timeline-list">
              <view
                v-for="(step, index) in bookingSteps"
                :key="step.title"
                class="timeline-item"
              >
                <view class="timeline-step">{{ index + 1 }}</view>
                <view class="item-copy">
                  <text class="item-title">{{ step.title }}</text>
                  <text class="item-subtitle">{{ step.copy }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <uni-popup ref="issuePopupRef" type="bottom" background-color="#FFFFFF" :safe-area="true">
        <view class="issue-popup">
          <view class="issue-header">
            <view>
              <text class="issue-title">Report an issue</text>
              <text class="issue-subtitle">Tell us what went wrong so we can help quickly.</text>
            </view>
            <view class="issue-close" @tap="closeIssuePopup">
              <uni-icons type="closeempty" size="22" color="#64748B"></uni-icons>
            </view>
          </view>

          <uni-section title="Fault category" type="line"></uni-section>
          <view class="issue-tag-row">
            <uni-tag
              v-for="category in issueCategories"
              :key="category.value"
              :text="category.label"
              type="primary"
              circle
              :inverted="issueForm.category !== category.value"
              custom-style="margin-right: 10px; margin-bottom: 10px;"
              @click="issueForm.category = category.value"
            />
          </view>

          <uni-section title="Description" type="line"></uni-section>
          <uni-easyinput
            v-model="issueForm.description"
            type="textarea"
            maxlength="300"
            placeholder="Describe the fault, unusual noise, brake issue, battery issue, or anything else we should know."
          />

          <view class="issue-actions">
            <button class="ghost-action" @tap="closeIssuePopup">
              <text>Cancel</text>
            </button>
            <button
              class="primary-action issue-submit"
              :disabled="busyAction === 'issue'"
              @tap="submitIssue"
            >
              <text>{{ busyAction === 'issue' ? 'Submitting...' : 'Submit Report' }}</text>
            </button>
          </view>
        </view>
      </uni-popup>
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
import {
  clearStoredActiveRide,
  findActiveRide,
  getRideEndTime,
  getStoredActiveRide,
  getStoredUserId,
  normalizeActiveRide,
  setStoredActiveRide
} from '@/utils/activeRide.js'

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
const issuePopupRef = ref(null)
const issueForm = ref({
  category: 'OTHER',
  description: ''
})

const tripPreviewItems = [
  { label: 'Live timer', value: 'Countdown and time left' },
  { label: 'Scooter', value: 'Model, ID, and start time' },
  { label: 'Controls', value: 'Extend time or end ride' },
  { label: 'Support', value: 'Issue reporting tools' }
]

const tripChecklist = [
  {
    title: 'Watch your reserved time',
    copy: 'See the countdown and planned finish time as soon as a ride becomes active.'
  },
  {
    title: 'Handle ride actions quickly',
    copy: 'Extend the booking, report a fault, or end the ride without leaving this page.'
  },
  {
    title: 'Keep the ride transparent',
    copy: 'The dashboard keeps scooter, timing, and reserved total together while you ride.'
  }
]

const bookingPreviewItems = [
  { label: 'Scooter', value: 'Assigned after you choose on the map' },
  { label: 'Plan', value: 'Your selected duration and rate' },
  { label: 'Payment', value: 'Saved card or payment summary' },
  { label: 'Status', value: 'Booked, active, or completed' }
]

const bookingSteps = [
  {
    title: 'Choose a scooter',
    copy: 'Pick an available scooter from the map and open the booking sheet.'
  },
  {
    title: 'Confirm the plan',
    copy: 'Select your duration, review pricing, and start the ride.'
  },
  {
    title: 'Track the booking here',
    copy: 'This page keeps the reservation snapshot, payment detail, and next actions together.'
  }
]

const isBookingPage = computed(() => currentNavPage.value === 'booking')
const isTripPage = computed(() => !isBookingPage.value)

const heroContent = computed(() => {
  if (isBookingPage.value) {
    return {
      eyebrow: 'Booking Overview',
      title: activeRide.value
        ? 'Everything reserved for this ride in one place'
        : 'Book & Details is prepared for your next scooter order',
      subtitle: activeRide.value
        ? 'Review the reserved scooter, planned finish, payment snapshot, and next actions without switching context.'
        : 'This page becomes your booking summary as soon as you confirm a scooter, keeping the plan, payment, and key ride details together.',
      chip: activeRide.value ? 'Booking is active' : 'Waiting for booking'
    }
  }

  return {
    eyebrow: 'My Ride Dashboard',
    title: activeRide.value
      ? 'Stay in control while your scooter is on the move'
      : 'Your ride controls will appear here the moment a trip starts',
    subtitle: activeRide.value
      ? 'Track the remaining rental time, add more minutes, end the ride safely, or report a fault without leaving the dashboard.'
      : 'Use this page as your ride hub for countdowns, live controls, safety actions, and status updates once a scooter is in use.',
    chip: activeRide.value ? 'Ride in progress' : 'Ready when you are'
  }
})

const heroChipIcon = computed(() => (isBookingPage.value ? 'calendar-filled' : 'paperplane-filled'))
const extensionMinutes = computed(() => extensionOptions[selectedExtensionIndex.value] || extensionOptions[0])
const durationLabel = computed(() => `${Number(activeRide.value?.durationMinutes || 0)} min reserved`)
const countdownHint = computed(() => {
  if (!activeRide.value) return ''
  return `Scooter #${activeRide.value.scooterId} is active and scheduled to finish at ${plannedEndTime.value}.`
})
const bookingOverviewItems = computed(() => {
  if (!activeRide.value) return []

  return [
    { label: 'Booking', value: `#${activeRide.value.bookingId}` },
    { label: 'Scooter', value: `#${activeRide.value.scooterId} ${activeRide.value.scooterModel}` },
    { label: 'Booked from', value: formatDateTime(activeRide.value.startTime) },
    { label: 'Planned finish', value: plannedEndTime.value },
    { label: 'Reserved duration', value: durationLabel.value },
    { label: 'Reserved total', value: formatMoney(activeRide.value.totalCost) }
  ]
})

const countdownParts = computed(() => {
  const endTime = getRideEndTime(activeRide.value)
  if (!endTime) {
    return { totalSeconds: 0, day: 0, hour: 0, minute: 0, second: 0 }
  }

  const totalSeconds = Math.max(0, Math.floor((endTime.getTime() - Date.now()) / 1000))

  const day = Math.floor(totalSeconds / 86400)
  const hour = Math.floor((totalSeconds % 86400) / 3600)
  const minute = Math.floor((totalSeconds % 3600) / 60)
  const second = totalSeconds % 60

  return { totalSeconds, day, hour, minute, second }
})

const countdownExpired = computed(() => countdownReached.value || countdownParts.value.totalSeconds <= 0)

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

const formatDateTime = (value) => {
  if (!value) return 'Not available'

  const date = value instanceof Date ? value : new Date(String(value).replace(' ', 'T'))
  if (Number.isNaN(date.getTime())) return 'Not available'

  return date.toLocaleString('en-GB', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatMoney = (value) => `£${Number(value || 0).toFixed(2)}`

const updateActiveRideState = (ride) => {
  if (!ride) {
    countdownReached.value = false
    activeRide.value = null
    clearStoredActiveRide()
    return
  }

  const normalized = normalizeActiveRide(ride, activeRide.value || getStoredActiveRide() || {})
  countdownReached.value = false
  activeRide.value = setStoredActiveRide(normalized)
}

const enrichRideDetails = async (ride) => {
  if (!ride?.scooterId) return ride

  const hasBasePrice = Number(ride.basePrice || 0) > 0
  const hasPerMinutePrice = Number(ride.pricePerMinute || 0) > 0
  const hasPricing = hasBasePrice && hasPerMinutePrice
  const hasModel = Boolean(ride.scooterModel && ride.scooterModel !== 'Scooter')
  if (hasPricing && hasModel) return ride

  try {
    const scooter = await getScooterById(ride.scooterId)
    return normalizeActiveRide({
      ...ride,
      scooterModel: ride.scooterModel && ride.scooterModel !== 'Scooter' ? ride.scooterModel : scooter?.model,
      basePrice: hasBasePrice ? ride.basePrice : scooter?.basePrice,
      pricePerMinute: hasPerMinutePrice ? ride.pricePerMinute : scooter?.pricePerMin
    }, ride)
  } catch (error) {
    console.error('Failed to enrich ride details:', error)
    return ride
  }
}

const syncActiveRide = async () => {
  const cached = getStoredActiveRide()
  if (cached) {
    activeRide.value = cached
  }

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

const handleCountdownComplete = () => {
  countdownReached.value = true
}

const handleExtensionSelection = (event) => {
  selectedExtensionIndex.value = Number(event?.currentIndex || 0)
}

const handleExtendRide = async () => {
  if (!activeRide.value?.bookingId || busyAction.value) return

  busyAction.value = 'extend'
  try {
    const response = await extendRide(activeRide.value.bookingId, extensionMinutes.value)
    updateActiveRideState({
      ...activeRide.value,
      ...response,
      status: response?.bookingStatus || activeRide.value.status
    })

    countdownReached.value = false
    uni.showToast({
      title: `Added ${extensionMinutes.value} min`,
      icon: 'success'
    })
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

        uni.showModal({
          title: 'Ride ended',
          content: `Booking #${result?.bookingId || ''} finished. Total charged: ${formatMoney(result?.totalCost)}.`,
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
  issuePopupRef.value?.open()
}

const closeIssuePopup = () => {
  if (busyAction.value === 'issue') return
  issuePopupRef.value?.close()
}

const resetIssueForm = () => {
  issueForm.value = {
    category: 'OTHER',
    description: ''
  }
}

const submitIssue = async () => {
  if (!activeRide.value?.scooterId || busyAction.value) return

  const description = String(issueForm.value.description || '').trim()
  if (description.length < 8) {
    uni.showToast({
      title: 'Please add more detail',
      icon: 'none'
    })
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

    closeIssuePopup()
    resetIssueForm()
    uni.showToast({
      title: 'Issue reported',
      icon: 'success'
    })
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
  syncActiveRide()
})

onHide(() => {
  closeIssuePopup()
})

onUnload(() => {
  closeIssuePopup()
})
</script>

<style scoped>
.active-ride-page {
  min-height: calc(100vh - 160rpx);
  padding: 36rpx 24rpx 48rpx;
  background:
    radial-gradient(circle at top right, rgba(59, 130, 246, 0.18), transparent 36%),
    linear-gradient(180deg, #F8FBFF 0%, #EEF5FF 48%, #F8FBFF 100%);
}

.page-hero {
  padding: 40rpx 34rpx;
  border-radius: 36rpx;
  background: linear-gradient(135deg, #1D4ED8 0%, #2563EB 55%, #60A5FA 100%);
  color: #FFFFFF;
  box-shadow: 0 22rpx 60rpx rgba(37, 99, 235, 0.22);
  margin-bottom: 28rpx;
}

.hero-copy {
  margin-bottom: 24rpx;
}

.hero-eyebrow {
  display: inline-block;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.16);
  font-size: 22rpx;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.hero-title {
  display: block;
  margin-top: 20rpx;
  font-size: 46rpx;
  line-height: 1.18;
  font-weight: 800;
}

.hero-subtitle {
  display: block;
  margin-top: 16rpx;
  font-size: 26rpx;
  line-height: 1.65;
  color: rgba(255, 255, 255, 0.84);
}

.hero-chip {
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  padding: 12rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.88);
}

.hero-chip-text {
  font-size: 24rpx;
  color: #1D4ED8;
  font-weight: 700;
}

.dashboard-stack {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.booking-dashboard {
  gap: 18rpx;
}

.overview-grid,
.support-grid,
.preview-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.overview-tile,
.preview-tile {
  padding: 22rpx;
  border-radius: 24rpx;
  background: linear-gradient(180deg, #F8FBFF 0%, #EEF6FF 100%);
  border: 1rpx solid rgba(37, 99, 235, 0.10);
}

.overview-label,
.preview-label,
.placeholder-label {
  display: block;
  font-size: 22rpx;
  color: #64748B;
  margin-bottom: 8rpx;
}

.overview-value,
.preview-value,
.placeholder-value {
  display: block;
  font-size: 27rpx;
  color: #0F172A;
  font-weight: 700;
  line-height: 1.5;
}

.timer-shell {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.timer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  flex-wrap: wrap;
}

.timer-badge {
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  padding: 12rpx 20rpx;
  border-radius: 999rpx;
  background: #EFF6FF;
}

.timer-badge-text {
  font-size: 24rpx;
  color: #1D4ED8;
  font-weight: 700;
}

.timer-label {
  font-size: 24rpx;
  color: #64748B;
  font-weight: 600;
}

.countdown-wrap {
  padding: 30rpx 24rpx;
  border-radius: 28rpx;
  background: linear-gradient(180deg, #F8FBFF 0%, #EFF6FF 100%);
  border: 1rpx solid rgba(59, 130, 246, 0.16);
  display: flex;
  justify-content: center;
}

.timer-summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
}

.summary-item {
  padding: 22rpx;
  border-radius: 24rpx;
  background: #F8FAFC;
}

.summary-label {
  display: block;
  font-size: 22rpx;
  color: #64748B;
  margin-bottom: 8rpx;
}

.summary-value {
  display: block;
  font-size: 27rpx;
  color: #0F172A;
  font-weight: 700;
  line-height: 1.45;
}

.detail-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
}

.detail-pill {
  display: inline-flex;
  align-items: center;
  gap: 10rpx;
  padding: 16rpx 20rpx;
  border-radius: 999rpx;
  background: #EFF6FF;
  border: 1rpx solid rgba(37, 99, 235, 0.12);
}

.detail-pill-text {
  font-size: 24rpx;
  color: #1E3A8A;
  font-weight: 600;
}

.extend-preview {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  margin: 24rpx 0 20rpx;
}

.extend-preview-copy {
  padding: 22rpx;
  border-radius: 24rpx;
  background: linear-gradient(180deg, #F8FBFF 0%, #EEF6FF 100%);
}

.extend-preview-title {
  display: block;
  font-size: 22rpx;
  color: #64748B;
  margin-bottom: 8rpx;
}

.extend-preview-value {
  display: block;
  font-size: 27rpx;
  color: #0F172A;
  font-weight: 700;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.primary-action,
.secondary-action,
.danger-action,
.ghost-action {
  width: 100%;
  min-height: 92rpx;
  border-radius: 999rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  font-size: 28rpx;
  font-weight: 700;
  border: none;
}

.primary-action {
  background: linear-gradient(135deg, #2563EB, #1D4ED8);
  color: #FFFFFF;
  box-shadow: 0 14rpx 28rpx rgba(37, 99, 235, 0.20);
}

.secondary-action {
  background: #EFF6FF;
  color: #1D4ED8;
  border: 2rpx solid rgba(37, 99, 235, 0.14);
}

.danger-action {
  background: linear-gradient(135deg, #1E3A8A, #1D4ED8);
  color: #FFFFFF;
}

.ghost-action {
  background: #FFFFFF;
  color: #475569;
  border: 2rpx solid #E2E8F0;
}

.primary-action::after,
.secondary-action::after,
.danger-action::after,
.ghost-action::after {
  border: none;
}

.primary-action[disabled],
.secondary-action[disabled],
.danger-action[disabled] {
  opacity: 0.68;
}

.empty-state-shell {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  margin-top: 12rpx;
}

.empty-actions,
.mode-action-row {
  display: flex;
  gap: 16rpx;
}

.mode-action-row {
  margin-top: 22rpx;
}

.support-card {
  padding: 28rpx;
  border-radius: 30rpx;
  background: rgba(255, 255, 255, 0.94);
  border: 1rpx solid rgba(148, 163, 184, 0.14);
  box-shadow: 0 18rpx 48rpx rgba(15, 23, 42, 0.06);
}

.scaffold-card {
  background: linear-gradient(180deg, rgba(239, 246, 255, 0.92) 0%, rgba(255, 255, 255, 0.98) 100%);
}

.support-card-title {
  display: block;
  font-size: 30rpx;
  font-weight: 800;
  color: #0F172A;
}

.support-card-copy {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #64748B;
  line-height: 1.7;
}

.compact-action {
  min-height: 84rpx;
  margin-top: 22rpx;
  flex: 1;
}

.mode-action-row .compact-action,
.empty-actions .compact-action {
  margin-top: 0;
}

.checklist-list,
.timeline-list,
.placeholder-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  margin-top: 22rpx;
}

.checklist-item,
.timeline-item {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
}

.item-icon,
.timeline-step {
  width: 44rpx;
  height: 44rpx;
  border-radius: 14rpx;
  background: #EFF6FF;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.timeline-step {
  background: linear-gradient(135deg, #DBEAFE, #BFDBFE);
  color: #1D4ED8;
  font-size: 24rpx;
  font-weight: 800;
}

.item-copy {
  min-width: 0;
  flex: 1;
}

.item-title {
  display: block;
  font-size: 26rpx;
  font-weight: 700;
  color: #0F172A;
}

.item-subtitle {
  display: block;
  margin-top: 6rpx;
  font-size: 23rpx;
  line-height: 1.65;
  color: #64748B;
}

.placeholder-row {
  display: flex;
  justify-content: space-between;
  gap: 18rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #E2E8F0;
}

.placeholder-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.placeholder-value {
  text-align: right;
}

.empty-illustration {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 18rpx 12rpx 28rpx;
}

.empty-icon-ring {
  width: 124rpx;
  height: 124rpx;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.16), rgba(191, 219, 254, 0.5));
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
}

.empty-title {
  display: block;
  font-size: 38rpx;
  font-weight: 800;
  color: #0F172A;
}

.empty-subtitle {
  display: block;
  margin-top: 14rpx;
  font-size: 26rpx;
  color: #64748B;
  line-height: 1.65;
}

.issue-popup {
  padding: 30rpx 24rpx calc(env(safe-area-inset-bottom) + 30rpx);
  border-radius: 34rpx 34rpx 0 0;
  background: linear-gradient(180deg, #F8FBFF 0%, #FFFFFF 28%);
}

.issue-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 10rpx;
}

.issue-title {
  display: block;
  font-size: 34rpx;
  font-weight: 800;
  color: #0F172A;
}

.issue-subtitle {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #64748B;
  line-height: 1.6;
}

.issue-close {
  width: 64rpx;
  height: 64rpx;
  border-radius: 20rpx;
  background: #EFF6FF;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.issue-tag-row {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 16rpx;
}

.issue-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 24rpx;
}

.issue-submit {
  flex: 1;
}

@media (max-width: 750px) {
  .active-ride-page {
    padding: 24rpx 18rpx 40rpx;
  }

  .hero-title {
    font-size: 40rpx;
  }

  .timer-summary-grid,
  .extend-preview,
  .action-grid,
  .overview-grid,
  .support-grid,
  .preview-grid {
    grid-template-columns: 1fr;
  }

  .empty-actions,
  .mode-action-row,
  .issue-actions {
    flex-direction: column;
  }
}
</style>
