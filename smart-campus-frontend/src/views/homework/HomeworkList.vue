<template>
  <div class="homework-list-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的作业</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="未提交" :value="0" />
            <el-option label="已提交" :value="1" />
            <el-option label="已批改" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="homeworkList" v-loading="loading" border style="width: 100%">
        <el-table-column prop="homeworkId" label="ID" width="80" />
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="title" label="作业标题" show-overflow-tooltip />
        <el-table-column prop="teacherName" label="发布教师" width="120" />
        <el-table-column prop="deadline" label="截止日期" width="180" />
        <el-table-column label="提交状态" width="100">
          <template #default="scope">
            <el-tag :type="getSubmitStatusTagType(scope.row.submitStatus)">
              {{ formatSubmitStatus(scope.row.submitStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="80">
          <template #default="scope">
            {{ scope.row.score === null ? '-' : scope.row.score }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row)">
              查看/提交
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
import { getMyHomeworkList } from '@/api/homework'
import { ElMessage } from 'element-plus'

const router = useRouter()
const homeworkList = ref([])
const loading = ref(false)
const searchForm = reactive({
  keyword: '',
  status: null,
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const fetchHomeworks = async () => {
  loading.value = true
  try {
    const res = await getMyHomeworkList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    })
    if (res.code === 200) {
      homeworkList.value = res.data
      // API文档中 my-homework 返回的是数组，没有分页信息。这里假设后端会返回total
      // 如果后端不返回total，需要调整这里的逻辑或者前端自己计算总数
      // pagination.total = res.data.total // 如果后端返回分页信息
      pagination.total = res.data.length // 假设后端返回的是完整列表，前端自己计算总数
    }
  } catch (error) {
    console.error('获取作业列表失败:', error)
    ElMessage.error('获取作业列表失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchHomeworks()
}

const handleReset = () => {
  searchForm.keyword = '',
  searchForm.status = null,
  pagination.current = 1
  fetchHomeworks()
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchHomeworks()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  fetchHomeworks()
}

const formatSubmitStatus = (status) => {
  switch (status) {
    case 0: return '未提交'
    case 1: return '已提交'
    case 2: return '已批改'
    default: return '未知'
  }
}

const getSubmitStatusTagType = (status) => {
  switch (status) {
    case 0: return 'danger'
    case 1: return 'warning'
    case 2: return 'success'
    default: return 'info'
  }
}

const handleViewDetail = (row) => {
  router.push({ name: 'HomeworkSubmit', params: { id: row.homeworkId } })
}

onMounted(() => {
  fetchHomeworks()
})
</script>

<style scoped lang="scss">
.homework-list-container {
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
