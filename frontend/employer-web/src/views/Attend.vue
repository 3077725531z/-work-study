<template>
  <h2>考勤确认（实时）</h2>
  <el-table :data="rows">
    <el-table-column prop="studentId" label="学生ID" />
    <el-table-column prop="workDate" label="日期" />
    <el-table-column prop="status" label="状态" />
    <el-table-column label="操作">
      <template #default="{ row }">
        <el-button size="small" type="primary" @click="cf(row)">确认</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/request'
import { confirmAttend } from '../api'

const rows = ref([])

onMounted(async () => {
  const p = await api.get('/attendance/list', { params: { current: 1, size: 50 } })
  rows.value = p.records ?? p ?? []
})

async function cf(row) {
  await confirmAttend(row.id)
  row.confirmed = 1
}
</script>
