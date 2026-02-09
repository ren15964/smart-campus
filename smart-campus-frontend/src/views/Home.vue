<template>
  <div class="home-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-left">
          <h2>智慧校园管理平台</h2>
        </div>
        <div class="header-right">
          <MessageCenter />
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userStore.userInfo.realName }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-container>
        <!-- 侧边栏 -->
        <el-aside width="220px" class="aside">
          <el-menu
            :default-active="activeMenu"
            class="menu"
            router
          >
            <el-menu-item index="/">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </el-menu-item>

            <template v-for="g in menuGroups" :key="g.key">
              <el-sub-menu v-if="g.items.length > 1" :index="g.key">
                <template #title>
                  <el-icon><component :is="g.icon" /></el-icon>
                  <span>{{ g.title }}</span>
                </template>
                <el-menu-item
                  v-for="it in g.items"
                  :key="it.name"
                  :index="it.fullPath"
                >
                  <el-icon><component :is="it.icon || g.icon" /></el-icon>
                  <span>{{ it.title }}</span>
                </el-menu-item>
              </el-sub-menu>

              <el-menu-item
                v-else
                :index="g.items[0].fullPath"
              >
                <el-icon><component :is="g.items[0].icon || g.icon" /></el-icon>
                <span>{{ g.items[0].title }}</span>
              </el-menu-item>
            </template>
          </el-menu>
        </el-aside>

        <!-- 主内容区 -->
        <el-main class="main">
          <div v-if="$route.path !== '/'" class="page-shell">
            <div class="page-head">
              <div class="page-head__left">
                <div class="page-head__title">{{ pageTitle }}</div>
                <el-breadcrumb separator="/" class="page-head__breadcrumb">
                  <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                  <el-breadcrumb-item v-for="b in breadcrumbItems" :key="b.key">
                    {{ b.title }}
                  </el-breadcrumb-item>
                </el-breadcrumb>
              </div>
            </div>
            <router-view v-slot="{ Component }">
              <transition name="fade-slide" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </div>

          <div v-else class="welcome">
            <div class="welcome-card">
              <div class="welcome-card__title">欢迎使用智慧校园管理平台</div>
              <div class="welcome-card__meta">
                <div class="meta-item">
                  <div class="meta-label">当前用户</div>
                  <div class="meta-value">{{ userStore.userInfo.realName }}</div>
                </div>
                <div class="meta-item">
                  <div class="meta-label">用户角色</div>
                  <div class="meta-value">{{ getRoleName(userStore.userInfo.role) }}</div>
                </div>
              </div>
            </div>
            <div class="dashboard">
              <DashboardCharts />
            </div>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import DashboardCharts from '@/components/DashboardCharts.vue'
import MessageCenter from '@/components/MessageCenter.vue'
import { routes as appRoutes } from '@/router'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => {
  return route.path
})

const pageTitle = computed(() => route?.meta?.title || '')

const breadcrumbItems = computed(() => {
  // matched: [Home, Child]；我们固定显示“首页”，这里只输出子级标题即可
  const matched = route.matched || []
  const leaf = matched[matched.length - 1]
  const title = leaf?.meta?.title || ''
  if (!title || route.path === '/') return []
  return [{ key: leaf.name || leaf.path, title }]
})

const getRoleName = (role) => {
  const roleMap = {
    admin: '管理员',
    teacher: '教师',
    student: '学生'
  }
  return roleMap[role] || '未知'
}

function normalizeChildPath(p) {
  if (!p) return '/'
  if (p.startsWith('/')) return p
  return `/${p}`
}

function canAccessByRole(meta, role) {
  const roles = meta?.roles
  if (!roles || roles.length === 0) return true
  return !!role && roles.includes(role)
}

function inferGroupKey(child) {
  const p = child?.path || ''
  if (p.includes('user') || p.includes('classroom') || p.includes('selection')) return 'admin'
  if (p.includes('course')) return 'course'
  if (p.includes('grade')) return 'grade'
  if (p.includes('homework')) return 'homework'
  if (p.includes('resource')) return 'resource'
  if (p.includes('notice')) return 'notice'
  if (p.includes('news')) return 'news'
  if (p.includes('personal') || p.includes('password') || p.includes('profile')) return 'me'
  return 'other'
}

const groupMeta = {
  admin: { title: '管理', icon: 'Setting', order: 10 },
  course: { title: '课程教学', icon: 'Reading', order: 20 },
  grade: { title: '成绩', icon: 'DataAnalysis', order: 30 },
  homework: { title: '作业', icon: 'Notebook', order: 40 },
  resource: { title: '资源', icon: 'Files', order: 50 },
  notice: { title: '通知公告', icon: 'Bell', order: 60 },
  news: { title: '校园新闻', icon: 'ChatDotSquare', order: 70 },
  me: { title: '个人', icon: 'User', order: 80 },
  other: { title: '其它', icon: 'Menu', order: 90 }
}

