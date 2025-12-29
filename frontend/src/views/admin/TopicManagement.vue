<template>
  <div class="topic-management-container">
    <div class="page-header">
      <h2 class="page-title">选题管理</h2>
      <el-button type="primary" @click="dialogVisible = true">添加选题</el-button>
    </div>
    
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchQuery" placeholder="请输入课题名称或指导教师" clearable style="width: 300px; margin-right: 10px;">
        <template #append>
          <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>
    
    <!-- 选题列表 -->
    <el-table :data="topicList" style="width: 100%" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="课题名称" min-width="200" />
      <el-table-column prop="teacherName" label="指导教师" />
      <el-table-column prop="maxStudents" label="最大人数" width="100" />
      <el-table-column prop="currentStudents" label="已选人数" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '可选题' : '不可选题' }}
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
    
    <!-- 添加/编辑选题对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="topicFormRef" :model="topicForm" :rules="topicRules" label-width="100px">
        <el-form-item label="课题名称" prop="title">
          <el-input v-model="topicForm.title" placeholder="请输入课题名称" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="课题描述" prop="description">
          <el-input v-model="topicForm.description" placeholder="请输入课题描述" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="指导教师" prop="teacherName">
          <el-input v-model="topicForm.teacherName" placeholder="请输入指导教师" />
        </el-form-item>
        <el-form-item label="最大人数" prop="maxStudents">
          <el-input-number v-model="topicForm.maxStudents" :min="1" :max="20" placeholder="请输入最大可选学生数" />
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

const topicList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // add 或 edit
const dialogTitle = ref('添加选题')
const topicFormRef = ref()
const topicForm = reactive({
  id: null,
  title: '',
  description: '',
  teacherName: '',
  maxStudents: 1,
  currentStudents: 0,
  status: 1
})

// 表单验证规则
const topicRules = {
  title: [
    { required: true, message: '请输入课题名称', trigger: 'blur' }
  ],
  teacherName: [
    { required: true, message: '请输入指导教师', trigger: 'blur' }
  ],
  maxStudents: [
    { required: true, message: '请输入最大人数', trigger: 'blur' },
    { type: 'number', min: 1, message: '最大人数至少为1', trigger: 'blur' }
  ]
}

// 获取选题列表
const getTopicList = async () => {
  try {
    const response = await axios.get('/admin/topics', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        keyword: searchQuery.value
      }
    })
    if (response.code === 200) {
      topicList.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取选题列表失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getTopicList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getTopicList()
}

// 当前页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  getTopicList()
}

// 编辑选题
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogTitle.value = '编辑选题'
  Object.assign(topicForm, row)
  dialogVisible.value = true
}

// 删除选题
const handleDelete = async (row) => {
  try {
    await axios.delete(`/admin/topics/${row.id}`)
    ElMessage.success('删除成功')
    getTopicList()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 切换选题状态
const handleStatusChange = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await axios.put(`/admin/topics/${row.id}`, {
      id: row.id,
      status: newStatus
    })
    ElMessage.success('状态更新成功')
    getTopicList()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!topicFormRef.value) return
  
  await topicFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          // 添加选题
          await axios.post('/admin/topics', topicForm)
          ElMessage.success('添加成功')
        } else {
          // 编辑选题
          await axios.put(`/admin/topics/${topicForm.id}`, topicForm)
          ElMessage.success('编辑成功')
        }
        dialogVisible.value = false
        getTopicList()
        resetForm()
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '编辑失败')
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(topicForm, {
    id: null,
    title: '',
    description: '',
    teacherName: '',
    maxStudents: 1,
    currentStudents: 0,
    status: 1
  })
  dialogType.value = 'add'
  dialogTitle.value = '添加选题'
}

onMounted(() => {
  getTopicList()
})
</script>

<style scoped>
.topic-management-container {
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
