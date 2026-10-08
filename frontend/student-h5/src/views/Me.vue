<template>
  <div class="page hallmark">
    <header class="profile">
      <div class="avatar" aria-hidden="true">
        <svg viewBox="0 0 64 64" width="52" height="52">
          <circle cx="32" cy="32" r="30" fill="var(--color-accent)" />
          <circle cx="32" cy="26" r="12" fill="var(--color-accent-ink)" opacity="0.92" />
          <path d="M14 50c4-10 11-14 18-14s14 4 18 14" fill="var(--color-accent-ink)" opacity="0.92" />
          <circle cx="27.5" cy="25" r="1.8" fill="var(--color-accent)" />
          <circle cx="36.5" cy="25" r="1.8" fill="var(--color-accent)" />
          <path d="M28 30.5c1.5 1.5 6.5 1.5 8 0" stroke="var(--color-accent)" stroke-width="1.6" fill="none" stroke-linecap="round" />
        </svg>
      </div>
      <div class="who">
        <b class="name">{{ me.name || '加载中…' }}</b>
        <div class="sub">学号 {{ me.sno || sno }}</div>
        <div class="sub">{{ me.college || '' }} {{ me.grade || '' }}</div>
        <div class="sub">{{ me.phone || '' }} · {{ roleText }}</div>
      </div>
      <van-button size="small" round plain @click="reload">刷新</van-button>
    </header>

    <div class="stats">
      <div class="stat" @click="$router.push('/applies')">
        <b>{{ counts.applies }}</b><span>申请</span>
      </div>
      <div class="stat" @click="$router.push('/attend')">
        <b>{{ counts.attend }}</b><span>考勤</span>
      </div>
      <div class="stat" @click="$router.push('/salary')">
        <b>¥{{ counts.pay }}</b><span>累计工资</span>
      </div>
    </div>

    <van-cell-group inset class="hk-group">
      <van-cell title="我的课表" label="有课自动避让" is-link to="/timetable" icon="calendar-o" />
      <van-cell title="困难认定" label="档位/材料/进度" is-link to="/aid" icon="notes-o" />
      <van-cell title="请假申请" label="提前24小时" is-link to="/leave" icon="clock-o" />
      <van-cell title="工资申诉" label="3个工作日回复" is-link to="/appeal" icon="balance-o" />
      <van-cell title="通知公告" label="公示/提醒/到账" is-link to="/notices" icon="bell" />
    </van-cell-group>

    <van-cell-group inset class="hk-group grouped">
      <van-cell title="修改密码" label="定期更换更安全" is-link @click="showPwd = true" icon="lock" />
      <van-cell title="退出登录" is-link @click="logout" icon="close" />
    </van-cell-group>
    <p v-if="err" class="err">{{ err }}</p>

    <van-popup v-model:show="showPwd" position="bottom" round class="pwd-pop">
      <div class="pwd-form">
        <h3>修改密码</h3>
        <van-field v-model="pwd.old" type="password" label="旧密码" placeholder="请输入当前密码" />
        <van-field v-model="pwd.new" type="password" label="新密码" placeholder="6–20位" />
        <van-field v-model="pwd.confirm" type="password" label="确认密码" placeholder="再输一次" />
        <p v-if="pwdErr" class="err">{{ pwdErr }}</p>
        <div class="pwd-btns">
          <van-button round block @click="showPwd = false">取消</van-button>
          <van-button round block type="primary" :loading="pwdBusy" @click="doPwd">确认修改</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import api from '../api/request'
import { myApplies, uid } from '../api'

const me = ref({})
const sno = ref(String(localStorage.getItem('sno') || ''))
const role = ref(localStorage.getItem('role') || 'student')
const counts = ref({ applies: 0, attend: 0, pay: 0 })
const err = ref('')
const router = useRouter()
const showPwd = ref(false)
const pwd = ref({ old: '', new: '', confirm: '' })
const pwdErr = ref('')
const pwdBusy = ref(false)

const roleText = computed(() => (role.value === 'student' ? '学生' : role.value))

