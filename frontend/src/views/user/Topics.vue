<template>
  <div class="topics-container">
    <h2 class="page-title">选题列表</h2>
    
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
      <el-table-column prop="description" label="课题描述" min-width="300">
        <template #default="scope">
          <el-popover
            placement="top"
            :width="600"
            trigger="hover"
            :content="scope.row.description || '暂无描述'"
          >
            <template #reference>
              <div class="description-preview">{{ scope.row.description || '暂无描述' }}</div>
            </template>
          </el-popover>
        </template>
      </el-table-column>
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
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small" 
            @click="handleSelectTopic(scope.row)"
            :disabled="scope.row.status === 0 || scope.row.currentStudents >= scope.row.maxStudents || selectedTopic !== null"
          >
            {{ isSelected(scope.row.id) ? '已选择' : '选择' }}
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
    
    <!-- 已选课题信息 -->
    <el-card v-if="selectedTopic" class="selected-topic-card">
      <template #header>
        <div class="card-header">
          <span>已选课题</span>
        </div>
      </template>
      <div class="selected-topic-info">
        <h3>{{ selectedTopic.title }}</h3>
        <p><strong>指导教师：</strong>{{ selectedTopic.teacherName }}</p>
        <p><strong>课题描述：</strong>{{ selectedTopic.description || '暂无描述' }}</p>
        <el-button type="danger" @click="handleCancelTopic">取消选择</el-button>
      </div>
    </el-card>
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
const selectedTopic = ref(null)

// 获取选题列表
const getTopicList = async () => {
  try {
    const response = await axios.get('/user/topics', {
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

// 获取已选课题
const getSelectedTopic = async () => {
  try {
    const response = await axios.get('/user/topics/selected')
    if (response.code === 200) {
      selectedTopic.value = response.data
    }
  } catch (error) {
    console.error('获取已选课题失败:', error)
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

// 检查是否已选择该课题
const isSelected = (topicId) => {
  return selectedTopic.value && selectedTopic.value.id === topicId
}

// 选择课题
const handleSelectTopic = async (topic) => {
  try {
    await axios.post(`/user/topics/${topic.id}/select`)
    ElMessage.success('选题成功')
    getSelectedTopic()
    getTopicList()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '选题失败')
  }
}

// 取消选择课题
const handleCancelTopic = async () => {
  try {
    await axios.post(`/user/topics/${selectedTopic.value.id}/cancel`)
    ElMessage.success('取消选题成功')
    selectedTopic.value = null
    getTopicList()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '取消选题失败')
  }
}

onMounted(() => {
  getTopicList()
  getSelectedTopic()
})
</script>

<style scoped>
.topics-container {
  padding: 20px;
}

.page-title {
  color: #304156;
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.description-preview {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.selected-topic-card {
  margin-top: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-topic-info {
  padding: 20px;
}

.selected-topic-info h3 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #304156;
}

.selected-topic-info p {
  margin: 10px 0;
  line-height: 1.5;
}
</style>
