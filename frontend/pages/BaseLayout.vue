<template>
  <view class="base-layout">
    <!-- Navigation Bar -->
    <view class="navbar" :style="{ paddingTop: statusBarHeight + 'px' }" id="main-navbar">
      <view class="nav-container">
        <!-- Left: Logo -->
        <view class="logo-container" @tap="goToHome">
          <view class="logo-icon">
            <svg fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M12.08,19H7a1,1,0,0,1,0-2h5.08a7,7,0,0,1,5.86-5.91,1,1,0,0,1,.3,2,5,5,0,0,0-4.19,4.22A2,2,0,0,1,12.08,19Z" style="fill: #2563EB;"></path>
              <path d="M19.56,15.06,18,5h2a1,1,0,0,0,0-2H18a2,2,0,0,0-2,2.3l1.55,10.07a3,3,0,1,0,2-.31ZM19,19a1,1,0,1,1,1-1A1,1,0,0,1,19,19ZM5,15a3,3,0,1,0,3,3A3,3,0,0,0,5,15Zm0,4a1,1,0,1,1,1-1A1,1,0,0,1,5,19Z" style="fill: #111827;"></path>
            </svg>
          </view>
          <text class="logo-text">ScooterGo</text>
        </view>

        <!-- Center: Quick Nav Links -->
        <view class="nav-menu">
          <!-- Find a Scooter: map pin icon -->
          <view class="menu-item-wrap" :class="{ active: currentPage === 'find-scooter' }" @tap="goToFindScooter">
            <svg class="menu-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/>
              <circle cx="12" cy="9" r="2.5" stroke="currentColor" stroke-width="1.8"/>
            </svg>
            <text class="menu-label">Find a Scooter</text>
            <view class="menu-indicator"></view>
          </view>

          <!-- Book & Details: clipboard / tag icon -->
          <view class="menu-item-wrap" :class="{ active: currentPage === 'booking' }" @tap="goToBooking">
            <svg class="menu-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="4" y="4" width="16" height="16" rx="2" stroke="currentColor" stroke-width="1.8"/>
              <path d="M8 10h8M8 14h5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
              <path d="M8 6V3M16 6V3" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            </svg>
            <text class="menu-label">Book & Details</text>
            <view class="menu-indicator"></view>
          </view>

          <!-- My Ride: steering / ride control icon -->
          <view class="menu-item-wrap" :class="{ active: currentPage === 'trip' }" @tap="goToTrip">
            <svg class="menu-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.8"/>
              <path d="M12 3v4M12 17v4M3 12h4M17 12h4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
              <circle cx="12" cy="12" r="2.5" stroke="currentColor" stroke-width="1.8"/>
            </svg>
            <text class="menu-label">My Ride</text>
            <view class="menu-indicator"></view>
          </view>

          <view
            v-if="canAccessDashboard"
            class="menu-item-wrap"
            :class="{ active: currentPage === 'admin-dashboard' }"
            @tap="goToDashboard"
          >
            <svg class="menu-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 5.5h16M4 12h16M4 18.5h16" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
              <path d="M6 4v16M12 8v12M18 10v10" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            </svg>
            <text class="menu-label">Dashboard</text>
            <view class="menu-indicator"></view>
          </view>
        </view>

        <!-- Right: Auth area -->
        <view class="nav-right">
          <!-- Not logged in: show Login button -->
          <button v-if="!isLoggedIn" class="nav-pill-btn" @tap="goToLogin">
            <text class="nav-pill-btn-text">Log In</text>
          </button>

          <!-- Logged in: show user avatar -->
          <view v-else class="user-avatar-wrap" @tap="goToProfile">
            <image
              v-if="userAvatar"
              :src="userAvatar"
              class="user-avatar"
              mode="aspectFill"
            />
            <view v-else class="user-avatar-placeholder">
              <text class="user-avatar-initial">{{ userInitial }}</text>
            </view>
            <view class="avatar-online-dot"></view>
          </view>
        </view>
      </view>
    </view>

    <!-- Slot for page content -->
    <view
      :class="['content-wrapper', { 'content-with-mobile-nav': showMobileBottomNav }]"
      :style="{ paddingTop: actualNavbarHeight + 'px' }"
    >
      <slot></slot>
    </view>

    <!-- Compact Footer -->
    <view v-if="showFooter" class="footer">
      <view class="footer-content">
        <!-- Footer Top Section -->
        <view class="footer-top">
          <view class="footer-column">
            <view class="footer-brand">
              <view class="footer-logo">
                <view class="footer-logo-icon">
                  <svg fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12.08,19H7a1,1,0,0,1,0-2h5.08a7,7,0,0,1,5.86-5.91,1,1,0,0,1,.3,2,5,5,0,0,0-4.19,4.22A2,2,0,0,1,12.08,19Z" style="fill: #93C5FD;"></path>
                    <path d="M19.56,15.06,18,5h2a1,1,0,0,0,0-2H18a2,2,0,0,0-2,2.3l1.55,10.07a3,3,0,1,0,2-.31ZM19,19a1,1,0,1,1,1-1A1,1,0,0,1,19,19ZM5,15a3,3,0,1,0,3,3A3,3,0,0,0,5,15Zm0,4a1,1,0,1,1,1-1A1,1,0,0,1,5,19Z" style="fill: #FFFFFF;"></path>
                  </svg>
                </view>
                <text class="footer-logo-text">ScooterGo</text>
              </view>
              <text class="footer-tagline">Ride Smart, Ride Green</text>
              <text class="footer-description">
                Your trusted electric scooter rental service. Eco-friendly transportation solutions for modern cities.
              </text>
            </view>
          </view>

          <view class="footer-column footer-services-col">
            <text class="footer-heading">Services</text>
            <view class="footer-services-grid">
              <view class="footer-service-item" @tap="goToFooterService('rent')">
                <text class="footer-service-text">Rent a Scooter</text>
              </view>
              <view class="footer-service-item" @tap="goToFooterService('pricing')">
                <text class="footer-service-text">Pricing Plans</text>
              </view>
              <view class="footer-service-item" @tap="goToFooterService('locations')">
                <text class="footer-service-text">Ride Locations</text>
              </view>
              <view class="footer-service-item" @tap="goToFooterService('business-plan')">
                <text class="footer-service-text">Business Solutions</text>
              </view>
            </view>
          </view>

          <view class="footer-column footer-contact-col">
            <text class="footer-heading">Contact</text>
            <view class="footer-contact-list">
              <view class="footer-contact-item">
                <view class="footer-contact-icon">
                  <uni-icons type="email" size="18" color="#2563EB"></uni-icons>
                </view>
                <text class="footer-contact footer-contact-stack">Support via in-app help</text>
              </view>
              <view class="footer-contact-item">
                <view class="footer-contact-icon">
                  <uni-icons type="phone" size="18" color="#2563EB"></uni-icons>
                </view>
                <text class="footer-contact footer-contact-stack">Service hours 09:00 - 18:00</text>
              </view>
            </view>
          </view>
        </view>

        <!-- Footer Bottom Section -->
        <view class="footer-bottom">
          <view class="footer-bottom-left">
            <text class="copyright">© 2026 ScooterGo Ltd. All rights reserved.</text>
          </view>
          <view class="footer-bottom-right">
            <text class="footer-legal" @tap="openLegalNotice('Privacy Policy')">Privacy Policy</text>
            <text class="footer-divider">•</text>
            <text class="footer-legal" @tap="openLegalNotice('Terms of Service')">Terms of Service</text>
            <text class="footer-divider">•</text>
            <text class="footer-legal" @tap="openLegalNotice('Cookie Policy')">Cookie Policy</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- Simple Footer for pages without full footer -->
    <view v-else class="footer-simple"></view>

    <view v-if="showMobileBottomNav" class="mobile-bottom-nav">
      <view
        v-for="item in mobileNavItems"
        :key="item.id"
        :class="['mobile-nav-item', activeMobileNavPage === item.id ? 'mobile-nav-item-active' : '']"
        @tap="item.action"
      >
        <view class="mobile-nav-icon">
          <svg v-if="item.id === 'home'" viewBox="0 0 24 24" fill="none">
            <path d="M3 10.5L12 3l9 7.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M5.5 9.5V20h13V9.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <svg v-else-if="item.id === 'find-scooter'" viewBox="0 0 24 24" fill="none">
            <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/>
            <circle cx="12" cy="9" r="2.5" stroke="currentColor" stroke-width="1.8"/>
          </svg>
          <svg v-else-if="item.id === 'ride'" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.8"/>
            <path d="M12 3v4M12 17v4M3 12h4M17 12h4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            <circle cx="12" cy="12" r="2.5" stroke="currentColor" stroke-width="1.8"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="1.8"/>
            <path d="M4 20c1.8-3.5 4.8-5 8-5s6.2 1.5 8 5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
        </view>
        <text class="mobile-nav-label">{{ item.label }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  navType: {
    type: String,
    default: 'default' // kept for backward compat
  },
  showMenu: {
    type: Boolean,
    default: true
  },
  showFooter: {
    type: Boolean,
    default: true
  },
  contentPaddingTop: {
    type: Number,
    default: 168
  },
  currentPage: {
    type: String,
    default: '' // 'find-scooter' | 'booking' | 'trip'
  }
})

