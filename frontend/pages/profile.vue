<template>
  <BaseLayout nav-type="default" :show-menu="true" :show-footer="true" current-page="profile">
    <view class="profile-page">
      <view class="profile-shell">
        <view v-if="!hasUserId" class="panel-card state-card auth-card">
          <text class="state-title">Sign in to sync your profile</text>
          <text class="state-text">
            Booking history, live ride status, and saved bank cards are loaded for the current account only.
          </text>
          <button class="primary-button" @tap="goToLogin">
            <text>Go to Login</text>
          </button>
        </view>

        <template v-else>
          <view class="hero-panel">
            <view class="hero-glow hero-glow-left"></view>
            <view class="hero-glow hero-glow-right"></view>

            <view class="hero-top">
              <view class="identity-group">
                <view class="avatar-shell">
                  <image v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar-image" mode="aspectFill" />
                  <text v-else class="avatar-initial">{{ userInitial }}</text>
                </view>

                <view class="identity-copy">
                  <text class="hero-eyebrow">Live Profile Sync</text>
                  <text class="hero-name">{{ displayName }}</text>
                  <text class="hero-meta">{{ userInfo.email || 'No email on file' }}</text>
                  <text class="hero-meta">{{ userInfo.city || 'City not set' }} | Member since {{ memberSince }}</text>
                </view>
              </view>

              <view class="hero-actions">
                <button class="ghost-button hero-button" :disabled="pageLoading" @tap="refreshData">
                  <text>{{ pageLoading ? 'Syncing...' : 'Refresh Data' }}</text>
                </button>
                <button class="primary-button hero-button" @tap="goToRidePage(activeBooking ? 'trip' : 'booking')">
                  <text>{{ activeBooking ? 'Open My Ride' : 'Open Book & Details' }}</text>
                </button>
              </view>
            </view>

            <view class="metric-grid">
              <view class="metric-card">
                <text class="metric-value">{{ completedRideCount }}</text>
                <text class="metric-label">Completed rides</text>
              </view>
              <view class="metric-card">
                <text class="metric-value">{{ pendingRideCount }}</text>
                <text class="metric-label">Pending or active</text>
              </view>
              <view class="metric-card">
                <text class="metric-value">{{ cards.length }}</text>
                <text class="metric-label">Saved cards</text>
              </view>
              <view class="metric-card">
                <text class="metric-value">{{ formatMoney(totalSpent) }}</text>
                <text class="metric-label">Completed spend</text>
              </view>
            </view>

            <view class="sync-banner">
              <view class="sync-copy">
                <text class="sync-title">{{ heroNoticeTitle }}</text>
                <text class="sync-text">{{ heroNoticeText }}</text>
              </view>
              <text class="sync-stamp">{{ lastSyncedLabel }}</text>
            </view>
          </view>

          <view class="panel-card">
            <view class="section-head">
              <view>
                <text class="section-title">Profile Details</text>
                <text class="section-subtitle">Real user data synced from the current account</text>
              </view>
              <button class="inline-link-button" @tap="toggleProfileEdit">
                <text>{{ editingProfile ? 'Close Editor' : 'Edit Info' }}</text>
              </button>
            </view>

            <view v-if="!editingProfile" class="detail-grid">
              <view class="detail-item">
                <text class="detail-label">Username</text>
                <text class="detail-value">{{ userInfo.username || '--' }}</text>
              </view>
              <view class="detail-item">
                <text class="detail-label">Email</text>
                <text class="detail-value">{{ userInfo.email || '--' }}</text>
              </view>
              <view class="detail-item">
                <text class="detail-label">Phone</text>
                <text class="detail-value">{{ userInfo.phone || '--' }}</text>
              </view>
              <view class="detail-item">
                <text class="detail-label">City</text>
                <text class="detail-value">{{ userInfo.city || '--' }}</text>
              </view>
            </view>

            <view v-else class="editor-grid">
              <view class="form-field">
                <text class="field-label">Display name</text>
                <input v-model="editForm.name" class="field-input" placeholder="Your name" />
              </view>
              <view class="form-field">
                <text class="field-label">Email</text>
                <input v-model="editForm.email" class="field-input" placeholder="name@example.com" />
              </view>
              <view class="form-field">
                <text class="field-label">Phone</text>
                <input v-model="editForm.phone" class="field-input" placeholder="07xxxxxxxxx" />
              </view>
              <view class="form-field">
                <text class="field-label">City</text>
                <input v-model="editForm.city" class="field-input" placeholder="Leeds" />
              </view>

              <view class="form-actions">
                <button class="ghost-button" @tap="cancelProfileEdit">
                  <text>Cancel</text>
                </button>
                <button class="primary-button" :disabled="savingProfile" @tap="saveProfile">
                  <text>{{ savingProfile ? 'Saving...' : 'Save Changes' }}</text>
                </button>
              </view>
            </view>
          </view>

          <view class="tab-strip">
            <view
              class="tab-pill"
              :class="{ 'tab-pill-active': activeTab === 'history' }"
              @tap="activeTab = 'history'"
            >
              <text class="tab-pill-title">Booking History</text>
              <text class="tab-pill-count">{{ bookings.length }}</text>
            </view>

            <view
              class="tab-pill"
              :class="{ 'tab-pill-active': activeTab === 'wallet' }"
              @tap="activeTab = 'wallet'"
            >
              <text class="tab-pill-title">Wallet / Cards</text>
              <text class="tab-pill-count">{{ cards.length }}</text>
            </view>
          </view>

          <view v-if="activeTab === 'history'" class="panel-card">
            <view class="section-head">
              <view>
                <text class="section-title">Booking History</text>
                <text class="section-subtitle">Pulled from the existing BookingController getHistory endpoint</text>
              </view>
              <button class="inline-link-button" :disabled="historyLoading" @tap="loadBookings">
                <text>{{ historyLoading ? 'Refreshing...' : 'Refresh History' }}</text>
              </button>
            </view>

            <view class="filter-row">
              <view
                v-for="filter in historyFilters"
                :key="filter.value"
                class="filter-chip"
                :class="{ 'filter-chip-active': historyFilter === filter.value }"
                @tap="historyFilter = filter.value"
              >
                <text class="filter-chip-text">{{ filter.label }}</text>
              </view>
            </view>

            <view v-if="historyLoading" class="state-card">
              <text class="state-title">Loading booking history...</text>
              <text class="state-text">We are syncing the latest orders from the backend.</text>
            </view>

            <view v-else-if="filteredBookings.length" class="history-list">
              <view v-for="booking in filteredBookings" :key="booking.bookingId" class="history-card">
                <view class="history-card-top">
                  <view>
                    <text class="history-booking-id">Booking #{{ booking.bookingId }}</text>
                    <text class="history-booking-time">{{ formatDateTime(booking.startTime) }}</text>
                  </view>

                  <view class="history-right">
                    <view class="status-pill" :class="statusClass(booking.status)">
                      <text class="status-pill-text">{{ formatStatusLabel(booking.status) }}</text>
                    </view>
                    <text class="history-amount">{{ formatMoney(booking.totalCost) }}</text>
                  </view>
                </view>

                <view class="history-meta-grid">
                  <view class="history-meta">
                    <text class="detail-label">Scooter</text>
                    <text class="detail-value">#{{ booking.scooterId || '--' }} {{ booking.scooterModel }}</text>
                  </view>
                  <view class="history-meta">
                    <text class="detail-label">Time window</text>
                    <text class="detail-value">{{ getBookingWindowLabel(booking) }}</text>
                  </view>
                  <view class="history-meta">
                    <text class="detail-label">Reserved time</text>
                    <text class="detail-value">{{ booking.durationMinutes ? `${booking.durationMinutes} min` : 'TBD' }}</text>
                  </view>
                  <view class="history-meta">
                    <text class="detail-label">Created</text>
                    <text class="detail-value">{{ formatDateShort(booking.createdAt || booking.startTime) }}</text>
                  </view>
                </view>

                <view class="history-actions">
                  <text v-if="canCancelBooking(booking)" class="history-note">
                    This order has not started yet, so the cancel endpoint is available.
                  </text>
                  <text v-else-if="booking.status === 'ACTIVE'" class="history-note">
                    This booking is live right now. Open My Ride to manage time, extension, and issue reporting.
                  </text>
                  <text v-else class="history-note">
                    Stored directly from your synced booking history.
                  </text>

                  <view class="history-action-row">
                    <button
                      v-if="canCancelBooking(booking)"
                      class="danger-button"
                      :disabled="cancellingBookingId === booking.bookingId"
                      @tap="confirmCancelBooking(booking)"
                    >
                      <text>{{ cancellingBookingId === booking.bookingId ? 'Cancelling...' : 'Cancel Booking' }}</text>
                    </button>

                    <button
                      v-if="booking.status === 'ACTIVE'"
                      class="secondary-button"
                      @tap="goToRidePage('trip')"
                    >
                      <text>Open My Ride</text>
                    </button>

                    <button
                      v-else
                      class="ghost-button"
                      @tap="goToFindScooter"
                    >
                      <text>{{ booking.status === 'COMPLETED' ? 'Book Again' : 'Find a Scooter' }}</text>
                    </button>
                  </view>
                </view>
              </view>
            </view>

            <view v-else class="state-card">
              <text class="state-title">No bookings matched this filter</text>
              <text class="state-text">
                Once you reserve or finish a scooter ride, the order history will appear here.
              </text>
              <button class="primary-button" @tap="goToFindScooter">
                <text>Find a Scooter</text>
              </button>
            </view>
          </view>

          <view v-else class="panel-card">
            <view class="section-head">
              <view>
                <text class="section-title">Wallet / Cards</text>
                <text class="section-subtitle">Saved cards are loaded through the existing bank card backend</text>
              </view>
              <button class="inline-link-button" :disabled="cardsLoading" @tap="loadCards">
                <text>{{ cardsLoading ? 'Refreshing...' : 'Refresh Cards' }}</text>
              </button>
            </view>

            <view class="wallet-banner">
              <view>
                <text class="wallet-banner-label">Secure storage</text>
                <text class="wallet-banner-title">Only masked card numbers are shown</text>
              </view>
              <text class="wallet-banner-value">{{ cards.length }} saved</text>
            </view>

            <view v-if="cardsLoading" class="state-card">
              <text class="state-title">Loading saved cards...</text>
              <text class="state-text">Your card list is being synced from the backend.</text>
            </view>

            <view v-else-if="cards.length" class="card-list">
              <view v-for="card in cards" :key="card.cardId" class="saved-card">
                <view class="saved-card-top">
                  <view>
                    <text class="saved-card-label">{{ card.isDefault ? 'Default card' : 'Saved card' }}</text>
                    <text class="saved-card-number">{{ formatMaskedCard(card.cardNumberMasked) }}</text>
                  </view>
                  <view class="status-pill status-card">
                    <text class="status-pill-text">{{ card.expiryDate || 'No expiry' }}</text>
                  </view>
                </view>

                <view class="saved-card-bottom">
                  <view class="saved-card-meta">
                    <text class="detail-label">Cardholder</text>
                    <text class="detail-value">{{ card.cardHolderName || displayName }}</text>
                  </view>
                  <view class="saved-card-meta">
                    <text class="detail-label">Storage</text>
                    <text class="detail-value">Masked by backend and safe for quick booking reuse</text>
                  </view>
                  <button
                    class="ghost-danger-button"
                    :disabled="removingCardId === card.cardId"
                    @tap="handleRemoveCard(card)"
                  >
                    <text>{{ removingCardId === card.cardId ? 'Removing...' : 'Remove' }}</text>
                  </button>
                </view>
              </view>
            </view>

            <view v-else class="state-card">
              <text class="state-title">No saved cards yet</text>
              <text class="state-text">
                Add one below and it will appear here with a masked number such as **** 1234.
              </text>
            </view>

            <view class="form-panel">
              <view class="section-head section-head-compact">
                <view>
                  <text class="section-title">Add a New Card</text>
                  <text class="section-subtitle">Stored securely for quicker bookings next time</text>
                </view>
              </view>

              <view class="editor-grid">
                <view class="form-field">
                  <text class="field-label">Cardholder name</text>
                  <input
                    v-model="cardForm.cardHolderName"
                    class="field-input"
                    placeholder="Name on card"
                  />
                </view>

                <view class="form-field">
                  <text class="field-label">Card number</text>
                  <input
                    :value="cardForm.number"
                    class="field-input"
                    maxlength="19"
                    placeholder="1234 5678 9012 3456"
                    @input="handleCardNumberInput"
                  />
                </view>

                <view class="form-row">
                  <view class="form-field form-field-half">
                    <text class="field-label">Expiry</text>
                    <input
                      :value="cardForm.expiryDate"
                      class="field-input"
                      maxlength="5"
                      placeholder="MM/YY"
                      @input="handleExpiryInput"
                    />
                  </view>

                  <view class="form-field form-field-half">
                    <text class="field-label">CVV</text>
                    <input
                      :value="cardForm.cvv"
                      class="field-input"
                      maxlength="4"
                      password
                      placeholder="123"
                      @input="handleCvvInput"
                    />
                  </view>
                </view>

                <view class="secure-note">
                  <uni-icons type="locked-filled" size="16" color="#0F766E"></uni-icons>
                  <text class="secure-note-text">
                    CVV is validated in the form but is not stored. The backend only keeps a masked card number.
                  </text>
                </view>

                <button class="primary-button" :disabled="savingCard" @tap="saveCard">
                  <text>{{ savingCard ? 'Saving Card...' : 'Save Card' }}</text>
                </button>
              </view>
            </view>
          </view>
        </template>
      </view>
    </view>
  </BaseLayout>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import BaseLayout from '@/pages/BaseLayout.vue'
