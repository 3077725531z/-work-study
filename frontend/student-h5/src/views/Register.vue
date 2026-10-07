<template>
  <div class="auth">
    <div class="hero">
      <div class="brand">新生注册</div>
      <h1>加入勤工助学</h1>
      <p class="sub">提交后待辅导员审核，审核通过即可申请岗位</p>
    </div>

    <div class="card">
      <van-field v-model="form.sno" label="学号" placeholder="6–20位字母或数字" maxlength="20" clearable />
      <van-field v-model="form.name" label="姓名" placeholder="如 张晓" clearable />
      <van-field v-model="form.college" label="学院" placeholder="如 信息学院" clearable />
      <van-field v-model="form.phone" label="手机" placeholder="用于接收录用短信" clearable />
      <van-field v-model="form.password" type="password" label="密码" placeholder="6–20位" clearable />
      <van-field v-model="confirm" type="password" label="确认密码" placeholder="再输一次" clearable />
      <p v-if="err" class="err">{{ err }}</p>
      <van-button type="primary" block round :loading="busy" @click="submit">
        提交注册
      </van-button>
      <div class="links">
        <span @click="$router.push('/login')">已有账号？去登录</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { register } from '../api'

const form = reactive({ sno: '', name: '', college: '', phone: '', password: '' })
const confirm = ref('')
const err = ref('')
const busy = ref(false)
const router = useRouter()

async function submit() {
  err.value = ''
  if (!/^[A-Za-z0-9]{6,20}$/.test(form.sno)) {
    err.value = '学号为6–20位字母或数字'
    return
  }
  if (!form.name.trim()) {
    err.value = '请填写姓名'
    return
  }
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    err.value = '手机号格式不正确'
    return
  }
  if (!form.password || form.password.length < 6 || form.password.length > 20) {
    err.value = '密码为6–20位'
    return
  }
  if (form.password !== confirm.value) {
    err.value = '两次输入的密码不一致'
    return
  }
  busy.value = true
  try {
    await register({ ...form })
    showToast('注册成功，待审核')
    router.push('/login')
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  } finally {
    busy.value = false
  }
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
  font-size: 28px;
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
  text-align: center;
  color: var(--color-accent);
  font-size: 13px;
}

.err {
  color: var(--color-danger);
  font-size: 13px;
  margin: 0;
}
</style>
