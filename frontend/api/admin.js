/**
 * 管理员数据统计相关API
 * 对应API文档的数据统计模块
 */

import request from '@/utils/request.js'

/**
 * [管理员] 获取收入报表
 * @param {Object} params - 查询参数
 * @param {string} params.type - 报表类型：daily（日报）或 weekly（周报）
 * @returns {Promise} - 返回收入报表数据
 */
export const getIncomeReport = (params = { type: 'daily' }) => {
  return request.get('/admin/reports/income', params)
}
