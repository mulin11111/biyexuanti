<template>
  <div class="statistics-container">
    <h2 class="page-title">数据统计</h2>
    
    <div class="statistics-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-number">{{ statistics.userCount }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-number">{{ statistics.topicCount }}</div>
            <div class="stat-label">选题总数</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-number">{{ statistics.progressCount }}</div>
            <div class="stat-label">进度总数</div>
          </div>
        </div>
      </el-card>
    </div>
    
    <div class="charts-container">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>选题状态分布</span>
          </div>
        </template>
        <div id="topicStatusChart" class="chart" ref="topicStatusChart"></div>
      </el-card>
      
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>进度状态分布</span>
          </div>
        </template>
        <div id="progressStatusChart" class="chart" ref="progressStatusChart"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

const statistics = ref({
  userCount: 0,
  topicCount: 0,
  progressCount: 0
})

const topicStatusChart = ref(null)
const progressStatusChart = ref(null)
let topicStatusChartInstance = null
let progressStatusChartInstance = null

// 获取统计数据
const getStatistics = async () => {
  try {
    const response = await axios.get('/admin/statistics')
    if (response.code === 200) {
      statistics.value = response.data
      initCharts()
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 初始化图表
const initCharts = () => {
  // 选题状态分布图表
  if (topicStatusChart.value) {
    if (topicStatusChartInstance) {
      topicStatusChartInstance.dispose()
    }
    topicStatusChartInstance = echarts.init(topicStatusChart.value)
    const topicOption = {
      title: {
        text: '选题状态分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '选题状态',
          type: 'pie',
          radius: '50%',
          data: [
            { value: 12, name: '可选题' },
            { value: 3, name: '不可选题' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    topicStatusChartInstance.setOption(topicOption)
  }

  // 进度状态分布图表
  if (progressStatusChart.value) {
    if (progressStatusChartInstance) {
      progressStatusChartInstance.dispose()
    }
    progressStatusChartInstance = echarts.init(progressStatusChart.value)
    const progressOption = {
      title: {
        text: '进度状态分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '进度状态',
          type: 'pie',
          radius: '50%',
          data: [
            { value: 8, name: '待审核' },
            { value: 15, name: '已通过' },
            { value: 2, name: '已驳回' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    progressStatusChartInstance.setOption(progressOption)
  }
}

// 监听窗口大小变化，调整图表大小
const handleResize = () => {
  if (topicStatusChartInstance) {
    topicStatusChartInstance.resize()
  }
  if (progressStatusChartInstance) {
    progressStatusChartInstance.resize()
  }
}

onMounted(() => {
  getStatistics()
  window.addEventListener('resize', handleResize)
})

// 组件销毁时移除事件监听
const cleanup = () => {
  window.removeEventListener('resize', handleResize)
  if (topicStatusChartInstance) {
    topicStatusChartInstance.dispose()
  }
  if (progressStatusChartInstance) {
    progressStatusChartInstance.dispose()
  }
}

// 监听组件销毁
import { onBeforeUnmount } from 'vue'
onBeforeUnmount(() => {
  cleanup()
})
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}

.page-title {
  color: #304156;
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
}

.statistics-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
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

.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart {
  width: 100%;
  height: 300px;
}
</style>
