<template>
  <BaseLayout nav-type="default" :show-menu="true" :show-footer="false" :content-padding-top="88">
    <!-- Page Wrapper -->
    <view class="page-wrapper">

      <!-- Loading Overlay -->
      <view v-if="loading" class="loading-overlay">
        <view class="loading-card">
          <view class="loading-spinner"></view>
          <text class="loading-text">Finding scooters near you...</text>
        </view>
      </view>

      <!-- Error Banner -->
      <view v-if="errorMsg" class="error-banner">
        <uni-icons type="info" size="18" color="#DC2626"></uni-icons>
        <text class="error-text">{{ errorMsg }}</text>
        <view class="error-retry" @tap="handleManualRefresh">
          <text class="retry-text">Refresh</text>
        </view>
      </view>

      <!-- Map Container -->
      <view class="map-wrapper">

        <!-- Leaflet Map (web-view) -->
        <web-view
          :class="['map-webview', mapInteractionLocked ? 'map-webview-blocked' : '']"
          :src="mapSrc"
          @message="onWebViewMessage"
        />

        <!-- Search Bar -->
        <view class="search-bar-wrapper">
          <view class="search-bar">
            <view class="search-icon" @tap="onSearch">
              <uni-icons type="search" size="20" color="#2563EB"></uni-icons>
            </view>
            <input
              class="search-input"
              v-model="searchQuery"
              placeholder="Search scooter ID, location, or city..."
              confirm-type="search"
              @confirm="onSearch"
            />
            <view v-if="searchQuery" class="search-clear" @tap="clearSearch">
              <uni-icons type="clear" size="18" color="#9CA3AF"></uni-icons>
            </view>
          </view>
        </view>

        <!-- Stats Badge -->
        <view class="stats-badge">
          <view class="stats-dot"></view>
          <text class="stats-text">{{ availableCount }} available nearby</text>
        </view>

        <!-- Map Controls -->
        <view class="map-controls">
          <view class="control-btn refresh-btn" :class="refreshing ? 'control-btn-loading' : ''" @tap="handleManualRefresh">
            <uni-icons :type="refreshing ? 'spinner-cycle' : 'refreshempty'" size="18" color="#0F172A"></uni-icons>
            <text class="control-btn-text">{{ refreshing ? 'Refreshing' : 'Refresh' }}</text>
          </view>
          <view class="control-btn locate-btn" :class="locating ? 'control-btn-loading' : ''" @tap="handleLocateTap">
            <uni-icons type="location-filled" size="20" color="#2563EB"></uni-icons>
            <text class="control-btn-text">{{ locating ? 'Locating' : 'Locate Me' }}</text>
          </view>
        </view>

        <!-- Scooter Popup -->
        <view v-if="selectedScooter" class="scooter-popup" @tap.stop>
          <view class="popup-drag" @tap="closePopup">
            <view class="popup-drag-bar"></view>
          </view>
          <view class="popup-header">
            <view :class="['popup-avatar', statusClass(selectedScooter.status)]">
              <uni-icons type="navigate" size="28" class="popup-scooter-icon"></uni-icons>
            </view>
            <view class="popup-info">
              <text class="popup-id">#{{ selectedScooter.id }} · {{ selectedScooter.model }}</text>
              <view class="popup-status-row">
                <view :class="['status-dot', 'dot-' + statusClass(selectedScooter.status)]"></view>
                <text :class="['popup-status-text', 'text-' + statusClass(selectedScooter.status)]">
                  {{ statusLabel(selectedScooter.status) }}
                </text>
              </view>
            </view>
            <view class="popup-close" @tap="closePopup">
              <uni-icons type="closeempty" size="20" color="#9CA3AF"></uni-icons>
            </view>
          </view>

          <view class="popup-meta-row">
            <view class="meta-chip">
              <uni-icons type="bolt" size="16" class="meta-chip-icon-svg"></uni-icons>
              <text class="meta-chip-val">{{ selectedScooter.batteryLevel }}%</text>
            </view>
            <view class="meta-chip">
              <uni-icons type="wallet" size="16" class="meta-chip-icon-svg"></uni-icons>
              <text class="meta-chip-val">£{{ selectedScooter.basePrice }} + £{{ selectedScooter.pricePerMin }}/min</text>
            </view>
          </view>

          <!-- Ride Button -->
          <button
            v-if="selectedScooter.status === 'AVAILABLE'"
            class="btn-ride"
            :disabled="riding"
            @tap="openBookingOptions(selectedScooter)"
          >
            <text v-if="riding">Starting ride...</text>
            <text v-else>Book Ride</text>
          </button>
          <view v-else class="btn-unavailable">
            <text>{{ selectedScooter.status === 'IN_USE' ? 'Currently In Use' : 'Under Maintenance' }}</text>
          </view>
        </view>

        <!-- Bottom Drawer Handle -->
        <view class="drawer-handle-area" @tap="toggleDrawer">
          <view class="drawer-handle-bar"></view>
          <view class="drawer-header-row">
            <view>
              <text class="drawer-title">Nearby Scooters</text>
              <text class="drawer-subtitle">{{ filteredScooters.length }} scooters found</text>
            </view>
            <view class="filter-toggle" @tap.stop="toggleFilter">
              <view :class="['filter-dot-small', filterAvailable ? 'filter-dot-active' : '']"></view>
              <text :class="['filter-toggle-text', filterAvailable ? 'filter-active' : '']">
                {{ filterAvailable ? 'Available Only' : 'All Scooters' }}
              </text>
            </view>
          </view>
        </view>

      </view>

      <!-- Drawer Overlay -->
      <view v-if="drawerOpen" class="drawer-overlay" @tap="closeDrawer"></view>

      <!-- Bottom Drawer -->
      <view :class="['bottom-drawer', drawerOpen ? 'drawer-open' : 'drawer-closed']">
        <view class="drawer-inner">

          <!-- Drawer Drag Handle -->
          <view class="drawer-drag-area" @tap="toggleDrawer">
            <view class="drawer-drag-bar"></view>
          </view>

          <!-- Drawer Header -->
          <view class="drawer-section-header">
            <view class="drawer-section-title-row">
              <uni-icons type="navigate" size="22" class="drawer-title-icon"></uni-icons>
              <text class="drawer-section-title">Scooter List</text>
            </view>
            <view class="filter-chip" @tap="toggleFilter">
              <view :class="['filter-chip-dot', filterAvailable ? 'chip-dot-active' : '']"></view>
              <text :class="['filter-chip-label', filterAvailable ? 'chip-active' : '']">
                {{ filterAvailable ? 'Available Only' : 'Show All' }}
              </text>
            </view>
          </view>

          <!-- Scooter List -->
          <scroll-view class="scooter-list" scroll-y enable-flex :show-scrollbar="false">
            <view
              v-for="scooter in filteredScooters"
              :key="scooter.id"
              :class="['scooter-card', selectedScooter && selectedScooter.id === scooter.id ? 'card-selected' : '']"
              @tap="selectScooterFromList(scooter)"
            >
              <view :class="['scooter-avatar', 'avatar-' + statusClass(scooter.status)]">
                <uni-icons type="navigate" size="28" class="avatar-icon"></uni-icons>
              </view>
              <view class="card-body">
                <view class="card-top-row">
                  <text class="card-id">#{{ scooter.id }} · {{ scooter.model }}</text>
                  <view :class="['status-badge', 'badge-' + statusClass(scooter.status)]">
                    <view :class="['badge-dot', 'dot-' + statusClass(scooter.status)]"></view>
                    <text class="badge-text">{{ statusLabel(scooter.status) }}</text>
                  </view>
                </view>
                <text class="card-location">{{ scooter.location || 'No location info' }}</text>
                <view class="card-meta-row">
                  <view class="card-meta">
                    <uni-icons type="bolt" size="14" class="card-meta-icon"></uni-icons>
                    <text class="card-meta-text">{{ scooter.batteryLevel }}%</text>
                  </view>
                  <view class="card-meta">
                    <uni-icons type="wallet" size="14" class="card-meta-icon"></uni-icons>
                    <text class="card-meta-text">£{{ scooter.basePrice }}+£{{ scooter.pricePerMin }}/min</text>
                  </view>
                </view>
              </view>
              <view v-if="scooter.status === 'AVAILABLE'" class="ride-btn-small" @tap.stop="openBookingOptions(scooter)">
                <text class="ride-btn-text">Ride</text>
              </view>
            </view>

            <!-- Empty State -->
            <view v-if="filteredScooters.length === 0 && !loading" class="empty-state">
              <view class="empty-icon-wrap">
                <uni-icons type="search" size="40" color="#D1D5DB"></uni-icons>
              </view>
              <text class="empty-title">No scooters found</text>
              <text class="empty-sub">{{ filterAvailable ? 'No available scooters nearby' : 'Pull to refresh' }}</text>
              <view v-if="filterAvailable" class="btn-outline" @tap="filterAvailable = false; syncFilter()">
                Show All Scooters
              </view>
            </view>
          </scroll-view>

        </view>
      </view>

    </view>
    <BookingOptions
      :visible="bookingOptionsVisible"
      :scooter="bookingScooter"
      :submitting="riding"
      @close="closeBookingOptions"
      @confirm="confirmRideStart"
    />
  </BaseLayout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import BaseLayout from '@/pages/BaseLayout.vue'
