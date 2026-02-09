import request from '@/utils/request'

/**
 * 获取通知公告列表
 * @param {*} params
 * @returns
 */
export function getNoticeList(params) {
  return request({
    url: '/notice/list',
    method: 'get',
    params
  })
}

/**
 * 获取通知详情
 * @param {*} id 通知ID
 * @returns
 */
export function getNoticeDetail(id) {
  return request({
    url: `/notice/${id}`,
    method: 'get'
  })
}

/**
 * 发布通知（管理员）
 * @param {*} data
 * @returns
 */
export function publishNotice(data) {
  return request({
    url: '/notice/publish',
    method: 'post',
    data
  })
}

/**
 * 更新通知（管理员）
 * @param {*} id 通知ID
 * @param {*} data
 * @returns
 */
export function updateNotice(id, data) {
  return request({
    url: `/notice/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除通知（管理员）
 * @param {*} id 通知ID
 * @returns
 */
export function deleteNotice(id) {
  return request({
    url: `/notice/${id}`,
    method: 'delete'
  })
}

/**
 * 标记通知已读
 * @param {*} noticeId 通知ID
 * @returns
 */
export function markNoticeAsRead(noticeId) {
  return request({
    url: '/notice/mark-read',
    method: 'post',
    data: { noticeId }
  })
}
