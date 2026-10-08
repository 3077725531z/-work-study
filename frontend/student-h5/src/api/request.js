import axios from 'axios'
import { showToast } from 'vant'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

function toError(data) {
  const err = new Error(data?.msg || '请求失败')
  err.code = data?.code
  err.payload = data?.data
  return err
}

api.interceptors.response.use(
  (res) => {
    const data = res.data
    if (data && typeof data.code !== 'undefined' && data.code !== 200) {
      if (data.code === 401) {
        localStorage.clear()
        window.location.href = '/login'
      }
      return Promise.reject(toError(data))
    }
    return data?.data ?? data
  },
  (err) => {
    if (!err.response) {
      showToast('网络异常，请检查后端是否启动')
    } else if (err.response.status === 401) {
      localStorage.clear()
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export default api