import BookingOptions from '@/components/BookingOptions.vue'
import { getAllScooters, startRide as startRideApi } from '@/api/scooter.js'
import { getUserBookings } from '@/api/booking.js'
import { findActiveRide, getStoredActiveRide, setStoredActiveRide } from '@/utils/activeRide.js'

/**
 * Reactive state variables
 */
const scooters        = ref([])
const loading         = ref(false)
const errorMsg        = ref('')
const searchQuery     = ref('')
const filterAvailable = ref(false)
const drawerOpen      = ref(false)
const selectedScooter = ref(null)
const riding          = ref(false)
const mapReady        = ref(false)
const locating        = ref(false)
const refreshing      = ref(false)
const bookingOptionsVisible = ref(false)
const bookingScooter  = ref(null)
const currentUserLocation = ref(null)
const hasAttemptedInitialLocate = ref(false)

const locationAliases = [
  { label: 'Shanghai', names: ['shanghai', 'sh', '\u4e0a\u6d77'], lat: 31.2304, lng: 121.4737, zoom: 13, radiusKm: 18 },
  { label: 'Beijing', names: ['beijing', 'bj', '\u5317\u4eac'], lat: 39.9042, lng: 116.4074, zoom: 12, radiusKm: 20 },
  { label: 'Shenzhen', names: ['shenzhen', 'sz', '\u6df1\u5733'], lat: 22.5431, lng: 114.0579, zoom: 13, radiusKm: 18 },
  { label: 'Guangzhou', names: ['guangzhou', 'gz', '\u5e7f\u5dde'], lat: 23.1291, lng: 113.2644, zoom: 13, radiusKm: 18 },
  { label: 'Hangzhou', names: ['hangzhou', 'hz', '\u676d\u5dde'], lat: 30.2741, lng: 120.1551, zoom: 13, radiusKm: 18 },
  { label: 'Chengdu', names: ['chengdu', 'cd', '\u6210\u90fd'], lat: 30.5728, lng: 104.0668, zoom: 13, radiusKm: 18 },
  { label: 'Nanjing', names: ['nanjing', 'nj', '\u5357\u4eac'], lat: 32.0603, lng: 118.7969, zoom: 13, radiusKm: 18 },
  { label: 'Suzhou', names: ['suzhou', '\u82cf\u5dde'], lat: 31.2989, lng: 120.5853, zoom: 13, radiusKm: 18 },
  { label: 'Chongqing', names: ['chongqing', 'cq', '\u91cd\u5e86'], lat: 29.4316, lng: 106.9123, zoom: 12, radiusKm: 22 },
  { label: 'Wuhan', names: ['wuhan', 'wh', '\u6b66\u6c49'], lat: 30.5928, lng: 114.3055, zoom: 12, radiusKm: 20 },
  { label: "Xi'an", names: ["xian", "xi'an", '\u897f\u5b89'], lat: 34.3416, lng: 108.9398, zoom: 12, radiusKm: 18 },
  { label: 'Hong Kong', names: ['hongkong', 'hong kong', 'hk', '\u9999\u6e2f'], lat: 22.3193, lng: 114.1694, zoom: 12, radiusKm: 16 },
  { label: 'Singapore', names: ['singapore', 'sg'], lat: 1.3521, lng: 103.8198, zoom: 12, radiusKm: 16 },
  { label: 'Tokyo', names: ['tokyo', '\u4e1c\u4eac'], lat: 35.6762, lng: 139.6503, zoom: 12, radiusKm: 20 },
  { label: 'Seoul', names: ['seoul', '\u9996\u5c14'], lat: 37.5665, lng: 126.9780, zoom: 12, radiusKm: 18 },
  { label: 'Bangkok', names: ['bangkok', '\u66fc\u8c37'], lat: 13.7563, lng: 100.5018, zoom: 12, radiusKm: 18 },
  { label: 'Dubai', names: ['dubai', '\u8fea\u62dc'], lat: 25.2048, lng: 55.2708, zoom: 12, radiusKm: 18 },
  { label: 'London', names: ['london', '\u4f26\u6566'], lat: 51.5072, lng: -0.1276, zoom: 12, radiusKm: 20 },
]

