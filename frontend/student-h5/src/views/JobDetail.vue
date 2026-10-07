<template>
  <div class="page hallmark">
    <van-skeleton :loading="loading" :row="5">
      <header class="hero">
        <div>
          <span class="brand">{{ job.code || '岗位' }} · {{ job.status || '' }}</span>
          <h2>{{ job.title || '岗位详情' }}</h2>
          <p class="sub">{{ job.place || '' }} · {{ job.workTime || '' }}</p>
        </div>
        <div class="paybox">
          <b>{{ job.payAmount ?? '—' }}</b><span>元/时</span>
        </div>
      </header>

      <section class="card">
        <h3>工作内容</h3>
        <p>{{ job.content || '暂无介绍' }}</p>
      </section>

      <section class="card">
        <h3>岗位要求</h3>
        <p>{{ job.requirement || '暂无要求' }}</p>
      </section>

      <section class="card tip-card">
        <h3>须知</h3>
        <p>考试周提前3天请假不计缺勤 · 工资次月10日前发放 · 有课时段不可排班</p>
      </section>
    </van-skeleton>

    <p v-if="err" class="err">{{ err }}</p>

    <div class="sticky">
      <van-button type="primary" block round :disabled="job.status && job.status !== '招募中'" @click="go">
        {{ job.status === '招募中' || !job.status ? '立即申请' : `该岗位${job.status}，暂不可申请` }}
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { jobOne } from '../api'

const route = useRoute()
const router = useRouter()
const job = ref({})
const err = ref('')
const loading = ref(true)

onMounted(async () => {
  try {
    job.value = await jobOne(route.params.id)
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  } finally {
    loading.value = false
  }
})

function go() {
  router.push(`/apply?jobId=${route.params.id}`)
}
</script>

<style scoped>
.hallmark { padding: var(--space-md); padding-bottom: 110px; }
.hero { background: var(--color-ink); color: var(--color-paper-2); border-radius: var(--radius); padding: var(--space-lg) var(--space-md); display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-sm); margin-bottom: var(--space-sm); }
.brand { display: inline-block; font-size: 12px; background: var(--color-accent); color: var(--color-accent-ink); border-radius: 999px; padding: 3px 10px; margin-bottom: var(--space-sm); }
.hero h2 { font-family: var(--font-display); font-style: normal; font-size: 24px; margin: 0 0 4px; overflow-wrap: anywhere; }
.hero .sub { font-size: 12px; opacity: 0.75; margin: 0; }
.paybox { text-align: center; flex-shrink: 0; }
.paybox b { font-family: var(--font-display); font-size: 30px; color: var(--color-accent); display: block; }
.paybox span { font-size: 12px; opacity: 0.75; }
.card { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); }
.card h3 { font-family: var(--font-display); font-style: normal; font-size: 16px; margin: 0 0 6px; }
.card p { font-size: 14px; margin: 0; }
.tip-card { background: var(--color-paper); }
.sticky { position: fixed; left: 0; right: 0; bottom: 50px; padding: var(--space-sm) var(--space-md); background: var(--color-paper); border-top: 1px solid var(--color-line); max-width: 560px; margin: 0 auto; }
.err { color: var(--color-danger); font-size: 13px; }
</style>
