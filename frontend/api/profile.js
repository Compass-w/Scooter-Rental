/**
 * User Profile API
 * Corresponds to User Profile module in API documentation (Section 3)
 * Base URL: /api/users
 */

import request from '@/utils/request.js'

/**
 * Get the current user's ID from local storage
 * @returns {number|string} userId
 */
const getUserId = () => {
  try {
    const s = uni.getStorageSync('userInfo')
    const o = s ? (typeof s === 'string' ? JSON.parse(s) : s) : {}
    return o.userId || o.id || ''
  } catch (e) {
    console.error('Failed to get userId:', e)
    return ''
  }
}

/**
 * Get User Profile
 * @returns {Promise} - Returns user profile data
 *
 * API: GET /users/{id}
 * Response: { userId, username, email, phone, role, createdAt }
 */
export const getProfile = () => {
  return request.get(`/users/${getUserId()}`)
}

/**
 * Update User Profile
 * @param {Object} data - Fields to update
 * @param {string} [data.username] - Display username
 * @param {string} [data.email] - Email address (must be unique)
 * @param {string} [data.phone] - Phone number
 * @param {string} [data.city] - City of residence
 * @returns {Promise} - Returns updated user profile
 *
 * API: PUT /users/{id}
 */
export const updateProfile = (data) => {
  return request.put(`/users/${getUserId()}`, data)
}

/**
 * Get Ride Statistics
 * @param {string} period - Time period: 'Week' | 'Month' | 'All' (default: 'Month')
 * @returns {Promise} - Returns ride stats (totalRides, totalKm, co2Saved, periodRides, etc.)
 *
 * API: GET /users/{id}/stats?period=
 */
export const getStats = (period = 'Month') => {
  return request.get(`/users/${getUserId()}/stats`, { period })
}

/**
 * Get Wallet
 * @returns {Promise} - Returns balance, autoTopUp flag, and paymentMethods[]
 *
 * API: GET /users/{id}/wallet
 */
export const getWallet = () => {
  return request.get(`/users/${getUserId()}/wallet`)
}

/**
 * Top Up Wallet
 * @param {number} amount - Amount in GBP to add (e.g. 10.00)
 * @returns {Promise} - Returns updated balance
 *
 * API: POST /users/{id}/wallet/topup
 */
export const topUpWallet = (amount) => {
  return request.post(`/users/${getUserId()}/wallet/topup`, { amount })
}

/**
 * Add Payment Card
 * @param {Object} data - Card details
 * @param {string} data.number - Full card number, 16 digits, no spaces
 * @param {string} data.expires - Expiry date in MM/YY format
 * @param {string} data.cvv - Security code, 3-4 digits
 * @returns {Promise} - Returns saved card object (cardId, brand, last4, expires, isDefault)
 *
 * API: POST /users/{id}/wallet/cards
 */
export const addCard = (data) => {
  return request.post(`/users/${getUserId()}/wallet/cards`, data)
}

/**
 * Set Default Payment Card
 * @param {string} cardId - ID of the card to set as default
 * @returns {Promise} - Returns null on success
 *
 * API: PATCH /users/{id}/wallet/cards/{cardId}/default
 */
export const setDefaultCard = (cardId) => {
  return request.patch(`/users/${getUserId()}/wallet/cards/${cardId}/default`)
}

/**
 * Remove Payment Card
 * @param {string} cardId - ID of the card to remove
 * @returns {Promise} - Returns null on success
 *
 * API: DELETE /users/{id}/wallet/cards/{cardId}
 */
export const removeCard = (cardId) => {
  return request.delete(`/users/${getUserId()}/wallet/cards/${cardId}`)
}

/**
 * Get Recent Trips
 * @param {number} limit - Max number of trips to return (default: 3)
 * @returns {Promise} - Returns { items: Trip[] }
 *
 * API: GET /users/{id}/trips?limit=
 */
export const getRecentTrips = (limit = 3) => {
  return request.get(`/users/${getUserId()}/trips`, { limit })
}

/**
 * Get Bookings
 * @param {string} status - 'upcoming' | 'past'
 * @returns {Promise} - Returns { items: Booking[] }
 *
 * API: GET /users/{id}/bookings?status=
 */
export const getBookings = (status) => {
  return request.get(`/users/${getUserId()}/bookings`, { status })
}

/**
 * Cancel Booking
 * @param {number} bookingId - ID of the booking to cancel
 * @returns {Promise} - Returns null on success
 *
 * API: POST /users/{id}/bookings/{bookingId}/cancel
 * Note: A £1.00 fee applies if cancelled less than 1 hour before the booking time
 */
export const cancelBooking = (bookingId) => {
  return request.post(`/users/${getUserId()}/bookings/${bookingId}/cancel`)
}

/**
 * Get User Settings
 * @returns {Promise} - Returns { notifications, emailNotif, location, dataShare, autoTopUp }
 *
 * API: GET /users/{id}/settings
 */
export const getSettings = () => {
  return request.get(`/users/${getUserId()}/settings`)
}

/**
 * Update User Settings
 * @param {Object} data - Only the fields to change (partial update)
 * @param {boolean} [data.notifications] - Push notification toggle
 * @param {boolean} [data.emailNotif] - Weekly email digest toggle
 * @param {boolean} [data.location] - Location services toggle
 * @param {boolean} [data.dataShare] - Analytics sharing toggle
 * @param {boolean} [data.autoTopUp] - Auto top-up toggle
 * @returns {Promise} - Returns null on success
 *
 * API: PATCH /users/{id}/settings
 */
export const updateSettings = (data) => {
  return request.patch(`/users/${getUserId()}/settings`, data)
}