// Leaflet map HTML path — place scooter-map.html inside /static/
const mapSrc = ref('/static/scooter-map.html')

// Auto-refresh timer reference
let refreshTimer = null
let locateRequest = null

// Cached iframe contentWindow — populated on first message from the map
let _mapWindow = null

// Commands queued before the map reports ready
const _pendingMessages = []

/**
 * Computed: number of available scooters
 */
const availableCount = computed(() =>
  scooters.value.filter(s => s.status === 'AVAILABLE').length
)

const mapInteractionLocked = computed(() =>
  drawerOpen.value || Boolean(selectedScooter.value) || bookingOptionsVisible.value
)

const normalizeSearchTerm = (value = '') =>
  String(value ?? '')
    .trim()
    .toLowerCase()
    .replace(/[\s'.-]+/g, '')

const getScooterCoordinates = (scooter) => {
  const lat = Number(scooter?.latitude ?? scooter?.lat)
  const lng = Number(scooter?.longitude ?? scooter?.lng)

  if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
    return null
  }

  return { lat, lng }
}

const resolveSearchLocationAlias = (normalizedQuery) => {
  if (!normalizedQuery) return null

  return locationAliases.find(location =>
    location.names.some(name => normalizeSearchTerm(name).includes(normalizedQuery))
  ) || null
}

const calculateDistanceKm = (from, to) => {
  const earthRadiusKm = 6371
  const toRadians = value => (value * Math.PI) / 180
  const deltaLat = toRadians(to.lat - from.lat)
  const deltaLng = toRadians(to.lng - from.lng)
  const lat1 = toRadians(from.lat)
  const lat2 = toRadians(to.lat)

  const a =
    Math.sin(deltaLat / 2) ** 2 +
    Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLng / 2) ** 2

  return earthRadiusKm * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}

const matchesSearchText = (value, normalizedQuery) =>
  normalizeSearchTerm(value).includes(normalizedQuery)

const matchesScooterQuery = (scooter, normalizedQuery, locationAlias = null) => {
  if (!normalizedQuery) return true

  const directMatch =
    matchesSearchText(scooter?.id, normalizedQuery) ||
    matchesSearchText(scooter?.model, normalizedQuery) ||
    matchesSearchText(scooter?.location, normalizedQuery)

  if (directMatch) return true
  if (!locationAlias) return false

  const coordinates = getScooterCoordinates(scooter)
  if (!coordinates) return false

  return calculateDistanceKm(coordinates, locationAlias) <= (locationAlias.radiusKm || 15)
}

const focusScooter = (scooter, zoom = 17) => {
  if (!scooter) return false

  selectedScooter.value = scooter
  drawerOpen.value = false

  const coordinates = getScooterCoordinates(scooter)
  if (!coordinates) {
    return false
  }

  sendToMap('flyTo', { ...coordinates, zoom })
  return true
}

/**
 * Computed: filtered scooter list based on search query and availability filter
 */
const filteredScooters = computed(() => {
  let list = scooters.value
  if (filterAvailable.value) {
    list = list.filter(s => s.status === 'AVAILABLE')
  }
  const normalizedQuery = normalizeSearchTerm(searchQuery.value)
  if (normalizedQuery) {
    const locationAlias = resolveSearchLocationAlias(normalizedQuery)
    list = list.filter(scooter => matchesScooterQuery(scooter, normalizedQuery, locationAlias))
  }
  return list
})

