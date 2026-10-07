<template>
  <div class="page hallmark">
    <header class="hero">
      <div>
        <span class="brand">工资 · {{ month }}</span>
        <h2>应发 ¥{{ total }}</h2>
        <p class="sub">{{ hoursTotal }}h · {{ statusText }} · 次月10日前发放</p>
      </div>
    </header>

    <section class="formula">
      <b>应发是怎么算的？</b>
      <p>应发 net ＝ 工时 hours × 单价 rate − 扣款 deduct。其中：工时来自部门确认的考勤汇总；单价取岗位发布时的薪资档快照，调薪不影响历史月份；扣款为缺勤/违规扣减，无则为 0。例：32h × 25元 − 0 ＝ ¥800。</p>
    </section>

    <van-tabs v-model:active="tab" class="hk-tabs">
      <van-tab title="账单" name="list" />
      <van-tab title="核算器" name="calc" />
      <van-tab title="申诉" name="appeal" />
    </van-tabs>

    <div v-if="tab === 'list'">
      <van-empty v-if="!rows.length" description="暂无工资记录" />
      <article v-for="s in rows" :key="s.id" class="bill">
        <div class="top">
          <b>{{ s.month }}</b>
          <van-tag :type="s.status === '已发放' ? 'success' : s.status === '已复核' ? 'primary' : 'warning'" round>{{ s.status }}</van-tag>
        </div>
        <div class="meta">工时{{ s.hours }}h × {{ s.rate }}元 · 扣{{ s.deduct ?? 0 }} · 应发¥{{ s.net }}</div>
        <div class="meta">银行卡尾号{{ s.bankTail ?? '—' }}</div>
        <van-button
          v-if="s.status === '待确认'"
          size="small"
          round
          type="primary"
          style="margin-top:8px"
          @click="openConfirm(s)"
        >
          核对并确认
        </van-button>
        <div v-else class="meta">已确认，不可修改；异议请走申诉</div>
      </article>
    </div>

    <div v-if="tab === 'calc'" class="panel">
      <van-field v-model="hours" label="工时" type="number" />
      <van-field v-model="rate" label="单价" type="number" />
      <van-field v-model="deduct" label="扣款" type="number" />
      <van-button type="primary" block round @click="doCalc">核算 net=hours×rate−deduct</van-button>
      <div v-if="calc" class="result">应发 ¥{{ calc.net }}</div>
    </div>

    <div v-if="tab === 'appeal'" class="panel">
      <van-field v-model="form.content" label="申诉" type="textarea" placeholder="如某日工时有误，写清日期和原因，≥10字" />
      <van-button type="primary" block round @click="submitAppeal">提交申诉</van-button>
      <p class="tip">3个工作日内回复，申诉期间工资状态冻结</p>
    </div>

    <p v-if="err" class="err">{{ err }}</p>

    <van-popup v-model:show="showCf" position="bottom" round class="cf-pop">
      <div class="cf">
        <h3>工时详情确认</h3>
        <van-cell title="月份" :value="cur.month" />
        <van-cell title="工时" :value="`${cur.hours}h`" />
        <van-cell title="单价" :value="`${cur.rate}元`" />
        <van-cell title="扣款" :value="`¥${cur.deduct ?? 0}`" />
        <van-cell title="应发 net=hours×rate−deduct" :value="`¥${cur.net}`" />
        <div class="terms">
          <b>确认条款（必读）</b>
          <p>1. 本人确认上述工时与考勤记录一致；2. 确认后该月工时锁定，不可修改；3. 有异议须先申诉，确认即视为放弃当月异议；4. 工资次月10日前发放到绑定银行卡。</p>
        </div>
        <van-checkbox v-model="agreed">我已阅读并同意上述条款</van-checkbox>
        <div class="cf-btns">
          <van-button round block @click="showCf = false">再想想</van-button>
          <van-button round block type="primary" :loading="cfBusy" :disabled="!agreed" @click="doConfirm">确认无误</van-button>
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
import { calcSalary, appeal, uid } from '../api'

