<template>
  <div class="my-courses-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
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

      <el-table :data="myCoursesList" v-loading="loading" border style="width: 100%">
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
        <el-table-column prop="selectionTime" label="选课时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="danger" link @click="handleDropCourse(scope.row)">
              退课
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyCourses, dropCourse } from '@/api/course-selection'
import { ElMessage, ElMessageBox } from 'element-plus'

const myCoursesList = ref([])
const loading = ref(false)
const searchForm = reactive({
  semester: '',
})

const fetchMyCourses = async () => {
  loading.value = true
  try {
    const res = await getMyCourses(searchForm)
    if (res.code === 200) {
      myCoursesList.value = res.data
    }
  } catch (error) {
    console.error('获取我的课程失败:', error)
    ElMessage.error('获取我的课程失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchMyCourses()
}

const handleReset = () => {
  searchForm.semester = ''
  fetchMyCourses()
}

const formatSchedule = (row) => {
  const weekDays = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
  return `${weekDays[row.weekDay]} ${row.startTime}-${row.endTime}`
}

const handleDropCourse = async (row) => {
  ElMessageBox.confirm(
    `确定要退选课程《${row.courseName}》吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await dropCourse(row.selectionId)
      if (res.code === 200) {
        ElMessage.success('退课成功')
        fetchMyCourses()
        // 可能需要通知选课列表更新，如果学生同时打开了选课页面
      } else {
        ElMessage.error(res.message || '退课失败')
      }
    } catch (error) {
      console.error('退课失败:', error)
      ElMessage.error('退课失败，请稍后再试')
    }
  }).catch(() => {
    ElMessage.info('已取消退课')
  })
}

onMounted(() => {
  fetchMyCourses()
})
</script>

<style scoped lang="scss">
.my-courses-container {
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
</style>