/**
 * Return CSS class name based on scooter status
 */
const statusClass = (status) => {
  if (status === 'AVAILABLE') return 'available'
  if (status === 'IN_USE')    return 'inuse'
  return 'maintenance'
}

/**
 * Return display label based on scooter status
 */
const statusLabel = (status) => {
  if (status === 'AVAILABLE') return 'Available'
  if (status === 'IN_USE')    return 'In Use'
  return 'Maintenance'
}

/**
 * Load scooter data from backend API
 * GET /api/scooters — returns all scooters (AVAILABLE, IN_USE, MAINTENANCE)
 */
const loadScooters = async ({ showLoadingOverlay = true } = {}) => {
  if (showLoadingOverlay) {
    loading.value = true
  }
  errorMsg.value = ''
  try {
    const data = await getAllScooters()
    scooters.value = Array.isArray(data) ? data : []
    if (mapReady.value) sendToMap('updateScooters', scooters.value)
    return scooters.value
  } catch (e) {
    errorMsg.value = 'Failed to load scooters. Check your connection.'
    throw e
  } finally {
    if (showLoadingOverlay) {
      loading.value = false
    }
  }
}

/**
 * Send a command message to the Leaflet map inside the web-view
 * @param {string} cmd - Command name (updateScooters / locate / filter / flyTo)
 * @param {*} data - Payload to send
 */
const sendToMap = (cmd, data) => {
  // Use cached window; fall back to DOM query before first message arrives
  if (!_mapWindow) {
    const frame =
      document.querySelector('.map-webview iframe') ||
      document.querySelector('uni-web-view iframe') ||
      document.querySelector('iframe')
    _mapWindow = frame?.contentWindow ?? null
  }
  // Map not ready yet — queue for later
  if (!_mapWindow) {
    _pendingMessages.push({ cmd, data })
    return
  }
  try {
    _mapWindow.postMessage(JSON.stringify({ cmd, data }), '*')
  } catch (e) {
    console.warn('[sendToMap] postMessage failed:', e)
  }
}

/**
 * Receive messages posted back from the Leaflet map
 * Handles: mapReady, markerTap, rideTap
 */
const onWebViewMessage = (e) => {
  let payload
  try {
    const raw = e.detail?.data
    payload = typeof raw === 'string' ? JSON.parse(raw) : (Array.isArray(raw) ? raw[0] : raw)
  } catch {
    return
  }
  _handleMapPayload(payload)
}

// Native window "message" listener — H5 platform only
// Captures e.source as the reliable iframe contentWindow reference
const _onWindowMessage = (e) => {
  if (!e.data) return
  let payload
  try {
    payload = typeof e.data === 'string' ? JSON.parse(e.data) : e.data
  } catch {
    return
  }
  if (e.source && !_mapWindow) {
    _mapWindow = e.source
  }
  _handleMapPayload(payload)
}

// Shared logic for handling map payloads from either platform
const _handleMapPayload = ({ type, data } = {}) => {
  if (type === 'mapReady') {
    mapReady.value = true
    // Push current scooter data once map is ready
    if (scooters.value.length) sendToMap('updateScooters', scooters.value)
    // Flush any commands that were queued before the map was ready
    while (_pendingMessages.length) {
      const { cmd, data: d } = _pendingMessages.shift()
      sendToMap(cmd, d)
    }
    if (currentUserLocation.value) {
      sendToMap('locate', currentUserLocation.value)
    } else if (!hasAttemptedInitialLocate.value) {
      requestUserLocation({ showErrorToast: false }).catch(() => {})
    }
  }

  if (type === 'markerTap') {
    selectedScooter.value = data
    drawerOpen.value = false
  }

  if (type === 'rideTap') {
    selectedScooter.value = data
    openBookingOptions(data)
  }
}

/**
 * Get user's current location and fly the map to it
 */
const locateMe = () => {
  uni.getLocation({
    type: 'wgs84',
    success: (res) => {
      sendToMap('locate', { lat: res.latitude, lng: res.longitude })
    },
    fail: () => {
      // Silent fail — map stays at default center (Leeds)
    }
  })
}

const requestUserLocation = ({ showErrorToast = true, toastOnSuccess = false } = {}) => {
  if (locateRequest) {
    return locateRequest
  }

  hasAttemptedInitialLocate.value = true
  locating.value = true

  locateRequest = new Promise((resolve, reject) => {
    uni.getLocation({
      type: 'wgs84',
      success: (res) => {
        const location = {
          lat: res.latitude,
          lng: res.longitude
        }

        currentUserLocation.value = location
        sendToMap('locate', location)

        if (toastOnSuccess) {
          uni.showToast({
            title: 'Location updated',
            icon: 'none'
          })
        }

        resolve(location)
      },
      fail: (error) => {
        if (showErrorToast) {
          uni.showToast({
            title: 'Unable to get your location',
            icon: 'none'
          })
        }

        reject(error)
      },
      complete: () => {
        locating.value = false
        locateRequest = null
      }
    })
  })

  return locateRequest
}

const handleLocateTap = async () => {
  try {
    await requestUserLocation({
      showErrorToast: true,
      toastOnSuccess: true
    })
  } catch {
    // Toast is handled in requestUserLocation
  }
}

