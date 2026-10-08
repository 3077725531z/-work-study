<template>
  <div class="nf-wrap">
    <div class="nf-hero">
      <h1>404</h1>
      <p>页面走丢了</p>
      <van-button round type="primary" class="btn" @click="$router.replace('/')">返回大厅</van-button>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.nf-wrap {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: var(--color-paper);
  padding: var(--space-lg);
}
.nf-hero {
  text-align: center;
}
.nf-hero h1 {
  font-family: var(--font-display);
  font-size: 96px;
  margin: 0;
  color: var(--color-ink);
  line-height: 1;
}
.nf-hero p {
  color: var(--color-muted);
  margin: 8px 0 24px;
}
.btn { min-width: 180px; }
</style>