import {
  addBankCard,
  cancelBooking,
  getBankCards,
  getBookingHistory,
  getProfile,
  getStoredUserId,
  removeBankCard,
  updateProfile
} from '@/api/profile.js'

const historyFilters = [
  { label: 'All', value: 'all' },
  { label: 'Pending', value: 'PENDING' },
  { label: 'Active', value: 'ACTIVE' },
  { label: 'Completed', value: 'COMPLETED' },
  { label: 'Cancelled', value: 'CANCELLED' }
]

const userId = ref(getStoredUserId())
const pageLoading = ref(false)
const profileLoading = ref(false)
const historyLoading = ref(false)
const cardsLoading = ref(false)
const savingProfile = ref(false)
const savingCard = ref(false)
const cancellingBookingId = ref(null)
const removingCardId = ref(null)
const lastSyncedAt = ref('')

const activeTab = ref('history')
const historyFilter = ref('all')
const editingProfile = ref(false)

const userInfo = ref({
  userId: '',
  username: '',
  name: '',
  email: '',
  phone: '',
  city: '',
  avatar: '',
  createdAt: ''
})

const editForm = ref({
  name: '',
  email: '',
  phone: '',
  city: ''
})

const bookings = ref([])
const cards = ref([])
const cardForm = ref({
  cardHolderName: '',
  number: '',
  expiryDate: '',
  cvv: ''
})

