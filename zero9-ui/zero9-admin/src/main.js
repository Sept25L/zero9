import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'dayjs/locale/zh-cn'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import '@/router/permission'
import '@/assets/css/reset.css'
import 'virtual:svg-icons-register'
import SvgIcon from '@/components/SvgIcon'
import Editor from '@/components/Editor'
import UploadImage from '@/components/UploadImage'
import permission from './directives/permission'

const app = createApp(App)

app.directive('hasPermi', permission)

app.component('SvgIcon', SvgIcon)
app.component('Editor', Editor)
app.component('UploadImage', UploadImage)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})

app.mount('#app')