const menuGroups = computed(() => {
  const role = userStore?.userInfo?.role || userStore?.role || ''
  const home = (appRoutes || []).find(r => r && r.name === 'Home')
  const children = home?.children || []

  const visible = children
    .filter(c => c && c.meta && c.meta.hideInMenu !== true)
    .filter(c => canAccessByRole(c.meta, role))

  const map = new Map()
  for (const c of visible) {
    const gKey = inferGroupKey(c)
    if (!map.has(gKey)) {
      const gm = groupMeta[gKey] || groupMeta.other
      map.set(gKey, { key: gKey, title: gm.title, icon: gm.icon, order: gm.order, items: [] })
    }
    const gm = map.get(gKey)
    gm.items.push({
      name: c.name,
      title: c?.meta?.title || c.name,
      fullPath: normalizeChildPath(c.path),
      icon: c?.meta?.icon
    })
  }

  const arr = Array.from(map.values())
  arr.sort((a, b) => (a.order || 0) - (b.order || 0))
  return arr
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('退出成功')
      router.push('/login')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/personal-center')
  }
}

</script>

<style scoped lang="scss">
.home-container {
  width: 100%;
  height: 100vh;
  background: transparent;
}

.el-container {
  height: 100%;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.92);
  padding: 0 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);

  .header-left h2 {
    margin: 0;
    font-size: 20px;
    letter-spacing: 0.4px;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 5px;
    cursor: pointer;
    padding: 8px 10px;
    border-radius: 10px;
    background: rgba(255, 255, 255, 0.06);
    border: 1px solid rgba(255, 255, 255, 0.12);
    transition: transform 180ms ease, background 180ms ease;

    &:hover {
      transform: translateY(-1px);
      background: rgba(255, 255, 255, 0.09);
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.aside {
  background: rgba(255, 255, 255, 0.06);
  border-right: 1px solid rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);

  .menu {
    border: none;
    background: transparent;
    padding: 10px;
  }
}

.main {
  background: transparent;
  padding: 18px;
}

.page-shell {
  width: 100%;
  height: 100%;
  min-height: calc(100vh - 60px);
}

.page-head {
  width: 100%;
  margin-bottom: 12px;
  padding: 12px 14px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(255, 255, 255, 0.35);
  box-shadow: 0 18px 60px rgba(0, 0, 0, 0.18);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
}

.page-head__left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.page-head__title {
  font-size: 16px;
  font-weight: 800;
  color: rgba(15, 23, 42, 0.86);
}

.page-head__breadcrumb {
  :deep(.el-breadcrumb__inner) {
    color: rgba(15, 23, 42, 0.62);
  }
  :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
    color: rgba(15, 23, 42, 0.82);
    font-weight: 700;
  }
}

.welcome {
  width: 100%;
  height: calc(100vh - 60px);
  display: flex;
  align-items: stretch;
  justify-content: center;
  flex-direction: column;
  padding: 12px;
  gap: 16px;
}

.welcome-card {
  width: min(920px, 100%);
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(255, 255, 255, 0.35);
  border-radius: 18px;
  box-shadow: 0 22px 70px rgba(0, 0, 0, 0.34);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  padding: 26px 28px;
  animation: pageEnter 420ms cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.dashboard {
  width: min(920px, 100%);
  margin: 0 auto;
}

.welcome-card__title {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 0.3px;
  color: rgba(15, 23, 42, 0.9);
}

.welcome-card__meta {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.meta-item {
  background: rgba(2, 6, 23, 0.04);
  border: 1px solid rgba(2, 6, 23, 0.08);
  border-radius: 14px;
  padding: 14px 14px;
}

.meta-label {
  font-size: 12px;
  color: rgba(15, 23, 42, 0.6);
}

.meta-value {
  margin-top: 6px;
  font-size: 16px;
  font-weight: 600;
  color: rgba(15, 23, 42, 0.88);
}

:deep(.el-menu-item) {
  border-radius: 12px;
  margin: 6px 0;
}

:deep(.el-menu-item.is-active) {
  background: rgba(34, 211, 238, 0.14) !important;
  color: rgba(255, 255, 255, 0.92) !important;
}

:deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.09) !important;
}

@keyframes pageEnter {
  from {
    opacity: 0;
    transform: translateY(8px) scale(0.99);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>
