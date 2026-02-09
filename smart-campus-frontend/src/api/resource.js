import request from '@/utils/request'

/**
 * 获取课程资源列表
 * @param {*} params
 * @returns
 */
export function getResourceList(params) {
  return request({
    url: '/resource/list',
    method: 'get',
    params
  })
}

/**
 * 上传课程资源（教师）
 * @param {*} data
 * @returns
 */
export function uploadResource(data) {
  return request({
    url: '/resource/upload',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

/**
 * 下载课程资源
 * @param {*} id 资源ID
 * @returns
 */
export function downloadResource(id) {
  return request({
    url: `/resource/download/${id}`,
    method: 'get',
    responseType: 'blob', // 下载文件通常需要设置为blob
  })
}

/**
 * 删除课程资源（教师）
 * @param {*} id 资源ID
 * @returns
 */
export function deleteResource(id) {
  return request({
    url: `/resource/${id}`,
    method: 'delete'
  })
}

/**
 * 更新课程资源（教师）
 * @param {*} id 资源ID
 * @param {*} data
 * @returns
 */
export function updateResource(id, data) {
  return request({
    url: `/resource/${id}`,
    method: 'put',
    data
  })
}
