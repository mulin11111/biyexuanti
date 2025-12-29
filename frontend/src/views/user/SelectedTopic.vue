<template>
  <div class="selected-topic-container">
    <el-card shadow="hover" class="topic-card">
      <template #header>
        <div class="card-header">
          <span>已选课题信息</span>
        </div>
      </template>
      
      <div v-if="selectedTopic" class="topic-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课题名称">{{ selectedTopic.title }}</el-descriptions-item>
          <el-descriptions-item label="指导教师">{{ selectedTopic.teacher }}</el-descriptions-item>
          <el-descriptions-item label="课题类型">{{ selectedTopic.type }}</el-descriptions-item>
          <el-descriptions-item label="课题状态">{{ selectedTopic.status }}</el-descriptions-item>
          <el-descriptions-item label="最大人数" :span="2">{{ selectedTopic.maxStudents }}</el-descriptions-item>
          <el-descriptions-item label="课题描述" :span="2">{{ selectedTopic.description }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="action-buttons">
          <el-button type="danger" @click="cancelSelection">取消选题</el-button>
        </div>
      </div>
      
      <div v-else class="no-topic">
        <el-empty description="您尚未选择课题" />
        <el-button type="primary" @click="$router.push('/user/topics')" style="margin-top: 20px;">
          去选题
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from 'axios'

export default {
  name: 'SelectedTopic',
  setup() {
    const selectedTopic = ref(null)
    
    // 获取已选课题
    const fetchSelectedTopic = async () => {
      try {
        const response = await axios.get('/api/topics/selected')
        selectedTopic.value = response.data.data
      } catch (error) {
        console.error('获取已选课题失败:', error)
        if (error.response?.status === 404) {
          selectedTopic.value = null
        } else {
          ElMessage.error('获取已选课题失败')
        }
      }
    }
    
    // 取消选题
    const cancelSelection = async () => {
      try {
        await axios.delete('/api/topics/selected')
        ElMessage.success('取消选题成功')
        selectedTopic.value = null
      } catch (error) {
        console.error('取消选题失败:', error)
        ElMessage.error('取消选题失败')
      }
    }
    
    onMounted(() => {
      fetchSelectedTopic()
    })
    
    return {
      selectedTopic,
      cancelSelection
    }
  }
}
</script>

<style scoped>
.selected-topic-container {
  padding: 20px;
}

.topic-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.topic-info {
  margin-top: 20px;
}

.action-buttons {
  margin-top: 30px;
  text-align: right;
}

.no-topic {
  text-align: center;
  padding: 60px 20px;
}
</style>