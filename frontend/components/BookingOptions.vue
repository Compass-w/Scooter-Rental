<template>
  <view v-if="visible && scooter" class="booking-overlay" @tap="handleBackdropClose">
    <view class="booking-sheet" @tap.stop>
      <view class="sheet-handle"></view>

      <view class="sheet-header">
        <view>
          <text class="sheet-title">Booking Options</text>
          <text class="sheet-subtitle">Choose a hire period for scooter #{{ scooter.id }}</text>
        </view>
        <view class="sheet-close" @tap="handleClose">
          <uni-icons type="closeempty" size="22" color="#64748B"></uni-icons>
        </view>
      </view>

      <view class="scooter-summary">
        <view class="summary-pill">
          <uni-icons type="navigate" size="16" color="#2563EB"></uni-icons>
          <text class="summary-text">{{ scooter.model || 'Scooter' }}</text>
        </view>
        <view class="summary-pill">
          <uni-icons type="wallet" size="16" color="#2563EB"></uni-icons>
          <text class="summary-text">Packages from {{ formatCny(RENTAL_PACKAGE_PRICING.oneHour) }} per hour</text>
        </view>
      </view>

      <view class="section-block">
        <text class="section-title">Hire Period</text>
        <view class="plan-grid">
          <view
            v-for="option in planOptions"
            :key="option.value"
            :class="['plan-card', selectedPlan === option.value ? 'plan-card-active' : '']"
            @tap="selectedPlan = option.value"
          >
            <text class="plan-label">{{ option.label }}</text>
            <text class="plan-meta">{{ option.subtitle }}</text>
            <text class="plan-price">{{ formatCny(option.fixedPrice) }}</text>
          </view>
        </view>
      </view>

      <view class="section-block">
        <view class="section-row">
          <text class="section-title">Payment</text>
          <view class="demo-badge">
            <text class="demo-badge-text">Demo card flow</text>
          </view>
        </view>

        <view class="payment-form">
          <view class="input-group">
            <text class="input-label">Cardholder name</text>
            <input
              v-model="cardForm.name"
              class="input-control"
              type="text"
              maxlength="40"
              placeholder="Alex Rider"
            />
          </view>

          <view class="input-group">
            <text class="input-label">Card number</text>
            <input
              v-model="cardForm.number"
              class="input-control"
              type="number"
              maxlength="16"
              placeholder="4242424242424242"
            />
          </view>

          <view class="payment-row">
            <view class="input-group input-half">
              <text class="input-label">Expiry</text>
              <input
                v-model="cardForm.expiry"
                class="input-control"
                type="text"
                maxlength="5"
                placeholder="12/28"
              />
            </view>

            <view class="input-group input-half">
              <text class="input-label">CVC</text>
              <input
                v-model="cardForm.cvc"
                class="input-control"
                type="number"
                maxlength="4"
                placeholder="123"
              />
            </view>
          </view>
        </view>
      </view>

      <view class="section-block pricing-card">
        <view class="pricing-row">
          <text class="pricing-label">Hire period</text>
          <text class="pricing-value">{{ selectedOption.label }}</text>
        </view>
        <view class="pricing-row">
          <text class="pricing-label">Package price</text>
          <text class="pricing-value">{{ formatCny(totalPrice) }}</text>
        </view>
        <view class="pricing-row">
          <text class="pricing-label">Included time</text>
          <text class="pricing-value">{{ selectedOption.subtitle }}</text>
        </view>
        <view class="pricing-row pricing-row-total">
          <text class="pricing-total-label">Total today</text>
          <text class="pricing-total-value">{{ formatCny(totalPrice) }}</text>
        </view>
      </view>

      <text class="sheet-note">
        This simulates a card payment before we call the start ride API. No real charge is made.
      </text>

      <button
        class="confirm-button"
        :disabled="paymentProcessing || submitting"
        @tap="handleConfirm"
      >
        <text v-if="paymentProcessing">Authorising card...</text>
        <text v-else-if="submitting">Starting ride...</text>
        <text v-else>Pay {{ formatCny(totalPrice) }} and start ride</text>
      </button>
    </view>
  </view>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { formatCny, RENTAL_PACKAGE_PRICING } from '@/utils/pricing.js'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  scooter: {
    type: Object,
    default: null
  },
  submitting: {
    type: Boolean,
    default: false
  },
  preferredPlan: {
    type: String,
    default: '1_HOUR'
  }
})

