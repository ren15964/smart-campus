<template>
  <div class="admin-grade-audit">
    <h2>成绩审核管理</h2>
    <el-card>
      <el-table :data="auditList" border style="width: 100%">
        <el-table-column prop="studentName" label="学生姓名" width="120"></el-table-column>
        <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
        <el-table-column prop="originalScore" label="原成绩" width="100"></el-table-column>
        <el-table-column prop="modifiedScore" label="修改后成绩" width="120"></el-table-column>
        <el-table-column prop="reason" label="修改原因"></el-table-column>
        <el-table-column prop="teacherName" label="提交教师" width="120"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button link type="success" size="small" @click="approveGrade(scope.row)">批准</el-button>
            <el-button link type="danger" size="small" @click="rejectGrade(scope.row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const auditList = ref([]);

// 模拟获取待审核的成绩列表
const getPendingGradeAudits = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        data: [
          { id: 1, studentName: '张三', courseName: '数据结构', originalScore: 80, modifiedScore: 85, reason: '平时分录入错误', teacherName: '王老师' },
          { id: 2, studentName: '李四', courseName: '算法设计', originalScore: 75, modifiedScore: 78, reason: '期末卷面分有误', teacherName: '李老师' },
        ],
      });
    }, 800);
  });
};

// 模拟批准成绩修改
const approveGradeAudit = (id) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log(`批准成绩修改请求: ${id}`);
      resolve({ code: 200, msg: '成绩修改已批准' });
    }, 500);
  });
};

// 模拟拒绝成绩修改
const rejectGradeAudit = (id) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log(`拒绝成绩修改请求: ${id}`);
      resolve({ code: 200, msg: '成绩修改已拒绝' });
    }, 500);
  });
};

const fetchAudits = async () => {
  try {
    const res = await getPendingGradeAudits();
    if (res.code === 200) {
      auditList.value = res.data;
    } else {
      ElMessage.error(res.msg || '获取待审核成绩失败');
    }
  } catch (error) {
    ElMessage.error('网络错误或服务器异常');
  }
};

const approveGrade = async (row) => {
  ElMessageBox.confirm(
    `确认批准学生 ${row.studentName} 的课程 ${row.courseName} 成绩修改吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        const res = await approveGradeAudit(row.id);
        if (res.code === 200) {
          ElMessage.success(res.msg);
          fetchAudits(); // 刷新列表
        } else {
          ElMessage.error(res.msg || '批准失败');
        }
      } catch (error) {
        ElMessage.error('网络错误或服务器异常');
      }
    })
    .catch(() => {
      ElMessage.info('已取消批准');
    });
};

const rejectGrade = async (row) => {
  ElMessageBox.confirm(
    `确认拒绝学生 ${row.studentName} 的课程 ${row.courseName} 成绩修改吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        const res = await rejectGradeAudit(row.id);
        if (res.code === 200) {
          ElMessage.success(res.msg);
          fetchAudits(); // 刷新列表
        } else {
          ElMessage.error(res.msg || '拒绝失败');
        }
      } catch (error) {
        ElMessage.error('网络错误或服务器异常');
      }
    })
    .catch(() => {
      ElMessage.info('已取消拒绝');
    });
};

onMounted(() => {
  fetchAudits();
});
</script>

<style scoped>
.admin-grade-audit {
  padding: 20px;
}
</style>
