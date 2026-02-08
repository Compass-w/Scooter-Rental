<template>
  <BaseLayout nav-type="login" :show-menu="true" :show-footer="true" :content-padding-top="168">
    <!-- Main Content -->
    <view class="main-container">
      <view class="login-card">
        <!-- Card Header -->
        <view class="card-header">
          <view class="lock-icon-bg">
            <uni-icons type="locked-filled" size="40" color="#fff"></uni-icons>
          </view>
          <text class="title">Welcome Back</text>
          <view class="subtitle-box">
            <uni-icons type="checkmarkempty" size="16" color="#2563EB"></uni-icons>
            <text class="subtitle">Sign in to continue riding</text>
          </view>
        </view>

        <!-- Login Form -->
        <view class="form">
          <view class="form-group">
            <text class="label">Username</text>
            <view class="input-wrapper">
              <view class="icon-left">
                <uni-icons type="person" size="24" color="#D1D5DB"></uni-icons>
              </view>
              <input 
                class="input-pill" 
                v-model="username" 
                type="text" 
                placeholder="Enter your username" 
                @confirm="handleLogin"
              />
            </view>
          </view>

          <view class="form-group">
            <text class="label">Password</text>
            <view class="input-wrapper">
              <view class="icon-left">
                <uni-icons type="locked" size="24" color="#D1D5DB"></uni-icons>
              </view>
              <input 
                class="input-pill" 
                v-model="password" 
                :type="showPwd ? 'text' : 'password'" 
                placeholder="Enter your password" 
                @confirm="handleLogin"
              />
              <view class="icon-right" @tap="togglePassword">
                <uni-icons :type="showPwd ? 'eye' : 'eye-slash'" size="24" color="#9CA3AF"></uni-icons>
              </view>
            </view>
          </view>

          <view class="form-actions">
            <label class="checkbox-container">
              <checkbox :checked="remember" @change="toggleRemember" color="#2563EB" style="transform:scale(0.8)" />
              <text class="checkbox-label">Remember me</text>
            </label>
            <text class="link-text" @tap="goToForget">Forgot password?</text>
          </view>

          <button class="btn-primary-pill" @tap="handleLogin" :disabled="loading">
            <text v-if="loading">Signing in...</text>
            <text v-else>Sign In</text>
          </button>

          <view class="bottom-tips">
            <text>Don't have an account? </text>
            <text class="link-text" @tap="goToSignup">Sign up free</text>
          </view>
        </view>
      </view>
    </view>
  </BaseLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { login as loginApi, setUserInfo } from '@/api/user.js'
import request from '@/utils/request.js'
import BaseLayout from '@/pages/BaseLayout.vue'

// Reactive state variables
const username = ref('')
const password = ref('')
const showPwd = ref(false)
const remember = ref(false)
const loading = ref(false)

/**
 * Component mounted lifecycle hook
 * Load saved credentials
 */
onMounted(() => {
  // Load saved username if "remember me" was checked
  try {
    const savedUsername = uni.getStorageSync('savedUsername')
    if (savedUsername) {
      username.value = savedUsername
      remember.value = true
    }
  } catch (e) {
    console.error('Failed to load saved username:', e)
  }
})

/**
 * Toggle password visibility
 */
const togglePassword = () => { 
  showPwd.value = !showPwd.value 
}

/**
 * Toggle remember me checkbox
 */
const toggleRemember = () => { 
  remember.value = !remember.value 
}

/**
 * Handle user login
 * Validates input, calls login API, and handles navigation based on user role
 */
