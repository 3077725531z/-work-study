<template>
  <div class="auth">
    <div class="hero">
      <div class="brand">勤工助学</div>
      <h1>同学，你好</h1>
      <p class="sub">一站式申请 · 考勤 · 工资查询</p>
    </div>

    <div class="card">
      <van-field
        v-model="sno"
        label="学号"
        placeholder="6–20位字母或数字"
        maxlength="20"
        clearable
      />
      <van-field
        v-model="password"
        type="password"
        label="密码"
        placeholder="6–20位"
        clearable
      />
      <p v-if="err" class="err">{{ err }}</p>
      <van-button type="primary" block round :loading="busy" @click="doLogin">
        登录
      </van-button>
      <div class="links">
        <span @click="$router.push('/register')">新生注册</span>
        <span @click="forgot">忘记密码</span>
      </div>
    </div>

    <p class="foot">困难生优先推荐 · 有课时段自动避让</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { login } from '../api'

// 演示账号默认填充，正式上线前清空
const sno = ref('2023307120')
const password = ref('123456')
const err = ref('')
const busy = ref(false)
const router = useRouter()

async function doLogin() {
  err.value = ''
  if (!/^[A-Za-z0-9]{6,20}$/.test(sno.value)) {
    err.value = '学号为6–20位字母或数字'
    return
  }
  if (password.value.length < 6) {
    err.value = '密码至少6位'
    return
  }
  busy.value = true
  try {
    const r = await login(sno.value, password.value)
    localStorage.setItem('token', r.token)
    localStorage.setItem('role', r.role)
    localStorage.setItem('uid', String(r.uid ?? ''))
    if (r.sno) localStorage.setItem('sno', String(r.sno))
    else localStorage.setItem('sno', sno.value)
    if (!r.uid) {
      err.value = '后端未返回用户ID，请重启后端后再登录'
      return
    }
    showToast('欢迎回来')
    router.push('/jobs')
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  } finally {
    busy.value = false
  }
}

function forgot() {
  showToast('请凭学号+身份证后6位到辅导员处重置')
}
</script>

<style scoped>
.auth {
  min-height: 100vh;
  padding: calc(var(--space-xl) + 8px) var(--space-lg) var(--space-xl);
  background:
    radial-gradient(120px 120px at 85% 8%, var(--color-line), transparent),
    var(--color-paper);
}

.hero {
  margin-bottom: var(--space-lg);
}

.brand {
  display: inline-block;
  font-size: 12px;
  color: var(--color-accent-ink);
  background: var(--color-accent);
  border-radius: 999px;
  padding: 4px 12px;
  margin-bottom: var(--space-sm);
}

h1 {
  font-family: var(--font-display);
  font-size: 30px;
  margin: 0 0 4px;
}

.sub {
  color: var(--color-muted);
  font-size: 14px;
  margin: 0;
}

.card {
  background: var(--color-paper-2);
  border: 1px solid var(--color-line);
  border-radius: var(--radius);
  padding: var(--space-md);
  display: grid;
  gap: var(--space-sm);
}

.links {
  display: flex;
  justify-content: space-between;
  color: var(--color-accent);
  font-size: 13px;
}

.err {
  color: var(--color-danger);
  font-size: 13px;
  margin: 0;
}

.foot {
  text-align: center;
  color: var(--color-muted);
  font-size: 12px;
  margin-top: var(--space-lg);
}
</style>
