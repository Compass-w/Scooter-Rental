/**
 * 订单与租赁相关API
 * 对应API文档的订单与租赁模块
 */

import request from '@/utils/request.js'

/**
 * 创建订单/开始用车
 * @param {Object} data - 订单数据
 * @param {number} data.userId - 用户ID
 * @param {number} data.scooterId - 滑板车ID
 * @param {string} data.planType - 计费类型：PAY_AS_YOU_GO(按次), 1_HOUR(1小时), 1_DAY(1天)
 * @returns {Promise} - 返回订单信息
 */
export const startRide = (data) => {
  return request.post('/bookings/start', data)
}

/**
 * 结束用车/还车
 * @param {number} bookingId - 订单ID
 * @param {Object} data - 还车数据
 * @param {number} data.endLatitude - 结束位置纬度
 * @param {number} data.endLongitude - 结束位置经度
 * @returns {Promise} - 返回结算信息
 */
export const endRide = (bookingId, data) => {
  return request.post(`/bookings/${bookingId}/end`, data)
}

/**
 * 获取我的历史订单
 * @returns {Promise} - 返回历史订单列表
 */
export const getMyBookings = () => {
  return request.get('/bookings/my')
}
