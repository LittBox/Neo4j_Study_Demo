<template>
  <div class="home-container">
    <div class="page-header">
      <h1 class="page-title">健康数据总览</h1>
      <p class="page-description">查看您的健康数据统计和趋势</p>
    </div>
    
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="6" v-for="stat in stats" :key="stat.label">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近健康数据</span>
          </template>
          <el-table :data="recentData" style="width: 100%">
            <el-table-column prop="dataType" label="数据类型" width="120" />
            <el-table-column prop="value1" label="值1" />
            <el-table-column prop="value2" label="值2" />
            <el-table-column prop="unit" label="单位" />
            <el-table-column prop="recordTime" label="记录时间" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>数据统计</span>
          </template>
          <div id="dataChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { healthApi } from '@/api/health'
import * as echarts from 'echarts'

const loading = ref(false)
const stats = ref([
  { label: '总记录数', value: 0 },
  { label: '本月记录', value: 0 },
  { label: '用药记录', value: 0 },
  { label: '健康评分', value: '良好' }
])
const recentData = ref([])
let chartInstance = null

onMounted(() => {
  loadDashboard()
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
})

const loadDashboard = async () => {
  loading.value = true
  try {
    const res = await healthApi.getDashboard()
    const data = res.data
    
    if (data.archive) {
      stats.value[0].value = data.recentData?.length || 0
    }
    
    recentData.value = data.recentData || []
    
    // 初始化图表
    initChart(data.dataTypeCount || {})
  } catch (error) {
    console.error('加载看板数据失败', error)
  } finally {
    loading.value = false
  }
}

const initChart = (dataTypeCount) => {
  const chartDom = document.getElementById('dataChart')
  if (!chartDom) return
  
  chartInstance = echarts.init(chartDom)
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    color: ['#1F9F84', '#FD5C23'],
    series: [
      {
        type: 'pie',
        radius: '60%',
        data: Object.entries(dataTypeCount).map(([name, value]) => ({
          value,
          name
        })),
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
  
  chartInstance.setOption(option)
}
</script>

<style scoped>
.home-container {
  width: 100%;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 20px 0;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: var(--primary-color);
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: var(--text-light);
}
</style>
