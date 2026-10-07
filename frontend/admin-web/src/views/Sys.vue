<template>
  <h2>A12 系统设置（实时）</h2>
  <el-table :data="rows">
    <el-table-column prop="ckey" label="键" />
    <el-table-column prop="cval" label="值" />
    <el-table-column label="操作">
      <template #default="{ row }">
        <el-button size="small" @click="edit(row)">修改</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/request'

const rows = ref([])

onMounted(async () => {
  rows.value = await api.get('/system/config').catch(() => [])
})

async function edit(row) {
  const v = prompt(`修改 ${row.ckey}`, row.cval)
  if (v == null) return
  await api.post('/system/config', { ckey: row.ckey, cval: v })
  row.cval = v
}
</script>