const statusBarHeight = ref(0)
const navbarHeight = ref(88)
const actualNavbarHeight = ref(88)

// Auth state - read from storage
const isLoggedIn = ref(false)
const userAvatar = ref('')
const userName = ref('')
const userRole = ref('')

const userInitial = computed(() => {
  if (userName.value) return userName.value.charAt(0).toUpperCase()
  return 'U'
})

const canAccessDashboard = computed(() =>
  ['ADMIN', 'MANAGER'].includes(String(userRole.value || '').toUpperCase())
)

const activeMobileNavPage = computed(() => {
  if (props.currentPage === 'booking' || props.currentPage === 'trip') return 'ride'
  return props.currentPage
})

const showMobileBottomNav = computed(() =>
  props.showMenu && ['home', 'find-scooter', 'booking', 'trip', 'profile'].includes(props.currentPage)
)

/**
 * Read auth state from storage and update reactive refs.
 * Called on mount and whenever a login-success / logout event is received.
 */
const refreshAuthState = () => {
  try {
    const token = uni.getStorageSync('token') || uni.getStorageSync('userToken')
    const user = uni.getStorageSync('userInfo')
    if (token) {
      isLoggedIn.value = true
      if (user) {
        const userObj = typeof user === 'string' ? JSON.parse(user) : user
        userAvatar.value = userObj.avatar || userObj.avatarUrl || ''
        userName.value = userObj.name || userObj.nickname || userObj.username || ''
        userRole.value = userObj.role || ''
      }
    } else {
      isLoggedIn.value = false
      userAvatar.value = ''
      userName.value = ''
      userRole.value = ''
    }
  } catch (e) {
    isLoggedIn.value = false
    userRole.value = ''
  }
}

