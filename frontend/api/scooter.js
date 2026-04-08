/**
 * Scooter API Module
 * Connects to backend: /api/scooters
 */
import request from '@/utils/request.js'

/**
 * Get all available scooters for map display
 * GET /api/scooters/available
 */
export const getAvailableScooters = () => {
  return request.get('/scooters/available')
}

/**
 * Get all scooters (available + in-use) for list display
 * Calls /available with optional lat/lng params; falls back gracefully if unsupported.
 * NOTE: Backend currently exposes GET /api/scooters/available — no root /scooters list endpoint.
 * GET /api/scooters/available
 */
export const getAllScooters = (lat, lng) => {
  const params = {}
  if (lat != null) params.lat = lat
  if (lng != null) params.lng = lng
  return request.get('/scooters', params)
}

/**
 * Get scooter detail by ID
 * GET /api/scooters/{id}
 */
export const getScooterById = (id) => {
  return request.get(`/scooters/${id}`)
}

/**
 * Start a ride
 * POST /api/bookings/start
 */
export const startRide = (userIdOrPayload, scooterId, planType = 'PAY_AS_YOU_GO') => {
  if (typeof userIdOrPayload === 'object' && userIdOrPayload !== null) {
    return request.post('/bookings/start', userIdOrPayload)
  }

  return request.post('/bookings/start', {
    userId: userIdOrPayload,
    scooterId,
    planType
  })
}

/**
 * End an active ride
 * POST /api/bookings/{bookingId}/end
 */
export const endRide = (bookingId) => {
  return request.post(`/bookings/${bookingId}/end`)
}
