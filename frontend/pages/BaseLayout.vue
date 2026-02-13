<template>
  <view class="base-layout">
    <!-- Navigation Bar -->
    <view class="navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-container">
        <view class="logo-container" @tap="goToHome">
          <view class="logo-icon">
            <svg fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M12.08,19H7a1,1,0,0,1,0-2h5.08a7,7,0,0,1,5.86-5.91,1,1,0,0,1,.3,2,5,5,0,0,0-4.19,4.22A2,2,0,0,1,12.08,19Z" style="fill: #2563EB;"></path>
              <path d="M19.56,15.06,18,5h2a1,1,0,0,0,0-2H18a2,2,0,0,0-2,2.3l1.55,10.07a3,3,0,1,0,2-.31ZM19,19a1,1,0,1,1,1-1A1,1,0,0,1,19,19ZM5,15a3,3,0,1,0,3,3A3,3,0,0,0,5,15Zm0,4a1,1,0,1,1,1-1A1,1,0,0,1,5,19Z" style="fill: #000000;"></path>
            </svg>
          </view>
          <text class="logo-text">ScooterGo</text>
        </view>
        <view v-if="showMenu" class="nav-menu">
          <text class="menu-item">How It Works</text>
          <text class="menu-item">Pricing</text>
          <text class="menu-item">Locations</text>
          <text class="menu-item">Support</text>
        </view>
        <view class="nav-right">
          <button v-if="navType === 'login'" class="nav-pill-btn" @tap="goToSignup">Sign Up</button>
          <button v-else-if="navType === 'signup'" class="nav-pill-btn-outlined" @tap="goToLogin">Sign In</button>
          <text v-else-if="navType === 'simple'" class="nav-link" @tap="goToLogin">Back to Sign In</text>
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
    default: 'login' // 'login', 'signup', 'simple'
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
    default: 168 // navbarHeight + 80
  }
})

const statusBarHeight = ref(0)
const navbarHeight = ref(88)

onMounted(() => {
  const info = uni.getSystemInfoSync()
  statusBarHeight.value = info.statusBarHeight || 0
  navbarHeight.value = statusBarHeight.value + 50
})

const goToHome = () => {
  // Navigate to home page
  // uni.reLaunch({ url: '/pages/index' })
}

const goToLogin = () => {
  uni.navigateTo({ url: '/pages/login' })
}

const goToSignup = () => {
  uni.navigateTo({ url: '/pages/signup' })
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
  gap: 60rpx;
  align-items: center;
  flex: 1;
  justify-content: center;
  margin: 0 40rpx;
}

.menu-item {
  font-size: 30rpx;
  font-weight: 500;
  color: #4B5563;
  cursor: pointer;
  transition: color 0.3s;
  white-space: nowrap;
}

.menu-item:hover {
  color: #2563EB;
}

.nav-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

/* Pill-shaped buttons with reduced height */
.nav-pill-btn {
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  color: white;
  padding: 14rpx 44rpx;
  border-radius: 60rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
  box-shadow: 0 4rpx 12rpx rgba(37, 99, 235, 0.25);
  transition: all 0.3s;
  line-height: 2;
}

.nav-pill-btn::after {
  border: none;
}

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