<template>
  <h2 class="page-title">考勤监控</h2>
  <div class="hk-card">
    <div class="toolbar">
      <el-button @click="load">刷新</el-button>
    </div>
    <el-table :data="rows" stripe>
      <el-table-column prop="studentId" label="学生ID" width="100" />
      <el-table-column prop="jobId" label="岗位ID" width="100" />
      <el-table-column label="工作日期" width="120">
        <template #default="{ row }">{{ row.workDate?.slice(0, 10) }}</template>
      </el-table-column>
      <el-table-column label="上班打卡" width="160">
        <template #default="{ row }">{{ row.clockIn?.slice(11, 19) || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <span :class="['tag', row.status === '正常' ? 'tag-success' : 'tag-warn']">{{ row.status }}</span>
        </template>
      </el-table-column>
      <el-table-column label="确认" width="80" align="center">
        <template #default="{ row }">
          <el-icon v-if="row.confirmed === 1" color="var(--color-success)"><Check /></el-icon>
          <span v-else style="color:var(--color-muted)">待确认</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Check } from '@element-plus/icons-vue'
import api from '../api/request'

const rows = ref([])

async function load() {
  const p = await api.get('/attendance/list', { params: { current: 1, size: 50 } }).catch(() => ({}))
  rows.value = p.records ?? p ?? []
}

onMounted(load)
</script>
