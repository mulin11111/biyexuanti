import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'

// 创建Vue应用
const app = createApp(App)

// 配置Axios
axios.defaults.baseURL = '/api'
axios.defaults.headers.post['Content-Type'] = 'application/json'

// 请求拦截器，添加token
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器
axios.interceptors.response.use(response => {
  return response.data
}, error => {
  // 处理401未授权
  if (error.response && error.response.status === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }
  return Promise.reject(error)
})

// 挂载全局属性
app.config.globalProperties.$axios = axios

// 注册插件
app.use(router)
app.use(ElementPlus)

// 挂载应用
app.mount('#app')
