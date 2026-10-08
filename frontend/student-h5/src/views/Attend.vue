<template>
  <div class="page hallmark">
    <header class="hero">
      <div>
        <span class="brand">考勤 · {{ month }}（来自打卡记录）</span>
        <h2>{{ normal }}正常 · {{ late }}迟到</h2>
        <p class="sub">今日 {{ todayShift }} · 岗点半径内打卡 · 共{{ monthRows.length }}条记录</p>
      </div>
    </header>

    <section class="card">
      <h3>当前打卡岗位</h3>
      <van-field
        v-model="jobText"
        readonly
        placeholder="暂无可打卡岗位"
        @click="showJobs = true"
      />
      <van-popup v-model:show="showJobs" position="bottom">
        <van-picker
          :columns="duty.map((j) => ({ text: j.jobTitle, value: j.jobId }))"
          @confirm="onJob"
          @cancel="showJobs = false"
        />
      </van-popup>
      <div class="btns">
        <van-button type="primary" round block :loading="busy" :disabled="!canClock" @click="doClock('in')">上班打卡</van-button>
        <van-button round block :loading="busy" :disabled="!canClock" @click="doClock('out')">下班打卡</van-button>
      </div>
      <p v-if="!canClock" class="tip">申请通过并经公示确认后，才会出现可打卡岗位</p>
    </section>

    <p v-if="posText" class="pos">📍 当前定位：{{ posText }}</p>
    <p v-if="msg" class="ok">{{ msg }}</p>
    <p v-if="err" class="err">{{ err }}</p>

    <van-cell title="请假申请" label="考试周/病假提前审批，不计缺勤" is-link to="/leave" class="hk-cell" />

    <h3 class="sec">在岗列表（仅已通过可打卡）</h3>
    <van-empty v-if="!duty.length" description="暂无在岗岗位" />
    <article v-for="j in duty" :key="j.jobId" :class="['onduty', { active: j.jobId === jobId }]" @click="chooseJob(j)">
      <b>{{ j.jobTitle }}</b>
      <van-tag type="success" round size="mini">已通过</van-tag>
      <span class="sub">{{ j.workTime || '' }}</span>
    </article>

    <h3 class="sec">审核中（不可打卡）</h3>
    <article v-for="j in pending" :key="j.jobId" class="onduty dim">
      <b>{{ j.jobTitle }}</b>
      <van-tag type="warning" round size="mini">{{ j.status }}</van-tag>
      <span class="sub">通过后自动进入在岗列表</span>
    </article>

    <h3 class="sec">本月记录</h3>
    <van-empty v-if="!rows.length" description="本月暂无打卡" />
    <article v-for="a in rows" :key="a.id" class="rec">
      <b>{{ a.workDate }}</b>
      <van-tag :type="a.status === '正常' ? 'success' : a.status === '迟到' ? 'warning' : 'default'" round>{{ a.status }}</van-tag>
      <span class="cf">{{ a.confirmed ? '已确认' : '未确认' }}</span>
    </article>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { clock, uid, myApplies, myAttendance, jobOne } from '../api'

const router = useRouter()
const rows = ref([])
const duty = ref([])
const pending = ref([])
const jobId = ref(null)
const jobText = ref('')
const showJobs = ref(false)
const msg = ref('')
const err = ref('')
const busy = ref(false)
const posText = ref('')

const canClock = computed(() => !!jobId.value)
const month = computed(() => {
  const cur = new Date()
  const key = `${cur.getFullYear()}-${String(cur.getMonth() + 1).padStart(2, '0')}`
  return rows.value.find((a) => String(a.workDate).startsWith(key))?.workDate?.slice(0, 7) ?? key
})
const monthRows = computed(() => {
  const cur = new Date()
  const key = `${cur.getFullYear()}-${String(cur.getMonth() + 1).padStart(2, '0')}`
  return rows.value.filter((a) => String(a.workDate).startsWith(key))
})
const normal = computed(() => monthRows.value.filter((a) => a.status === '正常').length)
const late = computed(() => monthRows.value.filter((a) => a.status === '迟到').length)
const todayShift = computed(() => {
  const j = duty.value.find((x) => x.jobId === jobId.value) || duty.value[0]
  return j ? `${j.jobTitle} ${j.workTime || ''}` : '暂无在岗班次'
})

