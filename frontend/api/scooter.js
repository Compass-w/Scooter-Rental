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
 * GET /api/scooters
 */
export const getAllScooters = () => {
  return request.get('/scooters')
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
export const startRide = (userId, scooterId, planType = 'PAY_AS_YOU_GO') => {
  return request.post('/bookings/start', {
    userId,
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