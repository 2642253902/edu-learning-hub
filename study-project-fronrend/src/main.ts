
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import axios from 'axios'

// 创建前端应用实例，后续的状态、路由和 UI 组件都在这里统一挂载。
const app = createApp(App)

// 将后端地址注入 axios 全局配置，避免前端各处请求手动拼接基础域名。
// 运行时环境变量可能未设置，使用空字符串回退以避免在 replace 上抛错。
const apiBaseURL = import.meta.env.VITE_API_BASE_URL ?? ''
axios.defaults.baseURL = apiBaseURL.replace(/\/$/, '')

// 先注册前端状态管理，再注册路由和组件库，保证根组件启动时依赖已就绪。
app.use(createPinia())
app.use(router)
app.use(ElementPlus, { size: 'small', zIndex: 3000 })

// 最后挂载到页面容器，前端应用从这里正式进入可交互状态。
app.mount('#app')
