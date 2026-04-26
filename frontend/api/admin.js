import request from '@/utils/request.js'

const ADMIN_READ_OPTIONS = { timeout: 30000 }
const ADMIN_WRITE_OPTIONS = { timeout: 25000 }

export const getAdminDashboard = () => request.get('/admin/dashboard', {}, ADMIN_READ_OPTIONS)

export const createAdminScooter = (data) => request.post('/admin/scooters', data, ADMIN_WRITE_OPTIONS)

export const updateAdminScooter = (scooterId, data) => request.put(`/admin/scooters/${scooterId}`, data, ADMIN_WRITE_OPTIONS)

export const overrideScooterStatus = (scooterId, status) =>
  request.patch(`/admin/scooters/${scooterId}/status`, { status }, ADMIN_WRITE_OPTIONS)

export const getMaintenanceLogs = (scooterId) =>
  request.get(`/admin/scooters/${scooterId}/maintenance-logs`, {}, ADMIN_READ_OPTIONS)

export const addMaintenanceLog = (scooterId, data) =>
  request.post(`/admin/scooters/${scooterId}/maintenance-logs`, data, ADMIN_WRITE_OPTIONS)

export const updateAdminIssue = (issueId, data) =>
  request.patch(`/admin/issues/${issueId}`, data, ADMIN_WRITE_OPTIONS)

export const getStaffBookings = () =>
  request.get('/admin/pos/bookings', {}, ADMIN_READ_OPTIONS)

export const createStaffBooking = (data) =>
  request.post('/admin/pos/bookings', data, ADMIN_WRITE_OPTIONS)

export const sendStaffConfirmation = (bookingId, email) =>
  request.post(`/admin/pos/bookings/${bookingId}/confirmation`, { email }, ADMIN_WRITE_OPTIONS)

export const createOpsAssignment = (data) =>
  request.post('/admin/ops/assignments', data, ADMIN_WRITE_OPTIONS)

export const updateOpsAssignmentStatus = (assignmentId, data) =>
  request.patch(`/admin/ops/assignments/${assignmentId}`, data, ADMIN_WRITE_OPTIONS)
