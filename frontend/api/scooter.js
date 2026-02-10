/**
 * 滑板车管理相关API
 * 对应API文档的滑板车模块
 */

import request from '@/utils/request.js'

/**
 * 获取地图上可用的滑板车列表
 * @returns {Promise} - 返回可用滑板车列表
 */
export const getAvailableScooters = () => {
  return request.get('/scooters/available')
}

/**
 * 获取单个滑板车详情
 * @param {number} id - 滑板车ID
 * @returns {Promise} - 返回滑板车详情
 */
export const getScooterDetails = (id) => {
  return request.get(`/scooters/${id}`)
}

/**
 * [管理员] 添加新滑板车
 * @param {Object} data - 滑板车数据
 * @returns {Promise} - 返回添加结果
 */
export const addScooter = (data) => {
  return request.post('/admin/scooters', data)
}

/**
 * [管理员] 更新滑板车信息
 * @param {Object} data - 滑板车数据（需包含id）
 * @returns {Promise} - 返回更新结果
 */
export const updateScooter = (data) => {
  return request.put('/admin/scooters', data)
}
