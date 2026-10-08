<template>
  <h2 class="page-title">认定复审</h2>
  <div class="hk-card">
    <el-table :data="rows" stripe>
      <el-table-column prop="studentId" label="学生ID" width="100" />
      <el-table-column prop="level" label="申请档" width="100" />
      <el-table-column prop="reason" label="申请理由" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <span :class="['tag', row.status === '已认定' ? 'tag-success' : 'tag-warn']">{{ row.status }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="ok(row)">定为二档</el-button>
          <el-button size="small" @click="down(row)">降档/驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api/request'
import { aidReview } from '../api'

const rows = ref([])

async function load() {
  const p = await api.get('/aid/list', { params: { size: 50 } }).catch(() => ({}))
  rows.value = p.records ?? p ?? []
}

async function ok(row) {
  await aidReview(row.id, '二档')
  row.status = '已认定'
  row.level = '二档'
  ElMessage.success('已定为二档')
}

async function down(row) {
  await aidReview(row.id, '三档')
  row.status = '已认定'
  row.level = '三档'
  ElMessage.success('已降档')
}

onMounted(load)
</script>
