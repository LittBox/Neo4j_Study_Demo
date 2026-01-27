import request from '@/utils/request'

export const authApi = {
  // 登录
  login(data) {
    return request({
      url: '/api/auth/login',
      method: 'post',
      data
    })
  },
  
  // 注册
  register(data) {
    return request({
      url: '/api/auth/register',
      method: 'post',
      data
    })
  }
}
