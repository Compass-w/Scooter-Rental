/**
 * API Request Utility
 * Unified handling of requests, responses, errors, and tokens
 */

// API Base Configuration
const BASE_URL = 'http://localhost:8080/api'
const TIMEOUT = 10000

/**
 * Get stored token
 */
const getToken = () => {
  try {
    return uni.getStorageSync('token') || ''
  } catch (e) {
    console.error('Failed to get token:', e)
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
    console.error('Failed to save token:', e)
  }
}

/**
 * Clear token
 */
const clearToken = () => {
  try {
    uni.removeStorageSync('token')
  } catch (e) {
    console.error('Failed to clear token:', e)
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
        const { code, message, data } = res.data

        // 200 means success
        if (code === 200) {
          resolve(data)
        } 
        // 401 means unauthorized or token expired
        else if (code === 401) {
          clearToken()
          uni.showToast({
            title: 'Please login again',
            icon: 'none'
          })
          // Redirect to login page
          setTimeout(() => {
            uni.reLaunch({
              url: '/pages/login'
            })
          }, 1500)
          reject(new Error(message || 'Authentication failed'))
        }
        // Other errors
        else {
          uni.showToast({
            title: message || 'Request failed',
            icon: 'none'
          })
          reject(new Error(message || 'Request failed'))
        }
      },
      fail: (err) => {
        console.error('Request failed:', err)
        
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
  delete: del,
  getToken,
  setToken,
  clearToken,
  BASE_URL
}