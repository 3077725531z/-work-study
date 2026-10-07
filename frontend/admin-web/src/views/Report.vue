<template>
  <h2>A11 统计报表（实时 ECharts）</h2>
  <div id="chart" style="height:320px"></div>
</template>

<script setup>
import { onMounted } from 'vue'
import * as echarts from 'echarts'
import { stats } from '../api'

onMounted(async () => {
  const s = await stats()
  const el = document.getElementById('chart')
  const c = echarts.init(el)
  c.setOption({
    xAxis: { type: 'category', data: s.months ?? [] },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: s.amounts ?? [] }]
  })
})
</script>
