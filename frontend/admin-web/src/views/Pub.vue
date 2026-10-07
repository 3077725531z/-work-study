<template>
  <h2>A10 公示管理（实时）</h2>
  <el-form :model="f" label-width="80px" style="max-width:480px">
    <el-form-item label="标题"><el-input v-model="f.title" /></el-form-item>
    <el-form-item label="公示期"><el-input v-model="f.period" placeholder="9.25-9.27" /></el-form-item>
    <el-form-item><el-button type="primary" @click="submit">发布公示</el-button></el-form-item>
  </el-form>
  <el-table :data="rows">
    <el-table-column prop="title" label="标题" />
    <el-table-column prop="period" label="公示期" />
    <el-table-column prop="status" label="状态" />
  </el-table>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { pubs, publish } from '../api'

const rows = ref([])
const f = reactive({ title: '', period: '' })

async function load() {
  rows.value = await pubs()
}

async function submit() {
  await publish({ ...f })
  await load()
}

onMounted(load)
</script>
