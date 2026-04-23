<template>
  <view v-if="visible" class="viewer-overlay" @tap="emit('close')">
    <view class="viewer-dialog" @tap.stop>
      <view class="viewer-header">
        <view>
          <text class="viewer-eyebrow">3D Fleet Preview</text>
          <text class="viewer-title">{{ scooter?.displayName || 'Scooter Preview' }}</text>
        </view>
        <button class="viewer-close" @tap="emit('close')">
          <text>Close</text>
        </button>
      </view>

      <view class="viewer-content">
        <view class="viewer-stage-shell">
          <view class="viewer-stage-glow"></view>
          <view class="viewer-stage">
            <model-viewer
              v-if="viewerReady && scooter?.modelUrl"
              ref="viewerRef"
              class="viewer-model"
              :src="scooter.modelUrl"
              :poster="scooter.posterUrl || scooter.imageUrl"
              alt="Interactive 3D scooter model"
              camera-controls
              touch-action="pan-y"
              shadow-intensity="1"
              shadow-softness="0.9"
              exposure="1.02"
              environment-image="neutral"
              interaction-prompt="none"
              :auto-rotate="autoRotateAttribute"
              auto-rotate-delay="0"
              rotation-per-second="24deg"
              :camera-orbit="cameraOrbit"
              min-camera-orbit="auto 55deg 55%"
              max-camera-orbit="auto 92deg 180%"
              field-of-view="26deg"
              @pointerdown="pauseAutoRotate"
              @mousedown="pauseAutoRotate"
              @touchstart="pauseAutoRotate"
              @wheel="pauseAutoRotate"
              @load="handleLoad"
              @error="handleError"
            ></model-viewer>

            <view v-if="loading" class="viewer-state viewer-state-loading">
              <text class="viewer-state-title">Loading model...</text>
              <text class="viewer-state-copy">{{ loadingMessage }}</text>
            </view>

            <view v-else-if="loadError" class="viewer-state">
              <image class="viewer-fallback-image" :src="scooter?.posterUrl || scooter?.imageUrl" mode="aspectFit" />
              <text class="viewer-state-title">3D preview unavailable</text>
              <text class="viewer-state-copy">{{ loadError }}</text>
            </view>
          </view>
          <view class="viewer-stage-floor"></view>
        </view>

        <view class="viewer-panel">
          <text class="viewer-copy">{{ scooter?.performanceSummary }}</text>

          <view class="viewer-spec-grid">
            <view class="viewer-spec-card">
              <text class="viewer-spec-label">Top speed</text>
              <text class="viewer-spec-value">{{ scooter?.specs?.topSpeedKph }} km/h</text>
            </view>
            <view class="viewer-spec-card">
              <text class="viewer-spec-label">Range</text>
              <text class="viewer-spec-value">{{ scooter?.specs?.rangeKm }} km</text>
            </view>
            <view class="viewer-spec-card">
              <text class="viewer-spec-label">Motor</text>
              <text class="viewer-spec-value">{{ scooter?.specs?.motorPowerW }} W</text>
            </view>
            <view class="viewer-spec-card">
              <text class="viewer-spec-label">Best for</text>
              <text class="viewer-spec-value">{{ scooter?.useCase }}</text>
            </view>
          </view>

          <view class="viewer-controls">
            <button class="viewer-control-btn viewer-control-primary" @tap="toggleAutoRotate">
              <text>{{ autoRotateEnabled ? 'Pause Auto Rotate' : 'Start Auto Rotate' }}</text>
            </button>
            <button class="viewer-control-btn" @tap="rotateModel(-18)">
              <text>Rotate Left</text>
            </button>
            <button class="viewer-control-btn" @tap="rotateModel(18)">
              <text>Rotate Right</text>
            </button>
            <button class="viewer-control-btn" @tap="zoomModel(-10)">
              <text>Zoom In</text>
            </button>
            <button class="viewer-control-btn" @tap="zoomModel(10)">
              <text>Zoom Out</text>
            </button>
            <button class="viewer-control-btn" @tap="resetView">
              <text>Reset View</text>
            </button>
          </view>

          <view class="viewer-note">
            <text class="viewer-note-title">Viewing tips</text>
            <text class="viewer-note-copy">Drag to orbit the scooter, pinch or use the zoom buttons for detail, and leave auto-rotate on when you want a hands-free showroom spin.</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  scooter: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close'])

const viewerRef = ref(null)
const viewerReady = ref(false)
const loading = ref(false)
const loadError = ref('')
const slowLoadHint = ref(false)
const autoRotateEnabled = ref(true)
const orbitTheta = ref(0)
const orbitPhi = ref(74)
const orbitRadius = ref(108)
const isBrowser = typeof window !== 'undefined' && typeof document !== 'undefined'
let modelViewerLoaderPromise = null
let loadingTimer = 0

const cameraOrbit = computed(() =>
  `${orbitTheta.value}deg ${orbitPhi.value}deg ${orbitRadius.value}%`
)

const autoRotateAttribute = computed(() => (autoRotateEnabled.value ? '' : null))

