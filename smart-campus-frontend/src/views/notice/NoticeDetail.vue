<template>
  <div class="notice-detail-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>通知详情</span>
          <el-button type="primary" link @click="handleBack">返回</el-button>
        </div>
      </template>

      <div v-if="notice.id" class="detail-content">
        <h2 class="notice-title">{{ notice.title }}</h2>
        <div class="notice-meta">
          <span>发布人: {{ notice.publisherName }}</span>
          <span>优先级:
            <el-tag :type="getPriorityTagType(notice.priority)">{{ formatPriority(notice.priority) }}</el-tag>
          </span>
          <span>发布时间: {{ notice.publishTime }}</span>
          <span>阅读量: {{ notice.readCount }}</span>
        </div>
        <el-divider />
        <div class="notice-body" v-html="notice.content"></div>

        <div v-if="notice.attachment" class="notice-attachment">
          <el-link :href="notice.attachment" target="_blank" type="primary">
            <el-icon><Download /></el-icon> 附件下载
          </el-link>
        </div>
      </div>
      <AppEmpty v-else description="通知不存在或已删除">
        <el-button type="primary" @click="router.push({ name: 'NoticeList' })">返回列表</el-button>
      </AppEmpty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNoticeDetail, markNoticeAsRead } from '@/api/notice'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import AppEmpty from '@/components/AppEmpty.vue'

const route = useRoute()
const router = useRouter()
const noticeId = route.params.id

const notice = ref({
  id: null,
  title: '',
  content: '',
  publisherName: '',
  priority: 1,
  publishTime: '',
  readCount: 0,
  attachment: '',
  isRead: false,
})

const fetchNoticeDetail = async () => {
  if (!noticeId) {
    ElMessage.error('缺少通知ID')
    return
  }
  try {
    const res = await getNoticeDetail(noticeId)
    if (res.code === 200) {
      Object.assign(notice.value, res.data)
      // 如果通知未读，则标记为已读
      if (!res.data.isRead) {
        await markNoticeAsRead(noticeId)
        // 重新获取详情以更新阅读量和状态，或者直接更新本地状态
        // notice.value.isRead = true;
        // notice.value.readCount++;
      }
    } else {
      ElMessage.error(res.message || '获取通知详情失败')
    }
  } catch (error) {
    console.error('获取通知详情失败:', error)
    ElMessage.error('获取通知详情失败，请稍后再试')
  }
}

const formatPriority = (priority) => {
  switch (priority) {
    case 1: return '普通'
    case 2: return '重要'
    case 3: return '紧急'
    default: return '未知'
  }
}

const getPriorityTagType = (priority) => {
  switch (priority) {
    case 1: return 'info'
    case 2: return 'warning'
    case 3: return 'danger'
    default: return 'info'
  }
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  fetchNoticeDetail()
})
</script>

<style scoped lang="scss">
.notice-detail-container {
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-content {
  padding: 10px 30px 20px; // 调整内边距，左右略大，底部适中
}

.notice-title {
  font-size: 28px; // 略微增大字体
  font-weight: 800; // 加粗
  text-align: center;
  margin-bottom: 25px; // 调整间距
  color: var(--app-text); // 使用全局文本颜色
}

.notice-meta {
  display: flex;
  justify-content: center;
  gap: 25px; // 略微增大间距
  font-size: 13px; // 略微缩小字体
  color: var(--app-text-muted); // 使用全局柔和文本颜色
  margin-bottom: 25px; // 调整间距

  span {
    display: flex;
    align-items: center;
    gap: 8px; // 增大图标与文本间距
  }
}

.notice-body {
  line-height: 1.8;
  font-size: 15px; // 略微缩小字体
  color: var(--app-text); // 使用全局文本颜色
  min-height: 200px;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.notice-attachment {
  margin-top: 30px;
  text-align: right;
}
</style>
