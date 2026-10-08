<template>
  <div class="page hallmark">
    <van-skeleton :loading="loading" :row="5">
      <header class="hero">
        <div>
          <span class="brand">录用公示 · {{ status }}</span>
          <h2>{{ title || '公示详情' }}</h2>
          <p class="sub">公示期 {{ period || '—' }} · 异议电话 62201111</p>
        </div>
      </header>

      <section class="card">
        <h3>拟录名单（脱敏）</h3>
        <div v-if="names.length">
          <div v-for="(n, i) in names" :key="i" class="name-row">
            <b>{{ n.job }}</b><span>{{ n.who }}</span>
          </div>
        </div>
        <p v-else class="sub">{{ raw || '暂无名单' }}</p>
      </section>

      <section class="card tip-card">
        <h3>公示规则</h3>
        <p>脱敏显示 · 公示期内可实名异议 · 逾期自动生效并生成在岗记录 · 须点“我已知晓”后才可打卡</p>
      </section>
    </van-skeleton>

    <p v-if="err" class="err">{{ err }}</p>
    <p v-if="ok" class="ok">已确认，打卡限制已解除</p>

    <div class="sticky">
      <van-button type="primary" block round :loading="busy" :disabled="done" @click="read">
        {{ done ? '已确认' : '我已知晓并同意' }}
      </van-button>
      <van-button block round style="margin-top:8px" @click="object">实名异议</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import api from '../api/request'
import { pubRead, uid } from '../api'

const route = useRoute()
const router = useRouter()
const title = ref('')
const period = ref('')
const status = ref('公示中')
const raw = ref('')
const names = ref([])
const err = ref('')
const ok = ref(false)
const busy = ref(false)
const loading = ref(true)

const done = computed(() => {
  const ids = JSON.parse(localStorage.getItem('read-pubs') || '[]')
  return ids.map(String).includes(String(route.params.id)) || ok.value
})

function parse(content) {
  if (!content) {
    raw.value = '暂无名单'
    names.value = []
    return
  }
  try {
    const c = typeof content === 'string' ? JSON.parse(content) : content
    if (typeof c === 'string') {
      raw.value = c
      names.value = []
      return
    }
    const jobs = c.jobs ?? c.list ?? []
    if (Array.isArray(jobs) && jobs.length) {
      names.value = jobs.map((j) => ({ job: j.title || j.job || '岗位', who: j.names || j.who || '' }))
      raw.value = ''
    } else {
      raw.value = Object.values(c).join('；')
      names.value = []
    }
  } catch {
    raw.value = String(content)
    names.value = []
  }
}

onMounted(async () => {
  try {
    const list = await api.get('/publicities')
    const arr = Array.isArray(list) ? list : []
    const p = arr.find((x) => String(x.id) === String(route.params.id))
    if (p) {
      title.value = p.title
      period.value = p.period
      status.value = p.status || '公示中'
      parse(p.content)
    } else {
      err.value = '公示不存在或已撤下'
    }
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  } finally {
    loading.value = false
  }
})

async function read() {
  err.value = ''
  if (!uid()) {
    router.push('/login')
    return
  }
  busy.value = true
  try {
    await pubRead(route.params.id)
    const ids = new Set(JSON.parse(localStorage.getItem('read-pubs') || '[]'))
    ids.add(Number(route.params.id))
    localStorage.setItem('read-pubs', JSON.stringify([...ids]))
    ok.value = true
    showToast('已确认')
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  } finally {
    busy.value = false
  }
}

function object() {
  router.push({ path: '/appeal', query: { type: 'publicity' } })
}
</script>

<style scoped>
.hallmark { padding: var(--space-md); padding-bottom: 180px; }
.hero { background: var(--color-ink); color: var(--color-paper-2); border-radius: var(--radius); padding: var(--space-lg) var(--space-md); margin-bottom: var(--space-sm); }
.brand { display: inline-block; font-size: 12px; background: var(--color-accent); color: var(--color-accent-ink); border-radius: 999px; padding: 3px 10px; margin-bottom: var(--space-sm); }
.hero h2 { font-family: var(--font-display); font-style: normal; font-size: 22px; margin: 0 0 4px; overflow-wrap: anywhere; }
.hero .sub { font-size: 12px; opacity: 0.75; margin: 0; }
.card { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); }
.card h3 { font-family: var(--font-display); font-style: normal; font-size: 16px; margin: 0 0 6px; }
.card p { font-size: 13px; margin: 0; color: var(--color-muted); }
.tip-card { background: var(--color-paper); }
.name-row { display: flex; justify-content: space-between; gap: var(--space-sm); padding: 8px 0; border-bottom: 1px solid var(--color-line); font-size: 14px; }
.name-row:last-child { border-bottom: 0; }
.sticky { position: fixed; left: 0; right: 0; bottom: 50px; padding: var(--space-sm) var(--space-md); background: var(--color-paper); border-top: 1px solid var(--color-line); max-width: 560px; margin: 0 auto; }
.err { color: var(--color-danger); font-size: 13px; }
.ok { color: var(--color-success); font-size: 13px; }
</style>
