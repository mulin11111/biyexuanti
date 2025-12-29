<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ 'collapsed': isCollapsed }">
      <div class="sidebar-header">
        <h2 class="sidebar-title">{{ isCollapsed ? '系统' : '毕业设计选题系统' }}</h2>
        <el-button type="text" @click="toggleCollapse" class="collapse-btn">
          <el-icon>{{ isCollapsed ? 'Menu' : 'Close' }}</el-icon>
        </el-button>
      </div>
      <nav class="sidebar-menu">
        <!-- 管理员菜单 -->
        <template v-if="userInfo.role === 'admin'">
          <el-menu-item index="/dashboard" @click="$router.push('/dashboard')">
            <el-icon><House /></el-icon>
            <span>{{ isCollapsed ? '' : '首页' }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/users" @click="$router.push('/admin/users')">
            <el-icon><User /></el-icon>
            <span>{{ isCollapsed ? '' : '用户管理' }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/topics" @click="$router.push('/admin/topics')">
            <el-icon><Document /></el-icon>
            <span>{{ isCollapsed ? '' : '选题管理' }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/progress" @click="$router.push('/admin/progress')">
            <el-icon><Time /></el-icon>
            <span>{{ isCollapsed ? '' : '进度管理' }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/statistics" @click="$router.push('/admin/statistics')">
            <el-icon><DataAnalysis /></el-icon>
            <span>{{ isCollapsed ? '' : '数据统计' }}</span>
          </el-menu-item>
        </template>
        <!-- 用户菜单 -->
        <template v-else>
          <el-menu-item index="/user/topics" @click="$router.push('/user/topics')">
            <el-icon><Menu /></el-icon>
            <span>{{ isCollapsed ? '' : '选题列表' }}</span>
          </el-menu-item>
          <el-menu-item index="/user/selected-topic" @click="$router.push('/user/selected-topic')">
            <el-icon><DocumentChecked /></el-icon>
            <span>{{ isCollapsed ? '' : '已选课题' }}</span>
          </el-menu-item>
          <el-menu-item index="/user/progress" @click="$router.push('/user/progress')">
            <el-icon><EditPen /></el-icon>
            <span>{{ isCollapsed ? '' : '进度登记' }}</span>
          </el-menu-item>
        </template>
      </nav>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 顶部导航栏 -->
      <header class="top-header">
        <div class="header-left">
          <el-button type="text" @click="toggleCollapse" class="menu-btn">
            <el-icon><Menu /></el-icon>
          </el-button>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userInfo.realName }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区域 -->
      <div class="content-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  House, User, Document, Time, DataAnalysis, Menu, Close,
  DocumentChecked, EditPen, ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const isCollapsed = ref(false)

// 初始化时直接从localStorage加载用户信息
const loadUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    try {
      return JSON.parse(userInfoStr)
    } catch (error) {
      console.error('解析用户信息失败:', error)
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
      return {}
    }
  }
  return {}
}

const userInfo = ref(loadUserInfo())

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}

// 页面挂载时重新加载用户信息，确保数据最新
onMounted(() => {
  userInfo.value = loadUserInfo()
})
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏样式 */
.sidebar {
  width: 200px;
  background-color: #304156;
  color: #fff;
  transition: width 0.3s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background-color: #263445;
}

.sidebar-title {
  font-size: 18px;
  font-weight: bold;
  margin: 0;
  color: #fff;
  white-space: nowrap;
}

.collapse-btn {
  color: #fff;
  font-size: 18px;
}

.sidebar-menu {
  flex: 1;
  padding: 16px 0;
}

.sidebar-menu .el-menu-item {
  color: #fff;
  background-color: transparent;
  display: flex;
  align-items: center;
  height: 48px;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #409EFF;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #409EFF;
  color: #fff;
}

.sidebar-menu .el-icon {
  font-size: 18px;
  margin-right: 8px;
}

/* 主内容区样式 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航栏样式 */
.top-header {
  height: 60px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  z-index: 100;
}

.header-left .menu-btn {
  color: #333;
  font-size: 20px;
  margin-right: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #333;
}

.user-info .el-icon {
  margin-right: 8px;
}

/* 内容区域样式 */
.content-wrapper {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}
</style>
