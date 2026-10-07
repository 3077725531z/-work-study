<template>
  <div class="page hallmark">
    <header class="hero">
      <div>
        <span class="brand">申请进度</span>
        <h2>{{ statText }}</h2>
        <p class="sub">提交 → 部门审核 → 录用公示，全程可追踪</p>
      </div>
      <van-button size="small" round type="primary" to="/jobs">去大厅</van-button>
    </header>

    <van-tabs v-model:active="tab" @change="filter" sticky class="hk-tabs">
      <van-tab title="全部" name="" />
      <van-tab title="待审核" name="待审核" />
      <van-tab title="已通过" name="已通过" />
      <van-tab title="未通过" name="未通过" />
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="reload">
      <van-empty v-if="!rows.length && !loading" description="暂无申请，去大厅看看" />
      <article v-for="a in rows" :key="a.id" class="apply-card">
        <div class="top">
          <b>{{ a.jobTitle || `岗位${a.jobId}` }}</b>
          <van-tag :type="tagType(a.status)" round>{{ a.status }}</van-tag>
        </div>
        <div class="meta">{{ a.code || '' }} · {{ fmtTime(a.createTime) }}提交</div>
        <van-steps :active="stepOf(a.status)" class="steps">
          <van-step>提交</van-step>
          <van-step>部门审核</van-step>
          <van-step>录用公示</van-step>
        </van-steps>
        <div class="step-tip">{{ stepText(a.status) }}</div>
        <div v-if="a.deptComment" class="comment">评语：{{ a.deptComment }}</div>
        <div class="actions">
          <van-button v-if="a.status === '待审核'" size="small" round @click="withdraw(a)">撤回</van-button>
          <van-button v-if="a.status === '未通过'" size="small" round type="primary" :to="`/jobs/${a.jobId}`">重新申请</van-button>
          <van-button v-if="a.status === '已通过'" size="small" round type="primary" to="/attend">去打卡</van-button>
        </div>
      </article>
    </van-pull-refresh>
    <p v-if="err" class="err">{{ err }}</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast } from 'vant'
import { myApplies, withdrawApply, jobOne } from '../api'

const tab = ref('')
const all = ref([])
const rows = ref([])
const err = ref('')
const loading = ref(false)
const refreshing = ref(false)

const statText = computed(() => {
  const n = (s) => all.value.filter((a) => a.status === s).length
  return `共${all.value.length} · 待审${n('待审核')} · 通过${n('已通过')}`
})

function tagType(s) {
  return s === '已通过' ? 'success' : s === '未通过' ? 'danger' : 'warning'
}

function stepOf(s) {
  if (s === '已通过') return 2
  return 1
}

function stepText(s) {
  if (s === '已通过') return '部门已通过，等待公示上岗'
  if (s === '未通过') return '未通过，可看评语后重申'
  return '已提交，等待部门审核（非公示）'
}

function fmtTime(t) {
  return t ? String(t).slice(0, 10) : ''
}

async function load() {
  err.value = ''
  loading.value = true
  try {
    const list = await myApplies()
    all.value = Array.isArray(list) ? list : (list.records ?? [])
    for (const a of all.value) {
      if (a.jobId) {
        jobOne(a.jobId).then((j) => { a.jobTitle = j.title }).catch(() => {})
      }
    }
    filter()
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  } finally {
    loading.value = false
  }
}

function filter() {
  rows.value = tab.value ? all.value.filter((a) => a.status === tab.value) : [...all.value]
}

async function reload() {
  await load()
  refreshing.value = false
}

async function withdraw(a) {
  try {
    await withdrawApply(a.id)
    showToast('已撤回')
    await load()
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  }
}

onMounted(load)
</script>

<style scoped>
.hallmark { padding: var(--space-md); padding-bottom: calc(var(--space-xl) + 50px); }
.hero { background: var(--color-ink); color: var(--color-paper-2); border-radius: var(--radius); padding: var(--space-lg) var(--space-md); display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-sm); margin-bottom: var(--space-sm); }
.brand { display: inline-block; font-size: 12px; background: var(--color-accent); color: var(--color-accent-ink); border-radius: 999px; padding: 3px 10px; margin-bottom: var(--space-sm); }
.hero h2 { font-family: var(--font-display); font-style: normal; font-size: 22px; margin: 0 0 4px; }
.hero .sub { font-size: 12px; opacity: 0.75; margin: 0; }
.hk-tabs { border: 1px solid var(--color-line); border-radius: var(--radius); overflow: hidden; background: var(--color-paper-2); }
.apply-card { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); }
.top { display: flex; justify-content: space-between; align-items: center; gap: var(--space-sm); }
.meta { color: var(--color-muted); font-size: 12px; margin-top: 4px; }
.steps { margin-top: var(--space-sm); }
.step-tip { font-size: 12px; color: var(--color-muted); margin-top: 4px; }
.comment { font-size: 13px; margin-top: 6px; background: var(--color-paper); border-radius: 8px; padding: 8px; }
.actions { margin-top: 8px; display: flex; gap: 8px; }
.err { color: var(--color-danger); font-size: 13px; }
</style>
