<template>
  <h2>我的岗位（实时）</h2>
  <el-table :data="rows">
    <el-table-column prop="title" label="岗位" />
    <el-table-column prop="headcount" label="人数" />
    <el-table-column prop="status" label="状态" />
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { myJobs } from '../api'

const rows = ref([])

onMounted(async () => {
  const p = await myJobs(1)
  rows.value = p.records ?? p ?? []
})
</script>
