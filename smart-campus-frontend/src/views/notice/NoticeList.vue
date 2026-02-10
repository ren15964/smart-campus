<template>
  <div class="notice-list-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>通知公告</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="searchForm.keyword" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="searchForm.priority" placeholder="请选择优先级" clearable>
            <el-option label="普通" :value="1" />
            <el-option label="重要" :value="2" />
            <el-option label="紧急" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="noticeList" v-loading="loading" border style="width: 100%">
        <template #empty>
          <AppEmpty description="暂无通知">
            <el-button type="primary" @click="handleReset">清空筛选</el-button>
          </AppEmpty>
        </template>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="publisherName" label="发布人" width="120" />
        <el-table-column label="优先级" width="100">
          <template #default="scope">
            <el-tag :type="getPriorityTagType(scope.row.priority)">
              {{ formatPriority(scope.row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.isRead ? 'info' : 'success'">
              {{ scope.row.isRead ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNoticeList } from '@/api/notice'
import { ElMessage } from 'element-plus'
import AppEmpty from '@/components/AppEmpty.vue'

const router = useRouter()
const noticeList = ref([])
const loading = ref(false)
const searchForm = reactive({
  keyword: '',
  priority: null,
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const fetchNotices = async () => {
  loading.value = true
  try {
    const res = await getNoticeList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    })
    if (res.code === 200) {
      noticeList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取通知列表失败:', error)
    ElMessage.error('获取通知失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchNotices()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.priority = null
  pagination.current = 1
  fetchNotices()
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchNotices()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  fetchNotices()
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

const handleViewDetail = (row) => {
  router.push({ name: 'NoticeDetail', params: { id: row.id } })
}

onMounted(() => {
  fetchNotices()
})
</script>

<style scoped lang="scss">
.notice-list-container {
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 24px; // 略微增大间距
  padding-bottom: 15px; // 调整内边距
  border-bottom: 1px solid var(--app-border); // 使用全局边框变量
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px; // 略微增大间距
}
</style>
