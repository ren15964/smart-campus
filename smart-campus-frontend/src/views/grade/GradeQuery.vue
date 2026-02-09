<template>
  <div class="grade-query-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>成绩查询</span>
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
          <el-button type="success" @click="exportGrades" :disabled="!gradeList.length">导出成绩</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="gradeList" v-loading="loading" border style="width: 100%; margin-bottom: 20px;">
        <template #empty>
          <AppEmpty description="暂无成绩数据">
            <el-button type="primary" @click="handleReset">清空筛选</el-button>
          </AppEmpty>
        </template>
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="courseCode" label="课程编码" width="120" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="teacherName" label="任课教师" width="120" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="usualScore" label="平时成绩" width="100" />
        <el-table-column prop="examScore" label="期末成绩" width="100" />
        <el-table-column prop="totalScore" label="总成绩" width="100" />
        <el-table-column prop="gpa" label="绩点" width="80" />
      </el-table>

      <el-card class="statistics-card">
        <template #header>
          <span>成绩统计</span>
        </template>
        <div class="statistics-content">
          <div class="stat-item">
            <span>总学分:</span>
            <span class="stat-value">{{ gradeStatistics.totalCredits }}</span>
          </div>
          <div class="stat-item">
            <span>平均绩点:</span>
            <span class="stat-value">{{ gradeStatistics.averageGpa }}</span>
          </div>
          <div class="stat-item">
            <span>通过课程数:</span>
            <span class="stat-value">{{ gradeStatistics.passedCourses }}</span>
          </div>
          <div class="stat-item">
            <span>未通过课程数:</span>
            <span class="stat-value">{{ gradeStatistics.failedCourses }}</span>
          </div>
        </div>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyGrades, getGradeStatistics } from '@/api/grade'
import { ElMessage } from 'element-plus'
import AppEmpty from '@/components/AppEmpty.vue'

const gradeList = ref([])
const loading = ref(false)
const searchForm = reactive({
  semester: '',
})
const gradeStatistics = reactive({
  totalCredits: 0,
  averageGpa: 0,
  passedCourses: 0,
  failedCourses: 0,
})

const fetchGrades = async () => {
  loading.value = true
  try {
    const res = await getMyGrades(searchForm)
    if (res.code === 200) {
      gradeList.value = res.data
    }
  } catch (error) {
    console.error('获取个人成绩失败:', error)
    ElMessage.error('获取个人成绩失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const fetchStatistics = async () => {
  try {
    const res = await getGradeStatistics()
    if (res.code === 200) {
      Object.assign(gradeStatistics, res.data)
    }
  } catch (error) {
    console.error('获取成绩统计失败:', error)
    ElMessage.error('获取成绩统计失败，请稍后再试')
  }
}

const handleSearch = () => {
  fetchGrades()
}

const handleReset = () => {
  searchForm.semester = ''
  fetchGrades()
}

// 导出成绩为CSV
const exportGrades = () => {
  if (gradeList.value.length === 0) {
    ElMessage.warning('没有成绩数据可以导出！');
    return;
  }

  const headers = ['学期', '课程编码', '课程名称', '任课教师', '学分', '平时成绩', '期末成绩', '总成绩', '绩点'];
  const rows = gradeList.value.map(grade => [
    grade.semester,
    grade.courseCode,
    grade.courseName,
    grade.teacherName,
    grade.credit,
    grade.usualScore,
    grade.examScore,
    grade.totalScore,
    grade.gpa
  ]);

  let csvContent = headers.join(',') + '\n';
  rows.forEach(row => {
    csvContent += row.join(',') + '\n';
  });

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = `student_grades_${searchForm.semester || 'all'}.csv`;
  link.click();
  URL.revokeObjectURL(link.href);
  ElMessage.success('成绩导出成功！');
};

onMounted(() => {
  fetchGrades()
  fetchStatistics()
})
</script>

<style scoped lang="scss">
.grade-query-container {
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

.statistics-card {
  margin-top: 20px;
}

.statistics-content {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 15px;
  background-color: #f5f7fa;
  border-radius: 5px;
  min-width: 150px;

  span:first-child {
    color: #909399;
    font-size: 14px;
    margin-bottom: 5px;
  }

  .stat-value {
    font-size: 20px;
    font-weight: bold;
    color: #303133;
  }
}
</style>
