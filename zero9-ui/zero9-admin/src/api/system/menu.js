import request from '@/utils/request'

export function getCurrentMenus(query) {
    return request({
        url: '/menu/current-menu',
        method: 'get'
    })
}

export function getMenus(query) {
    return request({
        url: '/menu/list',
        method: 'get',
        params: query
    })
}

export function getMenu(id) {
    return request({
        url: `/menu/${id}`,
        method: 'get'
    })
}

export function addMenu(data) {
    return request({
        url: `/menu/add`,
        method: 'post',
        data
    })
}

export function editMenu(data) {
    return request({
        url: `/menu/edit`,
        method: 'post',
        data
    })
}

export function removeMenu(id) {
  return request({
    url: `/menu/${id}`,
    method: 'delete'
  })
}