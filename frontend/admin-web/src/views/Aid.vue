<template>
  <h2>A06 认定复审（实时）</h2>
  <el-table :data="rows">
    <el-table-column prop="studentId" label="学生ID" />
    <el-table-column prop="level" label="申请档" />
    <el-table-column prop="status" label="状态" />
    <el-table-column label="操作">
      <template #default="{ row }">
        <el-button size="small" type="primary" @click="ok(row)">定为二档</el-button>
        <el-button size="small" @click="down(row)">降档/驳回</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/request'
import { aidReview } from '../api'

const rows = ref([])

onMounted(async () => {
  rows.value = await api.get('/aid/list').catch(() => [])
  if (!Array.isArray(rows.value)) rows.value = rows.value.records ?? []
})

async function ok(row) {
  await aidReview(row.id, '二档')
  row.status = '已认定'
}

async function down(row) {
  await aidReview(row.id, '三档')
  row.status = '已认定'
}
</script>
