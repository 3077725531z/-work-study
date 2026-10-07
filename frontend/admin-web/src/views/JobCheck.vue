<template>
  <h2>A03 岗位审核</h2>
  <p>原型 prototype/admin-web.html 岗位审核表：通过上架 / 驳回填原因</p>
  <el-table :data="rows" style="width:100%">
    <el-table-column prop="title" label="岗位" />
    <el-table-column prop="headcount" label="人数" />
    <el-table-column prop="status" label="状态" />
    <el-table-column label="操作">
      <template #default="{ row }">
        <el-button size="small" type="primary" @click="ok(row)">通过上架</el-button>
        <el-button size="small" @click="no(row)">驳回</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { jobPage, jobPublish, jobReject } from '../api'

const rows = ref([])

onMounted(async () => {
  const page = await jobPage({ current: 1, size: 20 })
  rows.value = page.records ?? page ?? []
})

async function ok(row) {
  await jobPublish(row.id)
  row.status = '招募中'
}

async function no(row) {
  const reason = prompt('驳回原因（必填）')
  if (!reason) return
  await jobReject(row.id, reason)
  row.status = '驳回'
}
</script>
