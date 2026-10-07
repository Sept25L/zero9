// auth.js

import request from '@/utils/request'

// 登录接口
export function login(data) {
    return request({
        url: '/auth/login',
        headers: {isToken: false}, //告诉请求拦截器不需要token
        method: 'post',
        data
    })
}

// 获取登录 用户信息接口
export function getInfo() {
    return request({
        url: '/auth/getInfo',
        method: 'get'
    })
}

// 退出登录接口
export function logout() {
    return request({
        url: '/logout',
        method: 'post'
    })
}