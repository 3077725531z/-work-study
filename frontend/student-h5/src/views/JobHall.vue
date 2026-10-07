<template>
  <div class="page hallmark">
    <header class="hero">
      <div>
        <span class="brand">勤工助学 · 岗位大厅</span>
        <h2>找一份不误课的兼职</h2>
        <p class="sub">困难生优先 · 有课自动避让 · 工资按月发放</p>
      </div>
      <van-button size="small" round icon="bell" to="/notices">通知</van-button>
    </header>

    <van-search v-model="key" placeholder="搜索：图书馆 / 助理 / 晚班" @search="load" class="hk-search" />

    <div class="chips">
      <van-tag
        v-for="c in cats"
        :key="c"
        :plain="cat !== c"
        :type="cat === c ? 'primary' : 'default'"
        round
        size="large"
        @click="pickCat(c)"
      >
        {{ c }}
      </van-tag>
    </div>

    <van-dropdown-menu class="hk-menu">
      <van-dropdown-item v-model="sort" :options="sortOpts" @change="load" />
      <van-dropdown-item v-model="status" :options="statusOpts" @change="load" />
    </van-dropdown-menu>

    <van-pull-refresh v-model="refreshing" @refresh="reload">
      <van-empty v-if="!rows.length && !loading" description="暂无岗位，换个条件试试" />
      <article v-for="j in rows" :key="j.id" class="job-card" @click="$router.push(`/jobs/${j.id}`)">
        <div class="job-top">
          <b class="job-title">{{ j.title }}</b>
          <span class="pay">{{ j.payAmount ?? '' }}元/时</span>
        </div>
        <div class="meta">{{ j.place || '' }} · {{ j.workTime || '' }}</div>
        <div class="foot">
          <van-tag plain type="primary">剩{{ j.headcount }}人</van-tag>
          <van-tag :type="j.status === '招募中' ? 'success' : 'default'">{{ j.status }}</van-tag>
          <span class="more">详情 ›</span>
        </div>
      </article>
    </van-pull-refresh>
    <p v-if="err" class="err">{{ err }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { jobPage } from '../api'

const key = ref('')
const cat = ref('全部')
const cats = ['全部', '图书馆', '行政', '实验室']
const sort = ref('recommend')
const sortOpts = [
  { text: '推荐', value: 'recommend' },
  { text: '最新', value: 'new' },
  { text: '高薪', value: 'pay' }
]
const status = ref('招募中')
const statusOpts = [
  { text: '招募中', value: '招募中' },
  { text: '全部', value: '' }
]
const rows = ref([])
const err = ref('')
const loading = ref(false)
const refreshing = ref(false)

function pickCat(c) {
  cat.value = c
  load()
}

async function load() {
  err.value = ''
  loading.value = true
  try {
    const page = await jobPage({
      current: 1,
      size: 20,
      key: (key.value || (cat.value === '全部' ? '' : cat.value)) || undefined,
      status: status.value || undefined
    })
    let list = page.records ?? page ?? []
    if (sort.value === 'pay') {
      list = [...list].sort((a, b) => (b.payAmount ?? 0) - (a.payAmount ?? 0))
    }
    rows.value = list
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  } finally {
    loading.value = false
  }
}

async function reload() {
  await load()
  refreshing.value = false
}

load()
</script>

<style scoped>
.hallmark { padding: var(--space-md); padding-bottom: calc(var(--space-xl) + 50px); }
.hero { background: var(--color-ink); color: var(--color-paper-2); border-radius: var(--radius); padding: var(--space-lg) var(--space-md); display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-sm); margin-bottom: var(--space-md); }
.brand { display: inline-block; font-size: 12px; background: var(--color-accent); color: var(--color-accent-ink); border-radius: 999px; padding: 3px 10px; margin-bottom: var(--space-sm); }
.hero h2 { font-family: var(--font-display); font-style: normal; font-size: 24px; margin: 0 0 4px; }
.hero .sub { font-size: 12px; opacity: 0.75; margin: 0; }
.hk-search { border: 1px solid var(--color-line); border-radius: var(--radius); overflow: hidden; }
.chips { display: flex; gap: 8px; margin: 10px 0; flex-wrap: wrap; }
.hk-menu { border: 1px solid var(--color-line); border-radius: var(--radius); overflow: hidden; margin-bottom: var(--space-sm); }
.job-card { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); }
.job-top { display: flex; justify-content: space-between; align-items: center; gap: var(--space-sm); }
.job-title { font-size: 16px; overflow-wrap: anywhere; }
.pay { color: var(--color-accent); font-weight: 700; white-space: nowrap; }
.meta { color: var(--color-muted); font-size: 12px; margin-top: 4px; }
.foot { display: flex; gap: 8px; align-items: center; margin-top: 8px; }
.more { margin-left: auto; color: var(--color-accent); font-size: 13px; }
.err { color: var(--color-danger); font-size: 13px; }
</style>
