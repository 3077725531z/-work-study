<template>
  <div class="page hallmark">
    <header class="hero">
      <div>
        <span class="brand">请假</span>
        <h2>有事提前请假</h2>
        <p class="sub">考试周/病假提前24小时 · 批准后不计缺勤</p>
      </div>
    </header>

    <section class="card">
      <van-field
        v-model="jobText"
        label="岗位"
        readonly
        placeholder="选择请假岗位"
        @click="showJobs = true"
      />
      <van-popup v-model:show="showJobs" position="bottom">
        <van-picker
          :columns="jobs.map((j) => ({ text: j.title || `岗位${j.jobId}`, value: j.jobId }))"
          @confirm="onJob"
          @cancel="showJobs = false"
        />
      </van-popup>
      <van-field v-model="form.type" label="类型" readonly @click="pick = true" />
      <van-popup v-model:show="pick" position="bottom">
        <van-picker
          :columns="[{ text: '事假', value: '事假' }, { text: '病假', value: '病假' }, { text: '考试周', value: '考试周' }]"
          @confirm="onPick"
          @cancel="pick = false"
        />
      </van-popup>
      <van-field v-model="form.startDate" label="开始" type="date" />
      <van-field v-model="form.endDate" label="结束" type="date" />
      <van-field v-model="form.reason" label="原因" type="textarea" placeholder="≥10字" />
      <p v-if="err" class="err">{{ err }}</p>
      <p v-if="ok" class="ok">已提交，待部门审批</p>
      <van-button type="primary" block round :loading="busy" @click="submit">提交</van-button>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { leave, uid, myApplies, jobOne } from '../api'

const router = useRouter()
const form = reactive({ studentId: uid(), jobId: null, type: '事假', startDate: '', endDate: '', reason: '' })
const jobs = ref([])
const jobText = ref('')
const showJobs = ref(false)
const pick = ref(false)
const err = ref('')
const ok = ref(false)
const busy = ref(false)

function onPick({ selectedOptions }) {
  form.type = selectedOptions[0].value
  pick.value = false
}

function onJob({ selectedOptions }) {
  form.jobId = selectedOptions[0].value
  jobText.value = selectedOptions[0].text
  showJobs.value = false
}

onMounted(async () => {
  if (!uid()) {
    router.push('/login')
    return
  }
  form.studentId = uid()
  try {
    const list = await myApplies()
    const apps = Array.isArray(list) ? list : (list.records ?? [])
    const passed = apps.filter((a) => a.status === '已通过')
    const source = passed.length ? passed : []
    for (const a of source) {
      if (!a.jobTitle && a.jobId) {
        try {
          const j = await jobOne(a.jobId)
          a.jobTitle = j.title
        } catch {
          a.jobTitle = `岗位${a.jobId}`
        }
      }
    }
    jobs.value = source
    if (jobs.value.length) {
      form.jobId = jobs.value[0].jobId
      jobText.value = jobs.value[0].jobTitle || `岗位${jobs.value[0].jobId}`
    }
  } catch {
    jobs.value = []
  }
})

async function submit() {
  err.value = ''
  ok.value = false
  if (!uid()) {
    router.push('/login')
    return
  }
  form.studentId = uid()
  if (!form.jobId) {
    err.value = '暂无在岗岗位，须申请通过后才可请假'
    return
  }
  if (!form.startDate || !form.endDate) {
    err.value = '请选择起止日期'
    return
  }
  if ((form.reason || '').trim().length < 10) {
    err.value = '原因至少10字'
    return
  }
  busy.value = true
  try {
    await leave({ ...form })
    ok.value = true
    showToast('提交成功')
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  } finally {
    busy.value = false
  }
}
</script>

<style scoped>
.hallmark { padding: var(--space-md); padding-bottom: calc(var(--space-xl) + 50px); }
.hero { background: var(--color-ink); color: var(--color-paper-2); border-radius: var(--radius); padding: var(--space-lg) var(--space-md); margin-bottom: var(--space-sm); }
.brand { display: inline-block; font-size: 12px; background: var(--color-accent); color: var(--color-accent-ink); border-radius: 999px; padding: 3px 10px; margin-bottom: var(--space-sm); }
.hero h2 { font-family: var(--font-display); font-style: normal; font-size: 22px; margin: 0 0 4px; }
.hero .sub { font-size: 12px; opacity: 0.75; margin: 0; }
.card { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); display: grid; gap: var(--space-sm); }
.err { color: var(--color-danger); font-size: 13px; }
.ok { color: var(--color-success); font-size: 13px; }
</style>
