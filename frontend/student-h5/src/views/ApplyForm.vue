<template>
  <div class="page hallmark">
    <van-skeleton :loading="loading" :row="4">
      <header class="hero">
        <div>
          <span class="brand">确认申请</span>
          <h2>{{ job.title || '岗位' }}</h2>
          <p class="sub">{{ job.place || '' }} · {{ job.workTime || '' }} · {{ job.payAmount ?? '' }}元/时</p>
        </div>
      </header>

      <section class="card">
        <h3>申请人（自动带入）</h3>
        <p class="sub">{{ me.name || '' }} · {{ me.sno || '' }} · {{ me.college || '' }}</p>
      </section>

      <section class="card">
        <div class="sec-top">
          <h3>每周可到岗时段（至少选2段）</h3>
          <span class="count" :class="{ bad: slots.length < 2 }">{{ slots.length }}/{{ options.length }}</span>
        </div>
        <div class="slot-grid">
          <button
            v-for="s in options"
            :key="s"
            :class="['slot', { on: slots.includes(s), clash: clashOf(s) }]"
            @click="toggle(s)"
          >
            {{ s }}
            <span v-if="clashOf(s)" class="clash-tip">{{ clashOf(s) }}</span>
          </button>
        </div>
        <p class="tip">时段由部门发布时设定 · 红色=与课表/在岗重叠，选中有红色的将无法提交</p>
      </section>

      <section class="card">
        <h3>个人优势（50字内）</h3>
        <van-field
          v-model="remark"
          type="textarea"
          rows="2"
          maxlength="50"
          show-word-limit
          placeholder="如：做事细心，做过图书志愿者"
        />
      </section>
    </van-skeleton>

    <p v-if="conflict" class="conflict">与课程冲突：{{ conflict }}，请换时段</p>
    <p v-if="error" class="err">{{ error }}</p>
    <p v-if="ok" class="ok">提交成功，正在跳转进度页…</p>

    <div class="sticky">
      <van-button type="primary" block round :loading="busy" :disabled="done" @click="submit">
        {{ done ? '已提交' : '提交申请' }}
      </van-button>
      <p class="tip center">提交后不可修改，可撤回重提 · 每岗限申请1次</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import api from '../api/request'
import { jobOne, submitApply, myTimetable, uid } from '../api'

const route = useRoute()
const router = useRouter()
const job = ref({})
const me = ref({})
const slots = ref([])
const remark = ref('')
const error = ref('')
const conflict = ref('')
const ok = ref(false)
const busy = ref(false)
const done = ref(false)
const loading = ref(true)
const options = ref(['周一晚', '周二晚', '周三晚', '周四晚', '周五晚'])
const courses = ref([])

const jobId = () => Number(route.query.jobId || route.params.id || 0)

function parseSlots(s) {
  if (!s) return ['周一晚', '周二晚', '周三晚', '周四晚', '周五晚']
  const arr = String(s).split(/[,，]/).map((x) => x.trim()).filter(Boolean)
  return arr.length ? arr : ['周一晚', '周二晚', '周三晚', '周四晚', '周五晚']
}

function clashOf(s) {
  const hit = courses.value.find((c) => {
    const weekday = '周' + '一二三四五六日'[Math.max(0, Math.min(6, c.weekday - 1))]
    return s.includes(weekday) && s.includes(c.slot)
  })
  return hit ? `《${hit.course}》` : ''
}

function toggle(s) {
  if (slots.value.includes(s)) {
    slots.value = slots.value.filter((x) => x !== s)
  } else {
    slots.value = [...slots.value, s]
  }
  conflict.value = ''
}