const handleManualRefresh = async () => {
  if (refreshing.value) return

  refreshing.value = true

  try {
    const [scootersResult, locationResult] = await Promise.allSettled([
      loadScooters({ showLoadingOverlay: false }),
      requestUserLocation({ showErrorToast: true })
    ])

    if (scootersResult.status === 'fulfilled' && locationResult.status === 'fulfilled') {
      uni.showToast({
        title: 'Map refreshed',
        icon: 'none'
      })
    } else if (scootersResult.status === 'fulfilled') {
      uni.showToast({
        title: 'Scooters refreshed',
        icon: 'none'
      })
    }
  } finally {
    refreshing.value = false
  }
}

/**
 * Toggle available-only filter and sync to map
 */
const toggleFilter = () => {
  filterAvailable.value = !filterAvailable.value
  syncFilter()
}

/**
 * Sync current filter state to the Leaflet map
 */
const syncFilter = () => {
  sendToMap('filter', { filter: filterAvailable.value ? 'AVAILABLE' : 'ALL' })
}

/**
 * Toggle bottom drawer open/closed
 */
const toggleDrawer = () => {
  drawerOpen.value = !drawerOpen.value
  if (drawerOpen.value) selectedScooter.value = null
}

/**
 * Close bottom drawer
 */
const closeDrawer = () => {
  drawerOpen.value = false
}

/**
 * Select a scooter from the list and fly map to its location
 * @param {Object} scooter - Scooter data object
 */
const selectScooterFromList = (scooter) => {
  focusScooter(scooter)
}

/**
 * Read the cached user info and return the current user ID if available.
 */
const getStoredUserId = () => {
  try {
    const cached = uni.getStorageSync('userInfo')
    const userInfo = typeof cached === 'string' ? JSON.parse(cached) : cached
    return userInfo?.userId || userInfo?.id || ''
  } catch {
    return ''
  }
}

const getCurrentActiveRide = async (userId) => {
  const cachedRide = getStoredActiveRide()
  const matchingCachedRide = cachedRide?.userId && String(cachedRide.userId) === String(userId) && String(cachedRide.status || '').toUpperCase() === 'ACTIVE'
    ? cachedRide
    : null

  try {
    const bookings = await getUserBookings(userId)
    const liveRide = findActiveRide(bookings, matchingCachedRide || {})
    if (liveRide) {
      setStoredActiveRide(liveRide)
    }
    return liveRide
  } catch (error) {
    console.error('Failed to check active ride:', error)
    return matchingCachedRide
  }
}

const promptToResumeActiveRide = (activeRide) => {
  const bookingLabel = activeRide?.bookingId ? `Booking #${activeRide.bookingId}` : 'Your current ride'
  uni.showModal({
    title: 'Finish current ride first',
    content: `${bookingLabel} is still active. Please end it before renting another scooter.`,
    confirmText: 'Open My Ride',
    cancelText: 'Stay Here',
    success: ({ confirm }) => {
      if (confirm) {
        uni.navigateTo({ url: '/pages/active-ride?source=trip' })
      }
    }
  })
}

/**
 * Update a scooter status locally so the UI responds immediately.
 * @param {number|string} scooterId - Scooter ID
 * @param {string} nextStatus - New status value
 */
const updateScooterStatus = (scooterId, nextStatus) => {
  scooters.value = scooters.value.map(s =>
    String(s.id) === String(scooterId)
      ? { ...s, status: nextStatus }
      : s
  )

  if (selectedScooter.value && String(selectedScooter.value.id) === String(scooterId)) {
    selectedScooter.value = { ...selectedScooter.value, status: nextStatus }
  }

  if (mapReady.value) {
    sendToMap('updateScooters', scooters.value)
  }
}

/**
 * Open the booking options sheet for an available scooter.
 * @param {Object} scooter - Scooter to book
 */
const openBookingOptions = async (scooter) => {
  if (!scooter || scooter.status !== 'AVAILABLE' || riding.value) return

  const userId = getStoredUserId()
  if (userId) {
    const activeRide = await getCurrentActiveRide(userId)
    if (activeRide) {
      promptToResumeActiveRide(activeRide)
      return
    }
  }

  selectedScooter.value = scooter
  bookingScooter.value = scooter
  bookingOptionsVisible.value = true
  drawerOpen.value = false
}

/**
 * Close the booking options sheet unless a request is still in flight.
 */
const closeBookingOptions = () => {
  if (riding.value) return
  bookingOptionsVisible.value = false
  bookingScooter.value = null
}

/**
 * Confirm the booking after the simulated payment step.
 * @param {Object} paymentData - Selected plan and simulated payment details
 */
const confirmRideStart = async (paymentData) => {
  if (!bookingScooter.value) return

  const userId = getStoredUserId()
  if (!userId) {
    bookingOptionsVisible.value = false
    bookingScooter.value = null
    uni.showToast({ title: 'Please login first', icon: 'none' })
    setTimeout(() => uni.reLaunch({ url: '/pages/login' }), 1500)
    return
  }

  const activeRide = await getCurrentActiveRide(userId)
  if (activeRide) {
    bookingOptionsVisible.value = false
    bookingScooter.value = null
    promptToResumeActiveRide(activeRide)
    return
  }

  riding.value = true
  try {
    const booking = await startRideApi({
      userId,
      scooterId: bookingScooter.value.id,
      planType: paymentData.planType
    })

    updateScooterStatus(bookingScooter.value.id, 'IN_USE')
    setStoredActiveRide({
      ...booking,
      bookingId: booking?.bookingId,
      userId,
      scooterId: bookingScooter.value.id,
      scooterModel: bookingScooter.value.model,
      startTime: new Date().toISOString(),
      durationMinutes: booking?.durationMinutes ?? paymentData.durationMinutes,
      totalCost: booking?.estimatedCost ?? paymentData.totalPrice,
      basePrice: bookingScooter.value.basePrice,
      pricePerMinute: bookingScooter.value.pricePerMin,
      cardLast4: paymentData.cardLast4,
      status: booking?.bookingStatus ?? 'ACTIVE'
    })
    bookingOptionsVisible.value = false
    bookingScooter.value = null
    selectedScooter.value = null

    const estimatedCost = Number(booking?.estimatedCost ?? paymentData.totalPrice ?? 0).toFixed(2)
    const bookingLabel = booking?.bookingId ? `Booking #${booking.bookingId}` : 'Your booking'
    uni.showModal({
      title: 'Ride started',
      content: `${bookingLabel} is now active. Reserved total: £${estimatedCost}.`,
      showCancel: false
    })

    setTimeout(() => {
      loadScooters().catch(() => {})
    }, 1200)
  } catch (e) {
    console.error('Failed to start ride:', e)
  } finally {
    riding.value = false
  }
}

