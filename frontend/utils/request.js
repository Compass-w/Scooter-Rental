/**
 * API Request Utility
 * Unified handling of requests, responses, errors, and tokens
 */

// Import API configuration
import { API_CONFIG } from '@/config/api.config.js'

// API Base Configuration
const BASE_URL = API_CONFIG.BASE_URL
const TIMEOUT = API_CONFIG.TIMEOUT || 20000

/**
 * Get stored token
 */
const getToken = () => {
  try {
    return uni.getStorageSync('token') || ''
  } catch (e) {
    globalThis.__APP_LOGGER__?.error('Failed to get token:', e)
    return ''
  }
}

/**
 * Save token to local storage
 */
const setToken = (token) => {
  try {
    uni.setStorageSync('token', token)
  } catch (e) {
    globalThis.__APP_LOGGER__?.error('Failed to save token:', e)
  }
}

/**
 * Clear token
 */
const clearToken = () => {
  try {
    uni.removeStorageSync('token')
  } catch (e) {
    globalThis.__APP_LOGGER__?.error('Failed to clear token:', e)
  }
}

/**
 * Unified request wrapper
 * @param {Object} options - Request configuration
 * @returns {Promise} - Returns Promise object
 */
const request = (options) => {
  return new Promise((resolve, reject) => {
    // Build complete URL
    const url = options.url.startsWith('http') 
      ? options.url 
      : BASE_URL + options.url

    // Get token
    const token = getToken()

    // Build request headers
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    }

    // Add token to header if exists
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }

    // Send request
    uni.request({
      url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      timeout: options.timeout || TIMEOUT,
      success: (res) => {
        // Handle based on API standard response structure
        const payload = res.data || {}
        const { code, message, data, error } = payload
        const statusCode = Number(res.statusCode || 0)
        const normalizedUrl = String(options.url || '')

        // 200 means success
        if (code === 200) {
          resolve(data)
        } 
        // 401 means the session is missing/expired: clear auth and ask the user to log in again.
        else if (code === 401 || statusCode === 401) {
          clearToken()
          uni.showToast({
            title: message || error || 'Please login again',
            icon: 'none'
          })
          // Redirect to login page
          setTimeout(() => {
            uni.reLaunch({
              url: '/pages/login'
            })
          }, 1500)
          reject(new Error(message || error || 'Authentication failed'))
        }
        // 403 means the current user is authenticated but not allowed to perform this action.
        // Keep the token intact so one denied background request does not log out managers/admins.
        else if (code === 403 || statusCode === 403) {
          const errorMsg = message || error || 'Access denied'
          uni.showToast({
            title: errorMsg,
            icon: 'none'
          })
          reject(new Error(errorMsg))
        }
        // Other errors
        else {
          let errorMsg = message || error || `Request failed${statusCode ? ` (${statusCode})` : ''}`

          if (
            statusCode === 404 &&
            !message &&
            /\/auth\/(forgot-password|verify-reset-token|reset-password)$/.test(normalizedUrl)
          ) {
            errorMsg = 'Reset password API unavailable. Restart backend and try again.'
          }

          uni.showToast({
            title: errorMsg,
            icon: 'none'
          })
          reject(new Error(errorMsg))
        }
      },
      fail: (err) => {
        globalThis.__APP_LOGGER__?.error('Request failed:', err)
        
        // Network error message
        let errorMsg = 'Network error'
        if (err.errMsg) {
          if (err.errMsg.includes('timeout')) {
            errorMsg = 'Request timeout'
          } else if (err.errMsg.includes('fail')) {
            errorMsg = 'Network connection failed'
          }
        }
        
        uni.showToast({
          title: errorMsg,
          icon: 'none'
        })
        
        reject(err)
      }
    })
  })
}

/**
 * GET Request
 */
const get = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'GET',
    data,
    ...options
  })
}

/**
 * POST Request
 */
const post = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  })
}

/**
 * PUT Request
 */
const put = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  })
}

/**
 * PATCH Request
 */
const patch = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'PATCH',
    data,
    ...options
  })
}

/**
 * DELETE Request
 */
const del = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  })
}

export default {
  request,
  get,
  post,
  put,
  patch,
  delete: del,
  getToken,
  setToken,
  clearToken,
  BASE_URL
}
