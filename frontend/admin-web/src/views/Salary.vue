<template>
  <h2>A08 工资发放（实时）</h2>
  <el-table :data="rows">
    <el-table-column prop="studentId" label="学生ID" />
    <el-table-column prop="month" label="月份" />
    <el-table-column prop="net" label="应发" />
    <el-table-column prop="status" label="状态" />
    <el-table-column label="操作">
      <template #default="{ row }">
        <el-button size="small" type="primary" @click="cf(row)">复核</el-button>
        <el-button size="small" @click="pay(row)">发放</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/request'
import { salaryConfirm, salaryPay } from '../api'

const rows = ref([])

onMounted(async () => {
  const p = await api.get('/salaries/list', { params: { current: 1, size: 50 } }).catch(() => [])
  rows.value = p.records ?? p ?? []
})

async function cf(row) {
  await salaryConfirm(row.id)
  row.status = '已复核'
}

async function pay(row) {
  await salaryPay(row.id)
  row.status = '已发放'
}
</script>
