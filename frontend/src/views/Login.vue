<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <h2 class="login-title">毕业设计选题系统</h2>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  // 表单验证
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 发送登录请求
        const response = await axios.post('/auth/login', loginForm)
        if (response.code === 200) {
          // 保存token和用户信息到本地存储
          const loginData = response.data
          localStorage.setItem('token', loginData.token)
          localStorage.setItem('userInfo', JSON.stringify({
            id: loginData.id,
            username: loginData.username,
            realName: loginData.realName,
            email: loginData.email,
            role: loginData.role
          }))
          
          ElMessage.success('登录成功')
          
          // 根据角色跳转到对应首页
          if (loginData.role === 'admin') {
            router.push('/dashboard')
          } else {
            router.push('/user/topics')
          }
        } else {
          ElMessage.error(response.message || '登录失败')
        }
      } catch (error) {
        ElMessage.error(error.response?.message || '登录失败，请重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.login-form-wrapper {
  width: 400px;
  padding: 32px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 24px;
  color: #304156;
  font-size: 24px;
  font-weight: bold;
}

.login-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
}
</style>
