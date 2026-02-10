/**
 * 故障上报相关API
 * 对应API文档的故障上报模块
 */

import request from '@/utils/request.js'

/**
 * 上报故障
 * @param {Object} data - 故障数据
 * @param {number} data.userId - 用户ID
 * @param {number} data.scooterId - 滑板车ID
 * @param {string} data.description - 故障描述
 * @param {string} data.category - 故障类别，如：MECHANICAL（机械）
 * @returns {Promise} - 返回上报结果
 */
export const reportIssue = (data) => {
  return request.post('/issues', data)
}

/**
 * [管理员] 获取故障列表
 * @param {Object} params - 查询参数
 * @returns {Promise} - 返回故障列表
 */
export const getIssues = (params = {}) => {
  return request.get('/admin/issues', params)
}
