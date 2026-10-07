<template>
  <h2>发布岗位（实时）</h2>
  <el-form :model="f" label-width="90px" style="max-width:640px">
    <el-form-item label="名称" required><el-input v-model="f.title" placeholder="如 图书馆晚班助理" /></el-form-item>
    <el-form-item label="人数"><el-input-number v-model="f.headcount" :min="1" :max="20" /></el-form-item>
    <el-form-item label="地点"><el-input v-model="f.place" placeholder="如 图书馆一楼" /></el-form-item>
    <el-form-item label="薪资档">
      <el-select v-model="f.payTier" @change="onTier">
        <el-option label="A档 25元/时" value="A" />
        <el-option label="B档 28元/时" value="B" />
        <el-option label="C档 30元/时" value="C" />
      </el-select>
      <span style="margin-left:8px">单价 {{ f.payAmount }}元/时</span>
    </el-form-item>
    <el-form-item label="可到岗日" required>
      <el-checkbox-group v-model="days">
        <el-checkbox v-for="d in ['一', '二', '三', '四', '五', '六', '日']" :key="d" :label="d">周{{ d }}</el-checkbox>
      </el-checkbox-group>
    </el-form-item>
    <el-form-item label="可到岗段" required>
      <el-checkbox-group v-model="periods">
        <el-checkbox v-for="p in ['1-2节', '3-4节', '5-6节', '7-8节', '晚']" :key="p" :label="p">{{ p }}</el-checkbox>
      </el-checkbox-group>
    </el-form-item>
    <el-form-item label="组合预览">
      <el-tag v-for="s in previewList" :key="s" style="margin:0 6px 6px 0">{{ s }}</el-tag>
      <span v-if="!previewList.length">请先勾选日期和时段</span>
    </el-form-item>
    <el-form-item label="工作内容"><el-input v-model="f.content" type="textarea" rows="2" /></el-form-item>
    <el-form-item label="岗位要求"><el-input v-model="f.requirement" type="textarea" rows="2" /></el-form-item>
    <el-form-item>
      <el-button type="primary" :loading="busy" @click="submit">提交审核</el-button>
      <el-button @click="reset">重置</el-button>
    </el-form-item>
  </el-form>
  <p v-if="err" style="color:#b91c1c">{{ err }}</p>
  <p v-if="ok" style="color:#16a34a">已提交，待学工审核</p>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { pubJob } from '../api'

const TIER_PAY = { A: 25, B: 28, C: 30 }

const f = reactive({
  title: '',
  deptId: 1,
  headcount: 6,
  payTier: 'A',
  payAmount: 25,
  place: '',
  workTime: '',
  content: '',
  requirement: '责任心强，新生可报'
})
const days = ref(['一', '二', '三', '四', '五'])
const periods = ref(['晚'])
const err = ref('')
const ok = ref(false)
const busy = ref(false)

const previewList = computed(() => {
  const list = []
  for (const d of days.value) {
    for (const p of periods.value) {
      list.push(`周${d}${p}`)
    }
  }
  return list
})

function onTier() {
  f.payAmount = TIER_PAY[f.payTier] ?? 25
}

function reset() {
  f.title = ''
  f.place = ''
  f.content = ''
  days.value = ['一', '二', '三', '四', '五']
  periods.value = ['晚']
  err.value = ''
  ok.value = false
}

async function submit() {
  err.value = ''
  ok.value = false
  if (!f.title.trim()) {
    err.value = '请填写岗位名称'
    return
  }
  if (!days.value.length || !periods.value.length) {
    err.value = '请勾选可到岗日期和时段'
    return
  }
  f.workTime = previewList.value.join('、')
  busy.value = true
  try {
    await pubJob({ ...f, recruitSlots: previewList.value.join(',') })
    ok.value = true
    ElMessage.success('已提交审核')
  } catch (e) {
    err.value = e.response?.data?.msg || e.message
  } finally {
    busy.value = false
  }
}
</script>
