<template>
  <BaseLayout nav-type="signup" :show-menu="true" :show-footer="true" :content-padding-top="148">
    <view class="main-container">
      <view class="signup-card">

        <!-- ===== Card Header ===== -->
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

        <!-- ===== Form ===== -->
        <view class="form">

          <!-- Username -->
          <view class="form-group">
            <text class="label">Username</text>
            <view class="input-wrapper" :class="fieldState('username')">
              <view class="icon-left">
                <uni-icons type="person" size="24" :color="iconColor('username')"></uni-icons>
              </view>
              <input
                class="input-pill"
                v-model="username"
                type="text"
                placeholder="Choose a unique username"
                @blur="validateField('username')"
                @input="clearError('username')"
              />
              <view class="icon-right" v-if="touched.username">
                <uni-icons :type="errors.username ? 'closeempty' : 'checkmarkempty'" size="20" :color="errors.username ? '#EF4444' : '#10B981'"></uni-icons>
              </view>
            </view>
            <view class="hint-row" v-if="errors.username">
              <uni-icons type="info" size="14" color="#EF4444"></uni-icons>
              <text class="hint-error">{{ errors.username }}</text>
            </view>
            <view class="hint-row" v-else-if="touched.username && !errors.username">
              <uni-icons type="checkmarkempty" size="14" color="#10B981"></uni-icons>
              <text class="hint-success">Username looks good!</text>
            </view>
            <text class="input-hint" v-else>3–20 characters, letters, numbers and underscores only</text>
          </view>

          <!-- Email -->
          <view class="form-group">
            <text class="label">Email Address</text>
            <view class="input-wrapper" :class="fieldState('email')">
              <view class="icon-left">
                <uni-icons type="email" size="24" :color="iconColor('email')"></uni-icons>
              </view>
              <input
                class="input-pill"
                v-model="email"
                type="text"
                placeholder="your.email@example.com"
                @blur="validateField('email')"
                @input="clearError('email')"
              />
              <view class="icon-right" v-if="touched.email">
                <uni-icons :type="errors.email ? 'closeempty' : 'checkmarkempty'" size="20" :color="errors.email ? '#EF4444' : '#10B981'"></uni-icons>
              </view>
            </view>
            <view class="hint-row" v-if="errors.email">
              <uni-icons type="info" size="14" color="#EF4444"></uni-icons>
              <text class="hint-error">{{ errors.email }}</text>
            </view>
            <view class="hint-row" v-else-if="touched.email && !errors.email">
              <uni-icons type="checkmarkempty" size="14" color="#10B981"></uni-icons>
              <text class="hint-success">Valid email address</text>
            </view>
            <text class="input-hint" v-else>We'll send a confirmation to this address</text>
          </view>

          <!-- Phone -->
          <view class="form-group">
            <text class="label">Phone Number</text>
            <view class="input-wrapper" :class="fieldState('phone')">
              <view class="icon-left">
                <uni-icons type="phone" size="24" :color="iconColor('phone')"></uni-icons>
              </view>
              <input
                class="input-pill"
                v-model="phone"
                type="text"
                placeholder="07123456789"
                @blur="validateField('phone')"
                @input="clearError('phone')"
              />
              <view class="icon-right" v-if="touched.phone">
                <uni-icons :type="errors.phone ? 'closeempty' : 'checkmarkempty'" size="20" :color="errors.phone ? '#EF4444' : '#10B981'"></uni-icons>
              </view>
            </view>
            <view class="hint-row" v-if="errors.phone">
              <uni-icons type="info" size="14" color="#EF4444"></uni-icons>
              <text class="hint-error">{{ errors.phone }}</text>
            </view>
            <view class="hint-row" v-else-if="touched.phone && !errors.phone">
              <uni-icons type="checkmarkempty" size="14" color="#10B981"></uni-icons>
              <text class="hint-success">Valid UK mobile number</text>
            </view>
            <text class="input-hint" v-else>UK format: starts with 07, 11 digits total</text>
          </view>

          <!-- Password -->
          <view class="form-group">
            <text class="label">Password</text>
            <view class="input-wrapper" :class="fieldState('password')">
              <view class="icon-left">
                <uni-icons type="locked" size="24" :color="iconColor('password')"></uni-icons>
              </view>
              <input
                class="input-pill"
                v-model="password"
                :type="showPwd ? 'text' : 'password'"
                placeholder="Create a strong password"
                @blur="validateField('password')"
                @input="onPasswordInput"
              />
              <view class="icon-right" @tap="togglePassword">
                <uni-icons :type="showPwd ? 'eye' : 'eye-slash'" size="24" color="#9CA3AF"></uni-icons>
              </view>
            </view>
            <!-- Password strength bar -->
            <view class="pwd-strength-wrap" v-if="password.length > 0">
              <view class="pwd-strength-bar">
                <view
                  class="pwd-strength-fill"
                  :style="{ width: pwdStrength.pct + '%', background: pwdStrength.color }"
                ></view>
              </view>
              <text class="pwd-strength-label" :style="{ color: pwdStrength.color }">{{ pwdStrength.label }}</text>
            </view>
            <view class="hint-row" v-if="errors.password">
              <uni-icons type="info" size="14" color="#EF4444"></uni-icons>
              <text class="hint-error">{{ errors.password }}</text>
            </view>
            <text class="input-hint" v-else>Min 6 characters · Mix letters, numbers &amp; symbols for strength</text>
          </view>

          <!-- Confirm Password -->
          <view class="form-group">
            <text class="label">Confirm Password</text>
            <view class="input-wrapper" :class="fieldState('confirmPassword')">
              <view class="icon-left">
                <uni-icons type="locked" size="24" :color="iconColor('confirmPassword')"></uni-icons>
              </view>
              <input
                class="input-pill"
                v-model="confirmPassword"
                :type="showConfirmPwd ? 'text' : 'password'"
                placeholder="Re-enter your password"
                @blur="validateField('confirmPassword')"
                @input="clearError('confirmPassword')"
              />
              <view class="icon-right" @tap="toggleConfirmPassword">
                <uni-icons :type="showConfirmPwd ? 'eye' : 'eye-slash'" size="24" color="#9CA3AF"></uni-icons>
              </view>
            </view>
            <view class="hint-row" v-if="errors.confirmPassword">
              <uni-icons type="info" size="14" color="#EF4444"></uni-icons>
              <text class="hint-error">{{ errors.confirmPassword }}</text>
            </view>
            <view class="hint-row" v-else-if="touched.confirmPassword && !errors.confirmPassword">
              <uni-icons type="checkmarkempty" size="14" color="#10B981"></uni-icons>
              <text class="hint-success">Passwords match!</text>
            </view>
            <text class="input-hint" v-else>Must match the password above</text>
          </view>

          <!-- Terms -->
          <view class="terms-row" :class="{ 'terms-error': errors.terms }">
            <checkbox-group @change="toggleTerms">
              <label class="checkbox-container">
                <checkbox value="agree" :checked="agreeTerms" color="#2563EB" style="transform:scale(0.8)" />
                <text class="checkbox-label">
                  I agree to the <text class="link-text">Terms of Service</text> and <text class="link-text">Privacy Policy</text>
                </text>
              </label>
            </checkbox-group>
            <view class="hint-row" v-if="errors.terms" style="margin-left: 8rpx; margin-top: 8rpx;">
              <uni-icons type="info" size="14" color="#EF4444"></uni-icons>
              <text class="hint-error">{{ errors.terms }}</text>
            </view>
          </view>

          <!-- Submit -->
          <button class="btn-primary-pill" @tap="handleSignup" :disabled="loading">
            <text v-if="loading">Creating your account...</text>
            <text v-else>Create Account</text>
          </button>

          <!-- Global error message -->
          <view class="global-error" v-if="globalError">
            <uni-icons type="info" size="16" color="#EF4444"></uni-icons>
            <text class="global-error-text">{{ globalError }}</text>
          </view>

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
import { ref, reactive, computed } from 'vue'
import { register as registerApi } from '@/api/user.js'
import BaseLayout from '@/pages/BaseLayout.vue'

