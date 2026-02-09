import request from '@/utils/request'

/**
 * 获取当前学期可选课程列表（学生）
 * @param {*} params
 * @returns
 */
export function getAvailableCourses(params) {
  return request({
    url: '/course-selection/available',
    method: 'get',
    params
  })
}

/**
 * 获取当前学生已选课程列表（学生）
 * @param {*} params
 * @returns
 */
export function getMyCourses(params) {
  return request({
    url: '/course-selection/my-courses',
    method: 'get',
    params
  })
}

/**
 * 学生选课（学生）
 * @param {*} data
 * @returns
 */
export function selectCourse(data) {
  return request({
    url: '/course-selection/select',
    method: 'post',
    data
  })
}

/**
 * 学生退课（学生）
 * @param {*} id 选课记录ID
 * @returns
 */
export function dropCourse(id) {
  return request({
    url: `/course-selection/${id}`,
    method: 'delete'
  })
}

/**
 * 获取全局选课时间段（管理员）
 * @returns
 */
export function getSelectionPeriod() {
  return request({
    url: '/course-selection/period',
    method: 'get',
  })
}

/**
 * 设置/更新全局选课时间段（管理员）
 * @param {*} data
 * @returns
 */
export function saveSelectionPeriod(data) {
  return request({
    url: '/course-selection/period',
    method: 'post',
    data
  })
}

/**
 * 获取选课统计数据（管理员）
 * @returns
 */
export function getSelectionStatistics() {
  return request({
    url: '/course-selection/statistics',
    method: 'get',
  })
}
