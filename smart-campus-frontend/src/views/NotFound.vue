<template>
  <div class="status-page">
    <el-card class="status-card">
      <el-result
        icon="info"
        title="404 页面不存在"
        sub-title="你访问的页面可能已被删除、改名或暂时不可用。"
      >
        <template #extra>
          <div class="actions">
            <el-button @click="goBack">返回上一页</el-button>
            <el-button type="primary" @click="goHome">回到首页</el-button>
            <el-button v-if="!isLogin" type="success" plain @click="goLogin">去登录</el-button>
          </div>
          <div class="from-tip">当前路径：{{ route.fullPath }}</div>
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

const isLogin = computed(() => userStore.isLogin)

function goHome() {
  router.push('/')
}

function goBack() {
  router.back()
}

function goLogin() {
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
  color: rgba(15, 23, 42, 0.55);
  text-align: center;
  word-break: break-all;
}
</style>

