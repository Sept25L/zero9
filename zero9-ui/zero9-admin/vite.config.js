/**
 * vite配置文件主要负责：
 * 
 *  环境配置：根据不同环境加载不同配置
 *  插件管理：集成各种功能插件
 *  路径管理：简化导入路径
 *  构建优化： 配置生产环境打包策略
 *  开发服务器：提供热部署和代理功能
 * 
 * 关键点：
 *  
 *  使用函数式配置，支持动态参数
 *  路径别名配置
 *  开发代理解决跨域问题
 *  可扩展插件架构
 */

// 导入vite相关函数模块
import { defineConfig, loadEnv } from "vite"
// 导入path模块，用于处理文件路径
import path from 'path'
// 导入自定义插件文件
import createVitePlugins from './vite/plugins'


export default defineConfig(({mode, command}) => {

  // 加载环境变量，根据当前项目加载对应的 .env 文件
  const env = loadEnv(mode, process.cwd())

  // 返回vite配置对象
  return {

    /* -----------基础配置----------- */
    base: '/',  //部署时的基础路径

    /* -----------插件配置----------- */
    //  command === 'build' 判断是否为构建命令
    plugins: createVitePlugins(env, command === 'build'),

    /* -----------路径解析配置----------- */
    resolve: {

      //  路径别名配置
      alias: {
        // '~'指向项目路径
        '~': path.resolve(__dirname, './'),
        //  '@'指向src目录
        '@': path.resolve(__dirname, './src')
      },

      //  自动解析的扩展名
      extensions: [
        '.mjs',   //  ES模块文件
        '.js',     //  JavaScript文件
        '.ts',     //  TypeScript文件
        '.jsx',    //  React JSX
        '.tsx',    //  TypeScript文件 TSX
        '.json',   //  Json文件
        '.vue'     // Vue但组件文件
      ]
    },

    /* -----------开发服务器配置----------- */
    server: {

      //  端口号
      port: 90,

      /**
       * 主机配置
       * true表示监听所有地址（包括局域网），可通过IP访问
       * 也可以指定具体IP，如：'0.0.0.0'    '192.168.2.2'
       */
      host: true,

      // 启动是否自动打开浏览器
      open: false,

      // 代理配置（解决跨域问题）
      proxy: {

        // 将所有以'/api'的开头请求代理到后端服务器
        '/api': {
          
          // 目标服务器地址
          target: 'http://localhost:8081',

          //  修改请求头中的Origin为目标服务器地址
          changeOrigin: true,

          /**
           * 代理服务器工作原理：
           * 前端：http://localhost:90/api/getUsers   ->
           * vite代理服务器转发   ->
           * 后端：http://localhost:8081/getUsers
           */

          rewrite: p => p.replace(/^\/api/, '')
        }
      }
    }
  }
})
