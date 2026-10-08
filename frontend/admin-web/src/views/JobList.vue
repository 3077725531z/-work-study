<template>
  <h2 class="page-title">岗位管理</h2>
  <div class="hk-card">
    <div class="toolbar">
      <el-input v-model="key" placeholder="搜岗位名" style="width:220px" clearable @input="load" />
      <el-select v-model="status" placeholder="状态" style="width:120px" clearable @change="load">
        <el-option label="待审核" value="待审核" />
        <el-option label="招募中" value="招募中" />
        <el-option label="已下架" value="已下架" />
        <el-option label="驳回" value="驳回" />
      </el-select>
      <el-button @click="load">刷新</el-button>
      <el-button type="primary" @click="openPublish">+ 发布岗位</el-button>
    </div>
    <el-table :data="rows" stripe>
      <el-table-column prop="code" label="编号" width="120" />
      <el-table-column prop="title" label="岗位名称" />
      <el-table-column prop="headcount" label="人数" width="80" align="center" />
      <el-table-column prop="payAmount" label="时薪" width="100">
        <template #default="{ row }">{{ row.payAmount ?? '-' }} 元</template>
      </el-table-column>
      <el-table-column prop="place" label="地点" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <span :class="['tag', statusClass(row.status)]">{{ row.status }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" link @click="showDetail(row)">详情</el-button>
          <el-button v-if="row.status === '招募中'" size="small" @click="offline(row)">下架</el-button>
          <el-button v-if="row.status === '已下架'" size="small" type="success" @click="online(row)">上架</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <!-- 岗位详情弹窗 -->
  <el-dialog v-model="detailVisible" :title="detail.title || '岗位详情'" width="640px">
    <div v-if="detail" class="job-detail">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="编号">{{ detail.code }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <span :class="['tag', statusClass(detail.status)]">{{ detail.status }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="招聘人数">{{ detail.headcount }} 人</el-descriptions-item>
        <el-descriptions-item label="在岗人数">{{ detail.onboardCount ?? 0 }} 人</el-descriptions-item>
        <el-descriptions-item label="时薪">{{ detail.payAmount }} 元</el-descriptions-item>
        <el-descriptions-item label="工作地点">{{ detail.place }}</el-descriptions-item>
        <el-descriptions-item label="工作时间">{{ detail.workTime }}</el-descriptions-item>
        <el-descriptions-item label="薪资档">{{ detail.payTier }}</el-descriptions-item>
        <el-descriptions-item label="浏览量">{{ detail.viewCount ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="打卡坐标" :span="2">
          {{ detail.lat ?? '-' }}, {{ detail.lng ?? '-' }}（半径 {{ detail.radiusM ?? '-' }} 米）
        </el-descriptions-item>
        <el-descriptions-item label="可到岗时段" :span="2">
          <el-tag v-for="s in parseSlots(detail.recruitSlots)" :key="s" style="margin:2px">{{ s }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="岗位内容" :span="2">{{ detail.content }}</el-descriptions-item>
        <el-descriptions-item label="任职要求" :span="2">{{ detail.requirement }}</el-descriptions-item>
        <el-descriptions-item v-if="detail.rejectReason" label="驳回原因" :span="2">
          <span style="color:var(--color-danger)">{{ detail.rejectReason }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </el-dialog>

  <!-- 发布岗位弹窗 -->
  <el-dialog v-model="pubVisible" title="发布岗位" width="640px" @close="resetForm">
    <el-form :model="form" label-width="100px">
      <el-form-item label="岗位名称" required>
        <el-input v-model="form.title" placeholder="如：图书馆流通岗" />
      </el-form-item>
      <el-row :gutter="12">
        <el-col :span="12">
          <el-form-item label="招聘人数" required>
            <el-input-number v-model="form.headcount" :min="1" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="时薪(元)" required>
            <el-input-number v-model="form.payAmount" :min="0" :precision="1" style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="工作地点" required>
        <el-input v-model="form.place" placeholder="如：图书馆一楼" />
      </el-form-item>
      <el-form-item label="工作时间">
        <el-input v-model="form.workTime" placeholder="如：周一至周五 18:00-20:00" />
      </el-form-item>
      <el-form-item label="可到岗时段">
        <div style="display:flex;gap:24px;flex-wrap:wrap">
          <div>
            <div style="margin-bottom:6px;color:var(--color-muted);font-size:13px">星期</div>
            <el-checkbox-group v-model="selDays">
              <el-checkbox v-for="d in days" :key="d" :label="d">{{ d }}</el-checkbox>
            </el-checkbox-group>
          </div>
          <div>
            <div style="margin-bottom:6px;color:var(--color-muted);font-size:13px">时段</div>
            <el-checkbox-group v-model="selSlots">
              <el-checkbox v-for="s in slots" :key="s" :label="s">{{ s }}</el-checkbox>
            </el-checkbox-group>
          </div>
        </div>
        <div v-if="combinedSlots.length" style="margin-top:8px">
          <el-tag v-for="t in combinedSlots" :key="t" style="margin:2px">{{ t }}</el-tag>
        </div>
      </el-form-item>
      <el-form-item label="打卡坐标">
        <el-button :loading="locating" @click="getLocation">
          {{ form.lat && form.lng ? '重新定位' : '获取当前位置' }}
        </el-button>
        <span v-if="form.lat && form.lng" style="margin-left:12px;color:var(--color-muted)">
          纬度 {{ form.lat }}, 经度 {{ form.lng }}
        </span>
        <span v-else style="margin-left:12px;color:var(--color-muted)">未定位将不启用定点打卡</span>
      </el-form-item>
      <el-form-item label="打卡半径(米)">
        <el-input-number v-model="form.radiusM" :min="0" style="width:160px" />
      </el-form-item>
      <el-form-item label="岗位内容">
        <el-input v-model="form.content" type="textarea" :rows="3" placeholder="岗位职责描述" />
      </el-form-item>
      <el-form-item label="任职要求">
        <el-input v-model="form.requirement" type="textarea" :rows="2" placeholder="任职资格要求" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="pubVisible = false">取消</el-button>
      <el-button type="primary" :loading="publishing" @click="submitPublish">发布</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { jobPage, jobPublish } from '../api'
import api from '../api/request'

const rows = ref([])
const key = ref('')
const status = ref('')
const detailVisible = ref(false)
const detail = ref({})

// 发布表单
const pubVisible = ref(false)
const publishing = ref(false)
const locating = ref(false)
const form = ref({
  title: '', headcount: 1, payAmount: 25, workTime: '',
  place: '', lat: '', lng: '', radiusM: 100,
  content: '', requirement: '', payTier: 'A'
})

// 可到岗时段选择
const days = ['周一', '周二', '周三', '周四', '周五']
const slots = ['第1-2节', '第3-4节', '第5-6节', '第7-8节', '晚上']
const selDays = ref([])
const selSlots = ref([])
const combinedSlots = computed(() => {
  const arr = []
  for (const d of selDays.value) for (const s of selSlots.value) arr.push(`${d}${s}`)
  return arr
})

function statusClass(s) {
  if (s === '招募中') return 'tag-success'
  if (s === '驳回') return 'tag-danger'
  if (s === '已下架') return 'tag-warn'
  return 'tag-info'
}

function parseSlots(s) {
  if (!s) return []
  try { return JSON.parse(s) } catch { return s.split(',').filter(Boolean) }
}

async function load() {
  const page = await jobPage({ current: 1, size: 100, key: key.value, status: status.value })
  rows.value = page.records ?? page ?? []
}

async function showDetail(row) {
  detail.value = await api.get(`/jobs/${row.id}`)
  detailVisible.value = true
}

async function offline(row) {
  try {
    await ElMessageBox.confirm(`确定下架「${row.title}」？`, '下架确认', { type: 'warning' })
    await api.put(`/jobs/${row.id}/offline`)
    row.status = '已下架'
    ElMessage.success('已下架')
  } catch {}
}

async function online(row) {
  await jobPublish(row.id)
  row.status = '招募中'
  ElMessage.success('已上架')
}

function openPublish() {
  pubVisible.value = true
}

function getLocation() {
  if (!navigator.geolocation) {
    ElMessage.warning('浏览器不支持定位')
    return
  }
  locating.value = true
  navigator.geolocation.getCurrentPosition(
    (pos) => {
      form.value.lat = Number(pos.coords.latitude.toFixed(6))
      form.value.lng = Number(pos.coords.longitude.toFixed(6))
      ElMessage.success('定位成功')
      locating.value = false
    },
    (err) => {
      ElMessage.error('定位失败：' + err.message)
      locating.value = false
    },
    { enableHighAccuracy: true, timeout: 10000 }
  )
}

function resetForm() {
  form.value = {
    title: '', headcount: 1, payAmount: 25, workTime: '',
    place: '', lat: '', lng: '', radiusM: 100,
    content: '', requirement: '', payTier: 'A'
  }
  selDays.value = []
  selSlots.value = []
}

async function submitPublish() {
  if (!form.value.title.trim()) { ElMessage.warning('请输入岗位名称'); return }
  if (!form.value.place.trim()) { ElMessage.warning('请输入工作地点'); return }
  publishing.value = true
  try {
    const payload = { ...form.value }
    if (combinedSlots.value.length) {
      payload.recruitSlots = JSON.stringify(combinedSlots.value)
    }
    if (payload.lat) payload.lat = Number(payload.lat)
    if (payload.lng) payload.lng = Number(payload.lng)
    // 创建岗位（后端默认状态=待审核）
    const job = await api.post('/jobs', payload)
    // 管理员直接上架
    await jobPublish(job.id)
    ElMessage.success('发布成功')
    pubVisible.value = false
    await load()
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || '发布失败')
  } finally {
    publishing.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.job-detail { line-height: 1.6; }
</style>
