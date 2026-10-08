<template>
  <div class="page hallmark">
    <header class="hero">
      <div>
        <span class="brand">通知 · {{ unread }}条未读</span>
        <h2>别错过重要消息</h2>
        <p class="sub">录用公示 · 考勤提醒 · 工资到账</p>
      </div>
      <van-button size="small" round plain @click="reload">刷新</van-button>
    </header>

    <van-tabs v-model:active="tab" class="hk-tabs">
      <van-tab title="全部" name="" />
      <van-tab title="未读" name="unread" />
      <van-tab title="公示" name="pub" />
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="reload">
      <van-empty v-if="!filtered.length && !loading" description="暂无通知" />
      <article
        v-for="p in filtered"
        :key="p.id"
        :class="['note', { seen: isRead(p.id) }]"
        @click="openDetail(p)"
      >
        <span :class="['dot', { on: !isRead(p.id) }]" />
        <div class="body">
          <div class="top">
            <b>{{ p.title }}</b>
            <van-tag :type="isRead(p.id) ? 'default' : 'danger'" round size="mini">
              {{ isRead(p.id) ? '已读' : '未读' }}
            </van-tag>
          </div>
          <div class="sub">{{ p.period || fmtTime(p.createTime) }}</div>
          <div class="summary">{{ summaryOf(p) }}</div>
          <div class="go">查看详情 ›</div>
        </div>
      </article>
    </van-pull-refresh>
    <p v-if="err" class="err">{{ err }}</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { pubList, pubRead } from '../api'

const router = useRouter()

const rows = ref([])
const tab = ref('')
const err = ref('')
const loading = ref(false)
const refreshing = ref(false)
const readIds = ref(new Set(JSON.parse(localStorage.getItem('read-pubs') || '[]')))

const unread = computed(() => rows.value.filter((p) => !isRead(p.id)).length)

const filtered = computed(() => {
  if (tab.value === 'unread') return rows.value.filter((p) => !isRead(p.id))
  if (tab.value === 'pub') return rows.value
  return rows.value
})

function isRead(id) {
  return readIds.value.has(Number(id)) || readIds.value.has(String(id))
}

function fmtTime(t) {
  return t ? String(t).slice(0, 10) : ''
}

function summaryOf(p) {
  const text = humanize(p.content)
  return text.length > 60 ? text.slice(0, 60) + '…' : (text || '点击查看完整名单与公示期')
}

function humanize(content) {
  if (!content) return ''
  if (typeof content !== 'string') {
    return flatten(content)
  }
  try {
    return flatten(JSON.parse(content))
  } catch {
    return content
  }
}

function flatten(c) {
  if (typeof c === 'string') return c
  if (Array.isArray(c)) return c.map(flatten).join('；')
  if (typeof c === 'object') {
    const jobs = c.jobs ?? c.list ?? []
    if (Array.isArray(jobs) && jobs.length) {
      return jobs.map((j) => `${j.title || j.job || '岗位'}：${j.names || j.who || ''}`).join('；')
    }
    return Object.values(c).map(flatten).join('；')
  }
  return String(c ?? '')
}

async function openDetail(p) {
  if (!isRead(p.id)) {
    try {
      await pubRead(p.id)
      readIds.value.add(Number(p.id))
      localStorage.setItem('read-pubs', JSON.stringify([...readIds.value]))
    } catch { /* 后端可能是桩，忽略错误 */ }
  }
  router.push(`/pubs/${p.id}`)
}

async function reload() {
  err.value = ''
  loading.value = true
  try {
    rows.value = await pubList()
    readIds.value = new Set(JSON.parse(localStorage.getItem('read-pubs') || '[]'))
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

onMounted(reload)
</script>

<style scoped>
.hallmark { padding: var(--space-md); padding-bottom: calc(var(--space-xl) + 50px); }
.hero { background: var(--color-ink); color: var(--color-paper-2); border-radius: var(--radius); padding: var(--space-lg) var(--space-md); display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-sm); margin-bottom: var(--space-sm); }
.brand { display: inline-block; font-size: 12px; background: var(--color-accent); color: var(--color-accent-ink); border-radius: 999px; padding: 3px 10px; margin-bottom: var(--space-sm); }
.hero h2 { font-family: var(--font-display); font-style: normal; font-size: 22px; margin: 0 0 4px; }
.hero .sub { font-size: 12px; opacity: 0.75; margin: 0; }
.hk-tabs { border: 1px solid var(--color-line); border-radius: var(--radius); overflow: hidden; background: var(--color-paper-2); }
.note { display: flex; gap: 10px; background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); }
.note.seen { opacity: 0.75; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: var(--color-line); margin-top: 6px; flex-shrink: 0; }
.dot.on { background: var(--color-danger); }
.body { flex: 1; min-width: 0; display: grid; gap: 4px; }
.top { display: flex; justify-content: space-between; align-items: center; gap: var(--space-sm); }
.top b { font-size: 15px; overflow-wrap: anywhere; }
.sub { color: var(--color-muted); font-size: 12px; }
.summary { font-size: 13px; overflow-wrap: anywhere; }
.go { color: var(--color-accent); font-size: 13px; }
.err { color: var(--color-danger); font-size: 13px; }
</style>
