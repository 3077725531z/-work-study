<template>
  <div class="login-wrap">
    <div class="login-card">
      <h1 class="login-title">勤工助学管理</h1>
      <p class="login-sub">学工处后台</p>
      <el-form @submit.prevent="doLogin" style="margin-top:24px">
        <el-form-item>
          <el-input v-model="sno" placeholder="账号" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <p v-if="err" class="err">{{ err }}</p>
        <el-button type="primary" size="large" :loading="busy" style="width:100%" @click="doLogin">登录</el-button>
      </el-form>
      <p class="tip">演示账号：A001 / 123456</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/request'

const sno = ref('A001')
const password = ref('123456')
const err = ref('')
const busy = ref(false)
const router = useRouter()

async function doLogin() {
  err.value = ''
  if (!sno.value || !password.value) { err.value = '请输入账号和密码'; return }
  busy.value = true
  try {
    const r = await api.post('/auth/login', { sno: sno.value, password: password.value })
    if (r.role !== 'admin') { err.value = '该账号无管理员权限'; return }
    localStorage.setItem('admin-token', r.token)
    localStorage.setItem('admin-uid', r.uid)
    localStorage.setItem('admin-name', r.name)
    router.push('/dash')
  } catch (e) {
    err.value = e.response?.data?.msg || '登录失败'
  } finally {
    busy.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: var(--color-paper);
  padding: var(--space-lg);
}
.login-card {
  background: var(--color-paper-2);
  border: 1px solid var(--color-line);
  border-radius: var(--radius);
  padding: var(--space-xl);
  width: 100%;
  max-width: 380px;
}
.login-title {
  font-family: var(--font-display);
  font-size: 24px;
  margin: 0;
  color: var(--color-ink);
}
.login-sub {
  color: var(--color-muted);
  margin: 4px 0 0;
  font-size: 14px;
}
.err { color: var(--color-danger); font-size: 13px; margin: 0 0 8px; }
.tip { color: var(--color-muted); font-size: 12px; margin-top: 16px; text-align: center; }
</style>