const hasUserId = computed(() => Boolean(userId.value))
const displayName = computed(() => userInfo.value.name || userInfo.value.username || 'ScooterGo Rider')
const userInitial = computed(() => displayName.value.charAt(0).toUpperCase())
const memberSince = computed(() => {
  const date = toDate(userInfo.value.createdAt)
  return date ? String(date.getFullYear()) : '2026'
})

const activeBooking = computed(() => bookings.value.find(item => item.status === 'ACTIVE') || null)
const latestBooking = computed(() => bookings.value[0] || null)
const completedRideCount = computed(() => bookings.value.filter(item => item.status === 'COMPLETED').length)
const pendingRideCount = computed(() => bookings.value.filter(item => ['PENDING', 'ACTIVE'].includes(item.status)).length)
const totalSpent = computed(() => (
  bookings.value
    .filter(item => item.status === 'COMPLETED')
    .reduce((sum, item) => sum + Number(item.totalCost || 0), 0)
))

const filteredBookings = computed(() => {
  if (historyFilter.value === 'all') return bookings.value
  return bookings.value.filter(item => item.status === historyFilter.value)
})

const heroNoticeTitle = computed(() => {
  if (activeBooking.value) return `Booking #${activeBooking.value.bookingId} is active`
  if (latestBooking.value) return `Latest booking #${latestBooking.value.bookingId} is synced`
  return 'Your profile is ready to sync live booking data'
})

