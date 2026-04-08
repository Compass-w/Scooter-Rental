import request from '@/utils/request.js'

const USER_INFO_KEY = 'userInfo'

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

export const getStoredUserId = () => {
  try {
    const user = toObject(uni.getStorageSync(USER_INFO_KEY))
    return user?.userId || user?.id || ''
  } catch (error) {
    console.error('Failed to read user id from storage:', error)
    return ''
  }
}

const ensureUserId = (userId = getStoredUserId()) => {
  if (userId) return userId
  throw new Error('User ID is missing')
}

const normalizeCard = (card = {}) => {
  const masked = String(card.cardNumberMasked || '').trim()
  const digits = masked.replace(/\D/g, '')

  return {
    cardId: card.cardId ?? card.id ?? '',
    userId: card.userId ?? '',
    cardHolderName: card.cardHolderName || '',
    cardNumberMasked: masked || (digits ? `**** ${digits.slice(-4)}` : '**** ----'),
    expiryDate: card.expiryDate || card.expires || '',
    last4: digits.slice(-4),
    brand: card.brand || 'Bank Card',
    isDefault: Boolean(card.isDefault)
  }
}

export const getProfile = (userId = getStoredUserId()) => {
  const id = ensureUserId(userId)
  return request.get(`/users/${id}`)
}

export const updateProfile = (data) => {
  return request.put('/users/update', data)
}

export const getStats = (period = 'Week', userId = getStoredUserId()) => {
  const id = ensureUserId(userId)
  return request.get(`/users/${id}/stats`, { period })
}

export const getBookingHistory = (userId = getStoredUserId()) => {
  const id = ensureUserId(userId)
  return request.get(`/bookings/user/${id}`)
}

export const getBookings = async (status = 'all', userId = getStoredUserId()) => {
  const bookings = await getBookingHistory(userId)
  const list = Array.isArray(bookings) ? bookings : []
  const normalizedStatus = String(status || 'all').trim().toLowerCase()

  if (normalizedStatus === 'upcoming') {
    return list.filter(item => ['PENDING', 'ACTIVE'].includes(String(item?.status || '').toUpperCase()))
  }

  if (normalizedStatus === 'past') {
    return list.filter(item => ['COMPLETED', 'CANCELLED'].includes(String(item?.status || '').toUpperCase()))
  }

  return list
}

export const cancelBooking = (bookingId, userId = getStoredUserId()) => {
  const id = ensureUserId(userId)

  if (!bookingId) {
    return Promise.reject(new Error('Booking ID is required'))
  }

  return request.post('/bookings/cancel', {
    bookingId,
    userId: id
  })
}

export const getBankCards = async (userId = getStoredUserId()) => {
  const id = ensureUserId(userId)
  const cards = await request.get(`/cards/user/${id}`)
  return Array.isArray(cards) ? cards.map(normalizeCard) : []
}

export const addBankCard = (data, userId = getStoredUserId()) => {
  const id = ensureUserId(userId)

  return request.post('/cards/add', {
    userId: id,
    cardHolderName: data.cardHolderName || data.name || '',
    cardNumberMasked: data.cardNumberMasked || data.number || '',
    expiryDate: data.expiryDate || data.expires || '',
    isDefault: Boolean(data.isDefault)
  })
}

export const removeBankCard = (cardId) => {
  if (!cardId) {
    return Promise.reject(new Error('Card ID is required'))
  }

  return request.delete(`/cards/${cardId}`)
}

export const getWallet = async (userId = getStoredUserId()) => {
  const paymentMethods = await getBankCards(userId)

  return {
    balance: '0.00',
    autoTopUp: false,
    paymentMethods
  }
}

export const addCard = (data, userId = getStoredUserId()) => addBankCard(data, userId)

export const removeCard = (cardId) => removeBankCard(cardId)

export const topUpWallet = () => Promise.reject(new Error('Wallet top-up is not available on the current backend'))

export const getRecentTrips = async (limit = 3, userId = getStoredUserId()) => {
  const bookings = await getBookingHistory(userId)
  return (Array.isArray(bookings) ? bookings : [])
    .filter(item => String(item?.status || '').toUpperCase() === 'COMPLETED')
    .slice(0, limit)
}

export const getSettings = async (userId = getStoredUserId()) => {
  const id = ensureUserId(userId)
  return request.get(`/users/${id}/settings`)
}

export const updateSettings = (data = {}) => Promise.resolve(data)

export const setDefaultCard = () => Promise.reject(new Error('Default card update is not available on the current backend'))
