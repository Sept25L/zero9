import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken } from './auth'
import useUserStore from '@/stores/userStore'
import router from '@/router'

/**
 * ===========================
 * 全局状态控制
 * ===========================
 */

/**
 * 是否正在重新登录（防止多个请求同时401时弹多个弹窗）
 */
export let isReLogin = {
    show: false
}


/**
 * ===========================
 * 创建 axios 实例
 * ===========================
 */
const server = axios.create({
    // 后端基础地址（通过环境变量配置）
    baseURL: import.meta.env.VITE_APP_BASE_URL,

    // 请求超时时间（这里写的是 10分钟，注意注释写错了）
    timeout: 10 * 60 * 1000,

    // 默认请求头
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
})


/**
 * ===========================
 * 统一处理 401（token过期）
 * ===========================
 */
const handleReLogin = () => {
    if (isReLogin.show) return

    isReLogin.show = true

    ElMessageBox.confirm(
        '登录已过期, 是否重新登录?',
        'Warning',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
    .then(async () => {
        // 先关闭弹窗状态
        isReLogin.show = false

        try {
            await useUserStore().logout()
        } catch (error) {
            console.error('logout失败:', error)
        } finally {
            // 无论 logout 成不成功，都跳登录页
            router.replace('/login')
        }
    })
    .catch(() => {
        isReLogin.show = false
    })
}


/**
 * ===========================
 * 请求拦截器
 * ===========================
 * 作用：
 * 1. 自动携带 token
 * 2. 防止重复提交
 */
server.interceptors.request.use(config => {

    /**
     * 是否需要携带 token（默认需要）
     * 可通过 headers: { isToken: false } 关闭
     */
    const isToken = config.headers?.isToken !== false

    /**
     * 是否开启防重复提交（默认开启）
     */
    const isRepeatSubmit = config.headers?.rePeatSubmit !== false

    // 自动注入 token
    if (getToken() && isToken) {
        config.headers['Authorization'] = 'Bearer ' + getToken()
    }

    /**
     * ===========================
     * 防重复提交逻辑（仅 POST / PUT）
     * ===========================
     */
    const reqMethods = ['post', 'put']

    if (!isRepeatSubmit && reqMethods.includes(config.method)) {

        const requestObj = {
            url: config.url,
            data: typeof config.data === 'object'
                ? JSON.stringify(config.data)
                : config.data,
            time: new Date().getTime()
        }

        // 上一次请求记录
        const sessionObj = sessionStorage.getItem('sessionObj')
            ? JSON.parse(sessionStorage.getItem('sessionObj'))
            : null

        if (sessionObj) {
            const { url: s_url, data: s_data, time: s_time } = sessionObj
            const interval = 1000 // 1秒内视为重复

            if (
                s_data === requestObj.data &&
                (requestObj.time - s_time) < interval &&
                s_url === requestObj.url
            ) {
                ElMessage.error('当前操作过于频繁, 请稍后再试!')
                // ⚠️ 注意：这里只提示，没有真正取消请求（可以优化）
            }
        }

        // 缓存本次请求
        sessionStorage.setItem('sessionObj', JSON.stringify(requestObj))
    }

    return config
}, error => {
    // 请求发送失败（很少见）
    ElMessage.error(error)
})


/**
 * ===========================
 * 响应拦截器
 * ===========================
 * 作用：
 * 1. 统一处理业务状态码
 * 2. 统一处理401（token失效）
 * 3. 统一错误提示
 */
server.interceptors.response.use(
  resp => {

    /**
     * 处理文件流（下载）
     */
    if ('blob' === resp.request.requestType) {
        return resp.data
    }

    /**
     * 从响应获取响应码和信息
     */
    const code = resp.data.code || 200
    const msg = resp.data.msg

    /**
     * ===========================
     * 业务层 401（通常是后端返回 code=401）
     * ===========================
     */
    if (code === 401) {
        handleReLogin()
        return Promise.reject('登录已过期, 请重新登录!')
    }

    /**
     * ===========================
     * 非成功状态（业务错误）
     * ===========================
     */
    if (code !== 200) {
        ElMessage.error(msg)
        return Promise.reject(new Error(msg))
    }

    // 正常返回
    return resp.data
  },

  error => {

    /**
     * ===========================
     * HTTP层错误处理
     * ===========================
     */
    let { message, response } = error

    if (response?.status === 400) {
        ElMessage.error(response?.data.msg)
        return Promise.reject(response?.data.msg)
    }
    /**
     * 401：token失效 / 未认证
     */
    if (response?.status === 401) {
        handleReLogin()
        return Promise.reject('登录已过期, 请重新登录')
    }
    else if (response?.status === 403) {
        ElMessage.error(response?.data.msg)
        return Promise.reject(response?.data.msg)
    }
    else if (response?.status === 404) {
        ElMessage.error(response?.data.msg)
        return Promise.reject(response?.data.msg)
    }

    /**
     * ===========================
     * 网络错误映射（兜底）
     * ===========================
     */
    const errorMap = {
        'Network Error': '后端接口异常',
        'timeout': '系统接口请求超时',
        'Request failed with status code': '系统接口' + message.slice(-3) + '异常'
    }

    Object.keys(errorMap).forEach(key => {
        if (message.includes(key)) {
            message = errorMap[key]
        }
    })

    ElMessage.error(message)
    return Promise.reject(message)
  }
)

export default server