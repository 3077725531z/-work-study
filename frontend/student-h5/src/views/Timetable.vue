<template>
  <div class="page hallmark">
    <header class="hero">
      <div>
        <span class="brand">课表闭环 · {{ semester }}</span>
        <h2>先填课表，再申岗位</h2>
        <p class="sub">教务同步暂未对接，请手动填写；支持周次批量 · 在岗自动排入</p>
      </div>
      <van-button size="small" round type="primary" @click="showAdd = true">+ 加课程</van-button>
    </header>

    <div class="legend">
      <span><i class="sw course" />有课</span>
      <span><i class="sw work" />在岗</span>
      <span><i class="sw free" />可排</span>
    </div>

    <div class="grid-wrap">
      <div class="grid-head">
        <span />
        <span v-for="d in 7" :key="d">{{ '一二三四五六日'[d - 1] }}</span>
      </div>
      <div v-for="s in slots" :key="s" class="grid-row">
        <b>{{ s }}</b>
        <span
          v-for="d in 7"
          :key="d"
          :class="['cell', clsOf(s, d)]"
          @click="openCell(s, d)"
        >
          {{ shortOf(s, d) }}
        </span>
      </div>
    </div>

    <section class="card">
      <h3>我的课程/在岗（{{ rows.length }}）</h3>
      <van-empty v-if="!rows.length" description="课表为空，先加课程才能申请" />
      <div v-for="c in rows" :key="c.id" class="line">
        <b>{{ c.course }}</b>
        <span class="sub">周{{ '一二三四五六日'[c.weekday - 1] }} {{ c.slot }} · {{ c.weeks }} · {{ c.source === 'work' ? '在岗' : '课程' }}</span>
        <van-button v-if="c.source !== 'work'" size="mini" @click="del(c)">删除</van-button>
      </div>
    </section>

    <section class="card tip-card">
      <h3>闭环规则</h3>
      <p>无课表不可申请；申请时段命中红色/在岗格按周次重叠拦截；通过后在岗时段自动排入，后续申请自动避让。</p>
    </section>

    <van-popup v-model:show="showAdd" position="bottom" round class="pop">
      <div class="form">
        <h3>{{ editTitle }}</h3>
        <van-field v-model="f.course" label="课程" placeholder="如 数据库" />
        <div class="pick-row">
          <span class="lbl">星期</span>
          <van-tag v-for="d in 7" :key="d" :plain="f.weekday !== d" type="primary" @click="f.weekday = d">
            {{ '一二三四五六日'[d - 1] }}
          </van-tag>
        </div>
        <div class="pick-row">
          <span class="lbl">节次</span>
          <van-tag v-for="s in slots" :key="s" :plain="f.slot !== s" type="primary" @click="f.slot = s">
            {{ s }}
          </van-tag>
        </div>
        <div class="pick-row">
          <span class="lbl">周次</span>
          <van-button size="mini" @click="weekAll">全选</van-button>
          <van-button size="mini" @click="weekOdd">单周</van-button>
          <van-button size="mini" @click="weekEven">双周</van-button>
          <van-button size="mini" @click="weekFirst">1-8</van-button>
          <van-button size="mini" @click="weekLast">9-16</van-button>
        </div>
        <van-checkbox-group v-model="f.weekList" direction="horizontal" class="weeks">
          <van-checkbox v-for="w in 16" :key="w" :name="w" shape="square">{{ w }}</van-checkbox>
        </van-checkbox-group>
        <p v-if="ferr" class="err">{{ ferr }}</p>
        <van-button type="primary" block round :loading="busy" @click="save">保存</van-button>
      </div>
    </van-popup>

    <p v-if="err" class="err">{{ err }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import api from '../api/request'
import { myTimetable, saveTimetableOne, syncTimetable, uid } from '../api'

const router = useRouter()
const slots = ['1-2节', '3-4节', '5-6节', '7-8节', '晚']
const rows = ref([])
const err = ref('')
const semester = ref('2026-秋')
const showAdd = ref(false)
const busy = ref(false)
const ferr = ref('')
const editTitle = ref('加课程')
const f = ref({ weekday: 1, slot: '1-2节', course: '', weekList: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16] })

function cellRows(s, d) {
  return rows.value.filter((c) => c.slot === s && Number(c.weekday) === d)
}

function clsOf(s, d) {
  const list = cellRows(s, d)
  if (list.some((c) => c.source === 'work')) return 'work'
  if (list.length) return 'busy'
  return 'free'
}

function shortOf(s, d) {
  const list = cellRows(s, d)
  if (!list.length) return ''
  return list[0].course.length > 4 ? list[0].course.slice(0, 4) : list[0].course
}

