<template>
  <h2 class="page-title">工资发放</h2>
  <div class="hk-card">
    <div class="toolbar">
      <el-button @click="load">刷新</el-button>
    </div>
    <el-table :data="rows" stripe>
      <el-table-column prop="studentId" label="学生ID" width="100" />
      <el-table-column prop="month" label="月份" width="100" />
      <el-table-column prop="hours" label="工时" width="80" align="center" />
      <el-table-column prop="rate" label="单价" width="80" />
      <el-table-column prop="net" label="实发" width="100">
        <template #default="{ row }">¥ {{ row.net }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <span :class="['tag', statusClass(row.status)]">{{ row.status }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" :disabled="row.status !== '待复核'" @click="cf(row)">复核</el-button>
          <el-button size="small" type="success" :disabled="row.status !== '已复核'" @click="pay(row)">发放</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api/request'
import { salaryConfirm, salaryPay } from '../api'

const rows = ref([])

function statusClass(s) {
  if (s === '已发放') return 'tag-success'
  if (s === '已复核') return 'tag-info'
  return 'tag-warn'
}

async function load() {
  const p = await api.get('/salaries/list', { params: { current: 1, size: 50 } }).catch(() => ({}))
  rows.value = p.records ?? p ?? []
}

async function cf(row) {
  await salaryConfirm(row.id)
  row.status = '已复核'
  ElMessage.success('已复核')
}

async function pay(row) {
  await salaryPay(row.id)
  row.status = '已发放'
  ElMessage.success('已发放')
}

onMounted(load)
</script>
