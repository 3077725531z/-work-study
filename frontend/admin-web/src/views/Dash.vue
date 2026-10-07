<template>
  <h2>A02 总览（实时）</h2>
  <el-descriptions :column="2" border>
    <el-descriptions-item label="在招">{{ o.jobs }}</el-descriptions-item>
    <el-descriptions-item label="在岗">{{ o.onboard }}</el-descriptions-item>
    <el-descriptions-item label="待办">{{ o.todos }}</el-descriptions-item>
    <el-descriptions-item label="出勤率">{{ o.rate }}</el-descriptions-item>
  </el-descriptions>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { overview } from '../api'

const o = ref({})
onMounted(async () => { o.value = await overview() })
</script>