const router = useRouter()

const tab = ref('list')
const rows = ref([])
const err = ref('')
const hours = ref('32')
const rate = ref('25')
const deduct = ref('0')
const calc = ref(null)
const form = ref({ type: 'salary', content: '' })
const showCf = ref(false)
const cur = ref({})
const agreed = ref(false)
const cfBusy = ref(false)

const total = computed(() => rows.value.reduce((s, r) => s + Number(r.net ?? 0), 0))
const month = computed(() => rows.value[0]?.month ?? '—')
const statusText = computed(() => rows.value[0]?.status ?? '暂无')
const hoursTotal = computed(() => rows.value.reduce((s, r) => s + Number(r.hours ?? 0), 0))

async function load() {
  if (!uid()) return
  try {
    const p = await api.get('/salaries/list', { params: { current: 1, size: 24, studentId: uid() } })
    const list = p.records ?? p ?? []
    const mine = uid()
    rows.value = Array.isArray(list) ? list.filter((s) => String(s.studentId) === String(mine)) : []
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  }
}

async function doCalc() {
  try {
    calc.value = await calcSalary({ hours: hours.value, rate: rate.value, deduct: deduct.value })
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  }
}

async function submitAppeal() {
  if ((form.value.content || '').trim().length < 10) {
    err.value = '申诉内容至少10字'
    return
  }
  await appeal({ studentId: uid(), ...form.value })
  showToast('申诉已提交')
  form.value.content = ''
}

function openConfirm(s) {
  cur.value = s
  agreed.value = false
  showCf.value = true
}

async function doConfirm() {
  if (!agreed.value) return
  cfBusy.value = true
  try {
    await api.post(`/salaries/${cur.value.id}:confirm`)
    cur.value.status = '已复核'
    showCf.value = false
    showToast('已确认，待部门复核发放')
    await load()
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  } finally {
    cfBusy.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.hallmark { padding: var(--space-md); padding-bottom: calc(var(--space-xl) + 50px); }
.hero { background: var(--color-ink); color: var(--color-paper-2); border-radius: var(--radius); padding: var(--space-lg) var(--space-md); display: flex; justify-content: space-between; align-items: flex-start; gap: var(--space-sm); margin-bottom: var(--space-sm); }
.brand { display: inline-block; font-size: 12px; background: var(--color-accent); color: var(--color-accent-ink); border-radius: 999px; padding: 3px 10px; margin-bottom: var(--space-sm); }
.hero h2 { font-family: var(--font-display); font-style: normal; font-size: 26px; margin: 0 0 4px; }
.hero .sub { font-size: 12px; opacity: 0.75; margin: 0; }
.hk-tabs { border: 1px solid var(--color-line); border-radius: var(--radius); overflow: hidden; background: var(--color-paper-2); }
.formula { background: var(--color-paper); border: 1px dashed var(--color-accent); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); }
.formula b { font-size: 14px; }
.formula p { font-size: 13px; color: var(--color-muted); margin: 6px 0 0; }
.bill { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); }
.bill .top { display: flex; justify-content: space-between; align-items: center; }
.meta { color: var(--color-muted); font-size: 12px; margin-top: 4px; }
.panel { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); display: grid; gap: var(--space-sm); }
.result { font-family: var(--font-display); font-size: 22px; color: var(--color-accent); text-align: center; }
.tip { color: var(--color-muted); font-size: 12px; }
.err { color: var(--color-danger); font-size: 13px; }
.cf-pop { max-width: 560px; }
.cf { padding: var(--space-md); display: grid; gap: var(--space-sm); }
.cf h3 { font-family: var(--font-display); font-style: normal; margin: 0; }
.terms { background: var(--color-paper); border-radius: 8px; padding: 10px 12px; font-size: 13px; }
.terms p { margin: 6px 0 0; color: var(--color-muted); }
.cf-btns { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
</style>
