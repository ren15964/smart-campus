import request from '@/utils/request'

/**
 * 获取课程列表
 * @param {*} params
 * @returns
 */
export function getCourseList(params) {
  return request({
    url: '/course/list',
    method: 'get',
    params
  })
}

/**
 * 获取课程详情
 * @param {*} id 课程ID
 * @returns
 */
export function getCourseDetail(id) {
  return request({
    url: `/course/${id}`,
    method: 'get'
  })
}

/**
 * 添加课程（管理员）
 * @param {*} data
 * @returns
 */
export function addCourse(data) {
  return request({
    url: '/course/add',
    method: 'post',
    data
  })
}

/**
 * 更新课程（管理员）
 * @param {*} id 课程ID
 * @param {*} data
 * @returns
 */
export function updateCourse(id, data) {
  return request({
    url: `/course/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除课程（管理员）
 * @param {*} id 课程ID
 * @returns
 */
export function deleteCourse(id) {
  return request({
    url: `/course/${id}`,
    method: 'delete'
  })
}

/**
 * 获取教师所授课程列表
 * @param {*} params
 * @returns
 */
export function getTeacherCourses(params) {
  return request({
    url: '/course/teacher-courses',
    method: 'get',
    params
  })
}
