import { normalizeRidePricing } from '@/utils/pricing.js'

const ACTIVE_RIDE_KEY = 'activeRide'

const toObject = (value) => {
  if (!value) return null
  if (typeof value === 'string') {
    try {
      return JSON.parse(value)
    } catch {
      return null
    }
  }
  return typeof value === 'object' ? value : null
}

const toNumber = (value, fallback = 0) => {
  const next = Number(value)
  return Number.isFinite(next) ? next : fallback
}

const toNullableNumber = (value) => {
  if (value === null || value === undefined || String(value).trim() === '') {
    return null
  }

  const next = Number(value)
  return Number.isFinite(next) ? next : null
}

const toIsoString = (value, fallback = '') => {
  if (!value) return fallback
  if (value instanceof Date) return value.toISOString()

  const normalized = String(value).trim().replace(' ', 'T')
  const parsed = new Date(normalized)
  return Number.isNaN(parsed.getTime()) ? fallback : parsed.toISOString()
}

const normalizeStatus = (status) => String(status || '').trim().toUpperCase()

export const getStoredUserId = () => {
  try {
    const user = toObject(uni.getStorageSync('userInfo'))
    return user?.userId || user?.id || ''
  } catch {
    return ''
  }
}

export const normalizeActiveRide = (ride = {}, fallback = {}) => {
  const bookingId = ride.bookingId ?? ride.id ?? fallback.bookingId ?? fallback.id ?? null
  const scooterId = ride.scooterId ?? fallback.scooterId ?? null
  const durationMinutes = toNumber(ride.durationMinutes ?? fallback.durationMinutes, 0)
  const latitudeValue = toNullableNumber(ride.latitude ?? ride.lat ?? fallback.latitude ?? fallback.lat)
  const longitudeValue = toNullableNumber(ride.longitude ?? ride.lng ?? fallback.longitude ?? fallback.lng)
  const normalizedPricing = normalizeRidePricing({
    basePrice: ride.basePrice ?? fallback.basePrice,
    pricePerMinute: ride.pricePerMinute ?? ride.pricePerMin ?? fallback.pricePerMinute ?? fallback.pricePerMin
  })
  const totalCost = toNumber(
    ride.totalCost ?? ride.estimatedCost ?? ride.reservedTotal ?? fallback.totalCost ?? fallback.estimatedCost,
    0
  )

  return {
    bookingId,
    userId: ride.userId ?? fallback.userId ?? (getStoredUserId() || null),
    scooterId,
    scooterModel: ride.scooterModel ?? ride.model ?? fallback.scooterModel ?? fallback.model ?? 'Scooter',
    startTime: toIsoString(ride.startTime ?? fallback.startTime, new Date().toISOString()),
    durationMinutes,
    totalCost,
    basePrice: normalizedPricing.basePrice,
    pricePerMinute: normalizedPricing.pricePerMinute,
    cardLast4: String(ride.cardLast4 ?? fallback.cardLast4 ?? '').trim(),
    latitude: latitudeValue,
    longitude: longitudeValue,
    lat: latitudeValue,
    lng: longitudeValue,
    liveLocationLabel: String(ride.liveLocationLabel ?? fallback.liveLocationLabel ?? '').trim(),
    status: normalizeStatus(ride.status ?? ride.bookingStatus ?? fallback.status ?? fallback.bookingStatus),
    updatedAt: new Date().toISOString()
  }
}

export const setStoredActiveRide = (ride) => {
  const normalized = normalizeActiveRide(ride)
  uni.setStorageSync(ACTIVE_RIDE_KEY, JSON.stringify(normalized))
  return normalized
}

export const getStoredActiveRide = () => {
  const cached = toObject(uni.getStorageSync(ACTIVE_RIDE_KEY))
  if (!cached) return null
  return normalizeActiveRide(cached)
}

export const clearStoredActiveRide = () => {
  uni.removeStorageSync(ACTIVE_RIDE_KEY)
}

export const isRideActive = (ride) => normalizeStatus(ride?.status ?? ride?.bookingStatus) === 'ACTIVE'

export const getRideEndTime = (ride) => {
  const normalized = normalizeActiveRide(ride)
  const startAt = new Date(normalized.startTime).getTime()
  if (Number.isNaN(startAt) || normalized.durationMinutes <= 0) return null
  return new Date(startAt + (normalized.durationMinutes * 60 * 1000))
}

export const findActiveRide = (bookings = [], fallback = {}) => {
  if (!Array.isArray(bookings) || !bookings.length) return null

  const active = [...bookings]
    .filter(item => isRideActive(item))
    .sort((left, right) => {
      const leftTime = new Date(String(left?.startTime || '').replace(' ', 'T')).getTime() || 0
      const rightTime = new Date(String(right?.startTime || '').replace(' ', 'T')).getTime() || 0
      return rightTime - leftTime
    })[0]

  return active ? normalizeActiveRide(active, fallback) : null
}
