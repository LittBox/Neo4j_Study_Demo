import request from '@/utils/request'

export const aiApi = {
  // AI问答
  chat(data) {
    return request({
      url: '/api/ai/chat',
      method: 'post',
      data
    })
  },
  
  // 获取问答历史
  getHistory() {
    return request({
      url: '/api/ai/history',
      method: 'get'
    })
  }
}
