<template>
  <div class="graph-container">
    <div class="page-header">
      <h1 class="page-title">心脏疾病知识图谱</h1>
      <p class="page-description">可视化查看疾病、症状、治疗方案等关联关系</p>
    </div>
    
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>知识图谱可视化</span>
          <div>
            <el-select v-model="selectedDisease" placeholder="选择疾病" style="width: 200px; margin-right: 10px">
              <el-option
                v-for="disease in diseases"
                :key="disease.id"
                :label="disease.name"
                :value="disease.name"
              />
            </el-select>
            <el-button type="primary" @click="loadGraph" :loading="loading">加载图谱</el-button>
          </div>
        </div>
      </template>
      <div id="graphChart" class="health-graph-container"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { graphApi } from '@/api/graph'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const diseases = ref([])
const selectedDisease = ref('')
let chartInstance = null
let fullGraphData = null

onMounted(() => {
  loadDiseases()
  loadFullGraph()
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
})

const loadDiseases = async () => {
  try {
    const res = await graphApi.getAllDiseases()
    diseases.value = res.data || []
    if (diseases.value.length > 0) {
      selectedDisease.value = diseases.value[0].name
    } else {
      ElMessage.warning('暂无疾病数据')
    }
  } catch (error) {
    ElMessage.error('加载疾病列表失败：' + (error.message || '未知错误'))
  }
}

const loadGraph = async () => {
  if (!selectedDisease.value) {
    ElMessage.warning('请选择疾病')
    return
  }
  
  loading.value = true
  try {
    const res = await graphApi.getDiseaseGraph(selectedDisease.value, 4)
    const graphData = res.data
    
    if (!graphData || (!graphData.nodes || graphData.nodes.length === 0)) {
      ElMessage.warning('该疾病暂无知识图谱数据')
      return
    }
    
    await nextTick()
    initGraph(graphData)
  } catch (error) {
    ElMessage.error('加载图谱失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const loadFullGraph = async () => {
  loading.value = true
  try {
    const res = await graphApi.getFullGraph(2, 300)
    fullGraphData = res.data
    if (fullGraphData && fullGraphData.nodes && fullGraphData.nodes.length) {
      await nextTick()
      initGraph(fullGraphData)
    }
  } catch (error) {
    // 静默失败，不影响后续按疾病加载
  } finally {
    loading.value = false
  }
}

const initGraph = (graphData) => {
  const chartDom = document.getElementById('graphChart')
  if (!chartDom) {
    setTimeout(() => initGraph(graphData), 100)
    return
  }
  
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = echarts.init(chartDom)
  
  const categories = [
    { name: 'disease', itemStyle: { color: '#1F9F84' } },
    { name: 'symptom', itemStyle: { color: '#FD5C23' } },
    { name: 'riskFactor', itemStyle: { color: '#FFC107' } },
    { name: 'treatment', itemStyle: { color: '#2196F3' } },
    { name: 'drug', itemStyle: { color: '#9C27B0' } }
  ]
  
  const option = {
    tooltip: {
      formatter: function(params) {
        if (params.dataType === 'node') {
          return params.data.name
        } else {
          return params.data.source + ' -> ' + params.data.target
        }
      }
    },
    legend: {
      data: categories.map(c => c.name),
      top: 10
    },
    series: [
      {
        type: 'graph',
        layout: 'force',
        data: graphData.nodes || [],
        links: graphData.links || [],
        categories: categories,
        roam: true,
        label: {
          show: true,
          position: 'right',
          formatter: '{b}'
        },
        labelLayout: {
          hideOverlap: true
        },
        scaleLimit: {
          min: 0.4,
          max: 2
        },
        lineStyle: {
          color: 'source',
          curveness: 0.3
        },
        edgeLabel: {
          show: true,
          formatter: (params) => params?.data?.name || '',
          color: '#666',
          fontSize: 12
        },
        emphasis: {
          focus: 'adjacency',
          lineStyle: {
            width: 10
          }
        },
        force: {
          repulsion: 1000,
          gravity: 0.1,
          edgeLength: 200,
          layoutAnimation: true
        }
      }
    ]
  }
  
  chartInstance.setOption(option)
  
  window.addEventListener('resize', () => {
    if (chartInstance) {
      chartInstance.resize()
    }
  })
}
</script>

<style scoped>
.graph-container {
  width: 100%;
}
</style>
