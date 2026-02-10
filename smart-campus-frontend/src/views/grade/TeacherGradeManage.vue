<template>
  <div class="teacher-grade-manage">
    <h2>教师成绩管理</h2>
    <el-card>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="选择课程">
          <el-select v-model="queryForm.courseId" placeholder="请选择课程" @change="fetchGrades">
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="exportGrades" :disabled="!gradeList.length">导出成绩</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="gradeList" border style="width: 100%">
        <el-table-column prop="studentName" label="学生姓名"></el-table-column>
        <el-table-column prop="usualScore" label="平时分"></el-table-column>
        <el-table-column prop="finalScore" label="期末分"></el-table-column>
        <el-table-column prop="totalScore" label="总分"></el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="editGrade(scope.row)">修改</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="修改成绩"
      width="500"
    >
      <el-form :model="currentGrade" label-width="100px">
        <el-form-item label="学生姓名">
          <el-input v-model="currentGrade.studentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="平时分">
          <el-input-number v-model="currentGrade.usualScore" :min="0" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item label="期末分">
          <el-input-number v-model="currentGrade.finalScore" :min="0" :max="100"></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditGrade">
            提交修改
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getCourseStudentGrades } from '@/api/grade';

const queryForm = reactive({
  courseId: null,
});

const courseList = ref([]);
const gradeList = ref([]);
const dialogVisible = ref(false);
const currentGrade = reactive({
  id: null,
  studentName: '',
  usualScore: 0,
  finalScore: 0,
  totalScore: 0,
});

// 模拟获取教师所授课程列表
const getTeacherCourses = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve([
        { id: 1, name: '数据结构' },
        { id: 2, name: '算法设计' },
      ]);
    }, 500);
  });
};

// 模拟修改成绩API (假设后端有一个updateGrade接口)
const updateGrade = (data) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      console.log("模拟修改成绩请求", data);
      // 模拟成功或失败
      if (Math.random() > 0.3) { // 70% 成功
        resolve({ code: 200, msg: '成绩修改成功，等待审批！' });
      } else { // 30% 失败
        reject({ code: 500, msg: '成绩修改失败，操作受限或网络异常。' });
      }
    }, 800);
  });
};

const fetchGrades = async () => {
  if (queryForm.courseId) {
    // 调用实际的API (模拟数据)
    const res = await getCourseStudentGrades(queryForm.courseId);
    // 假设res.data是成绩列表
    gradeList.value = res.data || [
      { id: 1, studentName: '张三', usualScore: 85, finalScore: 90, totalScore: 88, studentId: 101, courseId: 1 },
      { id: 2, studentName: '李四', usualScore: 70, finalScore: 75, totalScore: 72.5, studentId: 102, courseId: 1 },
    ];
  } else {
    gradeList.value = [];
  }
};

// 导出成绩为CSV
const exportGrades = () => {
  if (gradeList.value.length === 0) {
    ElMessage.warning('没有成绩数据可以导出！');
    return;
  }

  const headers = ['学生姓名', '平时分', '期末分', '总分'];
  const rows = gradeList.value.map(grade => [
    grade.studentName,
    grade.usualScore,
    grade.finalScore,
    grade.totalScore
  ]);

  let csvContent = headers.join(',') + '\n';
  rows.forEach(row => {
    csvContent += row.join(',') + '\n';
  });

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = `grades_${queryForm.courseId || 'all'}.csv`;
  link.click();
  URL.revokeObjectURL(link.href);
  ElMessage.success('成绩导出成功！');
};

const editGrade = (row) => {
  Object.assign(currentGrade, row);
  dialogVisible.value = true;
};

const submitEditGrade = async () => {
  console.log('提交修改成绩:', currentGrade);
  try {
    const res = await updateGrade(currentGrade); // 调用模拟的修改成绩API
    if (res.code === 200) {
      ElMessage.success(res.msg);
      // 重新获取成绩列表以更新显示
      fetchGrades();
    } else {
      ElMessage.error(res.msg || '成绩修改失败');
    }
  } catch (error) {
    ElMessage.error(error.msg || '网络错误或服务器异常');
  }
  dialogVisible.value = false;
};

onMounted(async () => {
  courseList.value = await getTeacherCourses();
  // 默认加载第一个课程的成绩
  if (courseList.value.length > 0) {
    queryForm.courseId = courseList.value[0].id;
    fetchGrades();
  }
});
</script>

<style scoped>
.teacher-grade-manage h2 {
  font-size: 28px;
  font-weight: 800;
  color: var(--app-text);
  letter-spacing: 0.5px;
  margin-bottom: 20px;
}
.teacher-grade-manage {
  padding: 24px;
}
.query-form {
  margin-bottom: 24px;
}
</style>