const heroNoticeText = computed(() => {
  if (activeBooking.value) {
    return `Scooter #${activeBooking.value.scooterId || '--'} is in progress. Open My Ride to track the current reservation.`
  }

  if (latestBooking.value) {
    return `The most recent order is ${formatStatusLabel(latestBooking.value.status)} and was scheduled for ${formatDateTime(latestBooking.value.startTime)}.`
  }

  return 'As soon as you create a booking, your order history and saved cards will show up here automatically.'
})

const lastSyncedLabel = computed(() => {
  if (!lastSyncedAt.value) return 'Waiting for first sync'
  return `Last synced ${formatClock(lastSyncedAt.value)}`
})

const toDate = (value) => {
  if (!value) return null

  if (value instanceof Date) {
    return Number.isNaN(value.getTime()) ? null : value
  }

  if (Array.isArray(value)) {
    const [year, month, day, hour = 0, minute = 0, second = 0] = value
    const date = new Date(Number(year), Number(month) - 1, Number(day), Number(hour), Number(minute), Number(second))
    return Number.isNaN(date.getTime()) ? null : date
  }

  const normalized = String(value).trim().replace(' ', 'T')
  const date = new Date(normalized)
  return Number.isNaN(date.getTime()) ? null : date
}

const normalizeBooking = (booking = {}) => ({
  bookingId: booking.bookingId ?? booking.id ?? '',
  userId: booking.userId ?? '',
  scooterId: booking.scooterId ?? '',
  scooterModel: booking.scooterModel || 'Scooter',
  startTime: booking.startTime || '',
  endTime: booking.endTime || '',
  totalCost: Number(booking.totalCost || 0),
  durationMinutes: Number(booking.durationMinutes || 0),
  status: String(booking.status || booking.bookingStatus || '').trim().toUpperCase(),
  createdAt: booking.createdAt || booking.startTime || ''
})

const normalizeCard = (card = {}) => {
  const masked = String(card.cardNumberMasked || '').trim()
  const digits = masked.replace(/\D/g, '')

  return {
    cardId: card.cardId ?? card.id ?? '',
    userId: card.userId ?? '',
    cardHolderName: card.cardHolderName || '',
    cardNumberMasked: masked || (digits ? `**** ${digits.slice(-4)}` : '**** ----'),
    expiryDate: card.expiryDate || '',
    isDefault: Boolean(card.isDefault)
  }
}

