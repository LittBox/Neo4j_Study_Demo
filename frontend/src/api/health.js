import request from '@/utils/request'

export const healthApi = {
  // 获取健康档案
  getArchive() {
    return request({
      url: '/api/health/archive',
      method: 'get'
    })
  },
  
  // 更新健康档案
  updateArchive(data) {
    return request({
      url: '/api/health/archive',
      method: 'put',
      data
    })
  },
  
  // 保存健康数据
  saveHealthData(data) {
    return request({
      url: '/api/health/data',
      method: 'post',
      data
    })
  },
  
  // 获取健康数据列表
  getHealthDataList(dataType) {
    return request({
      url: '/api/health/data',
      method: 'get',
      params: { dataType }
    })
  },
  
  // 获取健康看板
  getDashboard() {
    return request({
      url: '/api/health/dashboard',
      method: 'get'
    })
  },
  
  // 保存用药记录
  saveMedicineRecord(data) {
    return request({
      url: '/api/health/medicine',
      method: 'post',
      data
    })
  },
  
  // 获取用药记录列表
  getMedicineRecordList() {
    return request({
      url: '/api/health/medicine',
      method: 'get'
    })
  }
}