// ─── Form fields ───────────────────────────────────────────────
const username        = ref('')
const email           = ref('')
const phone           = ref('')
const password        = ref('')
const confirmPassword = ref('')
const showPwd         = ref(false)
const showConfirmPwd  = ref(false)
const agreeTerms      = ref(false)
const loading         = ref(false)
const globalError     = ref('')

// ─── Per-field touched & error state ───────────────────────────
const touched = reactive({ username: false, email: false, phone: false, password: false, confirmPassword: false })
const errors  = reactive({ username: '', email: '', phone: '', password: '', confirmPassword: '', terms: '' })

// ─── Password strength ─────────────────────────────────────────
const pwdStrength = computed(() => {
  const p = password.value
  let score = 0
  if (p.length >= 6)  score++
  if (p.length >= 10) score++
  if (/[A-Z]/.test(p)) score++
  if (/\d/.test(p))    score++
  if (/[^A-Za-z0-9]/.test(p)) score++
  const map = [
    { pct: 20,  color: '#EF4444', label: 'Very Weak' },
    { pct: 40,  color: '#F97316', label: 'Weak' },
    { pct: 60,  color: '#EAB308', label: 'Fair' },
    { pct: 80,  color: '#3B82F6', label: 'Strong' },
    { pct: 100, color: '#10B981', label: 'Very Strong' },
  ]
  return map[Math.min(score, 4)]
})