const loadingMessage = computed(() => {
  if (slowLoadHint.value) {
    return 'This model is large and is still being prepared. Keep the dialog open a bit longer and it should finish loading.'
  }

  if (!props.scooter?.modelUrl) {
    return 'Preparing the interactive preview.'
  }

  if ((props.scooter?.modelUrl || '').includes('electric_scooter_3.glb')) {
    return 'This flagship model is large, so the first load can take noticeably longer than the other scooters.'
  }

  return 'Preparing the full GLB preview for inspection.'
})

const resetOrbitState = () => {
  orbitTheta.value = 0
  orbitPhi.value = 74
  orbitRadius.value = 108
}

const syncViewerAutoRotate = async () => {
  await nextTick()
  const viewer = viewerRef.value
  if (!viewer) return

  viewer.autoRotate = autoRotateEnabled.value
  if (autoRotateEnabled.value) {
    viewer.setAttribute('auto-rotate', '')
  } else {
    viewer.removeAttribute('auto-rotate')
  }
}

const setAutoRotate = async (enabled) => {
  autoRotateEnabled.value = enabled
  await syncViewerAutoRotate()
}

const pauseAutoRotate = () => {
  if (!autoRotateEnabled.value) return
  setAutoRotate(false)
}

const clearLoadingTimer = () => {
  if (loadingTimer) {
    window.clearTimeout(loadingTimer)
    loadingTimer = 0
  }
}

const ensureModelViewer = async () => {
  if (!isBrowser) return false
  if (customElements.get('model-viewer')) {
    viewerReady.value = true
    return true
  }

  if (!modelViewerLoaderPromise) {
    modelViewerLoaderPromise = import('@google/model-viewer')
  }

  await modelViewerLoaderPromise
  viewerReady.value = Boolean(customElements.get('model-viewer'))
  return viewerReady.value
}

const scheduleLongLoadHint = () => {
  if (!isBrowser) return
  clearLoadingTimer()
  loadingTimer = window.setTimeout(() => {
    if (!loading.value || loadError.value) return
    slowLoadHint.value = true
  }, 22000)
}

const handleLoad = () => {
  clearLoadingTimer()
  loading.value = false
  slowLoadHint.value = false
  loadError.value = ''
  syncViewerAutoRotate()
}

const handleError = () => {
  clearLoadingTimer()
  loading.value = false
  slowLoadHint.value = false
  loadError.value = 'The browser could not render this GLB model. Check whether the file is too heavy or needs to be simplified before publishing.'
}

const openViewer = async () => {
  if (!props.visible) return
  if (!isBrowser) {
    loading.value = false
    loadError.value = '3D preview is available on the web version of the homepage.'
    return
  }

  loading.value = true
  slowLoadHint.value = false
  loadError.value = ''
  await setAutoRotate(true)
  resetOrbitState()

  try {
    const ready = await ensureModelViewer()
    if (!ready) {
      throw new Error('model-viewer custom element was not registered')
    }

    await nextTick()
    scheduleLongLoadHint()
  } catch (error) {
    clearLoadingTimer()
    loading.value = false
    loadError.value = 'The 3D viewer could not initialize in this browser session.'
    console.error('Failed to initialize model-viewer', error)
  }
}

const rotateModel = (deltaDegrees) => {
  pauseAutoRotate()
  orbitTheta.value += deltaDegrees
}

const zoomModel = (deltaPercent) => {
  pauseAutoRotate()
  orbitRadius.value = Math.min(180, Math.max(60, orbitRadius.value + deltaPercent))
}

const toggleAutoRotate = () => {
  setAutoRotate(!autoRotateEnabled.value)
}

const resetView = () => {
  setAutoRotate(true)
  resetOrbitState()
}

watch(
  () => [props.visible, props.scooter?.modelUrl],
  async ([visible]) => {
    if (!visible) {
      clearLoadingTimer()
      loading.value = false
      slowLoadHint.value = false
      loadError.value = ''
      autoRotateEnabled.value = true
      return
    }

    await openViewer()
  },
  { immediate: true }
)

onBeforeUnmount(() => {
  clearLoadingTimer()
})
</script>

<style scoped>
.viewer-overlay {
  position: fixed;
  inset: 0;
  z-index: 2200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 36rpx;
  background: rgba(15, 23, 42, 0.72);
  backdrop-filter: blur(22px);
}

.viewer-dialog {
  width: min(1720rpx, 100%);
  max-height: calc(100vh - 72rpx);
  overflow: hidden;
  border-radius: 40rpx;
  background: linear-gradient(160deg, rgba(255, 255, 255, 0.98) 0%, rgba(239, 246, 255, 0.98) 100%);
  box-shadow: 0 30rpx 90rpx rgba(15, 23, 42, 0.22);
}

.viewer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  padding: 34rpx 38rpx 22rpx;
  border-bottom: 1rpx solid rgba(148, 163, 184, 0.24);
}

.viewer-eyebrow {
  display: block;
  font-size: 22rpx;
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: #2563eb;
}

