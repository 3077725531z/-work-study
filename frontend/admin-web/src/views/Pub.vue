<template>
  <h2 class="page-title">公示管理</h2>
  <div class="hk-card">
    <h3 class="card-title">发布新公示</h3>
    <el-form :model="f" label-width="80px" style="max-width:480px">
      <el-form-item label="标题">
        <el-input v-model="f.title" placeholder="公示标题" />
      </el-form-item>
      <el-form-item label="公示期">
        <el-input v-model="f.period" placeholder="如：10.08-10.12" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">发布公示</el-button>
      </el-form-item>
    </el-form>
  </div>
  <div class="hk-card">
    <h3 class="card-title">历史公示</h3>
    <el-table :data="rows" stripe>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="period" label="公示期" width="140" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <span class="tag tag-success">{{ row.status || '已发布' }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { pubs, publish } from '../api'

const rows = ref([])
const f = reactive({ title: '', period: '' })

async function load() {
  rows.value = await pubs()
}

async function submit() {
  if (!f.title.trim()) { ElMessage.warning('请输入标题'); return }
  await publish({ ...f })
  ElMessage.success('发布成功')
  f.title = ''
  f.period = ''
  await load()
}

onMounted(load)
</script>