onMounted(() => {
  const info = uni.getSystemInfoSync()
  statusBarHeight.value = info.statusBarHeight || 0
  navbarHeight.value = statusBarHeight.value + 50

  // Measure actual rendered navbar height to avoid gap between navbar and content
  uni.createSelectorQuery()
    .select('#main-navbar')
    .boundingClientRect((rect) => {
      if (rect && rect.height) {
        actualNavbarHeight.value = rect.height
      } else {
        actualNavbarHeight.value = navbarHeight.value
      }
    })
    .exec()

  // Check login state on mount
  refreshAuthState()

  // Subscribe to login / logout events emitted by login.vue and other pages,
  // so the navbar updates immediately without needing a full page recreate.
  uni.$on('login-success', refreshAuthState)
  uni.$on('user-logout', refreshAuthState)
  uni.$on('user-profile-updated', refreshAuthState)
})

onUnmounted(() => {
  uni.$off('login-success', refreshAuthState)
  uni.$off('user-logout', refreshAuthState)
  uni.$off('user-profile-updated', refreshAuthState)
})

const goToHome = () => {
  // Use reLaunch to always return to the index page regardless of navigation stack.
  // Try the common uni-app index path; adjust if your pages.json uses a different path.
  uni.reLaunch({
    url: '/pages/index/index',
    fail: () => {
      // Fallback: some projects register index directly under /pages/index
      uni.reLaunch({ url: '/pages/index' })
    }
  })
}

