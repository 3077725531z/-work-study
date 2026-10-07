import api from './request'

// E03 发布 / E04 我的岗位
export const pubJob = (d) => api.post('/jobs', d)
export const myJobs = (deptId) => api.get('/jobs', { params: { deptId, size: 50 } })
// E05 审核：冲突行后端已标红，前端禁用通过按钮
export const audit = (id, pass, comment) => api.post(`/applications/${id}:audit`, null, { params: { pass, comment } })
// E06/E07/E08 考勤请假工时
export const confirmAttend = (id) => api.post('/attendance/confirm', null, { params: { id } })
export const confirmSalary = (id) => api.post(`/salaries/${id}:confirm`)
// E09 评价
export const review = (d) => api.post('/reviews', d)
