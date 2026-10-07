<template>
  <h2>部门中心（实时）</h2>
  <el-button @click="logout">退出登录（清token）</el-button>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function logout() {
  localStorage.clear()
  router.push('/dash')
}
</script>