/**
 * Start a ride for the selected scooter
 * Retrieves userId from storage and calls the booking start API
 * @param {Object} scooter - Scooter to ride
 */
const startRide = async (scooter) => {
  if (scooter.status !== 'AVAILABLE') return

  // Get userId from local storage
  let userId
  try {
    const userInfo = uni.getStorageSync('userInfo')
    userId = userInfo?.userId
  } catch { }

  if (!userId) {
    uni.showToast({ title: 'Please login first', icon: 'none' })
    setTimeout(() => uni.reLaunch({ url: '/pages/login' }), 1500)
    return
  }

  const activeRide = await getCurrentActiveRide(userId)
  if (activeRide) {
    promptToResumeActiveRide(activeRide)
    return
  }

  riding.value = true
  try {
    await startRideApi(userId, scooter.id)
    uni.showToast({ title: 'Ride started! Enjoy 🛴', icon: 'success', duration: 2000 })
    selectedScooter.value = null
    // Refresh scooter list so status updates on the map
    setTimeout(() => {
      loadScooters().catch(() => {})
    }, 2000)
  } catch (e) {
    // Error toast is handled by request.js
  } finally {
    riding.value = false
  }
}

/**
 * Search scooters or supported city aliases and move the map accordingly.
 */
const onSearch = () => {
  const normalizedQuery = normalizeSearchTerm(searchQuery.value)
  if (!normalizedQuery) {
    selectedScooter.value = null
    return
  }

  const matches = filteredScooters.value
  if (matches.length) {
    focusScooter(matches[0], matches.length > 1 ? 15 : 17)
    if (matches.length > 1) {
      uni.showToast({
        title: `${matches.length} scooters found`,
        icon: 'none'
      })
    }
    return
  }

  const locationAlias = resolveSearchLocationAlias(normalizedQuery)
  if (locationAlias) {
    selectedScooter.value = null
    drawerOpen.value = false
    sendToMap('flyTo', {
      lat: locationAlias.lat,
      lng: locationAlias.lng,
      zoom: locationAlias.zoom || 13
    })
    uni.showToast({
      title: `Moved to ${locationAlias.label}`,
      icon: 'none'
    })
    return
  }

  uni.showToast({
    title: 'No scooter or city found',
    icon: 'none'
  })
}

/**
 * Clear search input
 */
const clearSearch = () => {
  searchQuery.value = ''
  selectedScooter.value = null
}

/**
 * Close the selected scooter popup
 */
const closePopup = () => {
  selectedScooter.value = null
}

/**
 * Component mounted lifecycle hook
 * Load scooter data and start auto-refresh timer
 */
onMounted(() => {
  // Register native message listener for H5 platform (browser iframe)
  if (typeof window !== 'undefined') {
    window.addEventListener('message', _onWindowMessage)
  }
  loadScooters().catch(() => {})
  requestUserLocation({ showErrorToast: false }).catch(() => {})
  refreshTimer = setInterval(() => {
    loadScooters({ showLoadingOverlay: false }).catch(() => {})
  }, 30000)
})

/**
 * Component unmounted lifecycle hook
 * Clear auto-refresh timer
 */
onUnmounted(() => {
  if (typeof window !== 'undefined') {
    window.removeEventListener('message', _onWindowMessage)
  }
  if (refreshTimer) clearInterval(refreshTimer)
  _mapWindow = null
})
</script>

<style scoped>
/* ========== Page Wrapper ========== */
.page-wrapper {
  position: relative;
  width: 100%;
  height: calc(100vh - 88rpx);
  overflow: hidden;
}

/* ========== Loading Overlay ========== */
.loading-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 500;
}

.loading-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 48rpx 60rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24rpx;
  box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.12);
}

.loading-spinner {
  width: 64rpx;
  height: 64rpx;
  border: 6rpx solid #E5E7EB;
  border-top-color: #2563EB;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 28rpx;
  color: #6B7280;
  font-weight: 500;
}

/* ========== Error Banner ========== */
.error-banner {
  position: absolute;
  top: 24rpx;
  left: 24rpx;
  right: 24rpx;
  background: #FFF1F2;
  border: 1.5rpx solid #FECDD3;
  border-radius: 20rpx;
  padding: 20rpx 28rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  z-index: 400;
}

.error-text {
  flex: 1;
  font-size: 26rpx;
  color: #DC2626;
}

.error-retry {
  padding: 8rpx 20rpx;
  background: #DC2626;
  border-radius: 50rpx;
}

.retry-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: 600;
}

