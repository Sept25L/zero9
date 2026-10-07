import { defineStore } from "pinia"
import { getToken, setToken, removeToken } from '@/utils/auth'
import { login, getInfo, logout } from '@/api/system/auth'
import { generateRoutes } from '@/utils/permission'

const useUserStore = defineStore(
    'user',
    {
        state: () => ({
            token: getToken(),
            id: '',
            nickName: '',
            avatar: '',
            menus: [],
            perms:[]
        }),
        getters: {
            getAvatar: (state) => {
                if (!state.avatar) return ''
                if (state.avatar.startsWith('http')) return state.avatar

                return import.meta.env.VITE_APP_BASE_URL + state.avatar
            }
        },
        actions: {
            login(user) {
                return new Promise((resolve, reject) => {
                    // 调用登录接口api
                    login(user).then(async resp => {
                        // 缓存token信息
                        setToken(resp.data.token)
                        this.token = resp.data.token
                        resolve()
                    })
                    .catch(error => {
                        reject(error)
                    })
                })
            },
            getInfo() {
                return new Promise((resolve, reject) => {
                    getInfo().then(resp => {
                        this.id = resp.data.user.id
                        this.nickName = resp.data.user.nickName
                        this.avatar = resp.data.user.avatar
                        // 2. 生成动态路由
                        this.menus = resp.data.menus
                        this.perms = resp.data.perms
                        generateRoutes(this.menus)
                        resolve(resp)
                    })
                    .catch(error => {
                        reject(error)
                    })
                })
            },
            logout() {
                return new Promise((resolve, reject) => {
                    logout(this.token).then(resp => {
                        resolve(resp)
                    })
                    .catch(error => {
                        reject(error)
                    })
                    .finally(() => {
                        this.id = ''
                        this.avatar  = ''
                        this.nickName = ''
                        this.token = ''
                        this.menus = []
                        this.perms = []
                        removeToken()
                        localStorage.removeItem('tabs')
                    })
                })
            }
        }
    }
)

export default useUserStore