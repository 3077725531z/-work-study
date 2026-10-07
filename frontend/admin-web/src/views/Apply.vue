<template>
  <h2>A05 申请抽查（实时）</h2>
  <el-table :data="rows">
    <el-table-column prop="code" label="编号" />
    <el-table-column prop="status" label="状态" />
    <el-table-column label="操作">
      <template #default="{ row }">
        <el-button size="small" type="primary" @click="pass(row)">确认</el-button>
        <el-button size="small" @click="back(row)">打回重审</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/request'
import { auditApply } from '../api'

const rows = ref([])

onMounted(async () => {
  rows.value = await api.get('/applies', { params: { current: 1, size: 50 } }).catch(() => [])
  if (!Array.isArray(rows.value)) rows.value = rows.value.records ?? []
})

async function pass(row) {
  await auditApply(row.id, true, '抽查确认')
  row.status = '已通过'
}

async function back(row) {
  await auditApply(row.id, false, '抽查打回重审')
  row.status = '未通过'
}
</script>