const formatDateTime = (value) => {
  const date = toDate(value)
  if (!date) return 'Not scheduled yet'

  return date.toLocaleString('en-GB', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDateShort = (value) => {
  const date = toDate(value)
  if (!date) return 'Not available'

  return date.toLocaleDateString('en-GB', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const formatClock = (value) => {
  const date = toDate(value)
  if (!date) return 'just now'

  return date.toLocaleTimeString('en-GB', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatMoney = (value) => `£${Number(value || 0).toFixed(2)}`

const formatStatusLabel = (status) => {
  const normalized = String(status || '').toUpperCase()
  return normalized.charAt(0) + normalized.slice(1).toLowerCase()
}

const formatMaskedCard = (value) => {
  const raw = String(value || '').trim()
  if (raw.includes('*')) return raw

  const digits = raw.replace(/\D/g, '')
  return digits ? `**** ${digits.slice(-4)}` : '**** ----'
}

const statusClass = (status) => `status-${String(status || '').toLowerCase()}`

const getBookingWindowLabel = (booking) => {
  const start = formatDateTime(booking.startTime)

  if (booking.endTime) {
    return `${start} | ${formatDateTime(booking.endTime)}`
  }

  if (booking.durationMinutes) {
    return `${start} | ${booking.durationMinutes} min reserved`
  }

  return start
}

const canCancelBooking = (booking) => booking.status === 'PENDING'

const updateLocalUserCache = (data) => {
  try {
    uni.setStorageSync('userInfo', JSON.stringify(data))
  } catch (error) {
    console.error('Failed to update local user cache:', error)
  }
}

const seedEditForm = () => {
  editForm.value = {
    name: userInfo.value.name || userInfo.value.username || '',
    email: userInfo.value.email || '',
    phone: userInfo.value.phone || '',
    city: userInfo.value.city || ''
  }
}

const loadProfile = async () => {
  if (!hasUserId.value) return

  profileLoading.value = true
  try {
    const cached = uni.getStorageSync('userInfo')
    const cachedUser = typeof cached === 'string' ? JSON.parse(cached || '{}') : (cached || {})
    const data = await getProfile(userId.value)

    userInfo.value = {
      userId: data?.userId || userId.value,
      username: data?.username || cachedUser.username || '',
      name: data?.name || data?.username || cachedUser.name || cachedUser.username || '',
      email: data?.email || cachedUser.email || '',
      phone: data?.phone || cachedUser.phone || '',
      city: data?.city || cachedUser.city || '',
      avatar: cachedUser.avatar || cachedUser.avatarUrl || '',
      createdAt: data?.createdAt || cachedUser.createdAt || ''
    }

    updateLocalUserCache({
      ...(cachedUser || {}),
      ...userInfo.value
    })
    seedEditForm()
  } catch (error) {
    console.error('Failed to load profile:', error)
  } finally {
    profileLoading.value = false
  }
}

const loadBookings = async () => {
  if (!hasUserId.value) return

  historyLoading.value = true
  try {
    const history = await getBookingHistory(userId.value)
    bookings.value = (Array.isArray(history) ? history : [])
      .map(normalizeBooking)
      .sort((left, right) => {
        const leftTime = toDate(left.createdAt || left.startTime)?.getTime() || 0
        const rightTime = toDate(right.createdAt || right.startTime)?.getTime() || 0
        return rightTime - leftTime
      })
  } catch (error) {
    console.error('Failed to load booking history:', error)
  } finally {
    historyLoading.value = false
  }
}

const loadCards = async () => {
  if (!hasUserId.value) return

  cardsLoading.value = true
  try {
    const nextCards = await getBankCards(userId.value)
    cards.value = nextCards.map(normalizeCard)
  } catch (error) {
    console.error('Failed to load bank cards:', error)
  } finally {
    cardsLoading.value = false
  }
}

const refreshData = async () => {
  userId.value = getStoredUserId()
  if (!userId.value) return

  pageLoading.value = true
  try {
    await Promise.allSettled([
      loadProfile(),
      loadBookings(),
      loadCards()
    ])
    lastSyncedAt.value = new Date().toISOString()
  } finally {
    pageLoading.value = false
  }
}

const toggleProfileEdit = () => {
  if (!editingProfile.value) {
    seedEditForm()
  }
  editingProfile.value = !editingProfile.value
}

const cancelProfileEdit = () => {
  editingProfile.value = false
  seedEditForm()
}

const saveProfile = async () => {
  const email = String(editForm.value.email || '').trim()
  if (!email) {
    uni.showToast({ title: 'Email is required', icon: 'none' })
    return
  }

  savingProfile.value = true
  try {
    await updateProfile({
      userId: userInfo.value.userId || userId.value,
      username: String(editForm.value.name || '').trim(),
      email,
      phone: String(editForm.value.phone || '').trim(),
      city: String(editForm.value.city || '').trim()
    })

    editingProfile.value = false
    await loadProfile()
    uni.showToast({ title: 'Profile updated', icon: 'success' })
  } catch (error) {
    console.error('Failed to save profile:', error)
  } finally {
    savingProfile.value = false
  }
}

const handleCardNumberInput = (event) => {
  const digits = String(event?.detail?.value || '').replace(/\D/g, '').slice(0, 16)
  cardForm.value.number = digits.replace(/(.{4})/g, '$1 ').trim()
}

const handleExpiryInput = (event) => {
  const digits = String(event?.detail?.value || '').replace(/\D/g, '').slice(0, 4)
  cardForm.value.expiryDate = digits.length > 2 ? `${digits.slice(0, 2)}/${digits.slice(2)}` : digits
}

const handleCvvInput = (event) => {
  cardForm.value.cvv = String(event?.detail?.value || '').replace(/\D/g, '').slice(0, 4)
}

const resetCardForm = () => {
  cardForm.value = {
    cardHolderName: '',
    number: '',
    expiryDate: '',
    cvv: ''
  }
}

const saveCard = async () => {
  const cardHolderName = String(cardForm.value.cardHolderName || '').trim()
  const digits = String(cardForm.value.number || '').replace(/\D/g, '')
  const expiry = String(cardForm.value.expiryDate || '').trim()
  const cvv = String(cardForm.value.cvv || '').trim()

  if (!cardHolderName) {
    uni.showToast({ title: 'Enter cardholder name', icon: 'none' })
    return
  }

  if (digits.length !== 16) {
    uni.showToast({ title: 'Enter a 16-digit card', icon: 'none' })
    return
  }

  if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(expiry)) {
    uni.showToast({ title: 'Use MM/YY expiry', icon: 'none' })
    return
  }

  if (!/^\d{3,4}$/.test(cvv)) {
    uni.showToast({ title: 'Enter valid CVV', icon: 'none' })
    return
  }

  savingCard.value = true
  try {
    await addBankCard({
      cardHolderName,
      cardNumberMasked: digits,
      expiryDate: expiry
    }, userId.value)

    resetCardForm()
    await loadCards()
    uni.showToast({ title: 'Card saved', icon: 'success' })
  } catch (error) {
    console.error('Failed to save card:', error)
  } finally {
    savingCard.value = false
  }
}

const confirmCancelBooking = (booking) => {
  uni.showModal({
    title: 'Cancel this booking?',
    content: 'This order has not started yet. Do you want to call the cancel endpoint now?',
    confirmColor: '#DC2626',
    success: async ({ confirm }) => {
      if (!confirm) return

      cancellingBookingId.value = booking.bookingId
      try {
        await cancelBooking(booking.bookingId, userId.value)
        await loadBookings()
        uni.showToast({ title: 'Booking cancelled', icon: 'success' })
      } catch (error) {
        console.error('Failed to cancel booking:', error)
      } finally {
        cancellingBookingId.value = null
      }
    }
  })
}

const handleRemoveCard = (card) => {
  uni.showModal({
    title: 'Remove saved card?',
    content: `Delete ${formatMaskedCard(card.cardNumberMasked)} from your account?`,
    confirmColor: '#DC2626',
    success: async ({ confirm }) => {
      if (!confirm) return

      removingCardId.value = card.cardId
      try {
        await removeBankCard(card.cardId)
        await loadCards()
        uni.showToast({ title: 'Card removed', icon: 'success' })
      } catch (error) {
        console.error('Failed to remove card:', error)
      } finally {
        removingCardId.value = null
      }
    }
  })
}

const goToRidePage = (source = 'trip') => {
  uni.navigateTo({ url: `/pages/active-ride?source=${source}` })
}

const goToFindScooter = () => {
  uni.navigateTo({ url: '/pages/find-scooter' })
}

const goToLogin = () => {
  uni.navigateTo({ url: '/pages/login' })
}

onShow(() => {
  userId.value = getStoredUserId()
  refreshData()
})
</script>

<style scoped>
.profile-page {
  min-height: calc(100vh - 160rpx);
  padding: 32rpx 24rpx 96rpx;
  background:
    radial-gradient(circle at top left, rgba(59, 130, 246, 0.16), transparent 34%),
    linear-gradient(180deg, #f6fbff 0%, #eef6ff 48%, #f8fafc 100%);
}

.profile-shell {
  width: 100%;
  max-width: 1200rpx;
  margin: 0 auto;
}

.hero-panel,
.panel-card {
  position: relative;
  overflow: hidden;
  border-radius: 36rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 24rpx 56rpx rgba(15, 23, 42, 0.08);
}

.hero-panel {
  padding: 36rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #0f3d91 0%, #2563eb 52%, #4f9cf9 100%);
}

.hero-glow {
  position: absolute;
  width: 260rpx;
  height: 260rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.12);
  filter: blur(8rpx);
}

.hero-glow-left {
  top: -80rpx;
  left: -40rpx;
}

.hero-glow-right {
  right: -30rpx;
  bottom: 80rpx;
}

.hero-top,
.metric-grid,
.sync-banner {
  position: relative;
  z-index: 1;
}

.hero-top {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 24rpx;
}

.identity-group {
  display: flex;
  align-items: center;
  gap: 22rpx;
  flex: 1;
  min-width: 0;
}

.avatar-shell {
  width: 118rpx;
  height: 118rpx;
  border-radius: 30rpx;
  background: rgba(255, 255, 255, 0.18);
  border: 2rpx solid rgba(255, 255, 255, 0.24);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.avatar-initial {
  font-size: 44rpx;
  font-weight: 800;
}

.identity-copy {
  min-width: 0;
}

.hero-eyebrow {
  display: block;
  font-size: 22rpx;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.74);
}

.hero-name {
  display: block;
  margin-top: 10rpx;
  font-size: 50rpx;
  line-height: 1.12;
  font-weight: 800;
}

.hero-meta {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.84);
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
  align-items: flex-start;
}

.hero-button {
  min-width: 240rpx;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 28rpx;
}

.metric-card {
  padding: 22rpx;
  border-radius: 26rpx;
  background: rgba(255, 255, 255, 0.14);
  border: 1rpx solid rgba(255, 255, 255, 0.16);
}

.metric-value {
  display: block;
  font-size: 34rpx;
  font-weight: 800;
}

.metric-label {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.8);
}

.sync-banner {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  align-items: center;
  margin-top: 24rpx;
  padding: 22rpx 24rpx;
  border-radius: 28rpx;
  background: rgba(9, 28, 68, 0.28);
}

.sync-copy {
  flex: 1;
  min-width: 0;
}

.sync-title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
}

.sync-text {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.84);
  line-height: 1.55;
}

.sync-stamp {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.74);
}