onMounted(async () => {
  if (!uid()) {
    router.push('/login')
    return
  }
  if (!jobId()) {
    error.value = '未指定岗位，请从大厅进入'
    loading.value = false
    return
  }
  try {
    const [j, p, t] = await Promise.all([
      jobOne(jobId()),
      api.get('/users', { params: { current: 1, size: 100 } }).catch(() => ({})),
      myTimetable(uid()).catch(() => [])
    ])
    job.value = j
    options.value = parseSlots(j.recruitSlots || j.recruit_slots)
    courses.value = Array.isArray(t) ? t : []
    const list = p.records ?? p ?? []
    me.value = list.find((u) => String(u.id) === String(uid())) || {}
  } catch (e) {
    error.value = e.response?.data?.msg || e.message
  } finally {
    loading.value = false
  }
})

async function submit() {
  error.value = ''
  conflict.value = ''
  if (busy.value || done.value) return
  if (slots.value.length < 2) {
    error.value = '至少选择2个可到岗时段'
    return
  }
  const clash = slots.value.filter((s) => clashOf(s))
  if (clash.length) {
    conflict.value = clash.map((s) => `${s}${clashOf(s)}`).join('、') + '，请取消红色时段'
    return
  }
  if (remark.value.length > 50) {
    error.value = '备注请控制在50字内'
    return
  }
  busy.value = true
  try {
    await submitApply({ studentId: uid(), jobId: jobId(), slots: slots.value, remark: remark.value })
    ok.value = true
    done.value = true
    showToast('提交成功')
    setTimeout(() => router.push('/applies'), 900)
  } catch (e) {
    const msg = e.response?.data?.msg || e.message
    if (e.code === 403 || /冲突/.test(msg)) {
      conflict.value = msg.replace(/^与课程冲突：?/, '')
    } else {
      error.value = msg
    }
  } finally {
    busy.value = false
  }
}
</script>

<style scoped>
.hallmark { padding: var(--space-md); padding-bottom: 150px; }
.hero { background: var(--color-ink); color: var(--color-paper-2); border-radius: var(--radius); padding: var(--space-lg) var(--space-md); margin-bottom: var(--space-sm); }
.brand { display: inline-block; font-size: 12px; background: var(--color-accent); color: var(--color-accent-ink); border-radius: 999px; padding: 3px 10px; margin-bottom: var(--space-sm); }
.hero h2 { font-family: var(--font-display); font-style: normal; font-size: 22px; margin: 0 0 4px; overflow-wrap: anywhere; }
.hero .sub { font-size: 12px; opacity: 0.75; margin: 0; }
.card { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); }
.card h3 { font-family: var(--font-display); font-style: normal; font-size: 16px; margin: 0 0 6px; }
.card .sub { font-size: 13px; color: var(--color-muted); margin: 0; }
.sec-top { display: flex; justify-content: space-between; align-items: center; }
.count { font-size: 13px; color: var(--color-success); }
.count.bad { color: var(--color-danger); }
.slot-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; margin-top: 8px; }
.slot { border: 1px solid var(--color-line); border-radius: var(--radius); background: var(--color-paper); padding: 12px 4px; font-size: 14px; color: var(--color-ink); }
.slot.on { border-color: var(--color-accent); background: var(--color-accent); color: var(--color-accent-ink); font-weight: 700; }
.slot.clash { border-color: var(--color-danger); color: var(--color-danger); }
.slot.clash.on { background: var(--color-danger); border-color: var(--color-danger); color: var(--color-paper-2); }
.clash-tip { display: block; font-size: 10px; font-weight: 400; }
.slot:focus-visible { outline: 3px solid var(--color-accent); outline-offset: 2px; }
.tip { color: var(--color-muted); font-size: 12px; }
.center { text-align: center; }
.conflict { color: var(--color-danger); font-size: 14px; background: var(--color-paper-2); border: 1px solid var(--color-danger); border-radius: var(--radius); padding: 10px 12px; }
.err { color: var(--color-danger); font-size: 13px; }
.ok { color: var(--color-success); font-size: 13px; }
.sticky { position: fixed; left: 0; right: 0; bottom: 50px; padding: var(--space-sm) var(--space-md); background: var(--color-paper); border-top: 1px solid var(--color-line); max-width: 560px; margin: 0 auto; }
</style>
