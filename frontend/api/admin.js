import request from '@/utils/request.js'

export const getAdminDashboard = () => request.get('/admin/dashboard')

export const createAdminScooter = (data) => request.post('/admin/scooters', data)

export const updateAdminScooter = (scooterId, data) => request.put(`/admin/scooters/${scooterId}`, data)

export const overrideScooterStatus = (scooterId, status) =>
  request.patch(`/admin/scooters/${scooterId}/status`, { status })

export const getMaintenanceLogs = (scooterId) =>
  request.get(`/admin/scooters/${scooterId}/maintenance-logs`)

export const addMaintenanceLog = (scooterId, data) =>
  request.post(`/admin/scooters/${scooterId}/maintenance-logs`, data)

export const updateAdminIssue = (issueId, data) =>
  request.patch(`/admin/issues/${issueId}`, data)

export const getStaffBookings = () =>
  request.get('/admin/pos/bookings')

export const createStaffBooking = (data) =>
  request.post('/admin/pos/bookings', data)

export const sendStaffConfirmation = (bookingId, email) =>
  request.post(`/admin/pos/bookings/${bookingId}/confirmation`, { email })