.viewer-title {
  display: block;
  margin-top: 10rpx;
  font-size: 44rpx;
  font-weight: 800;
  color: #0f172a;
}

.viewer-close,
.viewer-control-btn {
  height: 82rpx;
  line-height: 82rpx;
  border-radius: 999rpx;
  border: none;
  padding: 0 28rpx;
  font-size: 24rpx;
  font-weight: 700;
}

.viewer-close::after,
.viewer-control-btn::after {
  border: none;
}

.viewer-close {
  background: rgba(15, 23, 42, 0.08);
  color: #0f172a;
}

.viewer-content {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(460rpx, 0.95fr);
  gap: 28rpx;
  padding: 34rpx 38rpx 38rpx;
}

.viewer-stage-shell {
  position: relative;
  min-height: 860rpx;
  border-radius: 34rpx;
  overflow: hidden;
  background:
    radial-gradient(circle at 50% 18%, rgba(191, 219, 254, 0.84), transparent 36%),
    linear-gradient(180deg, #dbeafe 0%, #eff6ff 42%, #f8fafc 100%);
}

.viewer-stage-glow {
  position: absolute;
  top: -120rpx;
  left: 50%;
  width: 720rpx;
  height: 720rpx;
  transform: translateX(-50%);
  border-radius: 50%;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.16) 0%, rgba(37, 99, 235, 0) 68%);
  pointer-events: none;
}

.viewer-stage {
  position: relative;
  width: 100%;
  height: 860rpx;
}

.viewer-model {
  width: 100%;
  height: 100%;
  background: transparent;
  --poster-color: transparent;
  --progress-bar-color: #2563eb;
  --progress-mask: rgba(255, 255, 255, 0.8);
}

.viewer-stage-floor {
  position: absolute;
  left: 50%;
  bottom: 42rpx;
  width: 520rpx;
  height: 120rpx;
  transform: translateX(-50%);
  border-radius: 50%;
  background: radial-gradient(circle, rgba(15, 23, 42, 0.16) 0%, rgba(15, 23, 42, 0) 70%);
  filter: blur(18rpx);
  pointer-events: none;
}

.viewer-state {
  position: absolute;
  inset: 0;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
  text-align: center;
  background: rgba(248, 250, 252, 0.72);
}

.viewer-state-loading {
  backdrop-filter: blur(8px);
}

.viewer-fallback-image {
  width: 420rpx;
  height: 300rpx;
  margin-bottom: 26rpx;
}

.viewer-state-title {
  display: block;
  font-size: 34rpx;
  font-weight: 800;
  color: #0f172a;
}

.viewer-state-copy {
  display: block;
  max-width: 540rpx;
  margin-top: 14rpx;
  font-size: 24rpx;
  line-height: 1.6;
  color: #64748b;
}

.viewer-panel {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.viewer-copy {
  display: block;
  font-size: 28rpx;
  line-height: 1.72;
  color: #334155;
}

.viewer-spec-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
}

.viewer-spec-card,
.viewer-note {
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.86);
  border: 1rpx solid rgba(191, 219, 254, 0.54);
  box-shadow: 0 12rpx 28rpx rgba(37, 99, 235, 0.08);
}

.viewer-spec-card {
  padding: 24rpx 26rpx;
}

.viewer-spec-label {
  display: block;
  font-size: 21rpx;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #64748b;
}

.viewer-spec-value {
  display: block;
  margin-top: 12rpx;
  font-size: 30rpx;
  font-weight: 800;
  color: #0f172a;
}

.viewer-controls {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.viewer-control-btn {
  background: rgba(255, 255, 255, 0.92);
  color: #0f172a;
  box-shadow: 0 12rpx 26rpx rgba(15, 23, 42, 0.08);
}

.viewer-control-primary {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #ffffff;
}

.viewer-note {
  padding: 28rpx 30rpx;
}

.viewer-note-title {
  display: block;
  font-size: 24rpx;
  font-weight: 800;
  color: #1d4ed8;
}

.viewer-note-copy {
  display: block;
  margin-top: 12rpx;
  font-size: 23rpx;
  line-height: 1.65;
  color: #475569;
}

@media (max-width: 1100px) {
  .viewer-content {
    grid-template-columns: 1fr;
  }

  .viewer-stage-shell,
  .viewer-stage {
    min-height: 640rpx;
    height: 640rpx;
  }
}

@media (max-width: 750px) {
  .viewer-overlay {
    padding: 20rpx;
  }

  .viewer-dialog {
    max-height: calc(100vh - 40rpx);
    overflow-y: auto;
  }

  .viewer-header,
  .viewer-content {
    padding-left: 24rpx;
    padding-right: 24rpx;
  }

  .viewer-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .viewer-stage-shell,
  .viewer-stage {
    min-height: 520rpx;
    height: 520rpx;
  }

  .viewer-title {
    font-size: 36rpx;
  }

  .viewer-spec-grid,
  .viewer-controls {
    grid-template-columns: 1fr;
  }

  .viewer-close,
  .viewer-control-btn {
    width: 100%;
  }
}
</style>
