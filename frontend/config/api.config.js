/**
 * API Configuration File
 * Centralized management of API-related configuration
 */

const trimTrailingSlash = (value = '') => String(value || '').replace(/\/+$/, '')

const isLocalHostname = (hostname = '') =>
  ['localhost', '127.0.0.1', '::1'].includes(String(hostname || '').toLowerCase())

const resolveApiBaseUrl = () => {
  const explicitBaseUrl = trimTrailingSlash(
    process.env.UNI_APP_API_BASE_URL ||
    process.env.VITE_API_BASE_URL ||
    ''
  )

  if (explicitBaseUrl) {
    return explicitBaseUrl
  }

  if (typeof window !== 'undefined' && window.location?.origin) {
    const { origin, hostname } = window.location

    if (isLocalHostname(hostname)) {
      return 'http://localhost:8080/api'
    }

    return `${trimTrailingSlash(origin)}/api`
  }

  return 'http://localhost:8080/api'
}

// API Base Configuration
export const API_CONFIG = {
  // Base URL - can be overridden with UNI_APP_API_BASE_URL / VITE_API_BASE_URL
  BASE_URL: resolveApiBaseUrl(),
  
  // Request timeout (milliseconds)
  TIMEOUT: 10000,
  
  // Token storage key
  TOKEN_KEY: 'token',
  
  // User info storage key
  USER_INFO_KEY: 'userInfo',
  
  // Saved username key
  SAVED_USERNAME_KEY: 'savedUsername'
}

// Response Status Codes
export const RESPONSE_CODE = {
  SUCCESS: 200,        // Success
  CLIENT_ERROR: 400,   // Client error
  UNAUTHORIZED: 401,   // Not logged in / Unauthorized
  SERVER_ERROR: 500    // Server error
}

// User Roles
export const USER_ROLE = {
  USER: 'USER',       // Regular user
  ADMIN: 'ADMIN'      // Administrator
}

// Scooter Status
export const SCOOTER_STATUS = {
  AVAILABLE: 'AVAILABLE',     // Available
  IN_USE: 'IN_USE',          // In use
  MAINTENANCE: 'MAINTENANCE'  // Under maintenance
}

// Billing Plan Types
export const PLAN_TYPE = {
  PAY_AS_YOU_GO: 'PAY_AS_YOU_GO',  // Pay as you go
  ONE_HOUR: '1_HOUR',              // 1 hour plan
  FOUR_HOURS: '4_HOURS',           // 4 hour plan
  ONE_DAY: '1_DAY',                // 1 day plan
  ONE_WEEK: '1_WEEK',              // 1 week plan
  ONE_MONTH: '1_MONTH'             // 1 month plan
}

// Booking Status
export const BOOKING_STATUS = {
  PENDING: 'PENDING',       // Pending payment
  IN_PROGRESS: 'IN_PROGRESS', // In progress
  COMPLETED: 'COMPLETED',   // Completed
  CANCELLED: 'CANCELLED'    // Cancelled
}

// Issue Categories
export const ISSUE_CATEGORY = {
  MECHANICAL: 'MECHANICAL',   // Mechanical failure
  ELECTRICAL: 'ELECTRICAL',   // Electrical failure
  BATTERY: 'BATTERY',         // Battery issue
  OTHER: 'OTHER'              // Other
}

// Report Types
export const REPORT_TYPE = {
  DAILY: 'daily',    // Daily report
  WEEKLY: 'weekly'   // Weekly report
}

export default {
  API_CONFIG,
  RESPONSE_CODE,
  USER_ROLE,
  SCOOTER_STATUS,
  PLAN_TYPE,
  BOOKING_STATUS,
  ISSUE_CATEGORY,
  REPORT_TYPE
}
