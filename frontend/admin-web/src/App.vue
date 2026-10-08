<template>
  <!-- 登录页：纯全屏，无侧边栏 -->
  <router-view v-if="$route.meta.public" />
  <!-- 后台页：侧边栏 + 内容区 -->
  <el-container v-else class="admin-layout">
    <el-aside width="220px" class="admin-aside">
      <div class="admin-logo">勤工助学 · 学工处</div>
      <el-menu
        class="admin-menu"
        router
        :default-active="$route.path"
        background-color="transparent"
        text-color="#C7D2FE"
        active-text-color="#fff"
      >
        <el-menu-item v-for="m in menus" :key="m.path" :index="m.path">{{ m.label }}</el-menu-item>
      </el-menu>
      <div class="admin-hint">Ctrl + K 打开命令面板</div>
      <div class="admin-logout" @click="logout">退出登录</div>
    </el-aside>
    <el-main class="admin-main">
      <router-view />
    </el-main>
  </el-container>

  <!-- 命令面板 -->
  <div v-if="paletteOpen" class="palette-mask" @click.self="closePalette">
    <div class="palette">
      <input
        ref="paletteInput"
        v-model="paletteQuery"
        class="palette-input"
        placeholder="输入菜单名搜索…  ↑↓ 选择 · 回车跳转 · Esc 关闭"
        @keydown="onPaletteKey"
      />
      <ul class="palette-list">
        <li
          v-for="(m, i) in filteredMenus"
          :key="m.path"
          :class="['palette-item', { active: i === paletteIdx }]"
          @click="jumpTo(m.path)"
          @mouseenter="paletteIdx = i"
        >
          <span v-html="highlight(m.label, paletteQuery)"></span>
          <span class="palette-path">{{ m.path }}</span>
        </li>
        <li v-if="filteredMenus.length === 0" class="palette-empty">无匹配项</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const paletteOpen = ref(false)
const paletteQuery = ref('')
const paletteIdx = ref(0)
const paletteInput = ref(null)

const menus = [
  { path: '/dash', label: '总览驾驶舱' },
  { path: '/job', label: '岗位审核' },
  { path: '/jobs', label: '岗位管理' },
  { path: '/stu', label: '学生管理' },
  { path: '/apply', label: '申请抽查' },
  { path: '/aid', label: '认定复审' },
  { path: '/attend', label: '考勤监控' },
  { path: '/salary', label: '工资发放' },
  { path: '/appeal', label: '申诉工单' },
  { path: '/pub', label: '公示管理' },
  { path: '/report', label: '统计报表' },
  { path: '/sys', label: '系统设置' }
]

const filteredMenus = computed(() => {
  const q = paletteQuery.value.trim().toLowerCase()
  if (!q) return menus
  return menus.filter((m) => m.label.toLowerCase().includes(q) || m.path.includes(q))
})

function highlight(text, q) {
  const keyword = q.trim()
  if (!keyword) return text
  const idx = text.toLowerCase().indexOf(keyword.toLowerCase())
  if (idx < 0) return text
  const before = text.slice(0, idx)
  const match = text.slice(idx, idx + keyword.length)
  const after = text.slice(idx + keyword.length)
  return `${before}<mark>${match}</mark>${after}`
}

async function openPalette() {
  paletteQuery.value = ''
  paletteIdx.value = 0
  paletteOpen.value = true
  await nextTick()
  paletteInput.value?.focus()
}

function closePalette() {
  paletteOpen.value = false
}

function jumpTo(path) {
  router.push(path)
  closePalette()
}

function onPaletteKey(e) {
  if (e.key === 'ArrowDown') {
    e.preventDefault()
    paletteIdx.value = Math.min(paletteIdx.value + 1, filteredMenus.value.length - 1)
  } else if (e.key === 'ArrowUp') {
    e.preventDefault()
    paletteIdx.value = Math.max(paletteIdx.value - 1, 0)
  } else if (e.key === 'Enter') {
    e.preventDefault()
    const m = filteredMenus.value[paletteIdx.value]
    if (m) jumpTo(m.path)
  } else if (e.key === 'Escape') {
    closePalette()
  }
}

function onGlobalKey(e) {
  if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
    e.preventDefault()
    paletteOpen.value ? closePalette() : openPalette()
  }
}

function logout() {
  localStorage.removeItem('admin-token')
  localStorage.removeItem('admin-uid')
  localStorage.removeItem('admin-name')
  router.push('/login')
}

onMounted(() => window.addEventListener('keydown', onGlobalKey))
onUnmounted(() => window.removeEventListener('keydown', onGlobalKey))
</script>

<style scoped>
.admin-logout {
  color: var(--color-paper-2);
  opacity: 0.6;
  padding: 12px 16px;
  cursor: pointer;
  border-top: 1px solid oklch(40% 0.02 260);
  transition: opacity var(--dur) var(--ease-out);
  font-size: 14px;
}
.admin-logout:hover { opacity: 1; }
.admin-hint {
  color: var(--color-paper-2);
  opacity: 0.4;
  font-size: 11px;
  padding: 8px 16px 0;
}

/* 命令面板 */
.palette-mask {
  position: fixed;
  inset: 0;
  background: oklch(20% 0.02 260 / 0.55);
  backdrop-filter: blur(6px);
  z-index: 9999;
  display: grid;
  place-items: start center;
  padding-top: 120px;
}
.palette {
  width: 520px;
  max-width: 90vw;
  background: var(--color-paper-2);
  border-radius: var(--radius);
  box-shadow: 0 20px 60px oklch(20% 0.02 260 / 0.4);
  overflow: hidden;
}
.palette-input {
  width: 100%;
  border: none;
  outline: none;
  padding: 16px 20px;
  font-size: 16px;
  background: var(--color-paper-2);
  border-bottom: 1px solid var(--color-line);
  color: var(--color-ink);
  font-family: var(--font-body);
}
.palette-list {
  list-style: none;
  margin: 0;
  padding: 6px;
  max-height: 340px;
  overflow-y: auto;
}
.palette-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
  color: var(--color-ink);
  transition: background var(--dur) var(--ease-out);
}
.palette-item.active {
  background: var(--color-accent);
  color: var(--color-accent-ink);
}
.palette-item.active .palette-path { color: oklch(90% 0.02 260); }
.palette-path {
  font-size: 12px;
  color: var(--color-muted);
  font-family: monospace;
}
.palette-empty {
  padding: 20px;
  text-align: center;
  color: var(--color-muted);
  font-size: 14px;
}
.palette-item :deep(mark) {
  background: oklch(85% 0.12 80);
  color: inherit;
  border-radius: 3px;
  padding: 0 2px;
}
.palette-item.active :deep(mark) {
  background: oklch(70% 0.12 80);
}
</style>
