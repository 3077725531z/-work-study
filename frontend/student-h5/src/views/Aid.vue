<template>
  <div class="page hallmark">
    <header class="hero">
      <div>
        <span class="brand">困难认定</span>
        <h2>档位决定优先级</h2>
        <p class="sub">申请 → 辅导员初审 → 学院复审 → 公示建档</p>
      </div>
    </header>

    <section class="card">
      <van-field v-model="form.level" label="档位" readonly @click="pick = true" />
      <van-popup v-model:show="pick" position="bottom">
        <van-picker
          :columns="[{ text: '一档', value: '一档' }, { text: '二档', value: '二档' }, { text: '三档', value: '三档' }]"
          @confirm="onPick"
          @cancel="pick = false"
        />
      </van-popup>
      <van-field v-model="form.income" label="人均年收入" type="number" placeholder="元" />
      <van-field v-model="form.reason" label="理由" type="textarea" placeholder="≥30字，写清家庭情况" />
      <div class="up-title">证明材料（至少1份：身份证/贫困证明/承诺书，jpg/png/pdf≤5MB）</div>
      <van-uploader
        v-model="fileList"
        multiple
        :max-count="3"
        :after-read="uploadOne"
        :disabled="uploading"
        @delete="onDel"
      />
      <p v-if="uploading" class="tip">上传中…</p>
      <p v-if="err" class="err">{{ err }}</p>
      <van-button type="primary" block round :loading="busy" @click="submit">提交/更新</van-button>
    </section>

    <h3 class="sec">历史记录</h3>
    <van-empty v-if="!rows.length" description="暂无认定记录" />
    <article v-for="a in rows" :key="a.id" class="rec">
      <div class="rec-top">
        <b>{{ a.level }}</b>
        <van-tag :type="a.status === '已认定' ? 'success' : 'warning'" round>{{ a.status }}</van-tag>
      </div>
      <p :class="['reason', { clamp: !expanded.has(a.id) }]">{{ a.reason }}</p>
      <span v-if="(a.reason || '').length > 60" class="link" @click="toggleExp(a.id)">
        {{ expanded.has(a.id) ? '收起' : '展开' }}
      </span>
      <span v-if="fileCount(a)" class="sub">附件{{ fileCount(a) }}份 <span class="link" @click="viewFiles(a)">查看</span></span>
    </article>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { showToast } from 'vant'
import api from '../api/request'
import { aidApply, myAid, uid } from '../api'

const form = reactive({ studentId: uid(), level: '二档', income: '', reason: '' })
const rows = ref([])
const pick = ref(false)
const err = ref('')
const busy = ref(false)
const fileList = ref([])
const fileUrls = ref([])
const uploading = ref(false)
const expanded = ref(new Set())

function toggleExp(id) {
  const s = new Set(expanded.value)
  if (s.has(id)) s.delete(id)
  else s.add(id)
  expanded.value = s
}

async function uploadOne(file) {
  const items = Array.isArray(file) ? file : [file]
  uploading.value = true
  try {
    for (const it of items) {
      const fd = new FormData()
      fd.append('file', it.file)
      const r = await api.post('/files/upload', fd)
      fileUrls.value.push(r.url)
      it.status = 'done'
    }
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
    showToast('上传失败，已移除该文件')
    fileList.value = fileList.value.filter((x) => x.status === 'done')
  } finally {
    uploading.value = false
  }
}

function onPick({ selectedOptions }) {
  form.level = selectedOptions[0].value
  pick.value = false
}

async function load() {
  if (!uid()) return
  try {
    const list = await myAid()
    const all = Array.isArray(list) ? list : (list.records ?? [])
    const mine = uid()
    rows.value = all.filter((a) => String(a.studentId) === String(mine))
  } catch {
    rows.value = []
  }
}

function onDel(_, detail) {
  fileUrls.value.splice(detail.index, 1)
}

function fileCount(a) {
  try {
    const arr = typeof a.files === 'string' ? JSON.parse(a.files) : (a.files ?? [])
    return Array.isArray(arr) ? arr.length : 0
  } catch {
    return 0
  }
}

function viewFiles(a) {
  try {
    const arr = typeof a.files === 'string' ? JSON.parse(a.files) : a.files
    if (!arr || !arr.length) {
      showToast('暂无附件')
      return
    }
    const u = arr[0]
    window.open(u.startsWith('http') ? u : `http://localhost:8080${u}`, '_blank')
  } catch {
    showToast('附件无法打开')
  }
}

async function submit() {
  err.value = ''
  if ((form.reason || '').trim().length < 30) {
    err.value = '理由至少30字'
    return
  }
  if (!fileUrls.value.length) {
    err.value = '至少上传1份证明材料'
    return
  }
  busy.value = true
  try {
    await aidApply({ ...form, files: JSON.stringify(fileUrls.value) })
    showToast('提交成功')
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
.reason { font-size: 14px; margin: 0; overflow-wrap: anywhere; }
.reason.clamp { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.up-title { font-size: 14px; margin-top: 4px; }
.link { color: var(--color-accent); }
.err { color: var(--color-danger); font-size: 13px; }
</style>
