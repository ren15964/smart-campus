<template>
  <div class="course-students-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ courseName }} 学生名单</span>
          <el-button type="primary" link @click="handleBack">返回</el-button>
        </div>
      </template>

      <el-table :data="studentList" v-loading="loading" border style="width: 100%">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column prop="usualScore" label="平时成绩" width="100" />
        <el-table-column prop="examScore" label="期末成绩" width="100" />
        <el-table-column prop="totalScore" label="总成绩" width="100" />
        <el-table-column prop="gpa" label="绩点" width="80" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCourseStudentGrades } from '@/api/grade'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const scheduleId = route.params.scheduleId

const courseName = ref('未知课程') // 课程名称可以从上一个页面传递，或者通过API获取
const studentList = ref([])
const loading = ref(false)

const fetchStudentList = async () => {
  if (!scheduleId) {
    ElMessage.error('缺少开课计划ID')
    return
  }

  loading.value = true
  try {
    const res = await getCourseStudentGrades(scheduleId)
    if (res.code === 200) {
      studentList.value = res.data
      if (res.data.length > 0) {
        // 假设课程名称可以从学生名单的第一个学生记录中获取，或者通过单独的API获取
        // 这里只是一个简化的处理
        courseName.value = res.data[0].courseName || '未知课程'
      }
    }
  } catch (error) {
    console.error('获取学生名单失败:', error)
    ElMessage.error('获取学生名单失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  fetchStudentList()
})
</script>

<style scoped lang="scss">
.course-students-container {
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
