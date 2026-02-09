import request from '@/utils/request'

/**
 * 获取开课计划列表
 * @param {*} params
 * @returns
 */
export function getCourseScheduleList(params) {
  return request({
    url: '/course-schedule/list',
    method: 'get',
    params
  })
}

/**
 * 添加开课计划
 * @param {*} data
 * @returns
 */
export function addCourseSchedule(data) {
  return request({
    url: '/course-schedule/add',
    method: 'post',
    data
  })
}

/**
 * 更新开课计划
 * @param {*} id 开课计划ID
 * @param {*} data
 * @returns
 */
export function updateCourseSchedule(id, data) {
  return request({
    url: `/course-schedule/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除开课计划
 * @param {*} id 开课计划ID
 * @returns
 */
export function deleteCourseSchedule(id) {
  return request({
    url: `/course-schedule/${id}`,
    method: 'delete'
  })
}