// ─── Helpers ───────────────────────────────────────────────────
const validateEmail = v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)
const validatePhone = v => /^07\d{9}$/.test(v)

const fieldState = (field) => {
  if (!touched[field]) return ''
  return errors[field] ? 'input-error' : 'input-success'
}
const iconColor = (field) => {
  if (!touched[field]) return '#D1D5DB'
  return errors[field] ? '#EF4444' : '#10B981'
}

const clearError = (field) => { errors[field] = '' }

const validateField = (field) => {
  touched[field] = true
  switch (field) {
    case 'username':
      if (!username.value.trim())
        errors.username = 'Username is required'
      else if (username.value.trim().length < 3)
        errors.username = 'Username must be at least 3 characters'
      else if (username.value.trim().length > 20)
        errors.username = 'Username must be 20 characters or less'
      else if (!/^[a-zA-Z0-9_]+$/.test(username.value.trim()))
        errors.username = 'Only letters, numbers and underscores allowed'
      else
        errors.username = ''
      break
    case 'email':
      if (!email.value.trim())
        errors.email = 'Email address is required'
      else if (!validateEmail(email.value.trim()))
        errors.email = 'Please enter a valid email (e.g. name@example.com)'
      else
        errors.email = ''
      break
    case 'phone':
      if (!phone.value.trim())
        errors.phone = 'Phone number is required'
      else if (!validatePhone(phone.value.trim()))
        errors.phone = 'Must start with 07 and be exactly 11 digits (e.g. 07123456789)'
      else
        errors.phone = ''
      break
    case 'password':
      if (!password.value)
        errors.password = 'Password is required'
      else if (password.value.length < 6)
        errors.password = 'Password must be at least 6 characters'
      else
        errors.password = ''
      // Re-validate confirm too if already touched
      if (touched.confirmPassword) validateField('confirmPassword')
      break
    case 'confirmPassword':
      if (!confirmPassword.value)
        errors.confirmPassword = 'Please confirm your password'
      else if (confirmPassword.value !== password.value)
        errors.confirmPassword = 'Passwords do not match — please re-enter'
      else
        errors.confirmPassword = ''
      break
  }
}

const onPasswordInput = () => {
  clearError('password')
  if (touched.confirmPassword && confirmPassword.value)
    validateField('confirmPassword')
}

const togglePassword        = () => { showPwd.value = !showPwd.value }
const toggleConfirmPassword = () => { showConfirmPwd.value = !showConfirmPwd.value }
const toggleTerms = (e) => {
  agreeTerms.value = e.detail.value.includes('agree')
  if (agreeTerms.value) errors.terms = ''
}