async function load() {
  if (!uid()) {
    router.push('/login')
    return
  }
  try {
    const [p, applies] = await Promise.all([
      myAttendance(),
      myApplies().catch(() => [])
    ])
    const list = p.records ?? p ?? []
    const mine = uid()
    rows.value = Array.isArray(list) ? list.filter((a) => String(a.studentId) === String(mine)) : []

    const apps = Array.isArray(applies) ? applies : (applies.records ?? [])
    const withTitle = await Promise.all(apps.map(async (a) => {
      if (!a.jobTitle && a.jobId) {
        try {
          const j = await jobOne(a.jobId)
          return { ...a, jobTitle: j.title || `岗位${a.jobId}`, workTime: j.workTime }
        } catch {
          return { ...a, jobTitle: `岗位${a.jobId}` }
        }
      }
      return a
    }))
    duty.value = withTitle.filter((a) => a.status === '已通过')
    pending.value = withTitle.filter((a) => a.status !== '已通过')
    if (duty.value.length && !jobId.value) {
      jobId.value = duty.value[0].jobId
      jobText.value = duty.value[0].jobTitle
    }
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  }
}

function onJob({ selectedOptions }) {
  jobId.value = selectedOptions[0].value
  jobText.value = selectedOptions[0].text
  showJobs.value = false
}

function chooseJob(j) {
  jobId.value = j.jobId
  jobText.value = j.jobTitle
}

async function doClock(type) {
  msg.value = ''
  err.value = ''
  if (!uid()) {
    router.push('/login')
    return
  }
  if (!jobId.value) {
    err.value = '暂无可打卡岗位'
    return
  }
  busy.value = true
  try {
    const pos = await new Promise((resolve) => {
      if (!navigator.geolocation) return resolve({})
      navigator.geolocation.getCurrentPosition(
        (p) => resolve({ lat: p.coords.latitude, lng: p.coords.longitude }),
        () => resolve({}),
        { timeout: 5000 }
      )
    })
    if (pos.lat && pos.lng) {
      posText.value = `${pos.lat.toFixed(5)}, ${pos.lng.toFixed(5)}`
    } else {
      posText.value = '定位失败（请允许浏览器定位权限）'
    }
    const r = await clock({ studentId: uid(), jobId: jobId.value, type, ...pos })
    msg.value = `打卡成功 ${r.workDate ?? ''}`
    showToast('打卡成功')
    await load()
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  } finally {
    busy.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.hallmark { padding: var(--space-md); padding-bottom: calc(var(--space-xl) + 50px); }
.hero { background: var(--color-ink); color: var(--color-paper-2); border-radius: var(--radius); padding: var(--space-lg) var(--space-md); margin-bottom: var(--space-sm); }
.brand { display: inline-block; font-size: 12px; background: var(--color-accent); color: var(--color-accent-ink); border-radius: 999px; padding: 3px 10px; margin-bottom: var(--space-sm); }
.hero h2 { font-family: var(--font-display); font-style: normal; font-size: 24px; margin: 0 0 4px; }
.hero .sub { font-size: 12px; opacity: 0.75; margin: 0; }
.card { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); display: grid; gap: var(--space-sm); }
.card h3 { font-family: var(--font-display); font-style: normal; font-size: 16px; margin: 0; }
.btns { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
.hk-cell { border: 1px solid var(--color-line); border-radius: var(--radius); overflow: hidden; background: var(--color-paper-2); margin-top: var(--space-sm); }
.sec { font-family: var(--font-display); font-style: normal; font-size: 17px; margin: var(--space-md) 0 var(--space-sm); }
.onduty { display: grid; gap: 4px; background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: 10px 12px; margin-top: 8px; }
.onduty.active { border-color: var(--color-accent); }
.onduty.dim { opacity: 0.7; }
.onduty .sub { color: var(--color-muted); font-size: 12px; }
.rec { display: flex; gap: 8px; align-items: center; background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: 10px 12px; margin-top: 8px; }
.rec .cf { margin-left: auto; color: var(--color-muted); font-size: 12px; }
.pos { color: var(--color-muted); font-size: 12px; margin: 0; }
.ok { color: var(--color-success); font-size: 13px; }
.err { color: var(--color-danger); font-size: 13px; }
.tip { color: var(--color-muted); font-size: 12px; margin: 0; }
</style>
