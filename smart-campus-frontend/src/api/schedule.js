import request from '@/utils/request'

/**
 * 获取个人课表（学生/教师）
 * @param {*} params
 * @returns
 */
export function getMySchedule(params) {
  return request({
    url: '/schedule/my-schedule',
    method: 'get',
    params
  })
}

/**
 * 获取按周显示的课表
 * @param {*} params
 * @returns
 */
export function getWeekViewSchedule(params) {
  return request({
    url: '/schedule/week-view',
    method: 'get',
    params
  })
}
