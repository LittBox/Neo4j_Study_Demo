import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import MainLayout from '@/layouts/MainLayout.vue'
import LoginView from '@/views/LoginView.vue'
import HomeView from '@/views/HomeView.vue'
import GraphView from '@/views/GraphView.vue'
import AiQaView from '@/views/AiQaView.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: HomeView
      },
      {
        path: 'archive',
        name: 'Archive',
        component: () => import('@/views/ArchiveView.vue')
      },
      {
        path: 'data-track',
        name: 'DataTrack',
        component: () => import('@/views/DataTrackView.vue')
      },
      {
        path: 'graph',
        name: 'Graph',
        component: GraphView
      },
      {
        path: 'ai-qa',
        name: 'AiQa',
        component: AiQaView
      },
      {
        path: 'medicine',
        name: 'Medicine',
        component: () => import('@/views/MedicineView.vue')
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/SettingsView.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.path === '/login') {
    if (userStore.token) {
      next('/home')
    } else {
      next()
    }
  } else {
    if (userStore.token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
