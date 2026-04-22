import request from '@/utils/request.js'
import { findActiveRide, getStoredUserId } from '@/utils/activeRide.js'

export const startRide = (data) => {
  return request.post('/bookings/start', data)
}

export const getUserBookings = (userId = getStoredUserId()) => {
  if (!userId) {
    return Promise.resolve([])
  }

  return request.get(`/bookings/user/${userId}`)
}

export const getActiveRide = async (userId = getStoredUserId()) => {
  const bookings = await getUserBookings(userId)
  return findActiveRide(bookings)
}

export const extendRide = (bookingId, extraMinutes) => {
  return request.post(`/bookings/${bookingId}/extend`, { extraMinutes })
}

export const sendRideTelemetry = (bookingId, data = {}) => {
  return request.post(`/bookings/${bookingId}/telemetry`, data)
}

export const endRide = (bookingId, options = {}) => {
  const { useLegacy = true, ...payload } = options

  if (useLegacy) {
    return request.post('/bookings/end', { bookingId, ...payload })
  }

  return request.post(`/bookings/${bookingId}/end`, payload)
}
