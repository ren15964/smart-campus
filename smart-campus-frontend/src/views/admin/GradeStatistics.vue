<template>
  <div class="admin-grade-statistics">
    <h2>全校成绩统计</h2>
    <el-card>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="学期">
          <el-select v-model="queryForm.semester" placeholder="请选择学期" clearable @change="fetchStatistics">
            <el-option label="2023-2024-1" value="2023-2024-1"></el-option>
            <el-option label="2023-2024-2" value="2023-2024-2"></el-option>
            <el-option label="2024-2025-1" value="2024-2025-1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchStatistics">查询</el-button>
        </el-form-item>
      </el-form>

      <div class="statistics-display">
        <h3>统计概览</h3>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-statistic title="总平均分" :value="overallStatistics.averageScore.toFixed(2)" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="及格率" :value="(overallStatistics.passRate * 100).toFixed(2) + '%'" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="优秀率" :value="(overallStatistics.excellentRate * 100).toFixed(2) + '%'" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="总学生数" :value="overallStatistics.totalStudents" />
          </el-col>
        </el-row>

        <h3 style="margin-top: 20px;">成绩分布</h3>
        <div id="score-distribution-chart" style="width: 100%; height: 300px;"></div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
// 引入 Echarts (这里只是示意，实际项目需要安装和引入)
// import * as echarts from 'echarts';

const queryForm = reactive({
  semester: null,
});

const overallStatistics = reactive({
  averageScore: 0,
  passRate: 0,
  excellentRate: 0,
  totalStudents: 0,
});

const scoreDistributionData = ref([]);

// 模拟获取全校成绩统计数据
const getOverallGradeStatistics = (params) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log('模拟获取全校成绩统计:', params);
      resolve({
        code: 200,
        data: {
          averageScore: 78.5,
          passRate: 0.85,
          excellentRate: 0.25,
          totalStudents: 1200,
          scoreDistribution: [
            { name: '0-59', value: 150 },
            { name: '60-69', value: 300 },
            { name: '70-79', value: 400 },
            { name: '80-89', value: 250 },
            { name: '90-100', value: 100 },
          ],
        },
      });
    }, 800);
  });
};

const fetchStatistics = async () => {
  try {
    const res = await getOverallGradeStatistics(queryForm);
    if (res.code === 200) {
      Object.assign(overallStatistics, res.data);
      scoreDistributionData.value = res.data.scoreDistribution;
      renderChart();
    } else {
      ElMessage.error(res.msg || '获取统计数据失败');
    }
  } catch (error) {
    ElMessage.error('网络错误或服务器异常');
  }
};

const renderChart = () => {
  nextTick(() => {
    // 模拟 Echarts 渲染
    const chartDom = document.getElementById('score-distribution-chart');
    if (chartDom) {
      // const myChart = echarts.init(chartDom);
      // const option = {
      //   tooltip: { trigger: 'item' },
      //   legend: { orient: 'vertical', left: 'left' },
      //   series: [
      //     {
      //       name: '成绩分布',
      //       type: 'pie',
      //       radius: '50%',
      //       data: scoreDistributionData.value,
      //       emphasis: {
      //         itemStyle: {
      //           shadowBlur: 10,
      //           shadowOffsetX: 0,
      //           shadowColor: 'rgba(0, 0, 0, 0.5)',
      //         },
      //       },
      //     },
      //   ],
      // };
      // myChart.setOption(option);
      console.log('Echarts 模拟渲染成功', scoreDistributionData.value);
      ElMessage.info('图表已模拟渲染，请自行集成 Echarts 库以显示实际图表。');
    }
  });
};

onMounted(() => {
  fetchStatistics();
});
</script>

<style scoped>
.admin-grade-statistics h2 {
  font-size: 28px;
  font-weight: 800;
  color: var(--app-text);
  letter-spacing: 0.5px;
  margin-bottom: 20px;
}

.admin-grade-statistics h3 {
  font-size: 20px;
  font-weight: 700;
  color: var(--app-text);
  margin-bottom: 15px; 
}
.admin-grade-statistics {
  padding: 24px;
}
.query-form {
  margin-bottom: 24px;
}
.statistics-display {
  margin-top: 24px;
}
.admin-grade-statistics .el-statistic {
  --el-statistic-content-font-size: 28px;
}

.admin-grade-statistics .el-statistic :deep(.el-statistic__head) {
  color: var(--app-text-muted);
}

.admin-grade-statistics .el-statistic :deep(.el-statistic__content) {
  color: var(--app-text);
}
</style>
