<template>
  <div class="medicine-container">
    <div class="page-header">
      <h1 class="page-title">用药管理</h1>
      <p class="page-description">管理您的用药记录</p>
    </div>
    
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>添加用药记录</span>
        </div>
      </template>
      <el-form :model="medicineForm" label-width="120px" style="max-width: 800px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品名称" required>
              <el-input v-model="medicineForm.medicineName" placeholder="请输入药品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用法用量">
              <el-input v-model="medicineForm.dosage" placeholder="如：每次1片" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="服用频率">
              <el-input v-model="medicineForm.frequency" placeholder="如：每日3次" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="medicineForm.status" placeholder="选择状态">
                <el-option label="服用中" value="taking" />
                <el-option label="已完成" value="finished" />
                <el-option label="已停止" value="stopped" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-date-picker
                v-model="medicineForm.startDate"
                type="datetime"
                placeholder="选择开始日期"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker
                v-model="medicineForm.endDate"
                type="datetime"
                placeholder="选择结束日期"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="用药目的">
          <el-input
            v-model="medicineForm.purpose"
            type="textarea"
            :rows="3"
            placeholder="请输入用药目的"
          />
        </el-form-item>
        <el-form-item label="开药医生">
          <el-input v-model="medicineForm.doctorName" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="medicineForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveMedicine">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card style="margin-top: 20px">
      <template #header>
        <span>用药记录列表</span>
      </template>
      <el-table :data="medicineList" style="width: 100%">
        <el-table-column prop="medicineName" label="药品名称" />
        <el-table-column prop="dosage" label="用法用量" />
        <el-table-column prop="frequency" label="服用频率" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'taking'" type="success">服用中</el-tag>
            <el-tag v-else-if="row.status === 'finished'" type="info">已完成</el-tag>
            <el-tag v-else-if="row.status === 'stopped'" type="warning">已停止</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" />
        <el-table-column prop="endDate" label="结束日期" />
        <el-table-column prop="doctorName" label="医生" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { healthApi } from '@/api/health'
import { ElMessage } from 'element-plus'

const medicineList = ref([])
const medicineForm = reactive({
  medicineName: '',
  dosage: '',
  frequency: '',
  startDate: null,
  endDate: null,
  purpose: '',
  doctorName: '',
  status: 'taking',
  remark: ''
})

onMounted(() => {
  loadMedicineList()
})

const loadMedicineList = async () => {
  try {
    const res = await healthApi.getMedicineRecordList()
    medicineList.value = res.data || []
  } catch (error) {
    ElMessage.error('加载用药记录失败')
  }
}

const saveMedicine = async () => {
  if (!medicineForm.medicineName) {
    ElMessage.warning('请输入药品名称')
    return
  }
  
  try {
    await healthApi.saveMedicineRecord(medicineForm)
    ElMessage.success('保存成功')
    Object.assign(medicineForm, {
      medicineName: '',
      dosage: '',
      frequency: '',
      startDate: null,
      endDate: null,
      purpose: '',
      doctorName: '',
      status: 'taking',
      remark: ''
    })
    loadMedicineList()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}
</script>

<style scoped>
.medicine-container {
  width: 100%;
}
</style>
