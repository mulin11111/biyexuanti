<template>
  <div class="progress-container">
    <div class="page-header">
      <h2 class="page-title">进度管理</h2>
      <el-button type="primary" @click="dialogVisible = true">添加进度</el-button>
    </div>
    
    <!-- 进度列表 -->
    <el-table :data="progressList" style="width: 100%" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="progressStage" label="进度阶段" />
      <el-table-column prop="progressContent" label="进度内容" min-width="300">
        <template #default="scope">
          <el-popover
            placement="top"
            :width="600"
            trigger="hover"
            :content="scope.row.progressContent"
          >
            <template #reference>
              <div class="content-preview">{{ scope.row.progressContent }}</div>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="progressDate" label="提交时间" width="180" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="teacherComment" label="教师评语" min-width="200">
        <template #default="scope">
          <el-popover
            placement="top"
            :width="400"
            trigger="hover"
            :content="scope.row.teacherComment || '暂无评语'"
          >
            <template #reference>
              <div class="comment-preview">{{ scope.row.teacherComment || '暂无评语' }}</div>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small" 
            @click="handleEdit(scope.row)"
            :disabled="scope.row.status !== 'pending'"
          >
            编辑
          </el-button>
          <el-button 
            type="danger" 
            size="small" 
            @click="handleDelete(scope.row)"
            :disabled="scope.row.status !== 'pending'"
          >
            删除
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
    
    <!-- 添加/编辑进度对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="progressFormRef" :model="progressForm" :rules="progressRules" label-width="100px">
        <el-form-item label="进度阶段" prop="progressStage">
          <el-select v-model="progressForm.progressStage" placeholder="请选择进度阶段">
            <el-option label="开题报告" value="开题报告" />
            <el-option label="中期检查" value="中期检查" />
            <el-option label="结题验收" value="结题验收" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="进度内容" prop="progressContent">
          <el-input v-model="progressForm.progressContent" placeholder="请输入进度内容" type="textarea" :rows="5" />
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

const progressList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // add 或 edit
const dialogTitle = ref('添加进度')
const progressFormRef = ref()
const progressForm = reactive({
  id: null,
  progressStage: '',
  progressContent: ''
})

// 表单验证规则
const progressRules = {
  progressStage: [
    { required: true, message: '请选择进度阶段', trigger: 'change' }
  ],
  progressContent: [
    { required: true, message: '请输入进度内容', trigger: 'blur' },
    { min: 10, message: '进度内容至少10个字符', trigger: 'blur' }
  ]
}

// 获取进度列表
const getProgressList = async () => {
  try {
    const response = await axios.get('/user/progress')
    if (response.code === 200) {
      progressList.value = response.data
      total.value = response.data.length
    }
  } catch (error) {
    ElMessage.error('获取进度列表失败')
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getProgressList()
}

// 当前页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  getProgressList()
}

// 获取状态类型
const getStatusType = (status) => {
  switch (status) {
    case 'pending': return 'warning'
    case 'approved': return 'success'
    case 'rejected': return 'danger'
    default: return ''
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'pending': return '待审核'
    case 'approved': return '已通过'
    case 'rejected': return '已驳回'
    default: return ''
  }
}

// 编辑进度
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogTitle.value = '编辑进度'
  Object.assign(progressForm, {
    id: row.id,
    progressStage: row.progressStage,
    progressContent: row.progressContent
  })
  dialogVisible.value = true
}

// 删除进度
const handleDelete = async (row) => {
  try {
    await axios.delete(`/user/progress/${row.id}`)
    ElMessage.success('删除成功')
    getProgressList()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!progressFormRef.value) return
  
  await progressFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          // 添加进度
          await axios.post('/user/progress', progressForm)
          ElMessage.success('添加成功')
        } else {
          // 编辑进度
          await axios.put(`/user/progress/${progressForm.id}`, progressForm)
          ElMessage.success('编辑成功')
        }
        dialogVisible.value = false
        getProgressList()
        resetForm()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || (dialogType.value === 'add' ? '添加失败' : '编辑失败'))
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(progressForm, {
    id: null,
    progressStage: '',
    progressContent: ''
  })
  dialogType.value = 'add'
  dialogTitle.value = '添加进度'
}

onMounted(() => {
  getProgressList()
})
</script>

<style scoped>
.progress-container {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.content-preview {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.comment-preview {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px;
}

.dialog-footer {
  text-align: right;
}
</style>
