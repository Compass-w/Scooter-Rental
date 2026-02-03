<template>
  <BaseLayout nav-type="signup" :show-menu="true" :show-footer="true" :content-padding-top="148">
    <!-- Main Content -->
    <view class="main-container">
      <view class="signup-card">
        <!-- Card Header -->
        <view class="card-header">
          <view class="user-icon-bg">
            <uni-icons type="personadd-filled" size="40" color="#fff"></uni-icons>
          </view>
          <text class="title">Join ScooterGo</text>
          <view class="subtitle-box">
            <uni-icons type="checkmarkempty" size="16" color="#2563EB"></uni-icons>
            <text class="subtitle">Start your eco-friendly journey today</text>
          </view>
        </view>

        <!-- Registration Form -->
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
                placeholder="Choose a unique username" 
              />
            </view>
          </view>

          <view class="form-group">
            <text class="label">Email Address</text>
            <view class="input-wrapper">
              <view class="icon-left">
                <uni-icons type="email" size="24" color="#D1D5DB"></uni-icons>
              </view>
              <input 
                class="input-pill" 
                v-model="email" 
                type="text" 
                placeholder="your.email@example.com" 
              />
            </view>
          </view>

          <view class="form-group">
            <text class="label">Phone Number</text>
            <view class="input-wrapper">
              <view class="icon-left">
                <uni-icons type="phone" size="24" color="#D1D5DB"></uni-icons>
              </view>
              <input 
                class="input-pill" 
                v-model="phone" 
                type="text" 
                placeholder="07123456789" 
              />
            </view>
            <text class="input-hint">UK mobile number format</text>
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
                placeholder="Create a strong password" 
              />
              <view class="icon-right" @tap="togglePassword">
                <uni-icons :type="showPwd ? 'eye' : 'eye-slash'" size="24" color="#9CA3AF"></uni-icons>
              </view>
            </view>
            <text class="input-hint">At least 6 characters</text>
          </view>

          <view class="form-group">
            <text class="label">Confirm Password</text>
            <view class="input-wrapper">
              <view class="icon-left">
                <uni-icons type="locked" size="24" color="#D1D5DB"></uni-icons>
              </view>
              <input 
                class="input-pill" 
                v-model="confirmPassword" 
                :type="showConfirmPwd ? 'text' : 'password'" 
                placeholder="Re-enter your password" 
              />
              <view class="icon-right" @tap="toggleConfirmPassword">
                <uni-icons :type="showConfirmPwd ? 'eye' : 'eye-slash'" size="24" color="#9CA3AF"></uni-icons>
              </view>
            </view>
          </view>

          <view class="terms-row">
            <label class="checkbox-container">
              <checkbox :checked="agreeTerms" @change="toggleTerms" color="#2563EB" style="transform:scale(0.8)" />
              <text class="checkbox-label">
                I agree to the <text class="link-text">Terms of Service</text> and <text class="link-text">Privacy Policy</text>
              </text>
            </label>
          </view>

          <button class="btn-primary-pill" @tap="handleSignup" :disabled="loading">
            <text v-if="loading">Creating your account...</text>
            <text v-else>Create Account</text>
          </button>

          <view class="bottom-tips">
            <text>Already have an account? </text>
            <text class="link-text" @tap="goToLogin">Sign in here</text>
          </view>
        </view>
      </view>
    </view>
  </BaseLayout>
</template>

<script setup>
import { ref } from 'vue'
import { register as registerApi } from '@/api/user.js'
import BaseLayout from '@/pages/BaseLayout.vue'

// Reactive state variables
const username = ref('')
const email = ref('')
const phone = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPwd = ref(false)
const showConfirmPwd = ref(false)
const agreeTerms = ref(false)
const loading = ref(false)

/**
 * Toggle password visibility
 */
const togglePassword = () => { 
  showPwd.value = !showPwd.value 
}

/**
 * Toggle confirm password visibility
 */
const toggleConfirmPassword = () => { 
  showConfirmPwd.value = !showConfirmPwd.value 
}

/**
 * Toggle terms and conditions checkbox
 */
const toggleTerms = () => { 
  agreeTerms.value = !agreeTerms.value 
}

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
 * Validate UK phone number format
 * @param {string} phone - Phone number to validate
 * @returns {boolean} - True if phone format is valid (UK mobile)
 */
const validatePhone = (phone) => {
  const phoneRegex = /^07\d{9}$/
  return phoneRegex.test(phone)
}

/**
 * Handle user registration
 * Validates all inputs and calls registration API
 */
const handleSignup = async () => {
  // Validate all required fields are filled
  if (!username.value || !email.value || !phone.value || !password.value || !confirmPassword.value) {
    uni.showToast({ 
      title: 'Please fill in all fields', 
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

  // Validate phone number format
  if (!validatePhone(phone.value)) {
    uni.showToast({ 
      title: 'Please enter a valid UK mobile number (e.g., 07123456789)', 
      icon: 'none',
      duration: 2500
    })
    return
  }

  // Validate password length
  if (password.value.length < 6) {
    uni.showToast({ 
      title: 'Password must be at least 6 characters', 
      icon: 'none',
      duration: 2000
    })
    return
  }

  // Validate passwords match
  if (password.value !== confirmPassword.value) {
    uni.showToast({ 
      title: 'Passwords do not match', 
      icon: 'none',
      duration: 2000
    })
    return
  }

  // Validate terms agreement
  if (!agreeTerms.value) {
    uni.showToast({ 
      title: 'Please agree to Terms of Service', 
      icon: 'none',
      duration: 2000
    })
    return
  }

  // Start loading state
  loading.value = true

  try {
    // Call registration API
    await registerApi({
      username: username.value,
      email: email.value,
      phone: phone.value,
      password: password.value
    })

    // Show success message
    uni.showToast({ 
      title: 'Registration successful!', 
      icon: 'success',
      duration: 2000
    })

    // Navigate to login page after success
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/login' })
    }, 2000)

  } catch (error) {
    console.error('Registration failed:', error)
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
  padding: 60rpx 40rpx 80rpx;
}

/* ========== Signup Card ========== */
.signup-card {
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

.user-icon-bg {
  width: 120rpx;
  height: 120rpx;
  /* 从绿色改为蓝色渐变 */
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 30rpx;
  /* 阴影颜色也改为蓝色 */
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
  margin-bottom: 40rpx;
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

.input-hint {
  display: block;
  font-size: 24rpx;
  color: #9CA3AF;
  margin-left: 24rpx;
  margin-top: 8rpx;
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

.terms-row {
  margin: 30rpx 10rpx 40rpx;
}

.checkbox-container {
  display: flex;
  align-items: flex-start;
  color: #6B7280;
  font-size: 28rpx;
  line-height: 1.5;
}

.checkbox-label {
  margin-left: 8rpx;
}

.link-text {
  color: #2563EB;
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
  margin-top: 20rpx;
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
  margin-top: 45rpx;
  font-size: 30rpx;
  color: #6B7280;
}

/* Responsive adjustments for smaller screens */
@media (max-width: 750px) {
  .signup-card {
    padding: 60rpx 40rpx;
  }
  
  .main-container {
    padding: 40rpx 30rpx;
  }
}
</style>