<template>
  <h2 class="page-title">岗位审核</h2>
  <div class="hk-card">
    <div class="toolbar">
      <el-button @click="load">刷新列表</el-button>
    </div>
    <el-table :data="rows" stripe>
      <el-table-column prop="code" label="编号" width="120" />
      <el-table-column prop="title" label="岗位名称" />
      <el-table-column prop="headcount" label="人数" width="80" align="center" />
      <el-table-column prop="payAmount" label="时薪" width="100">
        <template #default="{ row }">{{ row.payAmount ?? '-' }} 元</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <span :class="['tag', statusClass(row.status)]">{{ row.status }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="ok(row)">通过上架</el-button>
          <el-button size="small" type="danger" @click="no(row)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { jobPage, jobPublish, jobReject } from '../api'

const rows = ref([])

function statusClass(s) {
  if (s === '招募中') return 'tag-success'
  if (s === '驳回') return 'tag-danger'
  return 'tag-warn'
}

async function load() {
  const page = await jobPage({ current: 1, size: 50, status: '待审核' })
  rows.value = page.records ?? page ?? []
}

async function ok(row) {
  await jobPublish(row.id)
  row.status = '招募中'
  ElMessage.success('已上架')
}

async function no(row) {
  try {
    const { value } = await ElMessageBox.prompt('请输入驳回原因（必填）', '驳回岗位', {
      inputValidator: (v) => !!v?.trim() || '原因不能为空'
    })
    await jobReject(row.id, value)
    row.status = '驳回'
    ElMessage.success('已驳回')
  } catch {}
}

onMounted(load)
</script>
