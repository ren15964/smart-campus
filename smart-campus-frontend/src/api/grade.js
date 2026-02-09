import request from '@/utils/request'

/**
 * 学生查询个人成绩
 * @param {*} params
 * @returns
 */
export function getMyGrades(params) {
  return request({
    url: '/grade/my-grades',
    method: 'get',
    params
  })
}

/**
 * 获取学生成绩统计信息
 * @returns
 */
export function getGradeStatistics() {
  return request({
    url: '/grade/statistics',
    method: 'get'
  })
}

/**
 * 教师查看所授课程的学生成绩列表
 * @param {*} scheduleId 开课计划ID
 * @returns
 */
export function getCourseStudentGrades(scheduleId) {
  return request({
    url: `/grade/course/${scheduleId}`,
    method: 'get'
  })
}

/**
 * 教师录入学生成绩
 * @param {*} data
 * @returns
 */
export function inputGrade(data) {
  return request({
    url: '/grade/input',
    method: 'post',
    data
  })
}

/**
 * 教师批量录入成绩
 * @param {*} data
 * @returns
 */
export function batchInputGrade(data) {
  return request({
    url: '/grade/batch-input',
    method: 'post',
    data
  })
}
