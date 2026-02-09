<template>
  <div class="schedule-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的课表</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="学期">
          <el-select v-model="searchForm.semester" placeholder="请选择学期" clearable @change="handleSemesterChange">
            <el-option label="2023-2024-1" value="2023-2024-1" />
            <el-option label="2023-2024-2" value="2023-2024-2" />
            <el-option label="2024-2025-1" value="2024-2025-1" />
          </el-select>
        </el-form-item>
        <el-form-item label="周次">
          <el-select v-model="searchForm.week" placeholder="请选择周次" clearable @change="handleWeekChange">
            <el-option v-for="i in 20" :key="i" :label="`第 ${i} 周`" :value="i" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchSchedule">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="schedule-content" v-loading="loading">
        <div class="time-axis">
          <div class="time-cell header">时间</div>
          <div class="time-cell" v-for="time in timeSlots" :key="time">{{ time }}</div>
        </div>
        <div class="week-view">
          <div class="week-day" v-for="day in weekDays" :key="day.value">
            <div class="day-header">{{ day.label }}</div>
            <div class="course-slot" v-for="time in timeSlots" :key="time">
              <div
                v-for="course in getCoursesAtTime(day.value, time)"
                :key="course.id"
                class="course-item"
                :style="getCourseItemStyle(course, time)"
              >
                {{ course.courseName }} <br />
                {{ course.teacherName }} <br />
                {{ course.classroom }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getWeekViewSchedule } from '@/api/schedule'
import { ElMessage } from 'element-plus'
import { getCurrentSemester, getCurrentWeek } from '@/utils/date'

const scheduleData = ref({}) // 存储从后端获取的课表数据
const loading = ref(false)
const searchForm = reactive({
  semester: getCurrentSemester(),
  week: getCurrentWeek(),
})

const weekDays = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 },
  { label: '周日', value: 7 },
]

const timeSlots = [
  '08:00', '09:00', '10:00', '11:00', '12:00',
  '13:00', '14:00', '15:00', '16:00', '17:00',
  '18:00', '19:00', '20:00', '21:00',
]

const fetchSchedule = async () => {
  loading.value = true
  try {
    const res = await getWeekViewSchedule(searchForm)
    if (res.code === 200) {
      scheduleData.value = res.data.courses || {}
    }
  } catch (error) {
    console.error('获取课表失败:', error)
    ElMessage.error('获取课表失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleSemesterChange = () => {
  fetchSchedule()
}

const handleWeekChange = () => {
  fetchSchedule()
}

const handleReset = () => {
  searchForm.semester = getCurrentSemester()
  searchForm.week = getCurrentWeek()
  fetchSchedule()
}

const getCoursesAtTime = (day, time) => {
  const dayCourses = scheduleData.value[day] || []
  return dayCourses.filter(course => {
    const [startHour, startMinute] = course.startTime.split(':').map(Number)
    const [endHour, endMinute] = course.endTime.split(':').map(Number)
    const [slotHour, slotMinute] = time.split(':').map(Number)

    const courseStartTimeInMinutes = startHour * 60 + startMinute
    const courseEndTimeInMinutes = endHour * 60 + endMinute
    const slotTimeInMinutes = slotHour * 60 + slotMinute

    // 课程开始时间早于或等于当前时间格，并且课程结束时间晚于当前时间格
    return courseStartTimeInMinutes <= slotTimeInMinutes && courseEndTimeInMinutes > slotTimeInMinutes
  })
}

const getCourseItemStyle = (course, time) => {
  const [startHour, startMinute] = course.startTime.split(':').map(Number)
  const [endHour, endMinute] = course.endTime.split(':').map(Number)
  const [slotHour, slotMinute] = time.split(':').map(Number)

  const courseStartTimeInMinutes = startHour * 60 + startMinute
  const courseEndTimeInMinutes = endHour * 60 + endMinute
  const slotTimeInMinutes = slotHour * 60 + slotMinute

  const slotHeight = 60 // 每个时间格的高度

  // 计算相对于当前时间格的偏移量和高度
  const offsetMinutes = courseStartTimeInMinutes - slotTimeInMinutes
  const durationMinutes = courseEndTimeInMinutes - courseStartTimeInMinutes

  if (offsetMinutes < 0) {
    // 如果课程开始时间早于当前时间格，则从当前时间格顶部开始显示
    return {
      top: '0px',
      height: `${(durationMinutes + offsetMinutes) / 60 * slotHeight}px`,
      backgroundColor: '#409EFF',
      color: 'white',
      zIndex: 1,
    }
  }

  return {
    top: `${offsetMinutes / 60 * slotHeight}px`,
    height: `${durationMinutes / 60 * slotHeight}px`,
    backgroundColor: '#409EFF',
    color: 'white',
    zIndex: 1,
  }
}

onMounted(() => {
  fetchSchedule()
})
</script>

<style scoped lang="scss">
.schedule-container {
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

.schedule-content {
  display: flex;
  border: 1px solid #EBEEF5;
  min-height: 600px;
  position: relative;
}

.time-axis {
  width: 80px;
  border-right: 1px solid #EBEEF5;
  flex-shrink: 0;

  .time-cell {
    height: 60px; /* 每个时间格的高度 */
    line-height: 60px;
    text-align: center;
    font-size: 14px;
    color: #606266;
    border-bottom: 1px solid #EBEEF5;

    &.header {
      background-color: #F5F7FA;
      font-weight: bold;
    }
  }
}

.week-view {
  flex-grow: 1;
  display: flex;

  .week-day {
    flex: 1;
    border-right: 1px solid #EBEEF5;
    position: relative;

    &:last-child {
      border-right: none;
    }

    .day-header {
      height: 60px; /* 与时间轴头部高度一致 */
      line-height: 60px;
      text-align: center;
      background-color: #F5F7FA;
      font-weight: bold;
      border-bottom: 1px solid #EBEEF5;
    }

    .course-slot {
      height: 60px; /* 每个时间格的高度 */
      border-bottom: 1px dashed #E4E7ED;
      position: relative;

      .course-item {
        position: absolute;
        left: 2px;
        right: 2px;
        padding: 5px;
        border-radius: 4px;
        font-size: 12px;
        line-height: 1.2;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: normal;
      }
    }
  }
}
</style>
