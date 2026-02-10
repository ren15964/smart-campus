<template>
  <div class="status-page">
    <el-card class="status-card">
      <el-result
        icon="warning"
        title="403 无权限"
        sub-title="你没有权限访问该页面，请联系管理员或切换账号。"
      >
        <template #extra>
          <div class="actions">
            <el-button @click="goBack">返回上一页</el-button>
            <el-button type="primary" @click="goHome">回到首页</el-button>
            <el-button type="danger" plain @click="reLogin">切换账号</el-button>
          </div>
          <div v-if="from" class="from-tip">尝试访问：{{ from }}</div>
        </template>
      </el-result>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const from = computed(() => route.query.from || '')

function goHome() {
  router.push('/')
}

function goBack() {
  router.back()
}

function reLogin() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped lang="scss">
.status-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.status-card {
  width: min(760px, 100%);
}

.actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.from-tip {
  margin-top: 10px;
  font-size: 12px;
  color: var(--app-text-muted); // 使用全局柔和文本颜色
  text-align: center;
  word-break: break-all;
}

:deep(.el-result__title p) {
  color: var(--app-text);
}

:deep(.el-result__subtitle p) {
  color: var(--app-text-muted);
}
</style>

