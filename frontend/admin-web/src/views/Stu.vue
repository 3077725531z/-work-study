<template>
  <h2 class="page-title">学生管理</h2>
  <div class="hk-card">
    <div class="toolbar">
      <el-input v-model="key" placeholder="搜学号/姓名" style="width:260px" clearable @input="load" />
      <el-button @click="load">刷新</el-button>
    </div>
    <el-table :data="filtered" stripe>
      <el-table-column prop="sno" label="学号" width="140" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="college" label="学院" />
      <el-table-column prop="grade" label="年级" width="80" />
      <el-table-column prop="phone" label="手机" width="130" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <span :class="['tag', row.status === '正常' ? 'tag-success' : 'tag-warn']">{{ row.status || '正常' }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api/request'

const rows = ref([])
const key = ref('')
const filtered = computed(() =>
  rows.value.filter((u) => !key.value || u.sno?.includes(key.value) || u.name?.includes(key.value)))

async function load() {
  const p = await api.get('/users', { params: { size: 100 } }).catch(() => ({}))
  rows.value = p.records ?? p ?? []
}

onMounted(load)
</script>
