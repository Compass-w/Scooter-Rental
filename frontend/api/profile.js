/**
 * User Profile API helpers.
 * The frontend profile page expects richer data than the current backend exposes,
 * so this module adapts existing endpoints and persists lightweight client-only
 * preferences where the backend does not yet provide a dedicated API.
 */

import request from '@/utils/request.js'

const SETTINGS_STORAGE_KEY = 'profileSettings'
const WALLET_STORAGE_KEY = 'walletMeta'

const DEFAULT_SETTINGS = {
  notifications: true,
  emailNotif: false,
  location: true,
  dataShare: true,
  autoTopUp: false
}

const DEFAULT_WALLET_META = {
  balance: '0.00',
  autoTopUp: false,
  defaultCardId: null
}

/**
 * Get the current user's ID from local storage.
 * @returns {number|string} userId
 */
const getUserId = () => {
  try {
    const cached = uni.getStorageSync('userInfo')
    const user = cached ? (typeof cached === 'string' ? JSON.parse(cached) : cached) : {}
    return user.userId || user.id || ''
  } catch (error) {
    globalThis.__APP_LOGGER__?.error('Failed to get userId:', error)
    return ''
  }
}

const readCurrentUser = () => {
  try {
    const cached = uni.getStorageSync('userInfo')
    return cached ? (typeof cached === 'string' ? JSON.parse(cached) : cached) : {}
  } catch {
    return {}
  }
}

const buildScopedKey = (baseKey) => `${baseKey}:${getUserId() || 'guest'}`

const readScopedStorage = (baseKey, fallback = {}) => {
  try {
    const cached = uni.getStorageSync(buildScopedKey(baseKey))
    if (!cached) return { ...fallback }

    const parsed = typeof cached === 'string' ? JSON.parse(cached) : cached
    return {
      ...fallback,
      ...(parsed || {})
    }
  } catch {
    return { ...fallback }
  }
}

const writeScopedStorage = (baseKey, value) => {
  uni.setStorageSync(buildScopedKey(baseKey), JSON.stringify(value))
  return value
}

const toNumber = (value, fallback = 0) => {
  const next = Number(value)
  return Number.isFinite(next) ? next : fallback
}

const normalizeStatus = (status) => String(status || '').trim().toUpperCase()

const toDate = (value) => {
  if (!value) return null

  const parsed = new Date(String(value).replace(' ', 'T'))
  return Number.isNaN(parsed.getTime()) ? null : parsed
}

const sortByStartDesc = (bookings = []) => [...bookings].sort((left, right) => {
  const leftTime = toDate(left?.startTime)?.getTime() || 0
  const rightTime = toDate(right?.startTime)?.getTime() || 0
  return rightTime - leftTime
})