/* ========== Map Container ========== */
.map-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.map-webview {
  width: 100%;
  height: 100%;
}

.map-webview-blocked {
  pointer-events: none;
}

/* ========== Search Bar ========== */
.search-bar-wrapper {
  position: absolute;
  top: 24rpx;
  left: 24rpx;
  right: 24rpx;
  z-index: 100;
}

.search-bar {
  background: rgba(255, 255, 255, 0.97);
  border-radius: 50rpx;
  display: flex;
  align-items: center;
  padding: 0 32rpx;
  height: 96rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.12);
  border: 1.5rpx solid rgba(37, 99, 235, 0.1);
}

.search-icon {
  margin-right: 18rpx;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  font-size: 30rpx;
  color: #1F2937;
  background: transparent;
}

.search-clear {
  padding: 10rpx;
  flex-shrink: 0;
}

/* ========== Stats Badge ========== */
.stats-badge {
  position: absolute;
  left: 24rpx;
  top: 150rpx;
  background: rgba(255, 255, 255, 0.97);
  border-radius: 50rpx;
  padding: 16rpx 24rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.1);
  border: 1.5rpx solid rgba(229, 231, 235, 0.8);
  z-index: 100;
}

.stats-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #16A34A;
  box-shadow: 0 0 0 4rpx rgba(22, 163, 74, 0.2);
  animation: pulse-dot 2s infinite;
}

@keyframes pulse-dot {
  0%, 100% { box-shadow: 0 0 0 4rpx rgba(22, 163, 74, 0.2); }
  50%       { box-shadow: 0 0 0 8rpx rgba(22, 163, 74, 0.1); }
}

.stats-text {
  font-size: 26rpx;
  color: #374151;
  font-weight: 600;
}

/* ========== Map Controls ========== */
.map-controls {
  position: absolute;
  right: 24rpx;
  top: 150rpx;
  z-index: 100;
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  align-items: flex-end;
}