// ─── Submit ────────────────────────────────────────────────────
const handleSignup = async () => {
  globalError.value = ''
  // Validate all fields
  ;['username', 'email', 'phone', 'password', 'confirmPassword'].forEach(f => validateField(f))
  if (!agreeTerms.value) {
    errors.terms = 'You must accept the Terms of Service and Privacy Policy to continue'
  } else {
    errors.terms = ''
  }

  const hasError = Object.values(errors).some(e => !!e)
  if (hasError) {
    uni.showToast({ title: 'Please fix the highlighted fields', icon: 'none', duration: 2000 })
    return
  }

  loading.value = true
  try {
    await registerApi({
      username:     username.value.trim(),
      email:        email.value.trim(),
      phoneNumber:  phone.value.trim(),
      passwordHash: password.value,
    })
    uni.showToast({ title: 'Account created successfully! 🎉', icon: 'success', duration: 2000 })
    setTimeout(() => uni.reLaunch({ url: '/pages/login' }), 2000)
  } catch (error) {
    console.error('Registration failed:', error)
    const code = error?.statusCode || error?.code
    const msg  = (error?.message || error?.msg || '').toLowerCase()
    // Backend returns HTTP 400 with "Username already exists" for duplicates (not 409)
    // So check message content first, before falling back to generic 400 handler
    if (code === 409 || msg.includes('exist') || msg.includes('already') || msg.includes('duplicate'))
      globalError.value = 'This email or username is already registered. Try signing in instead.'
    else if (code === 400)
      globalError.value = 'Some information is invalid. Please check your details and try again.'
    else if (code >= 500)
      globalError.value = 'Server error — please try again in a moment.'
    else
      globalError.value = 'Registration failed. Please check your connection and try again.'
  } finally {
    loading.value = false
  }
}

const goToLogin = () => { uni.navigateTo({ url: '/pages/login' }) }
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
  margin-bottom: 50rpx;
}

.user-icon-bg {
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

/* ========== Form ========== */
.form {
  margin-top: 0;
}

.form-group {
  margin-bottom: 44rpx;
}

.label {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 16rpx;
  margin-left: 20rpx;
}

/* ---- Input wrapper states ---- */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-wrapper.input-error .input-pill {
  border-color: #EF4444;
  background: #FFF5F5;
}

.input-wrapper.input-success .input-pill {
  border-color: #10B981;
  background: #F0FDF4;
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
  background: #fff;
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

/* ---- Hint / error / success rows ---- */
.input-hint {
  display: block;
  font-size: 24rpx;
  color: #9CA3AF;
  margin-left: 24rpx;
  margin-top: 10rpx;
}

.hint-row {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-left: 24rpx;
  margin-top: 10rpx;
}

.hint-error {
  font-size: 24rpx;
  color: #EF4444;
  line-height: 1.4;
}

.hint-success {
  font-size: 24rpx;
  color: #10B981;
}

/* ---- Password strength ---- */
.pwd-strength-wrap {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin: 14rpx 24rpx 0;
}

.pwd-strength-bar {
  flex: 1;
  height: 8rpx;
  background: #E5E7EB;
  border-radius: 4rpx;
  overflow: hidden;
}

.pwd-strength-fill {
  height: 100%;
  border-radius: 4rpx;
  transition: width 0.4s, background 0.4s;
}

.pwd-strength-label {
  font-size: 22rpx;
  font-weight: 600;
  white-space: nowrap;
}

/* ---- Terms ---- */
.terms-row {
  margin: 30rpx 10rpx 40rpx;
  padding: 20rpx;
  border-radius: 16rpx;
  transition: background 0.2s;
}

.terms-row.terms-error {
  background: #FFF5F5;
  border: 1rpx solid #FECACA;
  border-radius: 16rpx;
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

/* ---- Submit button ---- */
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

.btn-primary-pill::after { border: none; }

.btn-primary-pill[disabled] { opacity: 0.6; }

/* ---- Global error ---- */
.global-error {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  background: #FFF5F5;
  border: 1rpx solid #FECACA;
  border-radius: 20rpx;
  padding: 24rpx 28rpx;
  margin-top: 28rpx;
}

.global-error-text {
  font-size: 26rpx;
  color: #DC2626;
  line-height: 1.5;
}

/* ---- Bottom tips ---- */
.bottom-tips {
  text-align: center;
  margin-top: 45rpx;
  font-size: 30rpx;
  color: #6B7280;
}

/* ---- Responsive ---- */
@media (max-width: 750px) {
  .signup-card    { padding: 60rpx 40rpx; }
  .main-container { padding: 40rpx 30rpx; }
}
</style>