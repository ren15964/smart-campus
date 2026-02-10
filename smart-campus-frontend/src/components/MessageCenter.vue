<template>
  <el-popover
    placement="bottom-end"
    :width="360"
    trigger="click"
    @show="handleOpen"
  >
    <template #reference>
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="msg-badge">
        <el-button class="msg-btn" text>
          <el-icon><Bell /></el-icon>
        </el-button>
      </el-badge>
    </template>

    <div class="msg-panel">
      <div class="msg-panel__header">
        <div class="msg-title">消息中心</div>
        <div class="msg-meta">
          <el-tag v-if="unreadCount > 0" type="success" size="small">未读 {{ unreadCount }}</el-tag>
          <el-button link type="primary" :loading="loading" @click="refresh">刷新</el-button>
        </div>
      </div>

      <el-divider class="msg-divider" />

      <AppSkeleton :loading="loading" :rows="6">
        <el-empty v-if="items.length === 0" description="暂无通知" />
        <div v-else class="msg-list">
          <div
            v-for="n in items"
            :key="n.id"
            class="msg-item"
            :class="{ 'is-unread': !n.isRead }"
            @click="openNotice(n)"
          >
            <div class="msg-item__top">
              <div class="msg-item__title">{{ n.title || '（无标题）' }}</div>
              <el-tag :type="priorityTagType(n.priority)" size="small">
                {{ priorityText(n.priority) }}
              </el-tag>
            </div>
            <div class="msg-item__bottom">
              <div class="msg-item__time">{{ n.publishTime || '' }}</div>
              <div class="msg-item__state">{{ n.isRead ? '已读' : '未读' }}</div>
            </div>
          </div>
        </div>
      </AppSkeleton>

      <el-divider class="msg-divider" />

      <div class="msg-panel__footer">
        <el-button size="small" @click="goAll">查看全部</el-button>
      </div>
    </div>
  </el-popover>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getNoticeList, markNoticeAsRead } from '@/api/notice'
import AppSkeleton from '@/components/AppSkeleton.vue'

const router = useRouter()

const loading = ref(false)
const items = ref([])

const unreadCount = computed(() => items.value.filter(n => n && n.isRead === false).length)

function priorityText(p) {
  if (p === 3) return '紧急'
  if (p === 2) return '重要'
  return '普通'
}

function priorityTagType(p) {
  if (p === 3) return 'danger'
  if (p === 2) return 'warning'
  return 'info'
}

async function refresh() {
  loading.value = true
  try {
    const res = await getNoticeList({ current: 1, size: 8 })
    const records = res?.data?.records ?? []
    items.value = Array.isArray(records) ? records : []
  } finally {
    loading.value = false
  }
}

async function handleOpen() {
  // 每次打开都刷新一次，确保未读数实时
  await refresh()
}

async function openNotice(n) {
  if (!n?.id) return
  // 先乐观更新本地状态（减少“点开后未读角标不变”的观感）
  if (n.isRead === false) {
    n.isRead = true
    markNoticeAsRead(n.id).catch(() => {})
  }
  router.push({ name: 'NoticeDetail', params: { id: n.id } })
}

function goAll() {
  router.push({ name: 'NoticeList' })
}
</script>

<style scoped lang="scss">
.msg-btn {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  background: var(--app-bg-soft);
  border: 1px solid var(--app-border);
  color: var(--app-text);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.msg-btn:hover {
  transform: translateY(-1px);
  background: var(--app-bg);
}

.msg-badge :deep(.el-badge__content) {
  border: none;
}

.msg-panel {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.msg-panel__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.msg-title {
  font-weight: 800;
  color: var(--app-text);
}

.msg-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.msg-divider {
  margin: 10px 0;
}

.msg-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 360px;
  overflow: auto;
  padding-right: 2px;
}

.msg-item {
  padding: 10px 10px;
  border-radius: 12px;
  border: 1px solid var(--app-border);
  background: var(--app-bg-soft);
  cursor: pointer;
  transition: transform 160ms ease, background 160ms ease, border-color 160ms ease;
}

.msg-item:hover {
  transform: translateY(-1px);
  border-color: var(--primary-light-color);
  background: rgba(0, 123, 255, 0.08); // Element Plus blue with transparency
}

.msg-item.is-unread {
  border-color: var(--primary-light-color);
  background: rgba(0, 123, 255, 0.06);
}

.msg-item__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.msg-item__title {
  font-size: 14px;
  font-weight: 700;
  color: var(--app-text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.msg-item__bottom {
  margin-top: 6px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: var(--app-text-muted);
  font-size: 12px;
}

.msg-panel__footer {
  display: flex;
  justify-content: flex-end;
}
</style>

