import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  
  //  登录页面
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },

  //  注册页面
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/index'),
    redirect: '/index',
    children: [
      {
        path: 'index',
        name: 'Index',
        component: () => import('@/views/home/index'),
        meta: {title: '首页'}
      },
      {
        path: 'userinfo',
        name: 'userinfo',
        component: () => import('@/views/system/user/profile/userinfo'),
        meta: {title: '个人中心'}
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: routes,
})

export default router