const goToLogin = () => {
  uni.navigateTo({ url: '/pages/login' })
}

const goToSignup = () => {
  uni.navigateTo({ url: '/pages/signup' })
}

const goToFindScooter = () => {
  uni.navigateTo({ url: '/pages/find-scooter' })
}

const getCurrentRoute = () => {
  try {
    const pages = getCurrentPages()
    return pages[pages.length - 1]?.route || ''
  } catch {
    return ''
  }
}

const openIndexSection = (section) => {
  const selector = `#${section}`
  const currentRoute = getCurrentRoute()
  if (currentRoute === 'pages/index' || currentRoute === 'pages/index/index') {
    uni.pageScrollTo({
      selector,
      duration: 400,
      fail: () => {
        uni.reLaunch({ url: `/pages/index?section=${encodeURIComponent(section)}` })
      }
    })
    return
  }

  uni.reLaunch({ url: `/pages/index?section=${encodeURIComponent(section)}` })
}

const goToFooterService = (target) => {
  if (target === 'rent') {
    goToFindScooter()
    return
  }

  openIndexSection(target)
}

const openLegalNotice = (documentName) => {
  uni.showToast({
    title: `${documentName} coming soon`,
    icon: 'none',
    duration: 1800
  })
}

const goToBooking = () => {
  uni.navigateTo({ url: '/pages/active-ride?source=booking' })
}

const goToTrip = () => {
  uni.navigateTo({ url: '/pages/active-ride?source=trip' })
}

const goToDashboard = () => {
  if (props.currentPage === 'admin-dashboard') return
  uni.navigateTo({ url: '/pages/admin-dashboard' })
}

const goToProfile = () => {
  uni.navigateTo({ url: '/pages/profile' })
}

const goToMobileHome = () => {
  if (activeMobileNavPage.value === 'home') return
  goToHome()
}

const goToMobileFindScooter = () => {
  if (activeMobileNavPage.value === 'find-scooter') return
  goToFindScooter()
}

const goToMobileRide = () => {
  if (activeMobileNavPage.value === 'ride') return
  goToTrip()
}

const goToMobileAccount = () => {
  if (activeMobileNavPage.value === 'profile' && isLoggedIn.value) return
  if (isLoggedIn.value) {
    goToProfile()
    return
  }
  goToLogin()
}

const mobileNavItems = computed(() => ([
  { id: 'home', label: 'Home', action: goToMobileHome },
  { id: 'find-scooter', label: 'Map', action: goToMobileFindScooter },
  { id: 'ride', label: 'Ride', action: goToMobileRide },
  { id: 'profile', label: isLoggedIn.value ? 'Profile' : 'Login', action: goToMobileAccount }
]))
</script>

<style scoped>
/* ========== Base Layout ========== */
.base-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #EBF4FF 0%, #F9FAFB 100%);
}

.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* ========== Navigation Bar ========== */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  border-bottom: 1rpx solid rgba(37, 99, 235, 0.1);
  z-index: 1000;
}

.nav-container {
  max-width: 2200rpx;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 60rpx;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 20rpx;
  cursor: pointer;
  flex-shrink: 0;
  transition: transform 0.22s ease, opacity 0.22s ease;
}

