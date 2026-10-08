<template>
  <h2 class="page-title">系统设置</h2>
  <div class="hk-card">
    <el-table :data="rows" stripe>
      <el-table-column prop="ckey" label="配置键" width="200" />
      <el-table-column prop="cval" label="当前值" />
      <el-table-column label="操作" width="100" fixed="right" align="center">
        <template #default="{ row }">
          <el-button size="small" @click="edit(row)">修改</el-button>
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
  rows.value = await api.get('/system/config').catch(() => [])
}

async function edit(row) {
  try {
    const { value } = await ElMessageBox.prompt(`修改 ${row.ckey}`, '系统配置', { inputValue: row.cval })
    await api.post('/system/config', { ckey: row.ckey, cval: value })
    row.cval = value
    ElMessage.success('已更新')
  } catch {}
}

onMounted(load)
</script>
