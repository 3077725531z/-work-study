<template>
  <h2>A09 申诉工单（实时）</h2>
  <el-table :data="rows">
    <el-table-column prop="code" label="单号" />
    <el-table-column prop="type" label="类型" />
    <el-table-column prop="status" label="状态" />
    <el-table-column label="操作">
      <template #default="{ row }">
        <el-button size="small" type="primary" @click="done(row)">受理通过</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/request'

const rows = ref([])

onMounted(async () => {
  const p = await api.get('/appeals/list', { params: { current: 1, size: 50 } }).catch(() => [])
  rows.value = p.records ?? p ?? []
})

async function done(row) {
  await api.post(`/appeals/${row.id}:reply`, null, { params: { reply: '已处理' } })
  row.status = '已处理'
}
</script>
