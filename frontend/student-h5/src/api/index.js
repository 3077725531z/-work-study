import api from './request'

export const uid = () => {
  const v = Number(localStorage.getItem('uid'))
  return Number.isFinite(v) && v > 0 ? v : null
}
const SEMESTER = '2026-秋'

// P01/P02
export const login = (sno, password) => api.post('/auth/login', { sno, password })
export const register = (data) => api.post('/auth/register', data)

// P03/P04
export const jobPage = (params) => api.get('/jobs', { params })
export const jobOne = (id) => api.get(`/jobs/${id}`)

// P05/P06
export const submitApply = (data) => api.post('/applications', { ...data, studentId: uid() })
export const myApplies = () => api.get('/applications')
export const withdrawApply = (id) => api.post(`/applications/${id}:audit`, null, { params: { pass: false, comment: '学生撤回' } })

// P15
export const myTimetable = (semester = SEMESTER) => api.get('/timetable', { params: { semester } })
export const saveTimetableOne = (data) => api.post('/timetable', data)
export const syncTimetable = () => api.post('/timetable/sync')

// P07/P08
export const clock = (data) => api.post('/attendance/clock', { ...data, studentId: uid() })
export const myAttendance = () => api.get('/attendance/list', { params: { current: 1, size: 60 } })
export const leave = (data) => api.post('/leaves', { ...data, studentId: uid() })

// P09/P10
export const calcSalary = (data) => api.post('/salaries/calc', data)
export const mySalaries = () => api.get('/salaries/list', { params: { current: 1, size: 24 } })
export const appeal = (data) => api.post('/appeals', { ...data, studentId: uid() })

// P11
export const aidApply = (data) => api.post('/aid', { ...data, studentId: uid() })
export const myAid = () => api.get('/aid/list', { params: { current: 1, size: 10 } })

// P12
export const myReviews = () => api.get('/reviews/mine').catch(() => [])

// P13/P14
export const pubList = () => api.get('/publicities')
export const pubRead = (id) => api.post(`/publicities/${id}:read`)
