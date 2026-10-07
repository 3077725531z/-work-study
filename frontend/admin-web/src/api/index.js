import api from './request'

// A02 总览 / A11 统计
export const overview = () => api.get('/admin/overview')
export const stats = () => api.get('/admin/stats')
// A03 岗位审核
export const jobPage = (p) => api.get('/jobs', { params: p })
export const jobPublish = (id) => api.post(`/jobs/${id}:publish`)
export const jobReject = (id, reason) => api.post(`/jobs/${id}:reject`, null, { params: { reason } })
// A04 学生 / A05 抽查 / A06 认定
export const auditApply = (id, pass, comment) => api.post(`/applications/${id}:audit`, null, { params: { pass, comment } })
export const aidReview = (id, level) => api.post(`/aid/${id}:review`, null, { params: { level } })
// A08 工资
export const salaryConfirm = (id) => api.post(`/salaries/${id}:confirm`)
export const salaryPay = (id) => api.post(`/salaries/${id}:pay`)
// A10 公示
export const pubs = () => api.get('/publicities')
export const publish = (d) => api.post('/publicities', d)