function weeksText() {
  const w = [...f.value.weekList].sort((a, b) => a - b)
  if (w.length === 16) return '1-16周'
  const odd = w.every((x) => x % 2 === 1) && w.length === 8
  const even = w.every((x) => x % 2 === 0) && w.length === 8
  if (odd) return '单周'
  if (even) return '双周'
  if (w.length === 8 && w[0] === 1 && w[7] === 8) return '1-8周'
  if (w.length === 8 && w[0] === 9 && w[7] === 16) return '9-16周'
  return w.join(',') + '周'
}

function weekAll() { f.value.weekList = Array.from({ length: 16 }, (_, i) => i + 1) }
function weekOdd() { f.value.weekList = [1, 3, 5, 7, 9, 11, 13, 15] }
function weekEven() { f.value.weekList = [2, 4, 6, 8, 10, 12, 14, 16] }
function weekFirst() { f.value.weekList = [1, 2, 3, 4, 5, 6, 7, 8] }
function weekLast() { f.value.weekList = [9, 10, 11, 12, 13, 14, 15, 16] }

function openCell(s, d) {
  editTitle.value = '加课程'
  f.value.weekday = d
  f.value.slot = s
  f.value.course = ''
  showAdd.value = true
}

async function load() {
  if (!uid()) {
    router.push('/login')
    return
  }
  try {
    rows.value = await myTimetable(uid())
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  }
}

async function save() {
  ferr.value = ''
  if (!f.value.course.trim()) {
    ferr.value = '请填写课程名'
    return
  }
  if (!f.value.weekList.length) {
    ferr.value = '至少选1周'
    return
  }
  busy.value = true
  try {
    await saveTimetableOne({
      studentId: uid(),
      semester: semester.value,
      weekday: f.value.weekday,
      slot: f.value.slot,
      course: f.value.course.trim(),
      weeks: weeksText(),
      source: 'jw'
    })
    showAdd.value = false
    showToast('已保存')
    await load()
  } catch (e) {
    ferr.value = e.response?.data?.msg || e.message
  } finally {
    busy.value = false
  }
}

async function del(c) {
  try {
    await api.delete(`/timetable/${c.id}`)
    showToast('已删除')
    await load()
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  }
}

async function sync() {
  try {
    await syncTimetable(uid())
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
.legend { display: flex; gap: 12px; font-size: 12px; color: var(--color-muted); margin-bottom: 6px; }
.sw { display: inline-block; width: 10px; height: 10px; border-radius: 3px; margin-right: 4px; }
.sw.course { background: var(--color-danger); }
.sw.work { background: var(--color-accent); }
.sw.free { background: var(--color-line); }
.grid-wrap { overflow-x: auto; }
.grid-head, .grid-row { display: grid; grid-template-columns: 44px repeat(7, minmax(0, 1fr)); gap: 4px; align-items: stretch; margin-top: 4px; text-align: center; font-size: 11px; min-width: 340px; }
.grid-head { color: var(--color-muted); }
.grid-row b { align-self: center; font-size: 11px; }
.cell { border-radius: 6px; padding: 8px 1px; border: 1px solid var(--color-line); background: var(--color-paper-2); overflow-wrap: anywhere; min-height: 34px; }
.cell.free { color: var(--color-muted); }
.cell.busy { color: var(--color-danger); border-color: var(--color-danger); }
.cell.work { color: var(--color-accent-ink); background: var(--color-accent); border-color: var(--color-accent); font-weight: 700; }
.card { background: var(--color-paper-2); border: 1px solid var(--color-line); border-radius: var(--radius); padding: var(--space-md); margin-top: var(--space-sm); }
.card h3 { font-family: var(--font-display); font-style: normal; font-size: 16px; margin: 0 0 6px; }
.card p { font-size: 13px; margin: 0; color: var(--color-muted); }
.line { display: flex; gap: 8px; align-items: center; padding: 8px 0; border-bottom: 1px solid var(--color-line); font-size: 14px; }
.line:last-child { border-bottom: 0; }
.line .sub { color: var(--color-muted); font-size: 12px; margin-left: auto; }
.tip-card { background: var(--color-paper); }
.pop { max-width: 560px; max-height: 88vh; overflow-y: auto; }
.form { padding: var(--space-md); padding-bottom: var(--space-lg); display: grid; gap: var(--space-sm); }
.form h3 { font-family: var(--font-display); margin: 0; font-size: 18px; }
.pick-row { display: flex; gap: 6px; align-items: center; flex-wrap: wrap; }
.pick-row .van-tag { margin: 2px 0; }
.lbl { font-size: 13px; color: var(--color-muted); flex-shrink: 0; }
.weeks { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 6px; }
@media (min-width: 480px) {
  .weeks { grid-template-columns: repeat(8, minmax(0, 1fr)); }
}
.err { color: var(--color-danger); font-size: 13px; }
</style>
