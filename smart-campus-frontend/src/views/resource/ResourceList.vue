<template>
  <div class="resource-list-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>课程资源列表</span>
          <el-button type="primary" link @click="handleBack">返回</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="章节">
          <el-input v-model="searchForm.chapter" placeholder="请输入章节" clearable />
        </el-form-item>
        <el-form-item label="资源名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入资源名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="resourceList" v-loading="loading" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="resourceName" label="资源名称" show-overflow-tooltip />
        <el-table-column prop="resourceType" label="类型" width="100" />
        <el-table-column prop="chapter" label="章节" width="120" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="downloadCount" label="下载量" width="100" />
        <el-table-column prop="createTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" link @click="handlePreview(scope.row)">
              预览
            </el-button>
            <el-button type="success" link @click="handleDownload(scope.row)">
              下载
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
import { useRoute, useRouter } from 'vue-router'
import { getResourceList, downloadResource } from '@/api/resource'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const scheduleId = route.params.scheduleId // 从路由获取开课计划ID

const resourceList = ref([])
const loading = ref(false)
const searchForm = reactive({
  chapter: '',
  keyword: '',
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const fetchResources = async () => {
  if (!scheduleId) {
    ElMessage.error('缺少开课计划ID，无法获取资源')
    return
  }
  loading.value = true
  try {
    const res = await getResourceList({
      scheduleId: scheduleId,
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    })
    if (res.code === 200) {
      resourceList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取资源列表失败:', error)
    ElMessage.error('获取资源列表失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchResources()
}

const handleReset = () => {
  searchForm.chapter = '',
  searchForm.keyword = '',
  pagination.current = 1
  fetchResources()
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchResources()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  fetchResources()
}

const handlePreview = (row) => {
  // 简单的预览：新窗口打开文件URL
  if (row.filePath) {
    window.open(row.filePath, '_blank')
  } else {
    ElMessage.warning('文件路径不存在，无法预览')
  }
}

const handleDownload = async (row) => {
  try {
    const response = await downloadResource(row.id)
    const blob = new Blob([response], { type: response.headers['content-type'] })
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.setAttribute('download', row.resourceName) // 设置下载文件名
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(downloadUrl)
    ElMessage.success('下载成功')
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败，请稍后再试')
  }
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  fetchResources()
})
</script>

<style scoped lang="scss">
.resource-list-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
