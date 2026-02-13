/**
 * API Configuration File
 * Centralized management of API-related configuration
 */

// API Base Configuration
export const API_CONFIG = {
  // Base URL - switches based on environment
  BASE_URL: process.env.NODE_ENV === 'development' 
    ? 'http://localhost:8080/api' 
    : 'https://your-production-domain.com/api',
  
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
  ONE_DAY: '1_DAY'                 // 1 day plan
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