import axios from 'axios'

const api = axios.create({ baseURL: '/api', timeout: 10000 })

api.interceptors.request.use((c) => {
  const t = localStorage.getItem('admin-token')
  if (t) c.headers.Authorization = `Bearer ${t}`
  return c
})

api.interceptors.response.use(
  (res) => {
    const d = res.data
    if (d && d.code !== undefined && d.code !== 200) {
      return Promise.reject(new Error(d.msg || '请求失败'))
    }
    return d.data ?? d
  },
  (err) => {
    const msg = err.response?.data?.msg || err.message
    return Promise.reject(new Error(msg))
  }
)

export default api