async function loadMe() {
  err.value = ''
  try {
    const p = await api.get('/users', { params: { current: 1, size: 100 } })
    const list = p.records ?? p ?? []
    // 只按 uid 或 sno 精确匹配，不做 role 兜底（避免拿到别人的档案）
    const mine =
      list.find((u) => String(u.id) === String(uid())) ||
      list.find((u) => String(u.sno) === sno.value) ||
      null
    if (mine) {
      me.value = mine
      // 同步刷新本地 sno
      if (mine.sno) localStorage.setItem('sno', String(mine.sno))
    } else {
      err.value = '未找到你的档案，请联系辅导员确认学号'
    }
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  }
}

async function loadCounts() {
  try {
    const [applies, attend, pay] = await Promise.all([
      myApplies().catch(() => []),
      api.get('/attendance/list', { params: { current: 1, size: 60, studentId: uid() } }).catch(() => []),
      api.get('/salaries/list', { params: { current: 1, size: 24, studentId: uid() } }).catch(() => [])
    ])
    const t = (x) => (Array.isArray(x) ? x : (x.records ?? []))
    const a = t(applies)
    const at = t(attend).filter((x) => !x.studentId || x.studentId === uid())
    const sl = t(pay)
    counts.value = {
      applies: a.length,
      attend: at.length,
      pay: sl.reduce((s, r) => s + Number(r.net ?? 0), 0)
    }
  } catch {
    counts.value = { applies: 0, attend: 0, pay: 0 }
  }
}

async function reload() {
  await Promise.all([loadMe(), loadCounts()])
  showToast('已同步数据库')
}

async function doPwd() {
  pwdErr.value = ''
  if (!pwd.value.old) { pwdErr.value = '请输入旧密码'; return }
  if (!pwd.value.new || pwd.value.new.length < 6 || pwd.value.new.length > 20) {
    pwdErr.value = '新密码须为6–20位'
    return
  }
  if (pwd.value.new !== pwd.value.confirm) {
    pwdErr.value = '两次输入的新密码不一致'
    return
  }
  if (pwd.value.new === pwd.value.old) {
    pwdErr.value = '新密码不能与旧密码相同'
    return
  }
  pwdBusy.value = true
  try {
    await api.put(`/users/${uid()}/password`, {
      oldPassword: pwd.value.old,
      newPassword: pwd.value.new
    })
    showToast('密码修改成功，请重新登录')
    showPwd.value = false
    pwd.value = { old: '', new: '', confirm: '' }
    setTimeout(() => logout(), 800)
  } catch (e) {
    pwdErr.value = e.response?.data?.msg || e.message
  } finally {
    pwdBusy.value = false
  }
}

function logout() {
  localStorage.clear()
  router.push('/login')
}

onMounted(async () => {
  if (!uid()) {
    router.push('/login')
    return
  }
  await Promise.all([loadMe(), loadCounts()])
})
</script>

<style scoped>
.hallmark { padding: var(--space-md); padding-bottom: calc(var(--space-xl) + 50px); }
.profile { display: flex; gap: 12px; align-items: center; background: var(--color-ink); color: var(--color-paper-2); border-radius: var(--radius); padding: var(--space-md); margin-bottom: var(--space-sm); }
.avatar { width: 52px; height: 52px; border-radius: 50%; overflow: hidden; flex-shrink: 0; background: var(--color-paper-2); }
.who { flex: 1; min-width: 0; }
.profile .name { font-family: var(--font-display); font-style: normal; font-size: 17px; overflow-wrap: anywhere; }
.profile .sub { font-size: 12px; opacity: 0.75; }
.stats { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 8px; margin-bottom: var(--space-sm); }
.stat { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: 12px 8px; text-align: center; display: grid; gap: 2px; }
.stat b { font-family: var(--font-display); font-size: 18px; color: var(--color-accent); }
.stat span { font-size: 12px; color: var(--color-muted); }
.hk-group { border: 1px solid var(--color-line); border-radius: var(--radius); overflow: hidden; }
.grouped { margin-top: 12px; }
.err { color: var(--color-danger); font-size: 13px; }
.pwd-pop { max-width: 560px; }
.pwd-form { padding: var(--space-md); display: grid; gap: var(--space-sm); }
.pwd-form h3 { font-family: var(--font-display); margin: 0; font-size: 18px; }
.pwd-btns { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
</style>