const emit = defineEmits(['close', 'confirm'])

const planOptions = [
  { value: '1_HOUR', label: '1 hour', subtitle: '60 minutes included', minutes: 60, fixedPrice: RENTAL_PACKAGE_PRICING.oneHour },
  { value: '1_DAY', label: '1 day', subtitle: '24 hours included', minutes: 1440, fixedPrice: RENTAL_PACKAGE_PRICING.oneDay },
  { value: '1_WEEK', label: '1 week', subtitle: '7 days included', minutes: 10080, fixedPrice: RENTAL_PACKAGE_PRICING.oneWeek },
  { value: '1_MONTH', label: '1 month', subtitle: '30 days included', minutes: 43200, fixedPrice: RENTAL_PACKAGE_PRICING.oneMonth }
]

const selectedPlan = ref('1_HOUR')
const paymentProcessing = ref(false)
const cardForm = ref({
  name: 'Scooter Rider',
  number: '4242424242424242',
  expiry: '12/28',
  cvc: '123'
})

const selectedOption = computed(() =>
  planOptions.find(option => option.value === selectedPlan.value) || planOptions[0]
)

const totalPrice = computed(() => selectedOption.value.fixedPrice)

watch(
  () => [props.visible, props.scooter?.id, props.preferredPlan],
  ([isVisible]) => {
    if (!isVisible) {
      paymentProcessing.value = false
      return
    }

    selectedPlan.value = planOptions.some(option => option.value === props.preferredPlan)
      ? props.preferredPlan
      : '1_HOUR'
    paymentProcessing.value = false
  }
)

const handleBackdropClose = () => {
  if (paymentProcessing.value || props.submitting) return
  emit('close')
}

const handleClose = () => {
  if (paymentProcessing.value || props.submitting) return
  emit('close')
}

const validatePayment = () => {
  const digits = String(cardForm.value.number || '').replace(/\D/g, '')
  const expiry = String(cardForm.value.expiry || '').trim()
  const cvc = String(cardForm.value.cvc || '').replace(/\D/g, '')

  if (!String(cardForm.value.name || '').trim()) {
    uni.showToast({ title: 'Enter cardholder name', icon: 'none' })
    return false
  }
  if (digits.length < 12) {
    uni.showToast({ title: 'Enter a valid card number', icon: 'none' })
    return false
  }
  if (!/^\d{2}\/\d{2}$/.test(expiry)) {
    uni.showToast({ title: 'Use expiry in MM/YY', icon: 'none' })
    return false
  }
  if (cvc.length < 3) {
    uni.showToast({ title: 'Enter a valid CVC', icon: 'none' })
    return false
  }
  return true
}

const handleConfirm = async () => {
  if (!props.scooter || paymentProcessing.value || props.submitting) return
  if (!validatePayment()) return

  paymentProcessing.value = true
  await new Promise(resolve => setTimeout(resolve, 1400))
  paymentProcessing.value = false

  const digits = String(cardForm.value.number || '').replace(/\D/g, '')
  emit('confirm', {
    planType: selectedOption.value.value,
    durationMinutes: selectedOption.value.minutes,
    totalPrice: Number(totalPrice.value.toFixed(2)),
    cardLast4: digits.slice(-4)
  })
}
</script>

<style scoped>
.booking-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.56);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  z-index: 1600;
  padding: calc(env(safe-area-inset-top) + 150rpx) 24rpx calc(env(safe-area-inset-bottom) + 24rpx);
  box-sizing: border-box;
  overflow-y: auto;
}