.panel-card {
  padding: 28rpx;
  background: rgba(255, 255, 255, 0.94);
  border: 1rpx solid rgba(148, 163, 184, 0.18);
}

.section-head {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.section-head-compact {
  margin-bottom: 14rpx;
}

.section-title {
  display: block;
  color: #0f172a;
  font-size: 32rpx;
  font-weight: 800;
}

.section-subtitle {
  display: block;
  margin-top: 8rpx;
  color: #64748b;
  font-size: 22rpx;
  line-height: 1.55;
}

.inline-link-button {
  margin: 0;
  padding: 0;
  border: 0;
  background: transparent;
  color: #2563eb;
  font-size: 24rpx;
  font-weight: 700;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.detail-item,
.history-meta {
  padding: 20rpx;
  border-radius: 24rpx;
  background: #f8fbff;
  border: 1rpx solid rgba(191, 219, 254, 0.85);
}

.detail-label,
.field-label,
.saved-card-label {
  display: block;
  color: #64748b;
  font-size: 20rpx;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.detail-value {
  display: block;
  margin-top: 10rpx;
  color: #0f172a;
  font-size: 26rpx;
  font-weight: 700;
  line-height: 1.45;
}

.editor-grid {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.form-row {
  display: flex;
  gap: 16rpx;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.form-field-half {
  flex: 1;
}

.field-input {
  width: 100%;
  min-height: 92rpx;
  box-sizing: border-box;
  padding: 0 24rpx;
  border-radius: 24rpx;
  border: 1rpx solid #dbeafe;
  background: #f8fbff;
  color: #0f172a;
  font-size: 28rpx;
}

.field-input:focus {
  border-color: #2563eb;
}

.form-actions {
  display: flex;
  gap: 14rpx;
  margin-top: 8rpx;
}

.tab-strip {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.tab-pill {
  flex: 1;
  padding: 22rpx 24rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.78);
  border: 1rpx solid rgba(191, 219, 254, 0.85);
  box-shadow: 0 16rpx 32rpx rgba(15, 23, 42, 0.04);
}

.tab-pill-active {
  background: linear-gradient(135deg, #dbeafe 0%, #eff6ff 100%);
  border-color: #93c5fd;
}

.tab-pill-title {
  display: block;
  color: #0f172a;
  font-size: 28rpx;
  font-weight: 800;
}

.tab-pill-count {
  display: block;
  margin-top: 8rpx;
  color: #2563eb;
  font-size: 22rpx;
  font-weight: 700;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 22rpx;
}

.filter-chip {
  padding: 12rpx 18rpx;
  border-radius: 999rpx;
  background: #eff6ff;
  border: 1rpx solid #bfdbfe;
}

.filter-chip-active {
  background: #2563eb;
  border-color: #2563eb;
}

.filter-chip-text {
  color: #1e3a8a;
  font-size: 22rpx;
  font-weight: 700;
}

.filter-chip-active .filter-chip-text {
  color: #ffffff;
}

.history-list,
.card-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.history-card,
.saved-card,
.form-panel {
  border-radius: 30rpx;
  background: #f8fbff;
  border: 1rpx solid rgba(191, 219, 254, 0.85);
  padding: 22rpx;
}

.history-card-top,
.saved-card-top {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  align-items: flex-start;
}

.history-booking-id {
  display: block;
  color: #0f172a;
  font-size: 30rpx;
  font-weight: 800;
}

.history-booking-time {
  display: block;
  margin-top: 8rpx;
  color: #64748b;
  font-size: 22rpx;
}

.history-right {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
  align-items: flex-end;
}

.history-amount,
.wallet-banner-value,
.saved-card-number {
  color: #0f172a;
  font-size: 28rpx;
  font-weight: 800;
}

.history-meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 18rpx;
}

.history-actions {
  margin-top: 18rpx;
  padding-top: 18rpx;
  border-top: 1rpx solid rgba(191, 219, 254, 0.8);
}

.history-note {
  display: block;
  color: #475569;
  font-size: 22rpx;
  line-height: 1.55;
}

.history-action-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 16rpx;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 52rpx;
  padding: 0 18rpx;
  border-radius: 999rpx;
  background: #e2e8f0;
}

.status-pill-text {
  font-size: 20rpx;
  font-weight: 800;
}

.status-pending {
  background: #fef3c7;
}

.status-pending .status-pill-text {
  color: #92400e;
}

.status-active {
  background: #dbeafe;
}

.status-active .status-pill-text {
  color: #1d4ed8;
}

.status-completed {
  background: #dcfce7;
}

.status-completed .status-pill-text {
  color: #166534;
}

.status-cancelled {
  background: #fee2e2;
}

.status-cancelled .status-pill-text {
  color: #b91c1c;
}

.status-card {
  background: #e0f2fe;
}

.status-card .status-pill-text {
  color: #0c4a6e;
}

.wallet-banner {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  align-items: center;
  margin-bottom: 20rpx;
  padding: 22rpx 24rpx;
  border-radius: 28rpx;
  background: linear-gradient(135deg, #dcfce7 0%, #ecfeff 100%);
  border: 1rpx solid rgba(134, 239, 172, 0.8);
}

.wallet-banner-label {
  display: block;
  color: #0f766e;
  font-size: 20rpx;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.wallet-banner-title {
  display: block;
  margin-top: 8rpx;
  color: #134e4a;
  font-size: 28rpx;
  font-weight: 800;
}

.saved-card-number {
  display: block;
  margin-top: 10rpx;
}

.saved-card-bottom {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: 18rpx;
}

.saved-card-meta {
  min-width: 220rpx;
  flex: 1;
}

.secure-note {
  display: flex;
  gap: 12rpx;
  align-items: flex-start;
  padding: 18rpx 20rpx;
  border-radius: 22rpx;
  background: #ecfeff;
  border: 1rpx solid rgba(153, 246, 228, 0.85);
}

.secure-note-text {
  color: #115e59;
  font-size: 22rpx;
  line-height: 1.55;
}

.state-card {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  align-items: flex-start;
  padding: 32rpx 28rpx;
  border-radius: 30rpx;
  background: #f8fbff;
  border: 1rpx dashed #93c5fd;
}

.auth-card {
  max-width: 760rpx;
  margin: 0 auto;
}

.state-title {
  color: #0f172a;
  font-size: 32rpx;
  font-weight: 800;
}

.state-text {
  color: #64748b;
  font-size: 24rpx;
  line-height: 1.6;
}

.primary-button,
.secondary-button,
.ghost-button,
.danger-button,
.ghost-danger-button {
  min-height: 88rpx;
  margin: 0;
  padding: 0 26rpx;
  border-radius: 24rpx;
  font-size: 26rpx;
  font-weight: 800;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.primary-button {
  background: linear-gradient(135deg, #0f766e 0%, #14b8a6 100%);
  color: #ffffff;
  box-shadow: 0 16rpx 32rpx rgba(20, 184, 166, 0.22);
}

.secondary-button {
  background: linear-gradient(135deg, #2563eb 0%, #60a5fa 100%);
  color: #ffffff;
  box-shadow: 0 16rpx 32rpx rgba(37, 99, 235, 0.2);
}

.ghost-button {
  background: #ffffff;
  color: #1d4ed8;
  border: 1rpx solid #bfdbfe;
}

.danger-button {
  background: linear-gradient(135deg, #dc2626 0%, #ef4444 100%);
  color: #ffffff;
  box-shadow: 0 16rpx 32rpx rgba(239, 68, 68, 0.18);
}

.ghost-danger-button {
  background: #ffffff;
  color: #dc2626;
  border: 1rpx solid #fecaca;
}

.primary-button[disabled],
.secondary-button[disabled],
.ghost-button[disabled],
.danger-button[disabled],
.ghost-danger-button[disabled] {
  opacity: 0.65;
}

button::after {
  border: none;
}

@media (max-width: 900rpx) {
  .hero-top {
    flex-direction: column;
  }

  .hero-actions,
  .form-actions,
  .history-action-row,
  .tab-strip,
  .form-row {
    flex-direction: column;
  }

  .metric-grid,
  .detail-grid,
  .history-meta-grid {
    grid-template-columns: 1fr 1fr;
  }

  .history-card-top,
  .saved-card-top,
  .wallet-banner,
  .sync-banner,
  .saved-card-bottom {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 640rpx) {
  .profile-page {
    padding-left: 20rpx;
    padding-right: 20rpx;
  }

  .hero-panel,
  .panel-card {
    border-radius: 30rpx;
  }

  .metric-grid,
  .detail-grid,
  .history-meta-grid {
    grid-template-columns: 1fr;
  }

  .avatar-shell {
    width: 96rpx;
    height: 96rpx;
  }

  .hero-name {
    font-size: 42rpx;
  }
}
</style>
