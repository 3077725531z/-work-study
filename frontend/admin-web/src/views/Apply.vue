<template>
  <h2 class="page-title">申请抽查</h2>
  <div class="hk-card">
    <div class="toolbar">
      <el-button @click="load">刷新</el-button>
    </div>
    <el-table :data="rows" stripe>
      <el-table-column prop="code" label="编号" width="160" />
      <el-table-column prop="studentId" label="学生ID" width="100" />
      <el-table-column prop="jobId" label="岗位ID" width="100" />
      <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <span :class="['tag', statusClass(row.status)]">{{ row.status }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="pass(row)">确认通过</el-button>
          <el-button size="small" type="danger" @click="back(row)">打回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api/request'
import { auditApply } from '../api'

const rows = ref([])

function statusClass(s) {
  if (s === '已通过') return 'tag-success'
  if (s === '未通过') return 'tag-danger'
  return 'tag-warn'
}

async function load() {
  const p = await api.get('/applies', { params: { current: 1, size: 50 } }).catch(() => ({}))
  rows.value = p.records ?? p ?? []
}

async function pass(row) {
  await auditApply(row.id, true, '抽查确认')
  row.status = '已通过'
  ElMessage.success('已通过')
}

async function back(row) {
  await auditApply(row.id, false, '抽查打回重审')
  row.status = '未通过'
  ElMessage.success('已打回')
}

onMounted(load)
</script>
