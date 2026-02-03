<template>
  <BaseLayout nav-type="simple" :show-menu="false" :show-footer="false" :content-padding-top="148">
    <!-- Main Content -->
    <view class="main-container">
      <view class="reset-card">
        <!-- Card Header -->
        <view class="card-header">
          <view class="key-icon">
            <uni-icons type="redo-filled" size="40" color="#fff"></uni-icons>
          </view>
          <text class="title">Forgot Password?</text>
          <text class="subtitle">Don't worry, we'll help you reset it</text>
          <text class="description">
            Enter your email address and we'll send you a link to reset your password
          </text>
        </view>

        <!-- Reset Form -->
        <view class="form">
          <view class="form-group">
            <text class="label">Email Address</text>
            <view class="input-wrapper">
              <view class="icon-left">
                <uni-icons type="email" size="28" color="#9CA3AF"></uni-icons>
              </view>
              <input 
                class="input-pill" 
                v-model="email" 
                type="text" 
                placeholder="your.email@example.com" 
                @confirm="handleReset"
              />
            </view>
            <text class="input-hint">We'll send a reset link to this email</text>
          </view>

          <button class="btn-primary-pill" @tap="handleReset" :disabled="loading">
            <text v-if="loading">Sending reset link...</text>
            <text v-else>Send Reset Link</text>
          </button>

          <view class="info-box">
            <uni-icons type="info" size="20" color="#2563EB"></uni-icons>
            <text class="info-text">
              The reset link will expire in 24 hours for security purposes
            </text>
          </view>

          <view class="login-text">
            <text>Remember your password? </text>
            <text class="link" @tap="goToLogin">Back to Sign In</text>
          </view>

          <view class="help-section">
            <text class="help-title">Need more help?</text>
            <text class="help-text">
              Contact our support team at support@scootergo.com or call +44 20 1234 5678
            </text>
          </view>
        </view>
      </view>
    </view>
  </BaseLayout>
</template>

<script setup>
import { ref } from 'vue'
import { forgotPassword } from '@/api/user.js'
import BaseLayout from '@/pages/BaseLayout.vue'

// Reactive state variables
const email = ref('')
const loading = ref(false)

/**
 * Validate email format
 * @param {string} email - Email address to validate
 * @returns {boolean} - True if email format is valid
 */
const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

/**
 * Handle password reset request
 * Sends reset link to user's email via API
 */
const handleReset = async () => {
  // Validate email input
  if (!email.value) {
    uni.showToast({ 
      title: 'Please enter your email address', 
      icon: 'none',
      duration: 2000
    })
    return
  }
  
  // Validate email format
  if (!validateEmail(email.value)) {
    uni.showToast({ 
      title: 'Please enter a valid email address', 
      icon: 'none',
      duration: 2000
    })
    return
  }
  
  // Start loading state
  loading.value = true
  
  try {
    // Call forgot password API
    await forgotPassword({ email: email.value })
    
    // Show success message
    uni.showToast({ 
      title: 'Reset link sent to your email!', 
      icon: 'success',
      duration: 2000
    })
    
    // Clear email input
    email.value = ''
    
  } catch (error) {
    console.error('Failed to send reset link:', error)
    // Error message is already handled by request.js
  } finally {
    loading.value = false
  }
}

/**
 * Navigate to login page
 */
const goToLogin = () => {
  uni.navigateTo({ url: '/pages/login' })
}
</script>

<style scoped>
/* ========== Main Container ========== */
.main-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60rpx 40rpx;
}

.reset-card {
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

.key-icon {
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
  font-size: 48rpx;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 16rpx;
}

.subtitle {
  display: block;
  font-size: 30rpx;
  color: #6B7280;
  margin-bottom: 24rpx;
}

.description {
  display: block;
  font-size: 26rpx;
  color: #9CA3AF;
  line-height: 1.6;
  padding: 0 20rpx;
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

.icon-left {
  position: absolute;
  left: 32rpx;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-pill {
  width: 100%;
  padding: 30rpx 90rpx;
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

.input-hint {
  font-size: 24rpx;
  color: #9CA3AF;
  margin-left: 24rpx;
  margin-top: 12rpx;
  display: block;
}

.btn-primary-pill {
  width: 100%;
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  color: white;
  height: 110rpx;
  line-height: 110rpx;
  border-radius: 55rpx; /* 药丸样式 */
  font-size: 34rpx;
  font-weight: 600;
  border: none;
  margin-bottom: 40rpx;
  box-shadow: 0 8rpx 20rpx rgba(37, 99, 235, 0.3);
  transition: all 0.3s;
}

.btn-primary-pill[disabled] {
  opacity: 0.6;
}

.btn-primary-pill::after {
  border: none;
}

.info-box {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  padding: 30rpx;
  background: rgba(37, 99, 235, 0.05);
  border-radius: 24rpx;
  border: 1rpx solid rgba(37, 99, 235, 0.1);
  margin-bottom: 40rpx;
}

.info-text {
  font-size: 26rpx;
  color: #4B5563;
  line-height: 1.6;
  flex: 1;
}

.login-text {
  text-align: center;
  font-size: 30rpx;
  color: #6B7280;
  margin-bottom: 40rpx;
}

.link {
  color: #2563EB;
  font-weight: 600;
  cursor: pointer;
}

.help-section {
  text-align: center;
  padding: 40rpx 30rpx;
  background: #F9FAFB;
  border-radius: 24rpx;
  margin-top: 20rpx;
}

.help-title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 12rpx;
}

.help-text {
  display: block;
  font-size: 26rpx;
  color: #6B7280;
  line-height: 1.6;
}
</style>