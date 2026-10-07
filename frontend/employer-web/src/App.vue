<template>
  <el-container style="min-height:100vh">
    <el-aside width="220px" style="background:#0F3D2E">
      <div style="color:#fff;padding:16px">图书馆流通部</div>
      <el-menu router background-color="#0F3D2E" text-color="#A7F3D0" active-text-color="#fff">
        <el-menu-item index="/dash">部门首页</el-menu-item>
        <el-menu-item index="/pub">发布岗位</el-menu-item>
        <el-menu-item index="/jobs">我的岗位</el-menu-item>
        <el-menu-item index="/review">申请审核</el-menu-item>
        <el-menu-item index="/attend">考勤确认</el-menu-item>
        <el-menu-item index="/salary">工时复核</el-menu-item>
        <el-menu-item index="/me">部门中心</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main><router-view /></el-main>
  </el-container>
</template>
