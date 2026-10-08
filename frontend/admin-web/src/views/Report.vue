<template>
  <h2 class="page-title">统计报表</h2>
  <div class="hk-card">
    <div id="chart" style="height:360px"></div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { stats } from '../api'

let chart = null

onMounted(async () => {
  const s = await stats()
  const el = document.getElementById('chart')
  chart = echarts.init(el)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: s.months ?? [], axisLine: { lineStyle: { color: 'var(--color-line)' } } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: 'var(--color-line)' } } },
    series: [{
      type: 'bar',
      data: s.amounts ?? [],
      itemStyle: { color: 'var(--color-accent)', borderRadius: [6, 6, 0, 0] },
      barWidth: '40%'
    }]
  })
})

onUnmounted(() => { chart?.dispose() })
</script>