.booking-sheet {
  width: 100%;
  max-width: 860rpx;
  background: linear-gradient(180deg, #F8FBFF 0%, #FFFFFF 35%);
  border-radius: 36rpx;
  padding: 24rpx 28rpx 36rpx;
  box-shadow: 0 24rpx 60rpx rgba(15, 23, 42, 0.18);
  max-height: calc(100vh - env(safe-area-inset-top) - 190rpx);
  overflow-y: auto;
  margin: 0 auto;
}

.sheet-handle {
  width: 88rpx;
  height: 8rpx;
  border-radius: 999rpx;
  background: #cbd5e1;
  margin: 0 auto 24rpx;
}

.sheet-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.sheet-title {
  display: block;
  font-size: 38rpx;
  font-weight: 700;
  color: #0f172a;
}

.sheet-subtitle {
  margin-top: 8rpx;
  display: block;
  font-size: 25rpx;
  color: #475569;
}

.sheet-close {
  width: 68rpx;
  height: 68rpx;
  border-radius: 18rpx;
  background: #EFF6FF;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.scooter-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 28rpx;
}

.summary-pill {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 14rpx 18rpx;
  border-radius: 999rpx;
  background: #EFF6FF;
  border: 1rpx solid rgba(37, 99, 235, 0.14);
}

.summary-text {
  font-size: 24rpx;
  color: #1D4ED8;
  font-weight: 600;
}

.section-block {
  margin-bottom: 28rpx;
}

.section-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.section-title {
  display: block;
  font-size: 29rpx;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 18rpx;
}

.plan-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.plan-card {
  background: #f8fafc;
  border: 2rpx solid #e2e8f0;
  border-radius: 24rpx;
  padding: 22rpx;
}

.plan-card-active {
  border-color: #1D4ED8;
  background: linear-gradient(180deg, #EFF6FF 0%, #F8FBFF 100%);
  box-shadow: 0 10rpx 24rpx rgba(37, 99, 235, 0.14);
}

.plan-label {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #0f172a;
}

.plan-meta {
  display: block;
  margin-top: 6rpx;
  font-size: 23rpx;
  color: #64748b;
}

.plan-price {
  display: block;
  margin-top: 14rpx;
  font-size: 30rpx;
  font-weight: 700;
  color: #1D4ED8;
}

.demo-badge {
  margin-bottom: 18rpx;
  padding: 10rpx 16rpx;
  background: #DBEAFE;
  border-radius: 999rpx;
}

.demo-badge-text {
  font-size: 22rpx;
  color: #1D4ED8;
  font-weight: 600;
}

.payment-form {
  background: #fff;
  border: 2rpx solid #e2e8f0;
  border-radius: 28rpx;
  padding: 22rpx;
}

.payment-row {
  display: flex;
  gap: 16rpx;
}

.input-group {
  margin-bottom: 18rpx;
}

.input-group:last-child {
  margin-bottom: 0;
}

.input-half {
  flex: 1;
  min-width: 0;
}

.input-label {
  display: block;
  margin-bottom: 10rpx;
  font-size: 24rpx;
  color: #475569;
  font-weight: 600;
}

.input-control {
  width: 100%;
  height: 88rpx;
  border-radius: 20rpx;
  background: #f8fafc;
  border: 2rpx solid #e2e8f0;
  padding: 0 24rpx;
  box-sizing: border-box;
  overflow-y: auto;
  font-size: 28rpx;
  color: #0f172a;
}

.pricing-card {
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  border-radius: 28rpx;
  padding: calc(env(safe-area-inset-top) + 150rpx) 24rpx calc(env(safe-area-inset-bottom) + 24rpx);
}

.pricing-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
  margin-bottom: 14rpx;
}

.pricing-row:last-child {
  margin-bottom: 0;
}

.pricing-row-total {
  margin-top: 12rpx;
  padding-top: 18rpx;
  border-top: 1rpx solid rgba(255, 255, 255, 0.16);
}

.pricing-label,
.pricing-value {
  font-size: 25rpx;
  color: rgba(255, 255, 255, 0.8);
}

.pricing-total-label,
.pricing-total-value {
  font-size: 32rpx;
  font-weight: 700;
  color: #f8fafc;
}

.sheet-note {
  display: block;
  font-size: 23rpx;
  color: #64748b;
  line-height: 1.5;
  margin-bottom: 22rpx;
}

.confirm-button {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #2563EB 0%, #1D4ED8 100%);
  color: #fff;
  font-size: 30rpx;
  font-weight: 700;
  box-shadow: 0 12rpx 24rpx rgba(37, 99, 235, 0.22);
}

.confirm-button::after {
  border: none;
}

.confirm-button[disabled] {
  opacity: 0.68;
}
</style>

