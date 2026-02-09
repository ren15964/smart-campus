<template>
  <div class="admin-grade-exception-monitor">
    <h2>成绩异常监控</h2>
    <el-card>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="选择课程">
          <el-select v-model="queryForm.courseId" placeholder="请选择课程" clearable @change="fetchExceptions">
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchExceptions">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="exceptionList" border style="width: 100%">
        <el-table-column prop="studentName" label="学生姓名" width="120"></el-table-column>
        <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
        <el-table-column prop="score" label="成绩" width="100"></el-table-column>
        <el-table-column prop="exceptionType" label="异常类型" width="150">
          <template #default="scope">
            <el-tag :type="scope.row.exceptionType === '不及格' ? 'danger' : 'warning'">{{ scope.row.exceptionType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="viewDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';

const queryForm = reactive({
  courseId: null,
});

const courseList = ref([]);
const exceptionList = ref([]);

// 模拟获取教师所授课程列表 (复用之前模拟的，实际应有独立的管理员接口)
const getAdminCourses = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve([
        { id: 1, name: '数据结构' },
        { id: 2, name: '算法设计' },
      ]);
    }, 500);
  });
};

// 模拟获取异常成绩列表
const getGradeExceptions = (params) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log('模拟获取异常成绩:', params);
      if (params.courseId === 1) {
        resolve({
          code: 200,
          data: [
            { id: 1, studentName: '张三', courseName: '数据结构', score: 55, exceptionType: '不及格', description: '期末成绩低于60分' },
            { id: 2, studentName: '王五', courseName: '数据结构', score: 62, exceptionType: '成绩波动大', description: '平时分90，期末分30' },
          ],
        });
      } else if (params.courseId === 2) {
        resolve({
          code: 200,
          data: [
            { id: 3, studentName: '赵六', courseName: '算法设计', score: 58, exceptionType: '不及格', description: '总成绩低于60分' },
          ],
        });
      } else {
        resolve({ code: 200, data: [] });
      }
    }, 800);
  });
};

const fetchExceptions = async () => {
  try {
    const res = await getGradeExceptions(queryForm);
    if (res.code === 200) {
      exceptionList.value = res.data;
    } else {
      ElMessage.error(res.msg || '获取异常成绩失败');
    }
  } catch (error) {
    ElMessage.error('网络错误或服务器异常');
  }
};

const viewDetail = (row) => {
  ElMessage.info(`查看 ${row.studentName} 的 ${row.courseName} 成绩详情 (暂未实现)`);
  // TODO: 跳转到学生成绩详情页或弹出详情对话框
};

onMounted(async () => {
  courseList.value = await getAdminCourses();
  if (courseList.value.length > 0) {
    queryForm.courseId = courseList.value[0].id;
    fetchExceptions();
  }
});
</script>

<style scoped>
.admin-grade-exception-monitor {
  padding: 20px;
}
.query-form {
  margin-bottom: 20px;
}
</style>
