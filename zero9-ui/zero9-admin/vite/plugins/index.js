// 导入vite插件
import vue from '@vitejs/plugin-vue'
import setupSvgIcons from './svg-icons'

export default function createVitePlugins (viteEnv, isBuild = false) {
    // 创建插件数组，初始化包含vue插件
    let vitePlugins =  [vue()]
    // 添加自动导入插件
    vitePlugins.push(setupSvgIcons())
    // 返回插件数组
    return vitePlugins
}