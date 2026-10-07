import request from '@/utils/request'

// 获取用户
export function getUsers(query) {
    return request({
        url: '/user/list',
        method: 'get',
        params: query
    })
}

export function getUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

export function addUser(data) {
  return request({
    url: `/user/add`,
    method: 'post',
    data
  })
}

export function editUser(data) {
  return request({
    url: `/user/edit`,
    method: 'post',
    data
  })
}

export function updateUserStatus(data) {
  return request({
    url: `/user/change-status`,
    method: 'post',
    data
  })
}

export function removeUser(userIds) {
  return request({
    url: `/user/${userIds}`,
    method: 'delete'
  })
}

export function updateInfo(data) {
  return request({
    url: `/user/update`,
    method: 'post',
    data
  })
}

export function resetPwd(data) {
  return request({
    url: `/user/reset`,
    method: 'post',
    data
  })
}