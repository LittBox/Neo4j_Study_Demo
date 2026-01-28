<template>
  <el-container class="main-layout">
    <div v-if="isMobile && sidebarOpen" class="mobile-mask" @click="closeSidebar"></div>
    <el-aside width="200px" :class="['sidebar', { 'sidebar--open': sidebarOpen && isMobile }]">
      <div class="logo">
        <h2>健康管理</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/home">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/archive">
          <el-icon><Document /></el-icon>
          <span>健康档案</span>
        </el-menu-item>
        <el-menu-item index="/data-track">
          <el-icon><DataLine /></el-icon>
          <span>数据追踪</span>
        </el-menu-item>
        <el-menu-item index="/graph">
          <el-icon><Share /></el-icon>
          <span>知识图谱</span>
        </el-menu-item>
        <el-menu-item index="/ai-qa">
          <el-icon><ChatLineRound /></el-icon>
          <span>AI问答</span>
        </el-menu-item>
        <el-menu-item index="/medicine">
          <el-icon><Box /></el-icon>
          <span>用药管理</span>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <button class="mobile-menu-btn" type="button" @click="toggleSidebar" aria-label="打开菜单">
          <el-icon><Menu /></el-icon>
        </button>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span>{{ userStore.userInfo?.nickname || '用户' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted, onBeforeUnmount, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import {
  House,
  Document,
  DataLine,
  Share,
  ChatLineRound,
  Box,
  Menu,
  Setting,
  User
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const isMobile = ref(false)
const sidebarOpen = ref(false)

const updateIsMobile = () => {
  isMobile.value = window.matchMedia('(max-width: 600px)').matches
  if (!isMobile.value) sidebarOpen.value = false
}

onMounted(() => {
  updateIsMobile()
  window.addEventListener('resize', updateIsMobile)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateIsMobile)
})

const toggleSidebar = () => {
  if (!isMobile.value) return
  sidebarOpen.value = !sidebarOpen.value
}

const closeSidebar = () => {
  sidebarOpen.value = false
}

const handleMenuSelect = () => {
  if (isMobile.value) closeSidebar()
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.sidebar {
  background-color: var(--primary-color);
  color: #ffffff;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo h2 {
  color: #ffffff;
  font-size: 20px;
  font-weight: 600;
}

.sidebar-menu {
  border: none;
}

.header {
  background-color: #ffffff;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
}

.mobile-menu-btn {
  display: none;
  margin-right: auto;
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  cursor: pointer;
  color: var(--text-color);
}

.mobile-mask {
  display: none;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--text-color);
}

.main-content {
  background-color: var(--bg-color);
  padding: 20px;
}

@media (max-width: 600px) {
  .header {
    background-color: var(--primary-color);
    border-bottom-color: rgba(255, 255, 255, 0.12);
    justify-content: space-between;
    padding: 0 12px;
  }

  .mobile-menu-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    color: #fff;
  }

  .mobile-mask {
    display: block;
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.35);
    z-index: 999;
  }

  .user-info {
    color: #fff;
  }
}
</style>
