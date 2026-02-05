/**
 * API请求封装工具
 * 统一处理请求、响应、错误和token
 */

// API基础配置
const BASE_URL = 'http://localhost:8080/api'
const TIMEOUT = 10000

/**
 * 获取存储的token
 */
const getToken = () => {
  try {
    return uni.getStorageSync('token') || ''
  } catch (e) {
    console.error('获取token失败:', e)
    return ''
  }
}

/**
 * 保存token到本地存储
 */
const setToken = (token) => {
  try {
    uni.setStorageSync('token', token)
  } catch (e) {
    console.error('保存token失败:', e)
  }
}

/**
 * 清除token
 */
const clearToken = () => {
  try {
    uni.removeStorageSync('token')
  } catch (e) {
    console.error('清除token失败:', e)
  }
}

/**
 * 统一请求封装
 * @param {Object} options - 请求配置
 * @returns {Promise} - 返回Promise对象
 */
const request = (options) => {
  return new Promise((resolve, reject) => {
    // 构建完整URL
    const url = options.url.startsWith('http') 
      ? options.url 
      : BASE_URL + options.url

    // 获取token
    const token = getToken()

    // 构建请求头
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    }

    // 如果有token，添加到请求头
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }

    // 发起请求
    uni.request({
      url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      timeout: options.timeout || TIMEOUT,
      success: (res) => {
        // 根据API文档的标准响应结构处理
        const { code, message, data } = res.data

        // 200表示成功
        if (code === 200) {
          resolve(data)
        } 
        // 401表示未登录或token过期
        else if (code === 401) {
          clearToken()
          uni.showToast({
            title: 'Please login again',
            icon: 'none'
          })
          // 跳转到登录页
          setTimeout(() => {
            uni.reLaunch({
              url: '/pages/login'
            })
          }, 1500)
          reject(new Error(message || 'Authentication failed'))
        }
        // 其他错误
        else {
          uni.showToast({
            title: message || 'Request failed',
            icon: 'none'
          })
          reject(new Error(message || 'Request failed'))
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        
        // 网络错误提示
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
 * GET请求
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
 * POST请求
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
 * PUT请求
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
 * DELETE请求
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
