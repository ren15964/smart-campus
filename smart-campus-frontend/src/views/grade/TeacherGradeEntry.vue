<template>
  <div class="teacher-grade-entry">
    <h2>教师成绩录入</h2>
    <el-card>
      <el-form :model="gradeForm" label-width="120px" style="max-width: 600px; margin: 20px auto;">
        <el-form-item label="选择课程">
          <el-select v-model="gradeForm.courseId" placeholder="请选择课程" @change="handleCourseChange">
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择学生">
          <el-select v-model="gradeForm.studentId" placeholder="请选择学生">
            <el-option
              v-for="student in studentList"
              :key="student.id"
              :label="student.name"
              :value="student.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="平时分">
          <el-input-number v-model="gradeForm.usualScore" :min="0" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item label="期末分">
          <el-input-number v-model="gradeForm.finalScore" :min="0" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitGrade">保存成绩</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { inputGrade } from '@/api/grade';

const gradeForm = reactive({
  courseId: null,
  studentId: null,
  usualScore: 0,
  finalScore: 0,
});

const courseList = ref([]);
const studentList = ref([]);

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

// 模拟根据课程ID获取学生列表
const getStudentsByCourseId = (courseId) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      if (courseId === 1) {
        resolve([
          { id: 101, name: '张三' },
          { id: 102, name: '李四' },
        ]);
      } else if (courseId === 2) {
        resolve([
          { id: 103, name: '王五' },
          { id: 104, name: '赵六' },
        ]);
      } else {
        resolve([]);
      }
    }, 500);
  });
};

const handleCourseChange = async (courseId) => {
  studentList.value = await getStudentsByCourseId(courseId);
  gradeForm.studentId = null; // 重置学生选择
};

const submitGrade = async () => {
  console.log('提交成绩:', gradeForm);
  if (!gradeForm.courseId || !gradeForm.studentId) {
    ElMessage.error('请选择课程和学生');
    return;
  }

  // 模拟调用后端API提交成绩
  try {
    const res = await inputGrade(gradeForm);
    if (res.code === 200) {
      ElMessage.success('成绩提交成功！');
      // 可以清空表单或进行其他操作
      gradeForm.usualScore = 0;
      gradeForm.finalScore = 0;
    } else {
      ElMessage.error(res.msg || '成绩提交失败');
    }
  } catch (error) {
    ElMessage.error('网络错误或服务器异常');
  }
};

onMounted(async () => {
  courseList.value = await getTeacherCourses();
});
</script>

<style scoped>
.teacher-grade-entry h2 {
  font-size: 28px;
  font-weight: 800;
  color: var(--app-text);
  letter-spacing: 0.5px;
  margin-bottom: 20px;
}
.teacher-grade-entry {
  padding: 24px;
}
</style>