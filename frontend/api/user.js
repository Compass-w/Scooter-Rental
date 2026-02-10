/**
 * User Management API
 * Corresponds to User Management module in API documentation
 */

import request from '@/utils/request.js'

/**
 * User Registration
 * @param {Object} data - Registration data
 * @param {string} data.username - Username
 * @param {string} data.password - Password
 * @param {string} data.email - Email address
 * @param {string} data.phone - Phone number
 * @returns {Promise} - Returns registration result
 */
export const register = (data) => {
  return request.post('/users/register', data)
}

/**
 * User Login
 * @param {Object} data - Login data
 * @param {string} data.username - Username
 * @param {string} data.password - Password
 * @returns {Promise} - Returns login result including token and user info
 */
export const login = (data) => {
  return request.post('/users/login', data)
}

/**
 * Forgot Password - Send Reset Link
 * @param {Object} data - Email data
 * @param {string} data.email - User email address
 * @returns {Promise} - Returns send result
 * 
 * API Endpoint: POST /users/forgot-password
 * Request Body: { "email": "user@example.com" }
 * Response: { "code": 200, "message": "Reset link sent to email", "data": null }
 */
export const forgotPassword = (data) => {
  return request.post('/users/forgot-password', data)
}

/**
 * Reset Password
 * @param {Object} data - Password reset data
 * @param {string} data.token - Reset token (from email link)
 * @param {string} data.newPassword - New password
 * @returns {Promise} - Returns reset result
 * 
 * API Endpoint: POST /users/reset-password
 * Request Body: { "token": "reset_token_xxx", "newPassword": "newpass123" }
 * Response: { "code": 200, "message": "Password reset successfully", "data": null }
 */
export const resetPassword = (data) => {
  return request.post('/users/reset-password', data)
}

/**
 * Verify Reset Token Validity
 * @param {string} token - Reset token
 * @returns {Promise} - Returns verification result
 * 
 * API Endpoint: GET /users/verify-reset-token?token=xxx
 * Response: { "code": 200, "message": "Token is valid", "data": { "email": "user@example.com" } }
 */
export const verifyResetToken = (token) => {
  return request.get('/users/verify-reset-token', { token })
}

/**
 * User Logout (Client-side processing)
 * Clear locally stored token and user information
 */
export const logout = () => {
  try {
    // Clear token
    request.clearToken()
    // Clear user info
    uni.removeStorageSync('userInfo')
    // Navigate to login page
    uni.reLaunch({
      url: '/pages/login'
    })
  } catch (e) {
    console.error('Logout failed:', e)
  }
}

/**
 * Get Current User Info (from local storage)
 */
export const getUserInfo = () => {
  try {
    return uni.getStorageSync('userInfo') || null
  } catch (e) {
    console.error('Failed to get user info:', e)
    return null
  }
}

/**
 * Save User Info to Local Storage
 */
export const setUserInfo = (userInfo) => {
  try {
    uni.setStorageSync('userInfo', userInfo)
  } catch (e) {
    console.error('Failed to save user info:', e)
  }
}

/**
 * Check if User is Logged In
 */
export const isLoggedIn = () => {
  const token = request.getToken()
  return !!token
}