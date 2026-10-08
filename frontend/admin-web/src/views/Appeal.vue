<template>
  <h2 class="page-title">申诉工单</h2>
  <div class="hk-card">
    <el-table :data="rows" stripe>
      <el-table-column prop="code" label="单号" width="160" />
      <el-table-column prop="type" label="类型" width="100" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <span :class="['tag', row.status === '已处理' ? 'tag-success' : 'tag-warn']">{{ row.status }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" :disabled="row.status === '已处理'" @click="done(row)">受理</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../api/request'

const rows = ref([])

async function load() {
  const p = await api.get('/appeals/list', { params: { current: 1, size: 50 } }).catch(() => ({}))
  rows.value = p.records ?? p ?? []
}

async function done(row) {
  try {
    const { value } = await ElMessageBox.prompt('回复内容', '受理申诉', {
      inputValidator: (v) => !!v?.trim() || '回复不能为空'
    })
    await api.post(`/appeals/${row.id}:reply`, null, { params: { reply: value } })
    row.status = '已处理'
    ElMessage.success('已受理')
  } catch {}
}

onMounted(load)
</script>
