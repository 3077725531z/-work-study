<template>
  <div class="page hallmark">
    <header class="hero">
      <div>
        <span class="brand">申诉工单</span>
        <h2>有问题就申诉</h2>
        <p class="sub">工资/考勤/公示异议统一工单 · 3个工作日回复</p>
      </div>
    </header>

    <section class="card">
      <van-field :model-value="typeText(form.type)" label="类型" readonly @click="pick = true" />
      <van-popup v-model:show="pick" position="bottom">
        <van-picker
          :columns="[{ text: '工资', value: 'salary' }, { text: '考勤', value: 'attendance' }, { text: '公示', value: 'publicity' }]"
          @confirm="onPick"
          @cancel="pick = false"
        />
      </van-popup>
      <van-field v-model="form.content" label="内容" type="textarea" placeholder="写清日期和原因，≥10字" />
      <p v-if="err" class="err">{{ err }}</p>
      <p v-if="ok" class="ok">已提交，请等回复</p>
      <van-button type="primary" block round :loading="busy" @click="submit">提交申诉</van-button>
    </section>

    <h3 class="sec">我的工单</h3>
    <van-empty v-if="!rows.length" description="暂无工单" />
    <article v-for="a in rows" :key="a.id" class="rec">
      <b>{{ typeText(a.type) }}</b>
      <van-tag :type="a.status === '已处理' ? 'success' : 'warning'" round>{{ a.status }}</van-tag>
      <span class="sub">{{ a.content }}</span>
      <span v-if="a.reply" class="sub">回复：{{ a.reply }}</span>
    </article>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import api from '../api/request'
import { appeal, uid } from '../api'

const router = useRouter()
const route = useRoute()

const form = ref({ type: 'salary', content: '' })
const rows = ref([])
const pick = ref(false)
const err = ref('')
const ok = ref(false)
const busy = ref(false)

function typeText(t) {
  return t === 'salary' ? '工资' : t === 'attendance' ? '考勤' : t === 'publicity' ? '公示' : t
}

function onPick({ selectedOptions }) {
  form.value.type = selectedOptions[0].value
  pick.value = false
}

async function load() {
  if (route.query.type) {
    form.value.type = String(route.query.type)
  }
  if (!uid()) {
    router.push('/login')
    return
  }
  try {
    const p = await api.get('/appeals/list', { params: { current: 1, size: 20, studentId: uid() } })
    const list = p.records ?? p ?? []
    const mine = uid()
    rows.value = Array.isArray(list) ? list.filter((a) => String(a.studentId) === String(mine)) : []
  } catch {
    rows.value = []
  }
}

async function submit() {
  err.value = ''
  ok.value = false
  if (!uid()) {
    router.push('/login')
    return
  }
  if ((form.value.content || '').trim().length < 10) {
    err.value = '申诉内容至少10字'
    return
  }
  busy.value = true
  try {
    await appeal({ studentId: uid(), ...form.value })
    ok.value = true
    showToast('提交成功')
    form.value.content = ''
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
.hero h2 { font-family: var(--font-display); font-style: normal; font-size: 22px; margin: 0 0 4px; }
.hero .sub { font-size: 12px; opacity: 0.75; margin: 0; }
.card { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); display: grid; gap: var(--space-sm); }
.sec { font-family: var(--font-display); font-style: normal; font-size: 17px; margin: var(--space-md) 0 var(--space-sm); }
.rec { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: 10px 12px; margin-top: 8px; display: grid; gap: 4px; }
.rec .sub { color: var(--color-muted); font-size: 13px; }
.err { color: var(--color-danger); font-size: 13px; }
.ok { color: var(--color-success); font-size: 13px; }
</style>
