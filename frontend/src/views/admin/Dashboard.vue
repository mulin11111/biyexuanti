<template>
  <div class="dashboard-container">
    <h2 class="page-title">欢迎使用毕业设计选题系统</h2>
    <div class="statistics-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-number">{{ statistics.userCount }}</div>
            <div class="stat-label">用户总数</div>
          </div>
          <div class="stat-icon user-icon"><User /></div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-number">{{ statistics.topicCount }}</div>
            <div class="stat-label">选题总数</div>
          </div>
          <div class="stat-icon topic-icon"><Document /></div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-number">{{ statistics.progressCount }}</div>
            <div class="stat-label">进度总数</div>
          </div>
          <div class="stat-icon progress-icon"><Time /></div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { User, Document, Time } from '@element-plus/icons-vue'

const statistics = ref({
  userCount: 0,
  topicCount: 0,
  progressCount: 0
})

// 获取统计数据
const getStatistics = async () => {
  try {
    const response = await axios.get('/admin/statistics')
    if (response.code === 200) {
      statistics.value = response.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  getStatistics()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 24px;
  color: #304156;
  font-size: 20px;
  font-weight: bold;
}

.statistics-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #304156;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.stat-icon {
  font-size: 48px;
  opacity: 0.8;
}

.user-icon {
  color: #409EFF;
}

.topic-icon {
  color: #67C23A;
}

.progress-icon {
  color: #E6A23C;
}
</style>
