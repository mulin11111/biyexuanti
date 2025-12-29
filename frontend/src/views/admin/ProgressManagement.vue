<template>
  <div class="progress-management-container">
    <h2 class="page-title">进度管理</h2>
    
    <!-- 进度列表 -->
    <el-table :data="progressList" style="width: 100%" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="topicId" label="课题ID" width="100" />
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
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleAudit(scope.row)">审核</el-button>
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
    
    <!-- 审核对话框 -->
    <el-dialog v-model="auditDialogVisible" title="审核进度" width="500px">
      <el-form ref="auditFormRef" :model="auditForm" label-width="100px">
        <el-form-item label="进度阶段">
          <el-input v-model="auditForm.progressStage" disabled />
        </el-form-item>
        <el-form-item label="进度内容">
          <el-input v-model="auditForm.progressContent" type="textarea" :rows="4" disabled />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="auditForm.status" placeholder="请选择审核状态">
            <el-option label="通过" value="approved" />
            <el-option label="驳回" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="教师评语">
          <el-input v-model="auditForm.teacherComment" type="textarea" :rows="3" placeholder="请输入评语" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitAudit">确定</el-button>
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

// 审核对话框
const auditDialogVisible = ref(false)
const auditFormRef = ref()
const auditForm = reactive({
  id: null,
  progressStage: '',
  progressContent: '',
  status: 'approved',
  teacherComment: ''
})

// 获取进度列表
const getProgressList = async () => {
  try {
    const response = await axios.get('/admin/progress', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    if (response.code === 200) {
      progressList.value = response.data.records
      total.value = response.data.total
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

// 处理审核
const handleAudit = (row) => {
  Object.assign(auditForm, row)
  auditDialogVisible.value = true
}

// 提交审核
const handleSubmitAudit = async () => {
  try {
    await axios.put(`/admin/progress/${auditForm.id}`, {
      status: auditForm.status,
      teacherComment: auditForm.teacherComment
    })
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    getProgressList()
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

onMounted(() => {
  getProgressList()
})
</script>

<style scoped>
.progress-management-container {
  padding: 20px;
}

.page-title {
  color: #304156;
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
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
