import axios from 'axios'
const api = axios.create({ baseURL: '/api', timeout: 10000 })
api.interceptors.request.use((c) => {
  const t = localStorage.getItem('emp-token')
  if (t) c.headers.Authorization = `Bearer ${t}`
  return c
})
api.interceptors.response.use((res) => res.data.data ?? res.data)
export default api
