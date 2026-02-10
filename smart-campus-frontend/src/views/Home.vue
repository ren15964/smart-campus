<template>
  <div class="home-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-left">
          <el-button class="collapse-btn" text @click="toggleCollapse">
            <el-icon>
              <component :is="isCollapsed ? 'Expand' : 'Fold'" />
            </el-icon>
          </el-button>
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
            :collapse="isCollapsed"
            :default-openeds="openedGroups"
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
                  <el-breadcrumb-item v-if="currentGroupTitle">{{ currentGroupTitle }}</el-breadcrumb-item>
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

const isCollapsed = computed(() => userStore?.ui?.menuCollapsed === true)

const breadcrumbItems = computed(() => {
  // matched: [Home, Child]；我们固定显示“首页”，这里只输出子级标题即可
  const matched = route.matched || []
  const leaf = matched[matched.length - 1]
  const title = leaf?.meta?.title || ''
  if (!title || route.path === '/') return []
  return [{ key: leaf.name || leaf.path, title }]
})

const currentGroupTitle = computed(() => {
  if (route.path === '/') return ''
  const matched = route.matched || []
  const leaf = matched[matched.length - 1]
  const rawPath = leaf?.path || route.path || ''
  const p = String(rawPath).replace(/^\//, '')
  const gKey = inferGroupKey({ path: p })
  return (groupMeta[gKey] || groupMeta.other).title
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

const openedGroups = computed(() => {
  if (isCollapsed.value) return []
  if (route.path === '/') return []
  const p = String(route.path || '').replace(/^\//, '')
  const gKey = inferGroupKey({ path: p })
  return [gKey]
})

function toggleCollapse() {
  userStore.setMenuCollapsed(!isCollapsed.value)
}

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
  font-family: 'Inter', sans-serif; // 使用 Inter 字体，与全局保持一致
  background: transparent;
  overflow: hidden;
}

.el-container {
  height: 100%;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--app-surface); // 使用浅色背景变量
  color: var(--app-text); // 使用浅色文本变量
  padding: 0 18px;
  border-bottom: 1px solid var(--app-border); // 使用浅色边框变量
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08); // 调整阴影使其更柔和

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
    background: var(--app-bg-soft);
    border: 1px solid var(--app-border);
    transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);

    &:hover {
      transform: translateY(-1px);
      background: var(--app-bg);
    }
  }
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.collapse-btn {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  background: var(--app-bg-soft);
  border: 1px solid var(--app-border);
  color: var(--app-text);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.collapse-btn:hover {
  transform: translateY(-1px);
  background: var(--app-bg);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.aside {
  background: var(--app-surface);
  border-right: 1px solid var(--app-border);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  transition: width 0.3s ease; // 添加宽度过渡动画

  .menu {
    border: none;
    background: transparent;
    padding: 10px;
  }
}

.main {
  background: transparent;
  padding: 24px;
}

.page-shell {
  width: 100%;
  height: 100%;
  min-height: calc(100vh - 60px - 36px);
}



.page-head__left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-head__title {
  font-size: 18px;
  font-weight: 800;
  color: var(--app-text);
  letter-spacing: 0.5px;
}

.page-head__breadcrumb {
  :deep(.el-breadcrumb__inner) {
    color: var(--app-text-muted);
  }
  :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
    color: var(--app-text);
    font-weight: 700;
  }
}

.welcome {
  width: 100%;
  height: calc(100vh - 60px - 48px); // Adjust height for new padding
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 24px;
  gap: 24px;
}



.dashboard {
  width: min(920px, 100%);
  margin: 0 auto;
  padding-top: 20px;
}

.welcome-card__title {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0.5px;
  color: var(--app-text);
}

.welcome-card__meta {
  margin-top: 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); // 响应式布局
  gap: 16px;
}

.meta-item {
  background: var(--app-bg-soft);
  border: 1px solid var(--app-border);
  border-radius: 18px; // 略微增大圆角
  padding: 18px 20px; // 略微增大内边距
}

.meta-label {
  font-size: 13px;
  color: var(--app-text-muted);
}

.meta-value {
  margin-top: 8px;
  font-size: 17px;
  font-weight: 600;
  color: var(--app-text);
}

:deep(.el-menu-item) {
  border-radius: 12px;
  margin: 6px 0;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

:deep(.el-menu-item.is-active) {
  background: var(--primary-light-color) !important; // Element Plus 亮蓝色
  color: white !important;
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.2); // 添加轻微阴影
}

:deep(.el-menu-item:hover) {
  background: var(--app-bg-soft) !important; // 悬停背景使用浅色柔和背景
  transform: translateX(4px); // 添加X轴位移效果
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05); // 添加轻微阴影
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
