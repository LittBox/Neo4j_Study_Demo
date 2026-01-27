import request from '@/utils/request'

export const graphApi = {
  // 获取疾病知识图谱
  getDiseaseGraph(diseaseName, depth = 2) {
    return request({
      url: '/api/graph/disease',
      method: 'get',
      params: { diseaseName, depth }
    })
  },
  
  // 搜索疾病
  searchDiseases(keyword) {
    return request({
      url: '/api/graph/search',
      method: 'get',
      params: { keyword }
    })
  },
  
  // 获取所有疾病列表
  getAllDiseases() {
    return request({
      url: '/api/graph/diseases',
      method: 'get'
    })
  }
}
