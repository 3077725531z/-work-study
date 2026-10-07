<template>
  <h2>A07 考勤监控（实时）</h2>
  <el-table :data="rows">
    <el-table-column prop="studentId" label="学生ID" />
    <el-table-column prop="workDate" label="日期" />
    <el-table-column prop="status" label="状态" />
    <el-table-column prop="confirmed" label="确认" />
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/request'

const rows = ref([])

onMounted(async () => {
  const p = await api.get('/attendance/list', { params: { current: 1, size: 50 } }).catch(() => [])
  rows.value = p.records ?? p ?? []
})
</script>
