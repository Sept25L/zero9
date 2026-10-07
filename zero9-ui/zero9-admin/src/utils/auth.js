// 获取当前用户令牌
export function getToken() {
    return localStorage.getItem("tokenKey")
}

// 设置用户令牌
export function setToken(token) {
    return localStorage.setItem("tokenKey", token)
}

// 删除用户令牌
export function removeToken(token) {
    return localStorage.removeItem("tokenKey")
}