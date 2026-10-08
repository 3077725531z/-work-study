<template>
  <h2 class="page-title">总览驾驶舱</h2>

  <!-- KPI 卡片 -->
  <el-row :gutter="16" style="margin-bottom:16px">
    <el-col :span="6" v-for="item in kpi" :key="item.label">
      <div class="kpi-card" :class="item.cls">
        <div class="kpi-label">{{ item.label }}</div>
        <div class="kpi-value">{{ item.value }}</div>
      </div>
    </el-col>
  </el-row>

  <!-- 待办清单 + 预警 -->
  <el-row :gutter="16">
    <el-col :span="14">
      <div class="hk-card">
        <h3 class="card-title">待办清单</h3>
        <el-table :data="todoList" stripe size="small">
          <el-table-column prop="label" label="事项" />
          <el-table-column label="数量" width="80" align="center">
            <template #default="{ row }">
              <span :class="['tag', row.count > 0 ? 'tag-warn' : 'tag-success']">{{ row.count }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="$router.push(row.to)">去处理</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-col>
    <el-col :span="10">
      <div class="hk-card">
        <h3 class="card-title">考勤预警</h3>
        <div style="display:flex;justify-content:space-between;margin-bottom:12px">
          <span style="color:var(--color-muted)">今日出勤率</span>
          <span style="font-family:var(--font-display);font-size:24px;color:var(--color-accent)">{{ o.rate ?? 0 }}%</span>
        </div>
        <el-table :data="warnings" stripe size="small" empty-text="暂无异常">
          <el-table-column prop="name" label="学生" />
          <el-table-column label="异常次数" width="100" align="center">
            <template #default="{ row }">
              <span class="tag tag-danger">{{ row.cnt }} 次</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-col>
  </el-row>

  <!-- 月发放趋势 -->
  <div class="hk-card">
    <h3 class="card-title">月发放趋势</h3>
    <div id="dashChart" style="height:300px"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { overview, stats } from '../api'

const o = ref({})
let chart = null

const kpi = computed(() => [
  { label: '在招岗位', value: o.value.jobs ?? 0, cls: 'kpi-ink' },
  { label: '在岗学生', value: o.value.onboard ?? 0, cls: 'kpi-blue' },
  { label: '待办事项', value: o.value.todos ?? 0, cls: 'kpi-warn' },
  { label: '本月应发', value: '¥ ' + (o.value.payMonth ?? 0).toLocaleString(), cls: 'kpi-green' }
])

const todoList = computed(() => {
  const t = o.value.todoList ?? {}
  return [
    { label: '岗位审核', count: t.jobs ?? 0, to: '/job' },
    { label: '申请抽查', count: t.applies ?? 0, to: '/apply' },
    { label: '工资待发放', count: t.salary ?? 0, to: '/salary' },
    { label: '申诉工单', count: t.appeals ?? 0, to: '/appeal' }
  ]
})

const warnings = computed(() => o.value.warnings ?? [])

onMounted(async () => {
  o.value = await overview()
  // 月发放趋势图
  const s = await stats()
  const el = document.getElementById('dashChart')
  if (el) {
    chart = echarts.init(el)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: s.months ?? [] },
      yAxis: { type: 'value' },
      series: [{
        type: 'line', smooth: true, data: s.amounts ?? [],
        itemStyle: { color: 'var(--color-accent)' },
        areaStyle: { opacity: 0.1 }
      }]
    })
  }
})

onUnmounted(() => chart?.dispose())
</script>

<style scoped>
.kpi-card {
  border-radius: var(--radius);
  padding: 20px;
  color: #fff;
}
.kpi-label { font-size: 13px; opacity: 0.85; }
.kpi-value { font-family: var(--font-display); font-size: 28px; margin-top: 6px; }
.kpi-ink { background: var(--color-ink); }
.kpi-blue { background: oklch(55% 0.18 260); }
.kpi-warn { background: oklch(60% 0.16 60); }
.kpi-green { background: oklch(50% 0.15 150); }
</style>
