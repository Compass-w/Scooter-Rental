<template>
  <BaseLayout nav-type="simple" :show-menu="false" :show-footer="false" :content-padding-top="148">
    <!-- Main Content -->
    <view class="main-container">
      <view class="reset-card">
        <!-- Token Verification State -->
        <view v-if="verifying" class="loading-state">
          <view class="loading-icon">
            <uni-icons type="spinner-cycle" size="60" color="#2563EB"></uni-icons>
          </view>
          <text class="loading-text">Verifying reset link...</text>
        </view>

        <!-- Invalid Token State -->
        <view v-else-if="tokenInvalid" class="error-state">
          <view class="error-icon">
            <uni-icons type="closeempty" size="60" color="#EF4444"></uni-icons>
          </view>
          <text class="error-title">Invalid or Expired Link</text>
          <text class="error-message">
            This password reset link is invalid or has expired. Please request a new one.
          </text>
          <button class="btn-secondary-pill" @tap="goToForget">Request New Link</button>
        </view>

        <!-- Reset Password Form -->
        <view v-else class="form-container">
          <!-- Card Header -->
          <view class="card-header">
            <view class="lock-icon">
              <uni-icons type="locked-filled" size="50" color="#fff"></uni-icons>
            </view>
            <text class="title">Reset Your Password</text>
            <text class="subtitle">Enter your new password below</text>
            <view v-if="userEmail" class="email-display">
              <uni-icons type="email" size="18" color="#6B7280"></uni-icons>
              <text class="email-text">{{ userEmail }}</text>
            </view>
          </view>

          <!-- Password Form -->
          <view class="form">
            <view class="form-group">
              <text class="label">New Password</text>
              <view class="input-wrapper">
                <view class="icon-left">
                  <uni-icons type="locked" size="28" color="#9CA3AF"></uni-icons>
                </view>
                <input 
                  class="input-pill" 
                  v-model="newPassword" 
                  :type="showNewPwd ? 'text' : 'password'" 
                  placeholder="Enter new password" 
                />
                <view class="icon-right" @tap="toggleNewPassword">
                  <uni-icons :type="showNewPwd ? 'eye' : 'eye-slash'" size="28" color="#9CA3AF"></uni-icons>
                </view>
              </view>
              <text class="input-hint">At least 6 characters</text>
            </view>

            <view class="form-group">
              <text class="label">Confirm New Password</text>
              <view class="input-wrapper">
                <view class="icon-left">
                  <uni-icons type="locked" size="28" color="#9CA3AF"></uni-icons>
                </view>
                <input 
                  class="input-pill" 
                  v-model="confirmPassword" 
                  :type="showConfirmPwd ? 'text' : 'password'" 
                  placeholder="Re-enter new password" 
                  @confirm="handleResetPassword"
                />
                <view class="icon-right" @tap="toggleConfirmPassword">
                  <uni-icons :type="showConfirmPwd ? 'eye' : 'eye-slash'" size="28" color="#9CA3AF"></uni-icons>
                </view>
              </view>
            </view>

            <view class="password-requirements">
              <text class="requirements-title">Password Requirements:</text>
              <view class="requirement-item" :class="{ 'met': newPassword.length >= 6 }">
                <uni-icons :type="newPassword.length >= 6 ? 'checkmarkempty' : 'closeempty'" size="18" :color="newPassword.length >= 6 ? '#10B981' : '#9CA3AF'"></uni-icons>
                <text>At least 6 characters</text>
              </view>
              <view class="requirement-item" :class="{ 'met': newPassword === confirmPassword && newPassword.length > 0 }">
                <uni-icons :type="(newPassword === confirmPassword && newPassword.length > 0) ? 'checkmarkempty' : 'closeempty'" size="18" :color="(newPassword === confirmPassword && newPassword.length > 0) ? '#10B981' : '#9CA3AF'"></uni-icons>
                <text>Passwords match</text>
              </view>
            </view>

            <button class="btn-primary-pill" @tap="handleResetPassword" :disabled="loading">
              <text v-if="loading">Resetting password...</text>
              <text v-else>Reset Password</text>
            </button>

            <view class="login-text">
              <text>Remember your password? </text>
              <text class="link" @tap="goToLogin">Back to Sign In</text>
            </view>

            <view class="find-scooter-hint">
              <text class="hint-text">Already signed in? </text>
              <text class="link" @tap="goToFindScooter">Find a Scooter →</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </BaseLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { resetPassword, verifyResetToken } from '@/api/user.js'
import BaseLayout from '@/pages/BaseLayout.vue'

// Reactive state variables
const newPassword = ref('')
const confirmPassword = ref('')
const showNewPwd = ref(false)
const showConfirmPwd = ref(false)
const loading = ref(false)
const verifying = ref(true)
const tokenInvalid = ref(false)
const resetToken = ref('')
const userEmail = ref('')

/**
 * Component mounted lifecycle hook
 * Verify reset token from URL
 */
onMounted(async () => {
  // Get token from URL query parameters
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options || {}
  
  resetToken.value = options.token || ''
  
  if (!resetToken.value) {
    // No token provided in URL
    tokenInvalid.value = true
    verifying.value = false
    return
  }
  
  // Verify token with backend
  await verifyToken()
})

/**
 * Verify reset token validity
 */
