<template>
  <div class="course-selection-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>可选课程列表</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="searchForm.semester" placeholder="请选择学期" clearable>
            <el-option label="2023-2024-1" value="2023-2024-1" />
            <el-option label="2023-2024-2" value="2023-2024-2" />
            <el-option label="2024-2025-1" value="2024-2025-1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="courseList" v-loading="loading" border style="width: 100%">
        <template #empty>
          <AppEmpty description="暂无可选课程">
            <el-button type="primary" @click="handleReset">清空筛选</el-button>
          </AppEmpty>
        </template>
        <el-table-column prop="courseCode" label="课程编码" width="100" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="teacherName" label="任课教师" width="120" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column label="上课时间" width="150">
          <template #default="scope">
            {{ formatSchedule(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column prop="classroom" label="上课地点" width="120" />
        <el-table-column label="容量" width="100">
          <template #default="scope">
            {{ scope.row.selectedCount }} / {{ scope.row.capacity }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button
              type="primary"
              link
              :disabled="scope.row.isSelected || scope.row.selectedCount >= scope.row.capacity"
              @click="handleSelectCourse(scope.row)"
            >
              {{ scope.row.isSelected ? '已选' : '选课' }}
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
import { getAvailableCourses, selectCourse } from '@/api/course-selection'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppEmpty from '@/components/AppEmpty.vue'

const courseList = ref([])
const loading = ref(false)
const searchForm = reactive({
  keyword: '',
  semester: '',
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await getAvailableCourses({
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    })
    if (res.code === 200) {
      courseList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取可选课程失败:', error)
    ElMessage.error('获取课程失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchCourses()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.semester = ''
  pagination.current = 1
  fetchCourses()
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchCourses()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  fetchCourses()
}

const formatSchedule = (row) => {
  const weekDays = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
  return `${weekDays[row.weekDay]} ${row.startTime}-${row.endTime}`
}

const handleSelectCourse = async (row) => {
  ElMessageBox.confirm(
    `确定要选择课程《${row.courseName}》吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info',
    }
  ).then(async () => {
    try {
      const res = await selectCourse({ scheduleId: row.scheduleId })
      if (res.code === 200) {
        ElMessage.success('选课成功')
        fetchCourses()
        // 可能需要通知我的课程列表更新
      } else {
        ElMessage.error(res.message || '选课失败')
      }
    } catch (error) {
      console.error('选课失败:', error)
      ElMessage.error('选课失败，请稍后再试')
    }
  }).catch(() => {
    ElMessage.info('已取消选课')
  })
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped lang="scss">
.course-selection-container {
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
