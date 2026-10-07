import router from '@/router'
import { getToken } from '@/utils/auth'
import useUserStore from '@/stores/userStore'
import { isReLogin } from '@/utils/request'
import { ElMessage } from 'element-plus'

// 路由路径白名单
const whiteList = ['/login', '/register']

const isWhiteList = (path) => {
    return whiteList.includes(path)
}

// 全局路由前置守卫
router.beforeEach(async (to, from, next) => {

    // 没有 Token
    if (!getToken()) {

        // 白名单直接放行
        if (isWhiteList(to.path)) {
            return next()
        }

        // 非白名单跳转登录
        return next('/login')
    }

    // 有 Token
    const userStore = useUserStore()

    // 已登录用户访问登录/注册
    if (isWhiteList(to.path)) {
        return next('/')
    }

    // 用户信息不存在
    if (!userStore.id) {
        try {
            isReLogin.show = true

            // 获取用户信息
            // 同时生成动态路由
            await userStore.getInfo()
            isReLogin.show = false
            // 动态路由生成完成后
            // 重新匹配当前路由
            return next(to.path)

        } catch (error) {

            try {
                await userStore.logout()
            } catch (logoutError) {
                console.error('退出登录失败:', logoutError)
            } finally {
                isReLogin.show = false
            }

            ElMessage.error(
                error?.message || '登录状态已失效，请重新登录'
            )

            return next('/login')

        }
    }

    // 用户信息已经存在
    return next()
})