.logo-icon {
  width: 70rpx;
  height: 70rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon svg {
  width: 100%;
  height: 100%;
}

.logo-text {
  font-size: 42rpx;
  font-weight: 700;
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav-menu {
  display: flex;
  gap: 12rpx;
  align-items: center;
  flex: 1;
  justify-content: center;
  margin: 0 40rpx;
}

.menu-item-wrap {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 14rpx;
  padding: 18rpx 36rpx;
  border-radius: 20rpx;
  cursor: pointer;
  position: relative;
  transition: all 0.25s ease;
  color: #9CA3AF;
}

.menu-item-wrap:hover {
  background: rgba(37, 99, 235, 0.06);
  color: #2563EB;
}

.menu-item-wrap.active {
  background: rgba(37, 99, 235, 0.08);
  color: #2563EB;
}

.menu-label {
  font-size: 28rpx;
  font-weight: 500;
  color: currentColor;
  white-space: nowrap;
}

.menu-icon {
  width: 40rpx;
  height: 40rpx;
  stroke: currentColor;
  flex-shrink: 0;
}

.menu-indicator {
  position: absolute;
  bottom: 6rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 4rpx;
  background: #2563EB;
  border-radius: 2rpx;
  transition: width 0.25s ease;
}

.menu-item-wrap.active .menu-indicator {
  width: 40rpx;
}

.nav-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  min-width: 160rpx;
  justify-content: flex-end;
}

/* Pill-shaped buttons with reduced height */
.nav-pill-btn {
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  color: white;
  padding: 16rpx 48rpx;
  border-radius: 60rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
  box-shadow: 0 4rpx 16rpx rgba(37, 99, 235, 0.3);
  transition: all 0.3s;
  line-height: 2;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.nav-pill-btn-text {
  color: white;
  font-size: 28rpx;
  font-weight: 600;
}

.nav-pill-btn::after {
  border: none;
}

/* User Avatar */
.user-avatar-wrap {
  position: relative;
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.user-avatar-wrap:active {
  transform: scale(0.93);
}

.user-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  border: 3rpx solid #2563EB;
  object-fit: cover;
  display: block;
}

.user-avatar-placeholder {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  border: 3rpx solid rgba(37,99,235,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar-initial {
  color: white;
  font-size: 32rpx;
  font-weight: 700;
  line-height: 1;
}

.avatar-online-dot {
  position: absolute;
  bottom: 2rpx;
  right: 2rpx;
  width: 18rpx;
  height: 18rpx;
  background: #22C55E;
  border-radius: 50%;
  border: 3rpx solid white;
}

/* Legacy styles kept for backward compat */
.nav-pill-btn-outlined {
  background: white;
  color: #2563EB;
  padding: 14rpx 44rpx;
  border-radius: 60rpx; 
  font-size: 28rpx;
  font-weight: 600;
  border: 2rpx solid #2563EB;
  transition: all 0.3s;
  line-height: 2;
}

.nav-pill-btn-outlined::after {
  border: none;
}

.nav-link {
  color: #2563EB;
  font-size: 30rpx;
  font-weight: 600;
  cursor: pointer;
}

/* ========== Compact Footer ========== */
.footer {
  background: rgba(255, 255, 255, 0.92);
  color: #374151;
  margin-top: auto;
  border-top: 1rpx solid #E5E7EB;
}

.footer-content {
  max-width: 2200rpx;
  margin: 0 auto;
  padding: 44rpx 80rpx 28rpx;
}

.footer-brand {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  min-width: 0;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.footer-logo-icon {
  width: 56rpx;
  height: 56rpx;
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  border-radius: 14rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(37, 99, 235, 0.18);
  padding: 10rpx;
  flex-shrink: 0;
}

.footer-logo-icon svg {
  width: 100%;
  height: 100%;
}

.footer-logo-text {
  font-size: 34rpx;
  font-weight: 700;
  color: #1F2937;
}

.footer-description {
  font-size: 24rpx;
  line-height: 1.5;
  color: #6B7280;
  max-width: 760rpx;
}

.footer-contact {
  font-size: 24rpx;
  color: #6B7280;
  white-space: nowrap;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20rpx;
  padding-top: 22rpx;
  border-top: 1rpx solid #E5E7EB;
}

.footer-bottom-right,
.footer-heading,
.footer-link,
.footer-tagline,
.social-icons,
.social-icon,
.icon-svg,
.contact-heading,
.footer-legal,
.footer-divider {
  display: none;
}

.footer-bottom-left {
  display: block;
}

.copyright {
  font-size: 0;
  color: transparent;
}

.copyright::before {
  content: '© 2026 ScooterGo Ltd.';
  font-size: 24rpx;
  color: #9CA3AF;
}

.footer-caption {
  font-size: 24rpx;
  color: #94A3B8;
  text-align: right;
  display: none;
}

/* Simple Footer */
.footer-simple {
  min-height: 52rpx;
}

@media (hover: hover) {
  .logo-container:hover {
    transform: translateY(-2rpx);
    opacity: 0.92;
  }

  .nav-pill-btn:hover {
    transform: translateY(-3rpx);
    box-shadow: 0 12rpx 28rpx rgba(37, 99, 235, 0.28);
  }

  .user-avatar-wrap:hover {
    transform: translateY(-3rpx) scale(1.02);
    box-shadow: 0 10rpx 24rpx rgba(37, 99, 235, 0.18);
  }
}

/* Responsive adjustments for smaller screens */
@media (max-width: 900px) {
  .footer-content {
    padding: 40rpx 60rpx 24rpx;
  }
}

@media (max-width: 750px) {
  .nav-menu {
    display: none !important;
  }
  
  .nav-container {
    padding: 20rpx 30rpx;
  }

  .footer-content {
    padding: 36rpx 40rpx 24rpx;
  }

  .footer-bottom {
    flex-direction: column;
    align-items: flex-start;
    gap: 12rpx;
  }

  .footer-caption {
    text-align: left;
  }
}

/* ========== Footer Restore Overrides ========== */
.footer {
  background: #FFFFFF;
  color: #374151;
}

.footer-content {
  padding: 100rpx 80rpx 40rpx;
}

.footer-top {
  display: grid;
  grid-template-columns: 2fr 1.3fr 1.1fr;
  gap: 80rpx;
  padding-bottom: 80rpx;
  border-bottom: 1rpx solid #E5E7EB;
}

.footer-column {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.footer-brand {
  gap: 20rpx;
}

.footer-logo {
  margin-bottom: 8rpx;
}

.footer-logo-icon {
  width: 70rpx;
  height: 70rpx;
  border-radius: 16rpx;
  padding: 12rpx;
  box-shadow: 0 4rpx 12rpx rgba(37, 99, 235, 0.2);
}

.footer-logo-text {
  font-size: 42rpx;
}

.footer-tagline {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: #2563EB;
  margin-top: -8rpx;
}

.footer-description {
  font-size: 28rpx;
  line-height: 1.6;
  max-width: none;
}

.footer-heading,
.footer-link,
.social-icons,
.social-icon,
.icon-svg,
.contact-heading,
.footer-legal,
.footer-divider,
.footer-bottom-right {
  display: flex;
}

.footer-heading {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 8rpx;
}

.footer-link {
  display: block;
  font-size: 28rpx;
  color: #6B7280;
}

.footer-services-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx 28rpx;
}

.footer-service-item {
  display: flex;
  align-items: center;
  min-height: 44rpx;
  padding: 6rpx 0;
  cursor: pointer;
  transition: transform 0.22s ease, color 0.22s ease, opacity 0.22s ease;
}

.footer-service-text {
  font-size: 25rpx;
  font-weight: 600;
  line-height: 1.4;
  color: #475569;
  transition: color 0.22s ease;
}

.social-icons {
  gap: 20rpx;
  margin-top: 8rpx;
}

.social-icon {
  width: 72rpx;
  height: 72rpx;
  background: #F3F4F6;
  border: 1rpx solid #E5E7EB;
  border-radius: 50%;
  align-items: center;
  justify-content: center;
}

.icon-svg {
  width: 40rpx;
  height: 40rpx;
}

.contact-heading {
  display: block;
  margin-top: 16rpx;
}

.footer-contact {
  font-size: 28rpx;
  white-space: normal;
}

.footer-contact-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.footer-contact-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 6rpx 0;
  cursor: pointer;
  transition: transform 0.22s ease, color 0.22s ease, opacity 0.22s ease;
}

.footer-contact-icon {
  width: 36rpx;
  height: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: transform 0.22s ease;
}

.footer-contact-stack {
  display: block;
}

.footer-bottom {
  padding-top: 40rpx;
  border-top: none;
}

.footer-bottom-left {
  flex: 1;
  display: block;
}

.copyright {
  font-size: 0;
  color: transparent;
}

.copyright::before {
  content: '© 2026 ScooterGo Ltd. All rights reserved.';
  font-size: 26rpx;
  color: #9CA3AF;
}

.footer-bottom-right {
  align-items: center;
  gap: 20rpx;
  flex-wrap: wrap;
}

.footer-legal {
  display: block;
  font-size: 26rpx;
  color: #6B7280;
}

.footer-divider {
  display: block;
  font-size: 0;
  color: transparent;
}

.footer-divider::before {
  content: '•';
  font-size: 26rpx;
  color: #D1D5DB;
}

.footer-caption {
  display: none;
}

.footer-simple {
  min-height: 100rpx;
}

.mobile-bottom-nav {
  display: none;
}

.mobile-nav-item,
.mobile-nav-icon {
  transition: transform 0.22s ease, color 0.22s ease, background 0.22s ease, box-shadow 0.22s ease, opacity 0.22s ease;
}

@media (hover: hover) {
  .social-icon:hover {
    background: #2563EB;
    border-color: #2563EB;
    transform: translateY(-3rpx);
  }

  .social-icon:hover .icon-svg {
    fill: white !important;
  }

  .footer-service-item:hover,
  .footer-contact-item:hover {
    transform: translateX(8rpx);
  }

  .footer-service-item:hover .footer-service-text,
  .footer-contact-item:hover .footer-contact {
    color: #2563EB;
  }

  .footer-contact-item:hover .footer-contact-icon {
    transform: scale(1.08);
  }
}

@media (max-width: 1200px) {
  .footer-top {
    grid-template-columns: 1.8fr 1.2fr 1fr;
    gap: 60rpx;
  }
}

@media (max-width: 900px) {
  .footer-top {
    grid-template-columns: 1fr 1fr;
    gap: 50rpx;
  }

  .footer-contact-col {
    grid-column: 1 / -1;
  }

  .footer-content {
    padding: 80rpx 60rpx 40rpx;
  }
}

@media (max-width: 750px) {
  .footer-top {
    grid-template-columns: 1fr;
    gap: 50rpx;
  }

  .footer-services-grid {
    grid-template-columns: 1fr;
    gap: 12rpx;
  }

  .footer-content {
    padding: 60rpx 40rpx 30rpx;
  }

  .footer-bottom {
    flex-direction: column;
    text-align: center;
    gap: 30rpx;
  }

  .footer-bottom-right {
    justify-content: center;
  }
}

@media (max-width: 750px) {
  .footer,
  .footer-simple {
    display: none !important;
  }

  .content-with-mobile-nav {
    padding-bottom: calc(env(safe-area-inset-bottom) + 136rpx);
    box-sizing: border-box;
  }

  .mobile-bottom-nav {
    position: fixed;
    left: 16rpx;
    right: 16rpx;
    bottom: calc(env(safe-area-inset-bottom) + 14rpx);
    display: flex;
    align-items: stretch;
    justify-content: space-between;
    gap: 10rpx;
    padding: 12rpx;
    border-radius: 30rpx;
    background: rgba(255, 255, 255, 0.96);
    border: 1rpx solid rgba(219, 234, 254, 0.95);
    box-shadow: 0 20rpx 44rpx rgba(15, 23, 42, 0.14);
    backdrop-filter: blur(22px);
    z-index: 920;
  }

  .mobile-nav-item {
    flex: 1;
    min-width: 0;
    padding: 14rpx 10rpx 12rpx;
    border-radius: 24rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    color: #94A3B8;
  }

  .mobile-nav-item:active {
    transform: scale(0.97);
  }

  .mobile-nav-item-active {
    color: #2563EB;
    background: linear-gradient(180deg, #EFF6FF 0%, #FFFFFF 100%);
    box-shadow: inset 0 0 0 1rpx rgba(147, 197, 253, 0.7);
  }

  .mobile-nav-icon {
    width: 46rpx;
    height: 46rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .mobile-nav-icon svg {
    width: 100%;
    height: 100%;
    stroke: currentColor;
  }

  .mobile-nav-label {
    max-width: 100%;
    font-size: 21rpx;
    font-weight: 700;
    color: currentColor;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>
