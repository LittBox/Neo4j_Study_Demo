<template>
  <div class="data-track-container">
    <div class="page-header">
      <h1 class="page-title">健康数据追踪</h1>
      <p class="page-description">记录和查看您的日常健康数据</p>
    </div>
    
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>添加健康数据</span>
        </div>
      </template>
      <el-form :model="dataForm" label-width="120px" :inline="true">
        <el-form-item label="数据类型">
          <el-select v-model="dataForm.dataType" placeholder="选择数据类型" style="width: 150px" clearable>
            <el-option label="血压" value="blood_pressure" />
            <el-option label="心率" value="heart_rate" />
            <el-option label="血糖" value="blood_sugar" />
            <el-option label="体重" value="weight" />
            <el-option label="体温" value="temperature" />
          </el-select>
        </el-form-item>
        <el-form-item label="值1">
          <el-input-number v-model="dataForm.value1" :precision="2" style="width: 150px" />
        </el-form-item>
        <el-form-item label="值2" v-if="dataForm.dataType === 'blood_pressure'">
          <el-input-number v-model="dataForm.value2" :precision="2" style="width: 150px" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="dataForm.unit" placeholder="如：mmHg" style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveData">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card style="margin-top: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>数据列表</span>
          <el-select v-model="filterType" placeholder="筛选类型" style="width: 150px" @change="loadData">
            <el-option label="全部" value="" />
            <el-option label="血压" value="blood_pressure" />
            <el-option label="心率" value="heart_rate" />
            <el-option label="血糖" value="blood_sugar" />
            <el-option label="体重" value="weight" />
            <el-option label="体温" value="temperature" />
          </el-select>
        </div>
      </template>
      <el-table :data="dataList" style="width: 100%">
        <el-table-column prop="dataType" label="数据类型" width="120">
          <template #default="{ row }">
            <span>{{ getDataTypeLabel(row.dataType) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="value1" label="值1" />
        <el-table-column prop="value2" label="值2" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="recordTime" label="记录时间" width="180">
          <template #default="{ row }">
            <span>{{ formatTime(row.recordTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deviceType" label="设备类型" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { healthApi } from '@/api/health'
import { ElMessage } from 'element-plus'

const filterType = ref('')
const dataList = ref([])
const dataForm = reactive({
  dataType: '',
  value1: null,
  value2: null,
  unit: '',
  recordTime: new Date()
})

onMounted(() => {
  loadData()
})

const loadData = async () => {
  try {
    const res = await healthApi.getHealthDataList(filterType.value || null)
    dataList.value = res.data || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const saveData = async () => {
  if (!dataForm.dataType) {
    ElMessage.warning('请选择数据类型')
    return
  }
  if (dataForm.value1 === null) {
    ElMessage.warning('请输入值1')
    return
  }
  
  try {
    const data = {
      dataType: dataForm.dataType,
      value1: dataForm.value1,
      value2: dataForm.dataType === 'blood_pressure' ? dataForm.value2 : null,
      unit: dataForm.unit || '',
      recordTime: null
    }
    await healthApi.saveHealthData(data)
    ElMessage.success('保存成功')
    Object.assign(dataForm, {
      dataType: '',
      value1: null,
      value2: null,
      unit: '',
      recordTime: new Date()
    })
    loadData()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const getDataTypeLabel = (type) => {
  const typeMap = {
    'blood_pressure': '血压',
    'heart_rate': '心率',
    'blood_sugar': '血糖',
    'weight': '体重',
    'temperature': '体温'
  }
  return typeMap[type] || type
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}
</script>

<style scoped>
.data-track-container {
  width: 100%;
}
</style>
