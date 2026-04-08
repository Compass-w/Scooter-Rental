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
          <uni-icons type="navigate" size="16" color="#0F766E"></uni-icons>
          <text class="summary-text">{{ scooter.model || 'Scooter' }}</text>
        </view>
        <view class="summary-pill">
          <uni-icons type="wallet" size="16" color="#0F766E"></uni-icons>
          <text class="summary-text">Base £{{ formatMoney(basePrice) }} + £{{ formatMoney(pricePerMinute) }}/min</text>
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
            <text class="plan-price">£{{ formatMoney(calculatePrice(option.minutes)) }}</text>
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
          <text class="pricing-label">Unlock fee</text>
          <text class="pricing-value">£{{ formatMoney(basePrice) }}</text>
        </view>
        <view class="pricing-row">
          <text class="pricing-label">Usage charge</text>
          <text class="pricing-value">£{{ formatMoney(usageCharge) }}</text>
        </view>
        <view class="pricing-row pricing-row-total">
          <text class="pricing-total-label">Total today</text>
          <text class="pricing-total-value">£{{ formatMoney(totalPrice) }}</text>
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
        <text v-else>Pay £{{ formatMoney(totalPrice) }} and start ride</text>
      </button>
    </view>
  </view>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

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
  }
})

const emit = defineEmits(['close', 'confirm'])

const planOptions = [
  { value: '1_HOUR', label: '1hr', subtitle: '60 minutes', minutes: 60 },
  { value: '4_HOURS', label: '4hrs', subtitle: '240 minutes', minutes: 240 },
  { value: '1_DAY', label: '1day', subtitle: '24 hours', minutes: 1440 },
  { value: '1_WEEK', label: '1week', subtitle: '7 days', minutes: 10080 }
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

const basePrice = computed(() => Number(props.scooter?.basePrice ?? 0))
const pricePerMinute = computed(() => Number(props.scooter?.pricePerMin ?? 0))
const usageCharge = computed(() => calculatePrice(selectedOption.value.minutes) - basePrice.value)
const totalPrice = computed(() => calculatePrice(selectedOption.value.minutes))

watch(
  () => [props.visible, props.scooter?.id],
  ([isVisible]) => {
    if (!isVisible) {
      paymentProcessing.value = false
      return
    }

    selectedPlan.value = '1_HOUR'
    paymentProcessing.value = false
  }
)

const calculatePrice = (minutes) => {
  return basePrice.value + (pricePerMinute.value * minutes)
}

const formatMoney = (value) => {
  return Number(value || 0).toFixed(2)
}

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
    totalPrice: Number(formatMoney(totalPrice.value)),
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
  align-items: flex-end;
  justify-content: center;
  z-index: 900;
  padding: 24rpx;
  box-sizing: border-box;
}

.booking-sheet {
  width: 100%;
  max-width: 860rpx;
  background: linear-gradient(180deg, #f8fffd 0%, #ffffff 35%);
  border-radius: 36rpx;
  padding: 24rpx 28rpx 36rpx;
  box-shadow: 0 -20rpx 60rpx rgba(15, 23, 42, 0.18);
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
  background: #eef2ff;
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
  background: #ecfeff;
}

.summary-text {
  font-size: 24rpx;
  color: #0f766e;
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
  border-color: #0f766e;
  background: linear-gradient(180deg, #ecfeff 0%, #f0fdfa 100%);
  box-shadow: 0 10rpx 24rpx rgba(15, 118, 110, 0.12);
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
  color: #0f766e;
}

.demo-badge {
  margin-bottom: 18rpx;
  padding: 10rpx 16rpx;
  background: #dcfce7;
  border-radius: 999rpx;
}

.demo-badge-text {
  font-size: 22rpx;
  color: #166534;
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
  font-size: 28rpx;
  color: #0f172a;
}

.pricing-card {
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  border-radius: 28rpx;
  padding: 24rpx;
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
  background: linear-gradient(135deg, #0f766e 0%, #115e59 100%);
  color: #fff;
  font-size: 30rpx;
  font-weight: 700;
  box-shadow: 0 12rpx 24rpx rgba(15, 118, 110, 0.22);
}

.confirm-button::after {
  border: none;
}

.confirm-button[disabled] {
  opacity: 0.68;
}
</style>