const handleLogin = async () => {
  // Validate required fields
  if (!username.value || !password.value) {
    uni.showToast({ 
      title: 'Please fill in all fields', 
      icon: 'none' 
    })
    return
  }

  // Start loading state
  loading.value = true

  try {
    // Call login API
    const result = await loginApi({
      username: username.value,
      password: password.value
    })

    // Save authentication token
    if (result.token) {
      request.setToken(result.token)
    }

    // Save user information
    const userInfo = {
      userId: result.userId,
      username: username.value,
      role: result.role
    }
    setUserInfo(userInfo)

    // Handle "remember me" functionality
    if (remember.value) {
      uni.setStorageSync('savedUsername', username.value)
    } else {
      uni.removeStorageSync('savedUsername')
    }

    // Show success message
    uni.showToast({ 
      title: 'Login successful!', 
      icon: 'success',
      duration: 1500
    })

    // Navigate to appropriate page based on user role
    setTimeout(() => {
      if (result.role === 'ADMIN') {
        uni.reLaunch({ url: '/pages/index' })  // Navigate to admin dashboard
      } else {
        uni.reLaunch({ url: '/pages/index' })  // Navigate to user home
      }
    }, 1500)

  } catch (error) {
    console.error('Login failed:', error)
    // Error message is handled by request.js
  } finally {
    loading.value = false
  }
}

/**
 * Navigate to signup page
 */
const goToSignup = () => { 
  uni.navigateTo({ url: '/pages/signup' }) 
}

/**
 * Navigate to forgot password page
 */
const goToForget = () => { 
  uni.navigateTo({ url: '/pages/forget-password' }) 
}
</script>

<style scoped>
/* ========== Main Container ========== */
.main-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60rpx 40rpx 80rpx;
}

/* ========== Login Card ========== */
.login-card {
  background: rgba(255, 255, 255, 0.95);
  width: 100%;
  max-width: 800rpx;
  padding: 80rpx 60rpx;
  border-radius: 40rpx;
  box-shadow: 0 20rpx 60rpx rgba(37, 99, 235, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.8);
}

.card-header {
  text-align: center;
  margin-bottom: 60rpx;
}

.lock-icon-bg {
  width: 120rpx;
  height: 120rpx;
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 30rpx;
  box-shadow: 0 8rpx 20rpx rgba(37, 99, 235, 0.3);
}

.title {
  display: block;
  font-size: 52rpx;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 20rpx;
}

.subtitle-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
}

.subtitle {
  font-size: 28rpx;
  color: #6B7280;
}

.form {
  margin-top: 60rpx;
}

.form-group {
  margin-bottom: 48rpx;
}

.label {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 16rpx;
  margin-left: 20rpx;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-pill {
  width: 100%;
  padding: 32rpx 100rpx;
  border: 2rpx solid #E5E7EB;
  border-radius: 60rpx;
  font-size: 32rpx;
  height: 110rpx;
  box-sizing: border-box;
  transition: all 0.3s;
}

.input-pill:focus {
  border-color: #2563EB;
  box-shadow: 0 0 0 4rpx rgba(37, 99, 235, 0.1);
}

.icon-left { 
  position: absolute; 
  left: 38rpx; 
  z-index: 1;
}

.icon-right { 
  position: absolute; 
  right: 38rpx; 
  z-index: 1;
  cursor: pointer;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 40rpx 10rpx;
}

.checkbox-container {
  display: flex;
  align-items: center;
  color: #6B7280;
  font-size: 28rpx;
}

.checkbox-label {
  margin-left: 8rpx;
}

.link-text {
  color: #2563EB;
  font-size: 28rpx;
  font-weight: 600;
  cursor: pointer;
}

.btn-primary-pill {
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  color: white;
  height: 110rpx;
  line-height: 110rpx;
  border-radius: 55rpx;
  font-size: 34rpx;
  font-weight: 600;
  margin-top: 30rpx;
  box-shadow: 0 8rpx 20rpx rgba(37, 99, 235, 0.3);
  transition: all 0.3s;
}

.btn-primary-pill::after {
  border: none;
}

.btn-primary-pill[disabled] {
  opacity: 0.6;
}

.bottom-tips {
  text-align: center;
  margin-top: 50rpx;
  font-size: 30rpx;
  color: #6B7280;
}

/* Responsive adjustments for smaller screens */
@media (max-width: 750px) {
  .login-card {
    padding: 60rpx 40rpx;
  }
  
  .main-container {
    padding: 40rpx 30rpx;
  }
}
</style>