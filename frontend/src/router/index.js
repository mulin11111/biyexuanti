import { createRouter, createWebHistory } from 'vue-router'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../components/Layout.vue'),
    redirect: '/dashboard',
    children: [
      // 管理员路由
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/admin/Dashboard.vue'),
        meta: { title: '首页', role: 'admin' }
      },
      {
        path: '/admin/users',
        name: 'UserManagement',
        component: () => import('../views/admin/UserManagement.vue'),
        meta: { title: '用户管理', role: 'admin' }
      },
      {
        path: '/admin/topics',
        name: 'TopicManagement',
        component: () => import('../views/admin/TopicManagement.vue'),
        meta: { title: '选题管理', role: 'admin' }
      },
      {
        path: '/admin/progress',
        name: 'ProgressManagement',
        component: () => import('../views/admin/ProgressManagement.vue'),
        meta: { title: '进度管理', role: 'admin' }
      },
      {
        path: '/admin/statistics',
        name: 'Statistics',
        component: () => import('../views/admin/Statistics.vue'),
        meta: { title: '数据统计', role: 'admin' }
      },
      // 用户路由
      {
        path: '/user/topics',
        name: 'UserTopics',
        component: () => import('../views/user/Topics.vue'),
        meta: { title: '选题列表', role: 'user' }
      },
      {
        path: '/user/selected-topic',
        name: 'SelectedTopic',
        component: () => import('../views/user/SelectedTopic.vue'),
        meta: { title: '已选课题', role: 'user' }
      },
      {
        path: '/user/progress',
        name: 'UserProgress',
        component: () => import('../views/user/Progress.vue'),
        meta: { title: '进度管理', role: 'user' }
      }
    ]
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 毕业设计选题系统` : '毕业设计选题系统'

  // 登录页直接放行
  if (to.path === '/login') {
    next()
    return
  }

  // 检查是否登录
  const token = localStorage.getItem('token')
  if (!token) {
    next('/login')
    return
  }

  // 检查角色权限
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    if (to.meta.role && to.meta.role !== userInfo.role) {
      // 根据角色重定向到对应首页
      if (userInfo.role === 'admin') {
        next('/dashboard')
      } else {
        next('/user/topics')
      }
      return
    }
  } catch (error) {
    // 如果解析失败，重定向到登录页
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    next('/login')
    return
  }

  next()
})

export default router
