<template>
  <view class="base-layout">
    <!-- Navigation Bar -->
    <view class="navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
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
    <view class="content-wrapper" :style="{ paddingTop: contentPaddingTop + 'px' }">
      <slot></slot>
    </view>

    <!-- Professional Footer -->
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

          <view class="footer-column">
            <text class="footer-heading">Company</text>
            <text class="footer-link">About Us</text>
            <text class="footer-link">How It Works</text>
            <text class="footer-link">Careers</text>
            <text class="footer-link">Press Kit</text>
            <text class="footer-link">Blog</text>
          </view>

          <view class="footer-column">
            <text class="footer-heading">Services</text>
            <text class="footer-link">Rent a Scooter</text>
            <text class="footer-link">Pricing Plans</text>
            <text class="footer-link">Locations</text>
            <text class="footer-link">Business Solutions</text>
            <text class="footer-link">Student Discount</text>
          </view>

          <view class="footer-column">
            <text class="footer-heading">Support</text>
            <text class="footer-link">Help Center</text>
            <text class="footer-link">Safety Guidelines</text>
            <text class="footer-link">Contact Us</text>
            <text class="footer-link">Report Issue</text>
            <text class="footer-link">FAQ</text>
          </view>

          <view class="footer-column">
            <text class="footer-heading">Connect</text>
            <view class="social-icons">
              <view class="social-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="#6B7280" class="icon-svg">
                  <path d="M9.198 21.5h4v-8.01h3.604l.396-3.98h-4V7.5a1 1 0 0 1 1-1h3v-4h-3a5 5 0 0 0-5 5v3.01h-2.5v3.98h2.5v8.01Z"/>
                </svg>
              </view>
              <view class="social-icon">
                <uni-icons type="weibo" size="20" color="#6B7280"></uni-icons>
              </view>
              <view class="social-icon">
                <uni-icons type="email" size="20" color="#6B7280"></uni-icons>
              </view>
            </view>
            <text class="footer-heading contact-heading">Contact</text>
            <text class="footer-contact">support@scootergo.com</text>
            <text class="footer-contact">+44 20 1234 5678</text>
          </view>
        </view>

        <!-- Footer Bottom Section -->
        <view class="footer-bottom">
          <view class="footer-bottom-left">
            <text class="copyright">© 2026 ScooterGo Ltd. All rights reserved.</text>
          </view>
          <view class="footer-bottom-right">
            <text class="footer-legal">Privacy Policy</text>
            <text class="footer-divider">•</text>
            <text class="footer-legal">Terms of Service</text>
            <text class="footer-divider">•</text>
            <text class="footer-legal">Cookie Policy</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- Simple Footer for pages without full footer -->
    <view v-else class="footer-simple"></view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

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

// Auth state - read from storage
const isLoggedIn = ref(false)
const userAvatar = ref('')
const userName = ref('')

const userInitial = computed(() => {
  if (userName.value) return userName.value.charAt(0).toUpperCase()
  return 'U'
})

onMounted(() => {
  const info = uni.getSystemInfoSync()
  statusBarHeight.value = info.statusBarHeight || 0
  navbarHeight.value = statusBarHeight.value + 50

  // Check login state from local storage
  try {
    const token = uni.getStorageSync('token') || uni.getStorageSync('userToken')
    const user = uni.getStorageSync('userInfo')
    if (token) {
      isLoggedIn.value = true
      if (user) {
        const userObj = typeof user === 'string' ? JSON.parse(user) : user
        userAvatar.value = userObj.avatar || userObj.avatarUrl || ''
        userName.value = userObj.name || userObj.nickname || userObj.username || ''
      }
    }
  } catch (e) {
    isLoggedIn.value = false
  }
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

const goToBooking = () => {
  uni.navigateTo({ url: '/pages/booking' })
}

const goToTrip = () => {
  uni.navigateTo({ url: '/pages/trip' })
}

const goToProfile = () => {
  uni.navigateTo({ url: '/pages/profile' })
}
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
  transition: transform 0.2s;
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

/* ========== Professional Footer ========== */
.footer {
  background: #FFFFFF;
  color: #374151;
  margin-top: auto;
  border-top: 1rpx solid #E5E7EB;
}

.footer-content {
  max-width: 2200rpx;
  margin: 0 auto;
  padding: 100rpx 80rpx 40rpx;
}

.footer-top {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1.2fr;
  gap: 80rpx;
  padding-bottom: 80rpx;
  border-bottom: 1rpx solid #E5E7EB;
}

.footer-column {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

/* Footer Brand Section */
.footer-brand {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 8rpx;
}

.footer-logo-icon {
  width: 70rpx;
  height: 70rpx;
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(37, 99, 235, 0.2);
  padding: 12rpx;
}

.footer-logo-icon svg {
  width: 100%;
  height: 100%;
}

.footer-logo-text {
  font-size: 42rpx;
  font-weight: 700;
  color: #1F2937;
}

.footer-tagline {
  font-size: 32rpx;
  font-weight: 600;
  color: #2563EB;
  margin-top: -8rpx;
}

.footer-description {
  font-size: 28rpx;
  line-height: 1.6;
  color: #6B7280;
}

/* Footer Links */
.footer-heading {
  font-size: 32rpx;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 8rpx;
}

.footer-link {
  font-size: 28rpx;
  color: #6B7280;
  transition: color 0.3s;
  cursor: pointer;
}

.footer-link:active {
  color: #2563EB;
}

/* Social Icons */
.social-icons {
  display: flex;
  gap: 20rpx;
  margin-top: 8rpx;
}

.social-icon {
  width: 72rpx;
  height: 72rpx;
  background: #F3F4F6;
  border: 1rpx solid #E5E7EB;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.social-icon:active {
  background: #2563EB;
  border-color: #2563EB;
  transform: scale(0.95);
}

.social-icon:active uni-icons {
  color: white !important;
}

/* SVG icon styling for Facebook */
.icon-svg {
  width: 40rpx;
  height: 40rpx;
  transition: fill 0.3s;
}

.social-icon:active .icon-svg {
  fill: white !important;
}

/* Contact Info */
.contact-heading {
  margin-top: 16rpx;
}

.footer-contact {
  font-size: 28rpx;
  color: #6B7280;
}

/* Footer Bottom */
.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 40rpx;
  flex-wrap: wrap;
  gap: 20rpx;
}

.footer-bottom-left {
  flex: 1;
}

.copyright {
  font-size: 26rpx;
  color: #9CA3AF;
}

.footer-bottom-right {
  display: flex;
  align-items: center;
  gap: 20rpx;
  flex-wrap: wrap;
}

.footer-legal {
  font-size: 26rpx;
  color: #6B7280;
  cursor: pointer;
  transition: color 0.3s;
}

.footer-legal:active {
  color: #2563EB;
}

.footer-divider {
  color: #D1D5DB;
  font-size: 26rpx;
}

/* Simple Footer */
.footer-simple {
  min-height: 100rpx;
}

/* Responsive adjustments for smaller screens */
@media (max-width: 1200px) {
  .footer-top {
    grid-template-columns: 2fr 1fr 1fr 1fr;
    gap: 60rpx;
  }
}

@media (max-width: 900px) {
  .footer-top {
    grid-template-columns: 1fr 1fr;
    gap: 50rpx;
  }
  
  .footer-content {
    padding: 80rpx 60rpx 40rpx;
  }
}

@media (max-width: 750px) {
  .nav-menu {
    display: none !important;
  }
  
  .nav-container {
    padding: 20rpx 30rpx;
  }
  
  .footer-top {
    grid-template-columns: 1fr;
    gap: 50rpx;
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
</style>