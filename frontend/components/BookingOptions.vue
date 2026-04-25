<template>
  <view v-if="visible && scooter" class="booking-overlay" @tap="handleBackdropClose">
    <view class="booking-sheet" @tap.stop>
      <view class="sheet-handle"></view>

      <view class="sheet-header">
        <view>
          <text class="sheet-title">Booking Options</text>
          <text class="sheet-subtitle">Choose your plan and payment method. Extra details stay hidden until you open them.</text>
        </view>
        <view class="sheet-close" @tap="handleClose">
          <uni-icons type="closeempty" size="22" color="#64748B"></uni-icons>
        </view>
      </view>

      <view class="scooter-hero">
        <view class="hero-photo-frame" @tap="previewVisible = true">
          <image class="hero-photo" :src="selectedVehicle.imageUrl" mode="aspectFill" />
          <view class="hero-photo-badge">
            <uni-icons type="search" size="14" color="#FFFFFF"></uni-icons>
            <text class="hero-photo-badge-text">Tap to enlarge</text>
          </view>
        </view>

        <view class="hero-copy">
          <view class="hero-title-row">
            <text class="hero-model">{{ selectedVehicle.displayName }}</text>
            <view class="hero-category-pill">
              <text class="hero-category-pill-text">{{ selectedVehicle.category }}</text>
            </view>
          </view>
          <text class="hero-summary">{{ selectedVehicle.performanceSummary }}</text>

          <view class="hero-chip-row">
            <view class="hero-chip">
              <uni-icons type="paperplane" size="16" color="#2563EB"></uni-icons>
              <text class="hero-chip-text">{{ selectedVehicle.specs.topSpeedKph }} km/h</text>
            </view>
            <view class="hero-chip">
              <uni-icons type="paperplane" size="16" color="#2563EB"></uni-icons>
              <text class="hero-chip-text">{{ selectedVehicle.specs.rangeKm }} km range</text>
            </view>
            <view class="hero-chip">
              <uni-icons type="bolt" size="16" color="#2563EB"></uni-icons>
              <text class="hero-chip-text">{{ startingBatteryLevel }}% battery</text>
            </view>
          </view>

          <text class="hero-credit">{{ selectedVehicle.photoCredit }}</text>
        </view>
      </view>

      <view class="section-block">
        <text class="section-title">Rental Setup</text>

        <view class="toggle-row">
          <view
            v-for="service in serviceModes"
            :key="service.code"
            :class="['toggle-card', selectedServiceMode === service.code ? 'toggle-card-active' : '']"
            @tap="selectedServiceMode = service.code"
          >
            <text class="toggle-title">{{ service.label }}</text>
            <text class="toggle-desc">{{ service.summary }}</text>
          </view>
        </view>

        <view class="toggle-row market-row">
          <view
            v-for="market in marketProfiles"
            :key="market.code"
            :class="['toggle-card toggle-card-compact', selectedMarketCode === market.code ? 'toggle-card-active' : '']"
            @tap="selectedMarketCode = market.code"
          >
            <text class="toggle-title">{{ market.label }}</text>
            <text class="toggle-desc">{{ market.registrationLabel }}</text>
          </view>
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
            <text v-if="option.discountNote" class="plan-discount">{{ option.discountNote }}</text>
            <text class="plan-price">{{ formatCurrency(option.fixedPrice, activeCurrencyCode) }}</text>
          </view>
        </view>
      </view>

      <view class="section-block">
        <text class="section-title">Trip Summary</text>
        <view class="detail-grid detail-grid-compact">
          <view class="detail-card">
            <text class="detail-label">Starting battery</text>
            <text class="detail-value">{{ startingBatteryLevel }}%</text>
          </view>
          <view class="detail-card">
            <text class="detail-label">Overtime fee</text>
            <text class="detail-value">{{ formatCurrency(overtimeFeePer15Minutes, activeCurrencyCode) }}/15 min</text>
          </view>
        </view>
      </view>

      <view class="section-block">
        <view class="section-row">
          <text class="section-title">Registration & Payment</text>
          <view class="demo-badge">
            <text class="demo-badge-text">{{ requiresCardBinding ? 'Card binding required' : 'Real-name flow' }}</text>
          </view>
        </view>

        <view v-if="requiresRealName" class="payment-form">
          <view class="input-group">
            <text class="input-label">Real name</text>
            <input
              v-model="identityForm.name"
              :class="['input-control', formErrors.identityName ? 'input-control-error' : '']"
              type="text"
              maxlength="40"
              placeholder="Zhang Wei"
              @input="clearFieldError('identityName')"
            />
            <text v-if="formErrors.identityName" class="input-error">{{ formErrors.identityName }}</text>
          </view>
          <view class="input-group">
            <text class="input-label">National ID / passport</text>
            <input
              v-model="identityForm.idNumber"
              :class="['input-control', formErrors.identityNumber ? 'input-control-error' : '']"
              type="text"
              maxlength="24"
              placeholder="110101199901011234"
              @input="clearFieldError('identityNumber')"
            />
            <text v-if="formErrors.identityNumber" class="input-error">{{ formErrors.identityNumber }}</text>
          </view>
          <text class="flow-note">China sharing flow: real-name verification must be completed before scan-to-unlock is enabled.</text>
        </view>

        <view v-else class="payment-form">
          <view class="input-group">
            <text class="input-label">{{ selectedServiceMode === 'WALK_IN' ? 'Customer name' : 'Cardholder name' }}</text>
            <input
              v-model="cardForm.name"
              :class="['input-control', formErrors.cardName ? 'input-control-error' : '']"
              type="text"
              maxlength="40"
              placeholder="Alex Rider"
              @input="clearFieldError('cardName')"
            />
            <text v-if="formErrors.cardName" class="input-error">{{ formErrors.cardName }}</text>
          </view>

          <view class="input-group">
            <text class="input-label">Card number</text>
            <input
              v-model="cardForm.number"
              :class="['input-control', formErrors.cardNumber ? 'input-control-error' : '']"
              type="number"
              maxlength="16"
              placeholder="4242424242424242"
              @input="clearFieldError('cardNumber')"
            />
            <text v-if="formErrors.cardNumber" class="input-error">{{ formErrors.cardNumber }}</text>
          </view>

          <view class="payment-row">
            <view class="input-group input-half">
              <text class="input-label">Expiry</text>
              <input
                v-model="cardForm.expiry"
                :class="['input-control', formErrors.cardExpiry ? 'input-control-error' : '']"
                type="text"
                maxlength="5"
                placeholder="12/28"
                @input="clearFieldError('cardExpiry')"
              />
              <text v-if="formErrors.cardExpiry" class="input-error">{{ formErrors.cardExpiry }}</text>
            </view>

            <view class="input-group input-half">
              <text class="input-label">CVC</text>
              <input
                v-model="cardForm.cvc"
                :class="['input-control', formErrors.cardCvc ? 'input-control-error' : '']"
                type="number"
                maxlength="4"
                placeholder="123"
                @input="clearFieldError('cardCvc')"
              />
              <text v-if="formErrors.cardCvc" class="input-error">{{ formErrors.cardCvc }}</text>
            </view>
          </view>

          <text class="flow-note" v-if="selectedMarketCode === 'UK'">UK flow: a saved credit card is required at registration so overtime, parking penalties, or damage charges can be collected automatically.</text>
          <text class="flow-note" v-else>Walk-in flow: the clerk can bind a credit card on behalf of the customer, while registration remains optional for store-only rentals.</text>
        </view>
      </view>

      <view v-if="selectedServiceMode === 'WALK_IN'" class="section-block">
        <view class="section-row">
          <text class="section-title">Store Rental Setup</text>
          <view class="demo-badge">
            <text class="demo-badge-text">{{ selectedStoreChannelProfile.label }}</text>
          </view>
        </view>

        <view class="toggle-row">
          <view
            v-for="channel in storeRentalChannels"
            :key="channel.code"
            :class="['toggle-card', selectedStoreChannel === channel.code ? 'toggle-card-active' : '']"
            @tap="selectedStoreChannel = channel.code"
          >
            <text class="toggle-title">{{ channel.label }}</text>
            <text class="toggle-desc">{{ channel.summary }}</text>
          </view>
        </view>

        <view class="payment-form">
          <view class="input-group">
            <text class="input-label">Pickup store</text>
            <picker :range="storeBranchOptions" range-key="label" :value="pickupStoreIndex" @change="handlePickupStoreChange">
              <view :class="['input-control', 'input-control-picker', formErrors.pickupStore ? 'input-control-error' : '']">{{ pickupStoreLabel }}</view>
            </picker>
            <text v-if="formErrors.pickupStore" class="input-error">{{ formErrors.pickupStore }}</text>
            <text class="flow-note">{{ buildStoreAddressLabel(pickupStore) }}</text>
          </view>

          <view class="input-group">
            <text class="input-label">Return store</text>
            <picker :range="storeBranchOptions" range-key="label" :value="returnStoreIndex" @change="handleReturnStoreChange">
              <view :class="['input-control', 'input-control-picker', formErrors.returnStore ? 'input-control-error' : '']">{{ returnStoreLabel }}</view>
            </picker>
            <text v-if="formErrors.returnStore" class="input-error">{{ formErrors.returnStore }}</text>
            <text class="flow-note">{{ buildStoreAddressLabel(returnStore) }}</text>
          </view>
        </view>
      </view>

      <view class="section-block">
        <label class="liability-check">
          <checkbox :checked="liabilityAccepted" color="#2563EB" style="transform:scale(0.82)" @tap.stop="toggleLiability" />
          <text class="liability-copy">I understand that road-law compliance remains the rider's responsibility, and uncovered penalties or damage may still be charged.</text>
        </label>
        <text v-if="formErrors.liabilityAccepted" class="input-error input-error-block">{{ formErrors.liabilityAccepted }}</text>
      </view>

      <view v-if="validationSummary.length" class="section-block">
        <view class="validation-card">
          <text class="validation-title">Please complete the required information</text>
          <text class="validation-copy">{{ validationSummary.join(' · ') }}</text>
        </view>
      </view>

      <view class="section-block">
        <view class="disclosure-card">
          <view class="disclosure-header" @tap="toggleDisclosure('vehicle')">
            <text class="disclosure-title">Vehicle details</text>
            <text class="disclosure-toggle">{{ detailSections.vehicle ? 'Hide' : 'Show' }}</text>
          </view>
          <view v-if="detailSections.vehicle" class="disclosure-body">
            <view class="detail-grid">
              <view class="detail-card">
                <text class="detail-label">Top speed</text>
                <text class="detail-value">{{ selectedVehicle.specs.topSpeedKph }} km/h</text>
                <text class="detail-sub">Estimated maximum speed for this vehicle profile.</text>
              </view>
              <view class="detail-card">
                <text class="detail-label">Range</text>
                <text class="detail-value">{{ selectedVehicle.specs.rangeKm }} km</text>
                <text class="detail-sub">Typical range under normal urban riding conditions.</text>
              </view>
              <view class="detail-card">
                <text class="detail-label">Power</text>
                <text class="detail-value">{{ selectedVehicle.specs.motorPowerW }} W</text>
                <text class="detail-sub">Useful for hills, acceleration, and loaded rides.</text>
              </view>
              <view class="detail-card">
                <text class="detail-label">Telemetry</text>
                <text class="detail-value">{{ telemetrySummary }}</text>
                <text class="detail-sub">Live data supported by the vehicle profile and backend flow.</text>
              </view>
            </view>
          </view>
        </view>

        <view class="disclosure-card">
          <view class="disclosure-header" @tap="toggleDisclosure('rules')">
            <text class="disclosure-title">Rental rules and return checks</text>
            <text class="disclosure-toggle">{{ detailSections.rules ? 'Hide' : 'Show' }}</text>
          </view>
          <view v-if="detailSections.rules" class="disclosure-body">
            <view class="policy-panel">
              <view class="policy-row">
                <text class="policy-key">Onboarding</text>
                <text class="policy-value">{{ selectedMarketProfile.registrationRequirement }}</text>
              </view>
              <view class="policy-row">
                <text class="policy-key">Payment</text>
                <text class="policy-value">{{ selectedMarketProfile.paymentRequirement }}</text>
              </view>
              <view class="policy-row">
                <text class="policy-key">Return check</text>
                <text class="policy-value">{{ selectedMarketProfile.returnRequirement }}</text>
              </view>
            </view>
            <view class="scenario-list">
              <view v-for="item in selectedServiceProfile.highlights" :key="item" class="scenario-list-item">
                <view class="scenario-list-dot"></view>
                <text class="scenario-list-text">{{ item }}</text>
              </view>
            </view>
            <view class="detail-grid detail-grid-spaced">
              <view class="detail-card">
                <text class="detail-label">Estimated return battery</text>
                <text class="detail-value">{{ estimatedReturnBattery }}%</text>
                <text class="detail-sub">Used to preview the expected battery state at return.</text>
              </view>
              <view class="detail-card">
                <text class="detail-label">Electricity delta</text>
                <text class="detail-value">{{ formatCurrency(electricityFeeEstimate, activeCurrencyCode) }}</text>
                <text class="detail-sub">Applied mainly to store pickup or walk-in rental scenarios.</text>
              </view>
            </view>
          </view>
        </view>

        <view class="disclosure-card">
          <view class="disclosure-header" @tap="toggleDisclosure('terms')">
            <text class="disclosure-title">Insurance, parking, and liability terms</text>
            <text class="disclosure-toggle">{{ detailSections.terms ? 'Hide' : 'Show' }}</text>
          </view>
          <view v-if="detailSections.terms" class="disclosure-body">
            <view class="policy-panel">
              <view class="policy-row">
                <text class="policy-key">Insurance</text>
                <text class="policy-value">{{ insurancePolicy }}</text>
              </view>
              <view class="policy-row">
                <text class="policy-key">Parking rule</text>
                <text class="policy-value">{{ parkingRule }}</text>
              </view>
              <view class="policy-row">
                <text class="policy-key">Damage handling</text>
                <text class="policy-value">{{ damagePolicy }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view class="section-block pricing-card">
        <view class="pricing-row">
          <text class="pricing-label">Scooter</text>
          <text class="pricing-value">{{ selectedVehicle.displayName }}</text>
        </view>
        <view class="pricing-row">
          <text class="pricing-label">Scenario</text>
          <text class="pricing-value">{{ selectedMarketProfile.label }} · {{ selectedServiceProfile.label }}</text>
        </view>
        <view v-if="selectedServiceMode === 'WALK_IN'" class="pricing-row">
          <text class="pricing-label">Store route</text>
          <text class="pricing-value">{{ pickupStore.name }} → {{ returnStore.name }}</text>
        </view>
        <view v-if="selectedServiceMode === 'WALK_IN'" class="pricing-row">
          <text class="pricing-label">Booking channel</text>
          <text class="pricing-value">{{ selectedStoreChannelProfile.label }}</text>
        </view>
        <view class="pricing-row">
          <text class="pricing-label">Hire period</text>
          <text class="pricing-value">{{ selectedOption.label }}</text>
        </view>
        <view class="pricing-row">
          <text class="pricing-label">Base package</text>
          <text class="pricing-value">{{ formatCurrency(selectedOption.fixedPrice, activeCurrencyCode) }}</text>
        </view>
        <view class="pricing-row">
          <text class="pricing-label">Electricity delta</text>
          <text class="pricing-value">{{ formatCurrency(electricityFeeEstimate, activeCurrencyCode) }}</text>
        </view>
        <view class="pricing-row pricing-row-total">
          <text class="pricing-total-label">Total today</text>
          <text class="pricing-total-value">{{ formatCurrency(totalPrice, activeCurrencyCode) }}</text>
        </view>
      </view>

      <text class="sheet-note">
        This demo now reflects real scooter photos, model-specific pricing, region-specific onboarding, battery delta visibility, auto-charging for overtime, and safety disclaimers.
      </text>

      <button
        class="confirm-button"
        :disabled="paymentProcessing || submitting"
        @tap="handleConfirm"
      >
        <text v-if="paymentProcessing">Authorising trip...</text>
        <text v-else-if="submitting">Starting ride...</text>
        <text v-else>Confirm {{ formatCurrency(totalPrice, activeCurrencyCode) }} and unlock</text>
      </button>
    </view>

    <view v-if="previewVisible" class="preview-overlay" @tap="previewVisible = false">
      <view class="preview-dialog" @tap.stop>
        <view class="preview-stage">
          <view class="preview-rotator">
            <image class="preview-image" :src="selectedVehicle.imageUrl" mode="aspectFit" />
          </view>
        </view>
        <text class="preview-title">{{ selectedVehicle.displayName }}</text>
        <text class="preview-copy">Large product preview with a slow 3D-style rotation so the rider can inspect the selected scooter before payment.</text>
        <button class="preview-close" @tap="previewVisible = false">
          <text>Close Preview</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { RENTAL_PACKAGE_PRICING } from '@/utils/pricing.js'
import {
  detectMarketCode,
  formatCurrency,
  getCurrencyCodeForMarket
} from '@/utils/currency.js'
import {
  MARKET_PROFILES,
  SERVICE_MODE_PROFILES,
  createPlanPricingForProfile,
  enrichScooter,
  estimateBatteryDrop,
  estimateElectricityAdjustment,
  getMarketProfile,
  getServiceModeProfile
} from '@/utils/scooterCatalog.js'
import {
  STORE_BOOKING_CHANNELS,
  STORE_BRANCHES,
  buildStoreAddressLabel,
  buildStoreLabel,
  getStoreBookingChannel,
  getStoreBranch
} from '@/utils/storeRental.js'

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

const marketProfiles = MARKET_PROFILES
const serviceModes = SERVICE_MODE_PROFILES
const storeRentalChannels = STORE_BOOKING_CHANNELS
const selectedPlan = ref('1_HOUR')
const selectedMarketCode = ref(detectMarketCode())
const selectedServiceMode = ref('SHARING')
const selectedStoreChannel = ref('WALK_IN_COUNTER')
const pickupStoreCode = ref(STORE_BRANCHES[0]?.code || '')
const returnStoreCode = ref(STORE_BRANCHES[0]?.code || '')
const previewVisible = ref(false)
const paymentProcessing = ref(false)
const liabilityAccepted = ref(false)
const detailSections = ref({
  vehicle: false,
  rules: false,
  terms: false
})
const identityForm = ref({
  name: '',
  idNumber: ''
})
const cardForm = ref({
  name: '',
  number: '',
  expiry: '',
  cvc: ''
})
const formErrors = ref({
  identityName: '',
  identityNumber: '',
  cardName: '',
  cardNumber: '',
  cardExpiry: '',
  cardCvc: '',
  pickupStore: '',
  returnStore: '',
  liabilityAccepted: ''
})

const baseVehicle = computed(() => enrichScooter(props.scooter || {}))
const selectedVehicle = computed(() => baseVehicle.value)

const selectedMarketProfile = computed(() => getMarketProfile(selectedMarketCode.value))
const activeCurrencyCode = computed(() => getCurrencyCodeForMarket(selectedMarketCode.value))
const selectedServiceProfile = computed(() => getServiceModeProfile(selectedServiceMode.value))
const selectedStoreChannelProfile = computed(() => getStoreBookingChannel(selectedStoreChannel.value))
const pickupStore = computed(() => getStoreBranch(pickupStoreCode.value))
const returnStore = computed(() => getStoreBranch(returnStoreCode.value))
const storeBranchOptions = computed(() =>
  STORE_BRANCHES.map(branch => ({
    value: branch.code,
    label: buildStoreLabel(branch)
  }))
)
const pickupStoreIndex = computed(() => Math.max(0, storeBranchOptions.value.findIndex(option => option.value === pickupStoreCode.value)))
const returnStoreIndex = computed(() => Math.max(0, storeBranchOptions.value.findIndex(option => option.value === returnStoreCode.value)))
const requiresRealName = computed(() => selectedMarketCode.value === 'CN' && selectedServiceMode.value === 'SHARING')
const requiresCardBinding = computed(() => !requiresRealName.value)
const telemetrySummary = computed(() => (selectedVehicle.value.telemetry || []).join(', '))
const validationSummary = computed(() =>
  Object.values(formErrors.value).filter(Boolean)
)

const modelPricing = computed(() => createPlanPricingForProfile(selectedVehicle.value, RENTAL_PACKAGE_PRICING))

const planOptions = computed(() => [
  {
    value: '1_HOUR',
    label: '1 hour',
    subtitle: '60 minutes included',
    minutes: 60,
    fixedPrice: modelPricing.value.oneHour,
    discountNote: ''
  },
  {
    value: '4_HOURS',
    label: '4 hours',
    subtitle: '240 minutes included',
    minutes: 240,
    fixedPrice: modelPricing.value.fourHours,
    discountNote: `Save ${formatCurrency(Math.max(0, modelPricing.value.oneHour * 4 - modelPricing.value.fourHours), activeCurrencyCode.value)} vs hourly`
  },
  {
    value: '1_DAY',
    label: '1 day',
    subtitle: '24 hours included',
    minutes: 1440,
    fixedPrice: modelPricing.value.oneDay,
    discountNote: 'Best for full-day rentals'
  },
  {
    value: '1_WEEK',
    label: '1 week',
    subtitle: '7 days included',
    minutes: 10080,
    fixedPrice: modelPricing.value.oneWeek,
    discountNote: 'Lowest rate for long trips'
  }
])

const selectedOption = computed(() =>
  planOptions.value.find(option => option.value === selectedPlan.value) || planOptions.value[0]
)

const startingBatteryLevel = computed(() => Math.max(18, Number(props.scooter?.batteryLevel ?? selectedVehicle.value?.batteryLevel ?? 82)))
const estimatedReturnBattery = computed(() => {
  const drop = estimateBatteryDrop(selectedOption.value.minutes, selectedVehicle.value)
  return Math.max(12, startingBatteryLevel.value - drop)
})
const electricityFeeEstimate = computed(() => {
  if (selectedServiceMode.value !== 'WALK_IN') return 0
  return estimateElectricityAdjustment(startingBatteryLevel.value, estimatedReturnBattery.value)
})
const overtimeFeePer15Minutes = computed(() => Number((selectedOption.value.fixedPrice * 0.12).toFixed(2)))
const totalPrice = computed(() => Number((selectedOption.value.fixedPrice + electricityFeeEstimate.value).toFixed(2)))
const pickupStoreLabel = computed(() => buildStoreLabel(pickupStore.value))
const returnStoreLabel = computed(() => buildStoreLabel(returnStore.value))

const parkingRule = computed(() =>
  selectedServiceMode.value === 'SHARING'
    ? 'APP return requires a designated parking zone check before the scooter can be locked.'
    : 'Store return requires a staff handover or a geofenced shop drop-off photo.'
)

const damagePolicy = computed(() =>
  selectedServiceMode.value === 'SHARING'
    ? 'The rider must upload damage photos for major scratches, brake faults, or crash indicators before trip closure.'
    : 'Store staff performs a visual inspection and can mark repair costs against the saved card if damage is found.'
)

const insurancePolicy = computed(() =>
  selectedMarketCode.value === 'UK'
    ? 'Traffic liability reminder shown in-app; local insurance coverage and exclusions are disclosed before unlock.'
    : 'Verified riders see accident-coverage notes, helmet guidance, and liability exclusions before each unlock.'
)

const handlePickupStoreChange = (event) => {
  const option = storeBranchOptions.value[Number(event?.detail?.value ?? 0)]
  pickupStoreCode.value = option?.value || STORE_BRANCHES[0]?.code || ''
}

const handleReturnStoreChange = (event) => {
  const option = storeBranchOptions.value[Number(event?.detail?.value ?? 0)]
  returnStoreCode.value = option?.value || pickupStoreCode.value || STORE_BRANCHES[0]?.code || ''
}

watch(
  () => [props.visible, props.scooter?.id, props.preferredPlan],
  ([isVisible]) => {
    if (!isVisible) {
      paymentProcessing.value = false
      previewVisible.value = false
      return
    }

    selectedPlan.value = planOptions.value.some(option => option.value === props.preferredPlan)
      ? props.preferredPlan
      : '1_HOUR'
    selectedMarketCode.value = detectMarketCode()
    selectedServiceMode.value = 'SHARING'
    selectedStoreChannel.value = 'WALK_IN_COUNTER'
    pickupStoreCode.value = STORE_BRANCHES[0]?.code || ''
    returnStoreCode.value = STORE_BRANCHES[0]?.code || ''
    detailSections.value = {
      vehicle: false,
      rules: false,
      terms: false
    }
    liabilityAccepted.value = false
    identityForm.value = { name: '', idNumber: '' }
    cardForm.value = {
      name: '',
      number: '',
      expiry: '',
      cvc: ''
    }
    resetFormErrors()
    paymentProcessing.value = false
    previewVisible.value = false
  },
  { immediate: true }
)

const handleBackdropClose = () => {
  if (paymentProcessing.value || props.submitting || previewVisible.value) return
  emit('close')
}

const handleClose = () => {
  if (paymentProcessing.value || props.submitting) return
  emit('close')
}

const toggleLiability = () => {
  liabilityAccepted.value = !liabilityAccepted.value
  if (liabilityAccepted.value) {
    clearFieldError('liabilityAccepted')
  }
}

const toggleDisclosure = (section) => {
  detailSections.value = {
    ...detailSections.value,
    [section]: !detailSections.value[section]
  }
}

const resetFormErrors = () => {
  formErrors.value = {
    identityName: '',
    identityNumber: '',
    cardName: '',
    cardNumber: '',
    cardExpiry: '',
    cardCvc: '',
    pickupStore: '',
    returnStore: '',
    liabilityAccepted: ''
  }
}

const clearFieldError = (field) => {
  formErrors.value = {
    ...formErrors.value,
    [field]: ''
  }
}

const validateCard = () => {
  const digits = String(cardForm.value.number || '').replace(/\D/g, '')
  const expiry = String(cardForm.value.expiry || '').trim()
  const cvc = String(cardForm.value.cvc || '').replace(/\D/g, '')
  const nextErrors = {}

  if (!String(cardForm.value.name || '').trim()) {
    nextErrors.cardName = 'Enter the rider or cardholder name.'
  }
  if (digits.length < 12) {
    nextErrors.cardNumber = 'Enter a valid card number.'
  }
  if (!/^\d{2}\/\d{2}$/.test(expiry)) {
    nextErrors.cardExpiry = 'Use expiry in MM/YY.'
  }
  if (cvc.length < 3) {
    nextErrors.cardCvc = 'Enter a valid security code.'
  }
  formErrors.value = {
    ...formErrors.value,
    ...nextErrors
  }
  return !Object.keys(nextErrors).length
}

const validateIdentity = () => {
  const nextErrors = {}

  if (!String(identityForm.value.name || '').trim()) {
    nextErrors.identityName = 'Enter the rider real name.'
  }
  if (String(identityForm.value.idNumber || '').trim().length < 8) {
    nextErrors.identityNumber = 'Enter a valid ID or passport number.'
  }
  formErrors.value = {
    ...formErrors.value,
    ...nextErrors
  }
  return !Object.keys(nextErrors).length
}

const validateStoreFields = () => {
  const nextErrors = {}

  if (!pickupStore.value?.code) {
    nextErrors.pickupStore = 'Select a pickup store.'
  }
  if (!returnStore.value?.code) {
    nextErrors.returnStore = 'Select a return store.'
  }

  formErrors.value = {
    ...formErrors.value,
    ...nextErrors
  }
  return !Object.keys(nextErrors).length
}

const validateBookingForm = () => {
  resetFormErrors()

  let valid = true
  if (!liabilityAccepted.value) {
    formErrors.value = {
      ...formErrors.value,
      liabilityAccepted: 'Accept the liability notice before unlocking.'
    }
    valid = false
  }

  if (requiresRealName.value) {
    valid = validateIdentity() && valid
  }

  if (requiresCardBinding.value) {
    valid = validateCard() && valid
  }

  if (selectedServiceMode.value === 'WALK_IN') {
    valid = validateStoreFields() && valid
  }

  if (!valid) {
    uni.showToast({
      title: 'Please complete the highlighted information',
      icon: 'none'
    })
  }

  return valid
}

const handleConfirm = async () => {
  if (!props.scooter || paymentProcessing.value || props.submitting) return
  if (!validateBookingForm()) {
    return
  }

  paymentProcessing.value = true
  await new Promise(resolve => setTimeout(resolve, 1100))
  paymentProcessing.value = false

  const digits = String(cardForm.value.number || '').replace(/\D/g, '')
  emit('confirm', {
    planType: selectedOption.value.value,
    durationMinutes: selectedOption.value.minutes,
    totalPrice: totalPrice.value,
    cardLast4: requiresCardBinding.value ? digits.slice(-4) : '',
    scooterModel: selectedVehicle.value.displayName,
    imageUrl: selectedVehicle.value.imageUrl,
    gallery: selectedVehicle.value.gallery,
    photoCredit: selectedVehicle.value.photoCredit,
    profileSlug: selectedVehicle.value.profileSlug,
    specs: selectedVehicle.value.specs,
    telemetry: selectedVehicle.value.telemetry,
    performanceSummary: selectedVehicle.value.performanceSummary,
    marketCode: selectedMarketProfile.value.code,
    marketLabel: selectedMarketProfile.value.label,
    serviceMode: selectedServiceMode.value,
    serviceLabel: selectedServiceProfile.value.label,
    bookingChannel: selectedServiceMode.value === 'WALK_IN' ? selectedStoreChannel.value : '',
    bookingChannelLabel: selectedServiceMode.value === 'WALK_IN' ? selectedStoreChannelProfile.value.label : '',
    pickupStoreCode: selectedServiceMode.value === 'WALK_IN' ? pickupStore.value.code : '',
    pickupStoreName: selectedServiceMode.value === 'WALK_IN' ? pickupStore.value.name : '',
    pickupStoreAddress: selectedServiceMode.value === 'WALK_IN' ? buildStoreAddressLabel(pickupStore.value) : '',
    returnStoreCode: selectedServiceMode.value === 'WALK_IN' ? returnStore.value.code : '',
    returnStoreName: selectedServiceMode.value === 'WALK_IN' ? returnStore.value.name : '',
    returnStoreAddress: selectedServiceMode.value === 'WALK_IN' ? buildStoreAddressLabel(returnStore.value) : '',
    startBatteryLevel: startingBatteryLevel.value,
    estimatedReturnBattery: estimatedReturnBattery.value,
    electricityFeeEstimate: electricityFeeEstimate.value,
    overtimeFeePer15Minutes: overtimeFeePer15Minutes.value,
    parkingRule: parkingRule.value,
    damagePolicy: damagePolicy.value,
    insurancePolicy: insurancePolicy.value,
    liabilityAccepted: liabilityAccepted.value
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
  z-index: 1600;
  padding: calc(env(safe-area-inset-top) + 24rpx) 24rpx calc(env(safe-area-inset-bottom) + 24rpx);
  box-sizing: border-box;
}

.booking-sheet {
  width: 100%;
  max-width: 920rpx;
  background: linear-gradient(180deg, #F8FBFF 0%, #FFFFFF 34%);
  border-radius: 36rpx 36rpx 24rpx 24rpx;
  padding: 24rpx 28rpx 36rpx;
  box-shadow: 0 24rpx 60rpx rgba(15, 23, 42, 0.18);
  height: calc(100vh - env(safe-area-inset-top) - env(safe-area-inset-bottom) - 48rpx);
  max-height: calc(100vh - env(safe-area-inset-top) - env(safe-area-inset-bottom) - 48rpx);
  overflow-y: auto;
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
  line-height: 1.55;
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

.scooter-hero {
  display: grid;
  grid-template-columns: 280rpx minmax(0, 1fr);
  gap: 22rpx;
  margin-bottom: 28rpx;
}

.hero-photo-frame {
  position: relative;
  border-radius: 28rpx;
  overflow: hidden;
  min-height: 260rpx;
  background: linear-gradient(135deg, #DBEAFE, #EFF6FF);
  box-shadow: 0 14rpx 36rpx rgba(37, 99, 235, 0.12);
}

.hero-photo {
  width: 100%;
  height: 100%;
}

.hero-photo-badge {
  position: absolute;
  left: 18rpx;
  bottom: 18rpx;
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: 10rpx 14rpx;
  border-radius: 999rpx;
  background: rgba(15, 23, 42, 0.72);
}

.hero-photo-badge-text {
  font-size: 21rpx;
  color: #FFFFFF;
  font-weight: 600;
}

.hero-copy {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  min-width: 0;
}

.hero-title-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  align-items: center;
}

.hero-model {
  font-size: 36rpx;
  font-weight: 800;
  color: #0F172A;
}

.hero-category-pill {
  padding: 10rpx 16rpx;
  border-radius: 999rpx;
  background: #DBEAFE;
}

.hero-category-pill-text {
  font-size: 22rpx;
  color: #1D4ED8;
  font-weight: 700;
}

.hero-summary {
  font-size: 24rpx;
  line-height: 1.65;
  color: #475569;
}

.hero-chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.hero-chip {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 16rpx;
  border-radius: 999rpx;
  background: #EFF6FF;
  border: 1rpx solid rgba(37, 99, 235, 0.14);
}

.hero-chip-text {
  font-size: 22rpx;
  color: #1D4ED8;
  font-weight: 600;
}

.hero-credit {
  font-size: 21rpx;
  color: #94A3B8;
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

.toggle-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.market-row {
  margin-bottom: 20rpx;
}

.toggle-card {
  padding: 22rpx;
  border-radius: 26rpx;
  background: #F8FAFC;
  border: 2rpx solid #E2E8F0;
}

.toggle-card-compact {
  min-height: 0;
}

.toggle-card-active {
  border-color: #2563EB;
  background: linear-gradient(180deg, #EFF6FF 0%, #F8FBFF 100%);
  box-shadow: 0 10rpx 26rpx rgba(37, 99, 235, 0.12);
}

.toggle-title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: #0F172A;
}

.toggle-desc {
  display: block;
  margin-top: 10rpx;
  font-size: 23rpx;
  line-height: 1.6;
  color: #64748B;
}

.scenario-card {
  padding: 24rpx;
  border-radius: 28rpx;
  background: linear-gradient(135deg, #0F172A 0%, #1E293B 100%);
}

.scenario-title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: #F8FAFC;
  margin-bottom: 12rpx;
}

.scenario-copy {
  display: block;
  font-size: 23rpx;
  line-height: 1.65;
  color: rgba(255, 255, 255, 0.78);
}

.scenario-list {
  margin-top: 18rpx;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.scenario-list-item {
  display: flex;
  align-items: flex-start;
  gap: 10rpx;
}

.scenario-list-dot {
  width: 12rpx;
  height: 12rpx;
  margin-top: 10rpx;
  border-radius: 50%;
  background: #2563EB;
}

.scenario-list-text {
  flex: 1;
  font-size: 22rpx;
  line-height: 1.6;
  color: #475569;
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

.plan-discount {
  display: inline-flex;
  margin-top: 12rpx;
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  background: rgba(37, 99, 235, 0.08);
  color: #1D4ED8;
  font-size: 21rpx;
  font-weight: 700;
}

.plan-price {
  display: block;
  margin-top: 14rpx;
  font-size: 30rpx;
  font-weight: 700;
  color: #1D4ED8;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.detail-grid-compact .detail-card {
  min-height: 0;
}

.detail-grid-spaced {
  margin-top: 16rpx;
}

.detail-card {
  padding: 20rpx;
  border-radius: 24rpx;
  background: linear-gradient(180deg, #F8FBFF 0%, #FFFFFF 100%);
  border: 1rpx solid rgba(148, 163, 184, 0.22);
}

.detail-label {
  display: block;
  font-size: 22rpx;
  color: #94A3B8;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.detail-value {
  display: block;
  margin-top: 10rpx;
  font-size: 30rpx;
  font-weight: 800;
  color: #0F172A;
}

.detail-sub {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.55;
  color: #64748B;
}

.disclosure-card {
  margin-top: 16rpx;
  border-radius: 24rpx;
  border: 1rpx solid #E2E8F0;
  background: #FFFFFF;
  overflow: hidden;
}

.disclosure-card:first-child {
  margin-top: 0;
}

.disclosure-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 22rpx 24rpx;
}

.disclosure-title {
  font-size: 26rpx;
  font-weight: 700;
  color: #0F172A;
}

.disclosure-toggle {
  font-size: 22rpx;
  font-weight: 700;
  color: #2563EB;
}

.disclosure-body {
  padding: 0 24rpx 24rpx;
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

.input-control-error {
  border-color: #DC2626;
  background: #FEF2F2;
}

.input-control-picker {
  display: flex;
  align-items: center;
}

.input-error {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.5;
  color: #DC2626;
}

.input-error-block {
  margin-left: 52rpx;
}

.flow-note {
  display: block;
  margin-top: 6rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #64748B;
}

.policy-panel {
  padding: 22rpx;
  border-radius: 28rpx;
  background: #F8FAFC;
  border: 2rpx solid #E2E8F0;
}

.policy-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
  padding: 14rpx 0;
  border-bottom: 1rpx solid rgba(148, 163, 184, 0.22);
}

.policy-row:last-child {
  border-bottom: none;
}

.policy-key {
  font-size: 24rpx;
  font-weight: 700;
  color: #0F172A;
}

.policy-value {
  flex: 1;
  text-align: right;
  font-size: 23rpx;
  line-height: 1.6;
  color: #475569;
}

.liability-check {
  margin-top: 18rpx;
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
}

.liability-copy {
  flex: 1;
  font-size: 23rpx;
  line-height: 1.6;
  color: #475569;
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
  line-height: 1.55;
  margin-bottom: 22rpx;
}

.validation-card {
  border-radius: 24rpx;
  padding: 20rpx 22rpx;
  background: linear-gradient(180deg, #FFF7ED 0%, #FFFFFF 100%);
  border: 1rpx solid rgba(249, 115, 22, 0.18);
}

.validation-title {
  display: block;
  font-size: 25rpx;
  font-weight: 700;
  color: #9A3412;
}

.validation-copy {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #C2410C;
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

.confirm-button::after,
.preview-close::after {
  border: none;
}

.confirm-button[disabled] {
  opacity: 0.68;
  box-shadow: none;
}

.preview-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.72);
  z-index: 1700;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32rpx;
  box-sizing: border-box;
}

.preview-dialog {
  width: 100%;
  max-width: 760rpx;
  padding: 28rpx;
  border-radius: 34rpx;
  background: linear-gradient(180deg, #FFFFFF 0%, #F8FBFF 100%);
  box-shadow: 0 26rpx 80rpx rgba(15, 23, 42, 0.28);
}

.preview-stage {
  height: 520rpx;
  border-radius: 28rpx;
  background: radial-gradient(circle at center, #DBEAFE 0%, #EFF6FF 55%, #FFFFFF 100%);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-rotator {
  width: 82%;
  height: 82%;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: preview-spin 7s ease-in-out infinite;
  transform-style: preserve-3d;
}

.preview-image {
  width: 100%;
  height: 100%;
}

.preview-title {
  display: block;
  margin-top: 22rpx;
  font-size: 34rpx;
  font-weight: 800;
  color: #0F172A;
}

.preview-copy {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.65;
  color: #64748B;
}

.preview-close {
  margin-top: 22rpx;
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  border-radius: 999rpx;
  background: #0F172A;
  color: #FFFFFF;
  font-size: 28rpx;
  font-weight: 700;
}

@keyframes preview-spin {
  0% { transform: perspective(1200rpx) rotateY(-8deg) rotateX(2deg) scale(0.98); }
  50% { transform: perspective(1200rpx) rotateY(8deg) rotateX(-1deg) scale(1); }
  100% { transform: perspective(1200rpx) rotateY(-8deg) rotateX(2deg) scale(0.98); }
}

@media (max-width: 720px) {
  .booking-overlay {
    padding-top: calc(env(safe-area-inset-top) + 20rpx);
  }

  .scooter-hero,
  .toggle-row,
  .plan-grid,
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .payment-row {
    flex-direction: column;
    gap: 0;
  }

  .policy-row {
    flex-direction: column;
  }

  .policy-value {
    text-align: left;
  }
}
</style>