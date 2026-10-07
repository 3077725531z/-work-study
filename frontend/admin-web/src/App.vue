<template>
  <el-container style="min-height:100vh">
    <el-aside width="220px" style="background:#1E1B4B">
      <div style="color:#fff;padding:16px">勤工助学 · 学工处</div>
      <el-menu router background-color="#1E1B4B" text-color="#C7D2FE" active-text-color="#fff">
        <el-menu-item index="/dash">总览驾驶舱</el-menu-item>
        <el-menu-item index="/job">岗位审核</el-menu-item>
        <el-menu-item index="/stu">学生管理</el-menu-item>
        <el-menu-item index="/apply">申请抽查</el-menu-item>
        <el-menu-item index="/aid">认定复审</el-menu-item>
        <el-menu-item index="/attend">考勤监控</el-menu-item>
        <el-menu-item index="/salary">工资发放</el-menu-item>
        <el-menu-item index="/appeal">申诉工单</el-menu-item>
        <el-menu-item index="/pub">公示管理</el-menu-item>
        <el-menu-item index="/report">统计报表</el-menu-item>
        <el-menu-item index="/sys">系统设置</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main><router-view /></el-main>
  </el-container>
</template>
