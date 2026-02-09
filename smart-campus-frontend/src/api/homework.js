import request from '@/utils/request'

/**
 * 学生查看作业列表
 * @param {*} params
 * @returns
 */
export function getMyHomeworkList(params) {
  return request({
    url: '/homework/my-homework',
    method: 'get',
    params
  })
}

/**
 * 查看作业详情
 * @param {*} id 作业ID
 * @returns
 */
export function getHomeworkDetail(id) {
  return request({
    url: `/homework/${id}`,
    method: 'get'
  })
}

/**
 * 学生提交作业
 * @param {*} data
 * @returns
 */
export function submitHomework(data) {
  return request({
    url: '/homework/submit',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }, // 如果有文件上传
  })
}

/**
 * 教师发布作业
 * @param {*} data
 * @returns
 */
export function publishHomework(data) {
  return request({
    url: '/homework/publish',
    method: 'post',
    data
  })
}

/**
 * 教师更新作业
 * @param {*} id 作业ID
 * @param {*} data
 * @returns
 */
export function updateHomework(id, data) {
  return request({
    url: `/homework/${id}`,
    method: 'put',
    data
  })
}

/**
 * 教师删除作业
 * @param {*} id 作业ID
 * @returns
 */
export function deleteHomework(id) {
  return request({
    url: `/homework/${id}`,
    method: 'delete'
  })
}

/**
 * 教师查看作业提交情况
 * @param {*} id 作业ID
 * @returns
 */
export function getHomeworkSubmissions(id) {
  return request({
    url: `/homework/${id}/submissions`,
    method: 'get'
  })
}

/**
 * 教师批改作业
 * @param {*} data
 * @returns
 */
export function gradeHomework(data) {
  return request({
    url: '/homework/grade',
    method: 'post',
    data
  })
}