const verifyToken = async () => {
  verifying.value = true
  
  try {
    const result = await verifyResetToken(resetToken.value)
    
    // Token is valid, get user email if available
    if (result && result.email) {
      userEmail.value = result.email
    }
    
    tokenInvalid.value = false
  } catch (error) {
    console.error('Token verification failed:', error)
    tokenInvalid.value = true
  } finally {
    verifying.value = false
  }
}

/**
 * Toggle new password visibility
 */
const toggleNewPassword = () => {
  showNewPwd.value = !showNewPwd.value
}

/**
 * Toggle confirm password visibility
 */
const toggleConfirmPassword = () => {
  showConfirmPwd.value = !showConfirmPwd.value
}

/**
 * Handle password reset submission
 */
const handleResetPassword = async () => {
  // Validate new password
  if (!newPassword.value) {
    uni.showToast({
      title: 'Please enter new password',
      icon: 'none',
      duration: 2000
    })
    return
  }
  
  // Validate password length
  if (newPassword.value.length < 6) {
    uni.showToast({
      title: 'Password must be at least 6 characters',
      icon: 'none',
      duration: 2000
    })
    return
  }
  
  // Validate password confirmation
  if (newPassword.value !== confirmPassword.value) {
    uni.showToast({
      title: 'Passwords do not match',
      icon: 'none',
      duration: 2000
    })
    return
  }
  
  // Start loading state
  loading.value = true
  
  try {
    // Call reset password API
    await resetPassword({
      token: resetToken.value,
      newPassword: newPassword.value
    })
    
    // Show success message
    uni.showToast({
      title: 'Password reset successfully!',
      icon: 'success',
      duration: 2000
    })
    
    // Navigate to login page after success
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/login' })
    }, 2000)
    
  } catch (error) {
    console.error('Password reset failed:', error)
    // Error message is already handled by request.js
  } finally {
    loading.value = false
  }
}

/**
 * Navigate to find scooter page
 */
const goToFindScooter = () => {
  uni.reLaunch({ url: '/pages/find-scooter' })
}

/**
 * Navigate to login page
 */
const goToLogin = () => {
  uni.reLaunch({ url: '/pages/login' })
}

/**
 * Navigate to forgot password page
 */
const goToForget = () => {
  uni.redirectTo({ url: '/pages/forget-password' })
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

/* Loading State */
.loading-state {
  text-align: center;
  padding: 60rpx 0;
}

.loading-icon {
  width: 120rpx;
  height: 120rpx;
  background: rgba(37, 99, 235, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 30rpx;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.loading-text {
  font-size: 32rpx;
  color: #6B7280;
}

/* Error State */
.error-state {
  text-align: center;
  padding: 40rpx 0;
}

.error-icon {
  width: 120rpx;
  height: 120rpx;
  background: rgba(239, 68, 68, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 30rpx;
}

.error-title {
  display: block;
  font-size: 42rpx;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 20rpx;
}

.error-message {
  display: block;
  font-size: 28rpx;
  color: #6B7280;
  line-height: 1.6;
  margin-bottom: 40rpx;
}

.form-container {
  width: 100%;
}

.card-header {
  text-align: center;
  margin-bottom: 60rpx;
}

.lock-icon {
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

.email-display {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 20rpx 30rpx;
  background: rgba(37, 99, 235, 0.05);
  border-radius: 40rpx;
  margin: 20rpx auto 0;
  max-width: 500rpx;
}

.email-text {
  font-size: 26rpx;
  color: #4B5563;
  font-weight: 500;
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

.icon-left {
  position: absolute;
  left: 32rpx;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-right {
  position: absolute;
  right: 32rpx;
  z-index: 1;
  cursor: pointer;
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

.password-requirements {
  background: #F9FAFB;
  padding: 30rpx;
  border-radius: 20rpx;
  margin-bottom: 40rpx;
}

.requirements-title {
  display: block;
  font-size: 26rpx;
  font-weight: 600;
  color: #4B5563;
  margin-bottom: 16rpx;
}

.requirement-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 12rpx;
  font-size: 26rpx;
  color: #6B7280;
}

.requirement-item.met {
  color: #10B981;
}

.requirement-item:last-child {
  margin-bottom: 0;
}

.btn-primary-pill {
  width: 100%;
  background: linear-gradient(135deg, #2563EB, #1d4ed8);
  color: white;
  height: 110rpx;
  line-height: 110rpx;
  border-radius: 55rpx; 
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

.btn-secondary-pill {
  width: 100%;
  background: white;
  color: #2563EB;
  height: 110rpx;
  line-height: 110rpx;
  border-radius: 55rpx; 
  font-size: 34rpx;
  font-weight: 600;
  border: 2rpx solid #2563EB;
  transition: all 0.3s;
}

.btn-secondary-pill::after {
  border: none;
}

.login-text {
  text-align: center;
  font-size: 30rpx;
  color: #6B7280;
  margin-bottom: 24rpx;
}

.find-scooter-hint {
  text-align: center;
  font-size: 28rpx;
  color: #6B7280;
  padding: 24rpx;
  background: rgba(37, 99, 235, 0.04);
  border-radius: 20rpx;
  border: 1rpx solid rgba(37, 99, 235, 0.08);
}

.hint-text {
  color: #6B7280;
}

.link {
  color: #2563EB;
  font-weight: 600;
  cursor: pointer;
}
</style>