<template>
  <router-view />
  <van-tabbar v-if="showTab" route active-color="var(--color-accent)">
    <van-tabbar-item to="/jobs" icon="home-o">首页</van-tabbar-item>
    <van-tabbar-item to="/applies" icon="records-o">申请</van-tabbar-item>
    <van-tabbar-item to="/attend" icon="clock-o">考勤</van-tabbar-item>
    <van-tabbar-item to="/salary" icon="balance-o">工资</van-tabbar-item>
    <van-tabbar-item to="/me" icon="user-o">我的</van-tabbar-item>
  </van-tabbar>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
// 登录注册是全屏页，不显示底部导航
const showTab = computed(() => !route.meta.public)
</script>
