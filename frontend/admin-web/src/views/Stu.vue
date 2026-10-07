<template>
  <h2>A04 学生管理（实时）</h2>
  <el-input v-model="key" placeholder="搜学号/姓名" style="width:260px" @input="load" />
  <el-table :data="filtered" style="margin-top:12px">
    <el-table-column prop="sno" label="学号" />
    <el-table-column prop="name" label="姓名" />
    <el-table-column prop="college" label="学院" />
    <el-table-column prop="status" label="状态" />
  </el-table>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api/request'

const rows = ref([])
const key = ref('')
const filtered = computed(() =>
  rows.value.filter((u) => !key.value || u.sno?.includes(key.value) || u.name?.includes(key.value)))

async function load() {
  rows.value = await api.get('/users', { params: { size: 100 } }).catch(() => [])
  if (!Array.isArray(rows.value)) rows.value = rows.value.records ?? []
}

onMounted(load)
</script>
