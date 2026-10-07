import router from '@/router'

const modules = import.meta.glob('@/views/**/*.vue')

export const generateRoutes = (menus) => {

  menus.forEach(menu => {

    if (menu.menuType === 'C') {

      const componentPath = `/src/views${menu.component}.vue`

      const component = modules[componentPath]

      if (!component) {
        console.error('组件不存在:', componentPath)
        return
      }

      const route = {
        path: menu.path,
        name: menu.routeName || menu.path,
        meta: {title: menu.menuName},
        component
      }

      // 🔥 关键：注册路由
      router.addRoute('Layout', route)
    }

    if (menu.children && menu.children.length) {
      generateRoutes(menu.children)
    }

  })
}