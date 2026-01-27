<template>
  <div class="archive-container">
    <div class="page-header">
      <h1 class="page-title">健康档案</h1>
      <p class="page-description">管理您的个人健康档案信息</p>
    </div>
    
    <el-card v-loading="loading">
      <el-form :model="archiveForm" label-width="120px" style="max-width: 600px">
        <el-form-item label="出生日期">
          <el-date-picker
            v-model="archiveForm.birthDate"
            type="date"
            placeholder="选择出生日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="archiveForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身高(cm)">
          <el-input-number v-model="archiveForm.height" :min="0" :max="300" />
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="archiveForm.weight" :min="0" :max="500" />
        </el-form-item>
        <el-form-item label="血型">
          <el-select v-model="archiveForm.bloodType" placeholder="请选择血型">
            <el-option label="A型" value="A" />
            <el-option label="B型" value="B" />
            <el-option label="AB型" value="AB" />
            <el-option label="O型" value="O" />
          </el-select>
        </el-form-item>
        <el-form-item label="既往病史">
          <el-input
            v-model="archiveForm.medicalHistory"
            type="textarea"
            :rows="4"
            placeholder="请输入既往病史"
          />
        </el-form-item>
        <el-form-item label="过敏史">
          <el-input
            v-model="archiveForm.allergyHistory"
            type="textarea"
            :rows="4"
            placeholder="请输入过敏史"
          />
        </el-form-item>
        <el-form-item label="家族病史">
          <el-input
            v-model="archiveForm.familyHistory"
            type="textarea"
            :rows="4"
            placeholder="请输入家族病史"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveArchive" :loading="saving">
            保存
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { healthApi } from '@/api/health'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const archiveForm = reactive({
  birthDate: null,
  gender: '',
  height: null,
  weight: null,
  bloodType: '',
  medicalHistory: '',
  allergyHistory: '',
  familyHistory: ''
})

onMounted(() => {
  loadArchive()
})

const loadArchive = async () => {
  loading.value = true
  try {
    const res = await healthApi.getArchive()
    const data = res.data
    if (data) {
      Object.assign(archiveForm, {
        birthDate: data.birthDate,
        gender: data.gender || '',
        height: data.height,
        weight: data.weight,
        bloodType: data.bloodType || '',
        medicalHistory: data.medicalHistory || '',
        allergyHistory: data.allergyHistory || '',
        familyHistory: data.familyHistory || ''
      })
    }
  } catch (error) {
    ElMessage.error('加载健康档案失败')
  } finally {
    loading.value = false
  }
}

const saveArchive = async () => {
  saving.value = true
  try {
    await healthApi.updateArchive(archiveForm)
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.archive-container {
  width: 100%;
}
</style>
