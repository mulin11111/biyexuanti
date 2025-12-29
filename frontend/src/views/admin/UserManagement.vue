<template>
  <div class="user-management-container">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <el-button type="primary" @click="dialogVisible = true">添加用户</el-button>
    </div>
    
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchQuery" placeholder="请输入用户名或真实姓名" clearable style="width: 300px; margin-right: 10px;">
        <template #append>
          <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>
    
    <!-- 用户列表 -->
    <el-table :data="userList" style="width: 100%" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="真实姓名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="role" label="角色" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          <el-button :type="scope.row.status === 1 ? 'warning' : 'success'" size="small" @click="handleStatusChange(scope.row)">
            {{ scope.row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!dialogType === 'edit'">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const userList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // add 或 edit
const dialogTitle = ref('添加用户')
const userFormRef = ref()
const userForm = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  email: '',
  role: 'user',
  status: 1
})

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 获取用户列表
const getUserList = async () => {
  try {
    const response = await axios.get('/admin/users', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    if (response.code === 200) {
      userList.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getUserList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getUserList()
}

// 当前页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  getUserList()
}

// 编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogTitle.value = '编辑用户'
  Object.assign(userForm, row)
  dialogVisible.value = true
}

// 删除用户
const handleDelete = async (row) => {
  try {
    await axios.delete(`/admin/users/${row.id}`)
    ElMessage.success('删除成功')
    getUserList()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 切换用户状态
const handleStatusChange = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await axios.put(`/admin/users/${row.id}`, {
      status: newStatus
    })
    ElMessage.success('状态更新成功')
    getUserList()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          // 添加用户
          await axios.post('/admin/users', userForm)
          ElMessage.success('添加成功')
        } else {
          // 编辑用户
          await axios.put(`/admin/users/${userForm.id}`, userForm)
          ElMessage.success('编辑成功')
        }
        dialogVisible.value = false
        getUserList()
        resetForm()
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '编辑失败')
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(userForm, {
    id: null,
    username: '',
    password: '',
    realName: '',
    email: '',
    role: 'user',
    status: 1
  })
  dialogType.value = 'add'
  dialogTitle.value = '添加用户'
}

onMounted(() => {
  getUserList()
})
</script>

<style scoped>
.user-management-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  color: #304156;
  font-size: 20px;
  font-weight: bold;
  margin: 0;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  text-align: right;
}
</style>
