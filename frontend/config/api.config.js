/**
 * API配置文件
 * 统一管理API相关的配置信息
 */

// API基础配置
export const API_CONFIG = {
  // 基础URL - 根据环境切换
  BASE_URL: process.env.NODE_ENV === 'development' 
    ? 'http://localhost:8080/api' 
    : 'https://your-production-domain.com/api',
  
  // 请求超时时间（毫秒）
  TIMEOUT: 10000,
  
  // Token存储的key
  TOKEN_KEY: 'token',
  
  // 用户信息存储的key
  USER_INFO_KEY: 'userInfo',
  
  // 记住用户名的key
  SAVED_USERNAME_KEY: 'savedUsername'
}

// 响应状态码
export const RESPONSE_CODE = {
  SUCCESS: 200,        // 成功
  CLIENT_ERROR: 400,   // 客户端错误
  UNAUTHORIZED: 401,   // 未登录/未授权
  SERVER_ERROR: 500    // 服务器错误
}

// 用户角色
export const USER_ROLE = {
  USER: 'USER',       // 普通用户
  ADMIN: 'ADMIN'      // 管理员
}

// 滑板车状态
export const SCOOTER_STATUS = {
  AVAILABLE: 'AVAILABLE',     // 可用
  IN_USE: 'IN_USE',          // 使用中
  MAINTENANCE: 'MAINTENANCE'  // 维修中
}

// 计费类型
export const PLAN_TYPE = {
  PAY_AS_YOU_GO: 'PAY_AS_YOU_GO',  // 按次计费
  ONE_HOUR: '1_HOUR',              // 1小时套餐
  ONE_DAY: '1_DAY'                 // 1天套餐
}

// 订单状态
export const BOOKING_STATUS = {
  PENDING: 'PENDING',       // 待支付
  IN_PROGRESS: 'IN_PROGRESS', // 进行中
  COMPLETED: 'COMPLETED',   // 已完成
  CANCELLED: 'CANCELLED'    // 已取消
}

// 故障类别
export const ISSUE_CATEGORY = {
  MECHANICAL: 'MECHANICAL',   // 机械故障
  ELECTRICAL: 'ELECTRICAL',   // 电气故障
  BATTERY: 'BATTERY',         // 电池问题
  OTHER: 'OTHER'              // 其他
}

// 报表类型
export const REPORT_TYPE = {
  DAILY: 'daily',    // 日报
  WEEKLY: 'weekly'   // 周报
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