.control-btn {
  min-width: 188rpx;
  height: 84rpx;
  padding: 0 24rpx;
  background: rgba(255, 255, 255, 0.97);
  border-radius: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.control-btn-text {
  font-size: 24rpx;
  font-weight: 700;
  color: #0F172A;
}

.control-btn-loading {
  opacity: 0.72;
}

.refresh-btn {
  border: 1.5rpx solid rgba(15, 23, 42, 0.08);
}

.locate-btn {
  border: 1.5rpx solid rgba(37, 99, 235, 0.15);
}

.locate-btn .control-btn-text {
  color: #2563EB;
}

/* ========== Scooter Popup ========== */
.scooter-popup {
  position: absolute;
  bottom: 180rpx;
  left: 24rpx;
  right: 24rpx;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 32rpx;
  padding: 8rpx 36rpx 36rpx;
  box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.15);
  z-index: 200;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(20rpx);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.popup-drag {
  display: flex;
  justify-content: center;
  padding: 12rpx 0;
}

.popup-drag-bar {
  width: 60rpx;
  height: 8rpx;
  background: #E5E7EB;
  border-radius: 4rpx;
}

.popup-header {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.popup-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.popup-avatar.available {
  background: linear-gradient(135deg, #ECFDF5, #D1FAE5);
}

.popup-avatar.inuse {
  background: linear-gradient(135deg, #FFF1F2, #FFE4E6);
}

.popup-avatar.maintenance {
  background: linear-gradient(135deg, #FFFBEB, #FEF3C7);
}

.popup-scooter-icon {
  color: #2563EB;
}

.popup-info {
  flex: 1;
}

.popup-id {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 8rpx;
}

.popup-status-row {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.popup-status-text {
  font-size: 24rpx;
  font-weight: 600;
}

.popup-close {
  padding: 10rpx;
}

/* Status dot colors */
.status-dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: 50%;
}

.dot-available {
  background: #16A34A;
}

.dot-inuse {
  background: #DC2626;
}

.dot-maintenance {
  background: #D97706;
}

/* Status text colors */
.text-available {
  color: #16A34A;
}

.text-inuse {
  color: #DC2626;
}

.text-maintenance {
  color: #D97706;
}

/* Popup Meta Row */
.popup-meta-row {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.meta-chip {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: #F9FAFB;
  padding: 14rpx 20rpx;
  border-radius: 50rpx;
}

.meta-chip-icon-svg {
  color: #6B7280;
  flex-shrink: 0;
}

.meta-chip-val {
  font-size: 24rpx;
  color: #374151;
  font-weight: 500;
}

/* Ride Button */
.btn-ride {
  width: 100%;
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  color: white;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 48rpx;
  font-size: 32rpx;
  font-weight: 600;
  box-shadow: 0 8rpx 20rpx rgba(37, 99, 235, 0.3);
  border: none;
}

.btn-ride::after {
  border: none;
}

.btn-ride[disabled] {
  opacity: 0.6;
}

.btn-unavailable {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 48rpx;
  background: #F3F4F6;
  text-align: center;
  font-size: 30rpx;
  color: #9CA3AF;
  font-weight: 600;
}

/* ========== Bottom Drawer Handle ========== */
.drawer-handle-area {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 40rpx 40rpx 0 0;
  padding: 20rpx 36rpx 36rpx;
  box-shadow: 0 -8rpx 32rpx rgba(0, 0, 0, 0.1);
  z-index: 150;
  cursor: pointer;
}

.drawer-handle-bar {
  width: 80rpx;
  height: 8rpx;
  background: #D1D5DB;
  border-radius: 4rpx;
  margin: 0 auto 24rpx;
}

.drawer-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.drawer-title {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: #1F2937;
}

.drawer-subtitle {
  font-size: 26rpx;
  color: #6B7280;
}

.filter-toggle {
  display: flex;
  align-items: center;
  gap: 10rpx;
  background: #F3F4F6;
  padding: 14rpx 24rpx;
  border-radius: 50rpx;
  cursor: pointer;
}

.filter-dot-small {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #D1D5DB;
  transition: background 0.2s;
}

.filter-dot-active {
  background: #2563EB;
}

.filter-toggle-text {
  font-size: 24rpx;
  color: #9CA3AF;
  font-weight: 600;
}

.filter-active {
  color: #2563EB;
}

/* ========== Bottom Drawer ========== */
.drawer-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 290;
}

.bottom-drawer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  border-radius: 40rpx 40rpx 0 0;
  z-index: 300;
  transition: transform 0.4s cubic-bezier(0.32, 0.72, 0, 1);
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 -16rpx 48rpx rgba(0, 0, 0, 0.15);
}

.drawer-closed {
  transform: translateY(100%);
}

.drawer-open {
  transform: translateY(0);
}

.drawer-inner {
  display: flex;
  flex-direction: column;
  height: 80vh;
  min-height: 0;
}

.drawer-drag-area {
  padding: 24rpx 0 16rpx;
  display: flex;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
}

.drawer-drag-bar {
  width: 80rpx;
  height: 8rpx;
  background: #D1D5DB;
  border-radius: 4rpx;
}

.drawer-section-header {
  padding: 0 36rpx 28rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1.5rpx solid #F3F4F6;
  flex-shrink: 0;
}

.drawer-section-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #1F2937;
}

.filter-chip {
  display: flex;
  align-items: center;
  gap: 10rpx;
  background: #F3F4F6;
  padding: 16rpx 28rpx;
  border-radius: 50rpx;
  cursor: pointer;
}

.filter-chip-dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: 50%;
  background: #D1D5DB;
  transition: background 0.25s;
}

.chip-dot-active {
  background: #2563EB;
}

.filter-chip-label {
  font-size: 26rpx;
  color: #9CA3AF;
  font-weight: 600;
}

.chip-active {
  color: #2563EB;
}

/* ========== Scooter List ========== */
.scooter-list {
  flex: 1;
  height: 0;
  min-height: 0;
  padding: 16rpx 0;
  overscroll-behavior: contain;
}

.scooter-card {
  display: flex;
  align-items: center;
  padding: 28rpx 36rpx;
  gap: 20rpx;
  border-bottom: 1rpx solid #F9FAFB;
  transition: background 0.2s;
  cursor: pointer;
}

.scooter-card:active,
.card-selected {
  background: #EFF6FF;
}

.scooter-avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-available {
  background: linear-gradient(135deg, #ECFDF5, #D1FAE5);
}

.avatar-inuse {
  background: linear-gradient(135deg, #FFF1F2, #FFE4E6);
}

.avatar-maintenance {
  background: linear-gradient(135deg, #FFFBEB, #FEF3C7);
}

.avatar-icon {
  color: inherit;
}

/* Avatar icon color per status */
.avatar-available .avatar-icon { color: #16A34A; }
.avatar-inuse .avatar-icon { color: #DC2626; }
.avatar-maintenance .avatar-icon { color: #D97706; }

.card-body {
  flex: 1;
  min-width: 0;
}

.card-top-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8rpx;
}

.card-id {
  font-size: 28rpx;
  font-weight: 700;
  color: #1F2937;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 6rpx 16rpx;
  border-radius: 50rpx;
}

.badge-available {
  background: #ECFDF5;
}

.badge-inuse {
  background: #FFF1F2;
}

.badge-maintenance {
  background: #FFFBEB;
}

.badge-dot {
  width: 10rpx;
  height: 10rpx;
  border-radius: 50%;
}

.badge-text {
  font-size: 22rpx;
  font-weight: 600;
}

.badge-available .badge-text {
  color: #16A34A;
}

.badge-inuse .badge-text {
  color: #DC2626;
}

.badge-maintenance .badge-text {
  color: #D97706;
}

.card-location {
  font-size: 24rpx;
  color: #6B7280;
  margin-bottom: 10rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-meta-row {
  display: flex;
  gap: 20rpx;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 6rpx;
  color: #9CA3AF;
}

.card-meta-icon {
  color: #9CA3AF;
  flex-shrink: 0;
}

.card-meta-text {
  font-size: 22rpx;
  color: #9CA3AF;
}

.ride-btn-small {
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  border-radius: 18rpx;
  padding: 18rpx 28rpx;
  flex-shrink: 0;
  box-shadow: 0 4rpx 12rpx rgba(37, 99, 235, 0.3);
}

.ride-btn-text {
  font-size: 26rpx;
  font-weight: 700;
  color: #fff;
}

/* ========== Empty State ========== */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 60rpx;
  gap: 16rpx;
}

.empty-icon-wrap {
  width: 120rpx;
  height: 120rpx;
  background: #F3F4F6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8rpx;
}

/* Drawer title with icon */
.drawer-section-title-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.drawer-title-icon {
  color: #2563EB;
}

.empty-title {
  font-size: 34rpx;
  font-weight: 700;
  color: #1F2937;
}

.empty-sub {
  font-size: 28rpx;
  color: #9CA3AF;
  text-align: center;
}

.btn-outline {
  margin-top: 20rpx;
  border: 2rpx solid #2563EB;
  color: #2563EB;
  font-size: 28rpx;
  font-weight: 600;
  padding: 20rpx 44rpx;
  border-radius: 50rpx;
  cursor: pointer;
}
</style>
