<template>
  <div class="selection-manage-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header" style="font-size: 20px; font-weight: 700; color: var(--app-text);">
          <span>选课管理</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="custom-tabs">
        <el-tab-pane label="选课时间设置" name="period">
          <el-form
            :model="periodForm"
            :rules="periodRules"
            ref="periodFormRef"
            label-width="120px"
            class="period-form"
            v-loading="loading"
          >
            <el-form-item label="选课开始时间" prop="startTime">
              <el-date-picker
                v-model="periodForm.startTime"
                type="datetime"
                placeholder="选择开始日期时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                clearable
              />
            </el-form-item>
            <el-form-item label="选课结束时间" prop="endTime">
              <el-date-picker
                v-model="periodForm.endTime"
                type="datetime"
                placeholder="选择结束日期时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                clearable
                :disabled-date="(date) => disabledEndDate(date, periodForm.startTime)"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSavePeriod">保存设置</el-button>
              <el-button @click="handleResetPeriod">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="选课统计" name="statistics">
          <div class="statistics-display" v-loading="loading">
            <el-descriptions
              class="margin-top"
              title="选课总体统计"
              :column="3"
              border
            >
              <el-descriptions-item>
                <template #label>
                  <el-icon><Tickets /></el-icon>
                  总课程数
                </template>
                {{ statistics.totalCourses }}
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <el-icon><Avatar /></el-icon>
                  总学生数
                </template>
                {{ statistics.totalStudents }}
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <el-icon><Calendar /></el-icon>
                  平均选课门数
                </template>
                {{ statistics.averageCoursesPerStudent?.toFixed(2) || 0 }}
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <el-icon><DataAnalysis /></el-icon>
                  热门课程（前3）
                </template>
                <div v-if="statistics.hotCourses && statistics.hotCourses.length > 0">
                  <p v-for="(course, index) in statistics.hotCourses" :key="index">
                    {{ course.courseName }} ({{ course.selectedCount }}人)
                  </p>
                </div>
                <span v-else>暂无数据</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <el-icon><TrendCharts /></el-icon>
                  选课趋势
                </template>
                <span>（待图表实现）</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import {
  getSelectionPeriod,
  saveSelectionPeriod,
  getSelectionStatistics,
} from '@/api/course-selection'
import { ElMessage } from 'element-plus'
import { Tickets, Avatar, Calendar, DataAnalysis, TrendCharts } from '@element-plus/icons-vue'

const activeTab = ref('period')
const loading = ref(false)

// 选课时间设置
const periodFormRef = ref(null)
const periodForm = reactive({
  startTime: null,
  endTime: null,
})

const periodRules = {
  startTime: [{ required: true, message: '请选择选课开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择选课结束时间', trigger: 'change' }],
}

const fetchSelectionPeriod = async () => {
  loading.value = true
  try {
    const res = await getSelectionPeriod()
    if (res.code === 200 && res.data) {
      periodForm.startTime = res.data.startTime
      periodForm.endTime = res.data.endTime
    }
  } catch (error) {
    console.error('获取选课时间失败:', error)
    ElMessage.error('获取选课时间失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleSavePeriod = () => {
  periodFormRef.value?.validate(async (valid) => {
    if (valid) {
      if (new Date(periodForm.startTime).getTime() >= new Date(periodForm.endTime).getTime()) {
        ElMessage.error('结束时间必须晚于开始时间')
        return
      }
      try {
        const res = await saveSelectionPeriod(periodForm)
        if (res.code === 200) {
          ElMessage.success('选课时间设置成功')
        } else {
          ElMessage.error(res.message || '选课时间设置失败')
        }
      } catch (error) {
        console.error('保存选课时间失败:', error)
        ElMessage.error('保存选课时间失败，请稍后再试')
      }
    }
  })
}

const handleResetPeriod = () => {
  fetchSelectionPeriod() // 重置为当前已保存的时间
}

const disabledEndDate = (date, startTime) => {
  if (!startTime) return false
  const start = new Date(startTime)
  return date.getTime() < start.getTime()
}

// 选课统计
const statistics = reactive({
  totalCourses: 0,
  totalStudents: 0,
  averageCoursesPerStudent: 0,
  hotCourses: [],
})

const fetchSelectionStatistics = async () => {
  loading.value = true
  try {
    const res = await getSelectionStatistics()
    if (res.code === 200 && res.data) {
      Object.assign(statistics, res.data)
    }
  } catch (error) {
    console.error('获取选课统计失败:', error)
    ElMessage.error('获取选课统计失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

// 监听tab切换，加载对应数据
watch(activeTab, (newTab) => {
  if (newTab === 'period') {
    fetchSelectionPeriod()
  } else if (newTab === 'statistics') {
    fetchSelectionStatistics()
  }
})

onMounted(() => {
  fetchSelectionPeriod()
  fetchSelectionStatistics()
})
</script>

<style scoped lang="scss">
.selection-manage-container {
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.custom-tabs {
  margin-top: 24px;
}

.period-form {
  width: min(600px, 100%);
  margin: 20px auto;
}

.el-date-picker {
  width: 100%;
}

.statistics-display {
  padding: 0;
}

:deep(.el-descriptions__label) {
  color: var(--app-text-muted);
}

:deep(.el-descriptions__content) {
  color: var(--app-text);
}
</style>
