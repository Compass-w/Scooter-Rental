<template>
  <BaseLayout nav-type="default" :show-menu="true" :show-footer="false" :current-page="currentNavPage">
    <view class="ride-page">
      <view class="hero">
        <text class="eyebrow">{{ isBookingPage ? 'Booking Overview' : 'My Ride Dashboard' }}</text>
        <text class="title">{{ heroTitle }}</text>
        <text class="subtitle">{{ heroSubtitle }}</text>
        <view class="hero-chip">
          <view class="hero-chip-dot"></view>
          <text class="hero-chip-text">{{ heroChip }}</text>
        </view>
      </view>

      <view v-if="activeRide && countdownExpired" class="banner banner-alert">
        <text class="banner-text">Your reserved time has ended. Extend the booking or end the ride to keep things clear.</text>
      </view>
      <view v-else-if="activeRide" class="banner">
        <text class="banner-text">{{ countdownHint }}</text>
      </view>

      <view v-if="syncing" class="banner">
        <view class="sync-dot"></view>
        <text class="banner-text">Checking your latest booking details...</text>
      </view>

      <view v-if="activeRide && isTripPage" class="stack">
        <view class="card">
          <view class="card-head">
            <view>
              <text class="card-title">Remaining Rental Time</text>
              <text class="card-sub">Live countdown for your current booking</text>
            </view>
            <text class="badge">ACTIVE</text>
          </view>
          <view class="timer-box">
            <text class="timer-text">{{ countdownText }}</text>
          </view>
          <view class="grid">
            <view class="tile">
              <text class="tile-label">Scooter</text>
              <text class="tile-value">#{{ activeRide.scooterId }} {{ activeRide.scooterModel }}</text>
            </view>
            <view class="tile">
              <text class="tile-label">Started</text>
              <text class="tile-value">{{ formatDateTime(activeRide.startTime) }}</text>
            </view>
            <view class="tile">
              <text class="tile-label">Planned finish</text>
              <text class="tile-value">{{ plannedEndTime }}</text>
            </view>
            <view class="tile">
              <text class="tile-label">Reserved total</text>
              <text class="tile-value">{{ formatMoney(activeRide.totalCost) }}</text>
            </view>
          </view>
        </view>

        <view class="card">
          <view class="card-head">
            <view>
              <text class="card-title">Ride Actions</text>
              <text class="card-sub">Extend, report an issue, or end the ride here.</text>
            </view>
          </view>
          <text class="section-label">Extend booking</text>
          <view class="chips">
            <view
              v-for="(label, index) in extensionLabels"
              :key="label"
              :class="['chip', selectedExtensionIndex === index ? 'chip-active' : '']"
              @tap="selectedExtensionIndex = index"
            >
              <text :class="['chip-text', selectedExtensionIndex === index ? 'chip-text-active' : '']">{{ label }}</text>
            </view>
          </view>
          <view class="grid preview-grid">
            <view class="tile">
              <text class="tile-label">New planned finish</text>
              <text class="tile-value">{{ projectedEndTime }}</text>
            </view>
            <view class="tile">
              <text class="tile-label">Updated reserved total</text>
              <text class="tile-value">{{ projectedTotalCost }}</text>
            </view>
          </view>
          <view class="actions">
            <button class="btn btn-primary" :disabled="busyAction === 'extend'" @tap="handleExtendRide">
              <text>{{ busyAction === 'extend' ? 'Extending booking...' : `Extend by ${extensionMinutes} min` }}</text>
            </button>
            <button class="btn btn-secondary" @tap="openIssuePopup">
              <text>Report Issue</text>
            </button>
            <button class="btn btn-danger" :disabled="busyAction === 'end'" @tap="handleEndRide">
              <text>{{ busyAction === 'end' ? 'Ending ride...' : 'End Ride' }}</text>
            </button>
          </view>
        </view>
      </view>

      <view v-else-if="activeRide && isBookingPage" class="stack">
        <view class="card">
          <view class="card-head">
            <view>
              <text class="card-title">Booking Snapshot</text>
              <text class="card-sub">Current reservation details for this ride</text>
            </view>
            <text class="badge">ACTIVE</text>
          </view>
          <view class="grid">
            <view class="tile">
              <text class="tile-label">Booking</text>
              <text class="tile-value">#{{ activeRide.bookingId || 'Pending' }}</text>
            </view>
            <view class="tile">
              <text class="tile-label">Scooter</text>
              <text class="tile-value">#{{ activeRide.scooterId }} {{ activeRide.scooterModel }}</text>
            </view>
            <view class="tile">
              <text class="tile-label">Booked from</text>
              <text class="tile-value">{{ formatDateTime(activeRide.startTime) }}</text>
            </view>
            <view class="tile">
              <text class="tile-label">Planned finish</text>
              <text class="tile-value">{{ plannedEndTime }}</text>
            </view>
            <view class="tile">
              <text class="tile-label">Duration</text>
              <text class="tile-value">{{ durationLabel }}</text>
            </view>
            <view class="tile">
              <text class="tile-label">Reserved total</text>
              <text class="tile-value">{{ formatMoney(activeRide.totalCost) }}</text>
            </view>
          </view>
        </view>

        <view class="card">
          <view class="card-head">
            <view>
              <text class="card-title">Booking Controls</text>
              <text class="card-sub">Everything important stays here even before the ride starts.</text>
            </view>
          </view>
          <view class="detail-list">
            <text class="detail-item">Status {{ activeRide.status || 'ACTIVE' }}</text>
            <text class="detail-item">{{ durationLabel }}</text>
            <text class="detail-item">Finish {{ plannedEndTime }}</text>
            <text v-if="activeRide.cardLast4" class="detail-item">Card ending {{ activeRide.cardLast4 }}</text>
          </view>
          <view class="actions">
            <button class="btn btn-primary" :disabled="busyAction === 'extend'" @tap="handleExtendRide">
              <text>{{ busyAction === 'extend' ? 'Extending booking...' : `Extend by ${extensionMinutes} min` }}</text>
            </button>
            <button class="btn btn-secondary" @tap="goToRideDashboard">
              <text>Open My Ride</text>
            </button>
            <button class="btn btn-danger" :disabled="busyAction === 'end'" @tap="handleEndRide">
              <text>{{ busyAction === 'end' ? 'Ending ride...' : 'End Ride' }}</text>
            </button>
          </view>
        </view>
      </view>

      <view v-else-if="isTripPage" class="stack">
        <view class="card empty-card">
          <text class="empty-mark">R</text>
          <text class="card-title">My Ride is standing by</text>
          <text class="card-sub centered">
            Start a booking from the scooter map and this page will turn into your live ride dashboard.
          </text>
          <view class="actions">
            <button class="btn btn-primary" @tap="goToFindScooter">
              <text>Find a Scooter</text>
            </button>
          </view>
        </view>
        <view class="grid">
          <view class="tile">
            <text class="tile-label">Live timer</text>
            <text class="tile-value">Countdown and time left</text>
          </view>
          <view class="tile">
            <text class="tile-label">Scooter info</text>
            <text class="tile-value">Model, ID, and start time</text>
          </view>
          <view class="tile">
            <text class="tile-label">Ride controls</text>
            <text class="tile-value">Extend time or end ride</text>
          </view>
          <view class="tile">
            <text class="tile-label">Support</text>
            <text class="tile-value">Issue reporting tools</text>
          </view>
        </view>
      </view>

      <view v-else class="stack">
        <view class="card empty-card">
          <text class="empty-mark">B</text>
          <text class="card-title">Book & Details is ready for your next order</text>
          <text class="card-sub centered">
            Once you choose a scooter and confirm a plan, this page will keep the booking essentials together before and during the ride.
          </text>
          <view class="actions">
            <button class="btn btn-primary" @tap="goToFindScooter">
              <text>Book a Scooter</text>
            </button>
            <button class="btn btn-secondary" @tap="goToRideDashboard">
              <text>Open My Ride</text>
            </button>
          </view>
        </view>
        <view class="grid">
          <view class="tile">
            <text class="tile-label">Scooter</text>
            <text class="tile-value">Assigned after you choose on the map</text>
          </view>
          <view class="tile">
            <text class="tile-label">Plan</text>
            <text class="tile-value">Your selected duration and rate</text>
          </view>
          <view class="tile">
            <text class="tile-label">Payment</text>
            <text class="tile-value">Saved card or payment summary</text>
          </view>
          <view class="tile">
            <text class="tile-label">Status</text>
            <text class="tile-value">Booked, active, or completed</text>
          </view>
        </view>
      </view>

      <view v-if="issuePanelOpen" class="overlay" @tap="closeIssuePopup">
        <view class="sheet" @tap.stop>
          <view class="card-head">
            <view>
              <text class="card-title">Report an issue</text>
              <text class="card-sub">Tell us what went wrong so we can help quickly.</text>
            </view>
          </view>
          <text class="section-label">Fault category</text>
          <view class="chips">
            <view
              v-for="category in issueCategories"
              :key="category.value"
              :class="['chip', issueForm.category === category.value ? 'chip-active' : '']"
              @tap="issueForm.category = category.value"
            >
              <text :class="['chip-text', issueForm.category === category.value ? 'chip-text-active' : '']">{{ category.label }}</text>
            </view>
          </view>
          <text class="section-label">Description</text>
          <textarea
            v-model="issueForm.description"
            class="textarea"
            maxlength="300"
            placeholder="Describe the fault, unusual noise, brake issue, battery issue, or anything else we should know."
          />
          <view class="actions">
            <button class="btn btn-secondary" @tap="closeIssuePopup">
              <text>Cancel</text>
            </button>
            <button class="btn btn-primary" :disabled="busyAction === 'issue'" @tap="submitIssue">
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
.ride-page { min-height: calc(100vh - 160rpx); padding: 36rpx 24rpx 48rpx; background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.18), transparent 36%), linear-gradient(180deg, #F8FBFF 0%, #EEF5FF 48%, #F8FBFF 100%); }
.hero, .card, .sheet { background: rgba(255, 255, 255, 0.96); border: 1rpx solid rgba(148, 163, 184, 0.14); box-shadow: 0 18rpx 48rpx rgba(15, 23, 42, 0.06); }
.hero { padding: 34rpx; border-radius: 34rpx; background: linear-gradient(135deg, #1D4ED8 0%, #2563EB 55%, #60A5FA 100%); color: #fff; margin-bottom: 24rpx; }
.eyebrow { display: inline-block; padding: 10rpx 18rpx; border-radius: 999rpx; background: rgba(255,255,255,0.16); font-size: 22rpx; font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; }
.title { display: block; margin-top: 18rpx; font-size: 44rpx; line-height: 1.2; font-weight: 800; }
.subtitle { display: block; margin-top: 14rpx; font-size: 26rpx; line-height: 1.65; color: rgba(255,255,255,0.86); }
.hero-chip { display: inline-flex; align-items: center; gap: 10rpx; margin-top: 20rpx; padding: 12rpx 18rpx; border-radius: 999rpx; background: rgba(255,255,255,0.9); }
.hero-chip-dot, .sync-dot { width: 14rpx; height: 14rpx; border-radius: 50%; background: #2563EB; }
.hero-chip-text, .banner-text { font-size: 24rpx; font-weight: 700; color: #1D4ED8; }
.banner { display: flex; align-items: center; gap: 12rpx; margin-bottom: 18rpx; padding: 18rpx 20rpx; border-radius: 22rpx; background: rgba(255,255,255,0.92); border: 1rpx solid rgba(37,99,235,0.12); box-shadow: 0 12rpx 28rpx rgba(37,99,235,0.08); }
.banner-alert { background: #DBEAFE; }
.stack { display: flex; flex-direction: column; gap: 18rpx; }
.card { padding: 28rpx; border-radius: 30rpx; }
.card-head { display: flex; align-items: flex-start; justify-content: space-between; gap: 16rpx; margin-bottom: 18rpx; }
.card-title { display: block; font-size: 32rpx; font-weight: 800; color: #0F172A; }
.card-sub { display: block; margin-top: 8rpx; font-size: 24rpx; line-height: 1.6; color: #64748B; }
.centered { text-align: center; }
.badge { flex-shrink: 0; padding: 10rpx 16rpx; border-radius: 999rpx; background: #EFF6FF; color: #1D4ED8; font-size: 22rpx; font-weight: 700; }
.timer-box { display: flex; justify-content: center; padding: 32rpx 24rpx; margin-bottom: 18rpx; border-radius: 24rpx; background: linear-gradient(180deg, #F8FBFF 0%, #EFF6FF 100%); border: 1rpx solid rgba(59,130,246,0.16); }
.timer-text { font-size: 54rpx; font-weight: 800; color: #1D4ED8; letter-spacing: 0.06em; }
.grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16rpx; }
.tile { padding: 22rpx; border-radius: 22rpx; background: linear-gradient(180deg, #F8FBFF 0%, #EEF6FF 100%); border: 1rpx solid rgba(37,99,235,0.1); }
.tile-label, .section-label { display: block; font-size: 22rpx; color: #64748B; margin-bottom: 8rpx; font-weight: 700; text-transform: uppercase; letter-spacing: 0.04em; }
.tile-value { display: block; font-size: 27rpx; line-height: 1.5; color: #0F172A; font-weight: 700; }
.preview-grid { margin: 18rpx 0; }
.chips { display: flex; flex-wrap: wrap; gap: 12rpx; }
.chip { padding: 16rpx 22rpx; border-radius: 999rpx; background: #F1F5F9; border: 1rpx solid #E2E8F0; }
.chip-active { background: linear-gradient(135deg, #DBEAFE, #BFDBFE); border-color: rgba(37,99,235,0.18); }
.chip-text { font-size: 24rpx; font-weight: 700; color: #64748B; }
.chip-text-active { color: #1D4ED8; }
.detail-list { display: flex; flex-wrap: wrap; gap: 14rpx; margin-bottom: 18rpx; }
.detail-item { padding: 16rpx 20rpx; border-radius: 999rpx; background: #EFF6FF; border: 1rpx solid rgba(37,99,235,0.12); font-size: 24rpx; font-weight: 600; color: #1E3A8A; }
.actions { display: flex; flex-wrap: wrap; gap: 16rpx; }
.btn { min-height: 88rpx; border-radius: 999rpx; display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: 700; border: none; flex: 1 1 240rpx; }
.btn::after { border: none; }
.btn-primary { background: linear-gradient(135deg, #2563EB, #1D4ED8); color: #fff; box-shadow: 0 14rpx 28rpx rgba(37,99,235,0.2); }
.btn-secondary { background: #EFF6FF; color: #1D4ED8; border: 2rpx solid rgba(37,99,235,0.14); }
.btn-danger { background: linear-gradient(135deg, #1E3A8A, #1D4ED8); color: #fff; }
.btn[disabled] { opacity: 0.68; }
.empty-card { text-align: center; }
.empty-mark { display: inline-flex; width: 120rpx; height: 120rpx; border-radius: 50%; align-items: center; justify-content: center; margin: 0 auto 24rpx; background: radial-gradient(circle, rgba(59,130,246,0.16), rgba(191,219,254,0.5)); color: #1D4ED8; font-size: 42rpx; font-weight: 800; }
.overlay { position: fixed; inset: 0; background: rgba(15,23,42,0.48); z-index: 1200; display: flex; align-items: flex-end; }
.sheet { width: 100%; padding: 30rpx 24rpx calc(env(safe-area-inset-bottom) + 30rpx); border-radius: 34rpx 34rpx 0 0; background: linear-gradient(180deg, #F8FBFF 0%, #FFFFFF 28%); }
.textarea { width: 100%; min-height: 220rpx; padding: 22rpx; box-sizing: border-box; border-radius: 22rpx; background: #fff; border: 1rpx solid #D7E3F9; font-size: 26rpx; line-height: 1.6; color: #0F172A; margin-bottom: 18rpx; }
@media (max-width: 750px) {
  .ride-page { padding: 24rpx 18rpx 40rpx; }
  .title { font-size: 40rpx; }
  .timer-text { font-size: 42rpx; }
  .grid { grid-template-columns: 1fr; }
  .card-head, .actions { flex-direction: column; align-items: stretch; }
}
</style>
