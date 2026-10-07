import request from '@/utils/request'

// 获取角色

export function getRoles(query) {
    return request({
        url: '/role/list',
        method: 'get',
        params: query
    })
}

export function getCurrentUserRoles() {
    return request({
        url: '/role/current-user',
        method: 'get',
    })
}

export function getRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: `/role/add`,
    method: 'post',
    data
  })
}

export function editRole(data) {
  return request({
    url: `/role/edit`,
    method: 'put',
    data
  })
}

export function removeRole(roleIds) {
  return request({
    url: `/role/${roleIds}`,
    method: 'delete'
  })
}