const formatDateLabel = (value) => {
  const date = toDate(value)
  if (!date) return 'Not available'

  return date.toLocaleString('en-GB', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const extractLast4 = (value) => {
  const digits = String(value || '').replace(/\D/g, '')
  return digits.slice(-4) || '0000'
}

const inferCardBrand = (value) => {
  const digits = String(value || '').replace(/\D/g, '')
  if (digits.startsWith('4')) return 'VISA'
  if (/^5[1-5]/.test(digits)) return 'MC'
  if (/^3[47]/.test(digits)) return 'AMEX'
  return 'CARD'
}

const isBookingInPeriod = (booking, period = 'Month') => {
  const startAt = toDate(booking?.startTime)
  if (!startAt) return false

  const now = Date.now()
  const periodDays = {
    WEEK: 7,
    MONTH: 30,
    YEAR: 365,
    ALL: Number.POSITIVE_INFINITY
  }

  const days = periodDays[String(period || 'MONTH').toUpperCase()] ?? 30
  if (!Number.isFinite(days)) return true

  return (now - startAt.getTime()) <= days * 24 * 60 * 60 * 1000
}

const mapBookingToTrip = (booking) => {
  const bookingId = booking?.bookingId ?? booking?.id ?? ''
  const scooterId = booking?.scooterId ?? ''
  const scooterModel = booking?.scooterModel || 'Scooter'
  const duration = toNumber(booking?.durationMinutes, 0)
  const totalCost = toNumber(booking?.totalCost, 0).toFixed(2)
  const status = normalizeStatus(booking?.status)

  return {
    id: bookingId,
    bookingId,
    scooterId,
    scooterModel,
    scooterLabel: `#${scooterId} ${scooterModel}`.trim(),
    date: formatDateLabel(booking?.startTime),
    dateLabel: formatDateLabel(booking?.endTime || booking?.startTime),
    duration,
    cost: totalCost,
    status,
    statusLabel: status === 'ACTIVE' ? 'Active' : status === 'CANCELLED' ? 'Cancelled' : 'Completed'
  }
}

let bookingHistoryPromise = null
let bookingHistoryUserId = null

const getBookingHistory = async () => {
  const userId = getUserId()
  if (!userId) return []

  if (bookingHistoryPromise && bookingHistoryUserId === userId) {
    return bookingHistoryPromise
  }

  bookingHistoryUserId = userId
  bookingHistoryPromise = request.get(`/bookings/user/${userId}`)
    .then(data => (Array.isArray(data) ? data : []))
    .finally(() => {
      bookingHistoryPromise = null
    })

  return bookingHistoryPromise
}

/**
 * Get User Profile
 * @returns {Promise}
 */
export const getProfile = () => {
  return request.get(`/users/${getUserId()}`)
}

/**
 * Update User Profile
 * @param {Object} data
 * @returns {Promise}
 */
export const updateProfile = (data) => {
  return request.put('/users/update', data)
}

/**
 * Build ride statistics from real booking history.
 * @param {string} period
 * @returns {Promise<Object>}
 */
export const getStats = async (period = 'Month') => {
  const bookings = await getBookingHistory()
  const completed = sortByStartDesc(bookings).filter(item => normalizeStatus(item?.status) === 'COMPLETED')
  const filtered = completed.filter(item => isBookingInPeriod(item, period))

  const sumMinutes = (items) => items.reduce((total, item) => total + toNumber(item?.durationMinutes, 0), 0)
  const sumSpent = (items) => items.reduce((total, item) => total + toNumber(item?.totalCost, 0), 0)

  return {
    totalRides: completed.length,
    totalMinutes: sumMinutes(completed),
    totalSpent: sumSpent(completed).toFixed(2),
    currentRides: bookings.filter(item => normalizeStatus(item?.status) === 'ACTIVE').length,
    periodRides: filtered.length,
    periodMinutes: sumMinutes(filtered),
    periodSpent: sumSpent(filtered).toFixed(2)
  }
}

/**
 * Get Wallet
 * Uses real card data from the backend plus lightweight client metadata.
 * @returns {Promise<Object>}
 */
export const getWallet = async () => {
  const userId = getUserId()
  if (!userId) {
    return {
      balance: DEFAULT_WALLET_META.balance,
      autoTopUp: DEFAULT_WALLET_META.autoTopUp,
      paymentMethods: []
    }
  }

  const walletMeta = readScopedStorage(WALLET_STORAGE_KEY, DEFAULT_WALLET_META)
  const cards = await request.get(`/cards/user/${userId}`).catch(() => [])
  const defaultCardId =
    walletMeta.defaultCardId ??
    cards.find(card => Boolean(card?.isDefault))?.cardId ??
    cards[0]?.cardId ??
    null

  return {
    balance: String(walletMeta.balance ?? DEFAULT_WALLET_META.balance),
    autoTopUp: Boolean(walletMeta.autoTopUp),
    paymentMethods: cards.map(card => ({
      ...card,
      id: card.cardId,
      last4: extractLast4(card.cardNumberMasked),
      expires: card.expiryDate || '',
      brand: inferCardBrand(card.cardNumberMasked),
      isDefault: defaultCardId ? String(card.cardId) === String(defaultCardId) : Boolean(card.isDefault)
    }))
  }
}

/**
 * Top Up Wallet
 * Persists locally until a dedicated backend wallet API exists.
 * @param {number} amount
 * @returns {Promise<Object>}
 */
export const topUpWallet = async (amount) => {
  const current = readScopedStorage(WALLET_STORAGE_KEY, DEFAULT_WALLET_META)
  const nextBalance = (toNumber(current.balance) + toNumber(amount)).toFixed(2)
  writeScopedStorage(WALLET_STORAGE_KEY, {
    ...current,
    balance: nextBalance
  })

  return { balance: nextBalance }
}

/**
 * Add Payment Card
 * @param {Object} data
 * @returns {Promise<Object>}
 */
export const addCard = async (data) => {
  const userId = getUserId()
  if (!userId) {
    throw new Error('User ID is required')
  }

  const currentUser = readCurrentUser()
  await request.post('/cards/add', {
    userId,
    cardHolderName: currentUser.name || currentUser.username || 'ScooterGo Rider',
    cardNumberMasked: data.number,
    expiryDate: data.expires
  })

  const wallet = await getWallet()
  return wallet.paymentMethods[wallet.paymentMethods.length - 1] || {
    id: `temp-${Date.now()}`,
    last4: extractLast4(data.number),
    expires: data.expires,
    brand: inferCardBrand(data.number),
    isDefault: wallet.paymentMethods.length === 0
  }
}

/**
 * Set Default Payment Card
 * Persists locally until the backend exposes a dedicated endpoint.
 * @param {string} cardId
 * @returns {Promise<null>}
 */
export const setDefaultCard = async (cardId) => {
  const current = readScopedStorage(WALLET_STORAGE_KEY, DEFAULT_WALLET_META)
  writeScopedStorage(WALLET_STORAGE_KEY, {
    ...current,
    defaultCardId: cardId
  })

  return null
}

/**
 * Remove Payment Card
 * @param {string} cardId
 * @returns {Promise<null>}
 */
export const removeCard = async (cardId) => {
  await request.delete(`/cards/${cardId}`)

  const current = readScopedStorage(WALLET_STORAGE_KEY, DEFAULT_WALLET_META)
  if (String(current.defaultCardId || '') === String(cardId)) {
    writeScopedStorage(WALLET_STORAGE_KEY, {
      ...current,
      defaultCardId: null
    })
  }

  return null
}

/**
 * Get Recent Trips
 * @param {number} limit
 * @returns {Promise<Array>}
 */
export const getRecentTrips = async (limit = 3) => {
  const bookings = await getBookingHistory()
  return sortByStartDesc(bookings)
    .filter(item => normalizeStatus(item?.status) === 'COMPLETED')
    .map(mapBookingToTrip)
    .slice(0, limit)
}

/**
 * Get Bookings
 * @param {string} status
 * @returns {Promise<Array>}
 */
export const getBookings = async (status) => {
  const bookings = await getBookingHistory()
  const normalized = String(status || '').toLowerCase()

  return sortByStartDesc(bookings)
    .filter(item => {
      const bookingStatus = normalizeStatus(item?.status)
      if (normalized === 'upcoming') return bookingStatus === 'ACTIVE'
      if (normalized === 'past') return bookingStatus !== 'ACTIVE'
      return true
    })
    .map(mapBookingToTrip)
}

/**
 * Cancel Booking
 * @param {number} bookingId
 * @returns {Promise}
 */
export const cancelBooking = (bookingId) => {
  return request.post('/bookings/cancel', {
    bookingId,
    userId: getUserId()
  })
}

/**
 * Get User Settings
 * @returns {Promise<Object>}
 */
export const getSettings = async () => {
  return readScopedStorage(SETTINGS_STORAGE_KEY, DEFAULT_SETTINGS)
}

/**
 * Update User Settings
 * @param {Object} data
 * @returns {Promise<Object>}
 */
export const updateSettings = async (data) => {
  const merged = {
    ...DEFAULT_SETTINGS,
    ...readScopedStorage(SETTINGS_STORAGE_KEY, DEFAULT_SETTINGS),
    ...(data || {})
  }

  writeScopedStorage(SETTINGS_STORAGE_KEY, merged)

  const walletMeta = readScopedStorage(WALLET_STORAGE_KEY, DEFAULT_WALLET_META)
  if (Object.prototype.hasOwnProperty.call(data || {}, 'autoTopUp')) {
    writeScopedStorage(WALLET_STORAGE_KEY, {
      ...walletMeta,
      autoTopUp: Boolean(data.autoTopUp)
    })
  }

  return merged
}
