<template>
  <h2>E05 申请审核（含课表冲突列）</h2>
  <p>原型 employer-web.html：冲突行标红并禁用通过，后端 403 同规则</p>
  <el-table :data="rows">
    <el-table-column prop="student" label="学生" />
    <el-table-column prop="conflict" label="课表冲突">
      <template #default="{ row }">
        <el-tag v-if="row.conflict" type="danger">{{ row.conflict }}</el-tag>
        <el-tag v-else type="success">无冲突</el-tag>
      </template>
    </el-table-column>
    <el-table-column label="操作">
      <template #default="{ row }">
        <el-button size="small" type="primary" :disabled="!!row.conflict" @click="pass(row)">通过</el-button>
        <el-button size="small" @click="deny(row)">驳回</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref } from 'vue'
import { audit } from '../api'

const rows = ref([
  { id: 1, student: '张晓', conflict: '' },
  { id: 2, student: '李晨', conflict: '与《数据库》(周三晚)冲突' }
])

async function pass(row) { await audit(row.id, true, '安排周一三五班') }
async function deny(row) { await audit(row.id, false, '与课程冲突') }
</script>
