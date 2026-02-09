<template>
  <div class="course-schedule-manage-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>开课计划管理</span>
          <div>
            <el-button type="success" @click="handleAutoSchedule">自动排课</el-button>
            <el-button type="primary" @click="handleAddCourseSchedule">添加开课计划</el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入课程名称或教师姓名" clearable />
        </el-form-item>
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

      <el-table :data="scheduleList" v-loading="loading" border style="width: 100%">
        <template #empty>
          <AppEmpty description="暂无开课计划">
            <el-button type="primary" @click="handleReset">清空筛选</el-button>
          </AppEmpty>
        </template>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="teacherName" label="任课教师" width="120" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="上课时间" width="180">
          <template #default="scope">
            {{ formatSchedule(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column prop="classroom" label="上课地点" width="120" />
        <el-table-column label="容量" width="100">
          <template #default="scope">
            {{ scope.row.selectedCount || 0 }} / {{ scope.row.capacity }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" link @click="handleEditCourseSchedule(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDeleteCourseSchedule(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑开课计划弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑开课计划' : '添加开课计划'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        :model="scheduleForm"
        :rules="formRules"
        ref="scheduleFormRef"
        label-width="120px"
      >
        <el-form-item label="选择课程" prop="courseId">
          <el-select
            v-model="scheduleForm.courseId"
            placeholder="请选择课程"
            filterable
            :disabled="isEdit"
            @focus="fetchCourseOptions"
          >
            <el-option
              v-for="course in courseOptions"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择教师" prop="teacherId">
          <el-select
            v-model="scheduleForm.teacherId"
            placeholder="请选择教师"
            filterable
            @focus="fetchTeacherOptions"
          >
            <el-option
              v-for="teacher in teacherOptions"
              :key="teacher.id"
              :label="teacher.realName"
              :value="teacher.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="scheduleForm.semester" placeholder="请选择学期">
            <el-option label="2023-2024-1" value="2023-2024-1" />
            <el-option label="2023-2024-2" value="2023-2024-2" />
            <el-option label="2024-2025-1" value="2024-2025-1" />
          </el-select>
        </el-form-item>
        <el-form-item label="上课星期" prop="weekDay">
          <el-select v-model="scheduleForm.weekDay" placeholder="请选择星期">
            <el-option label="周一" :value="1" />
            <el-option label="周二" :value="2" />
            <el-option label="周三" :value="3" />
            <el-option label="周四" :value="4" />
            <el-option label="周五" :value="5" />
            <el-option label="周六" :value="6" />
            <el-option label="周日" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始节次" prop="startTime">
          <el-time-select
            v-model="scheduleForm.startTime"
            start="08:00"
            step="00:10"
            end="22:00"
            placeholder="选择开始时间"
          />
        </el-form-item>
        <el-form-item label="结束节次" prop="endTime">
          <el-time-select
            v-model="scheduleForm.endTime"
            start="08:00"
            step="00:10"
            end="22:00"
            placeholder="选择结束时间"
            :min-time="scheduleForm.startTime"
          />
        </el-form-item>
        <el-form-item label="上课地点" prop="classroom">
          <el-input v-model="scheduleForm.classroom" placeholder="请输入上课地点" />
        </el-form-item>
        <el-form-item label="课程容量" prop="capacity">
          <el-input-number v-model="scheduleForm.capacity" :min="10" :max="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import {
  getCourseScheduleList,
  addCourseSchedule,
  updateCourseSchedule,
  deleteCourseSchedule,
} from '@/api/course-schedule'
import { getCourseList } from '@/api/course'
import { getUserList } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppEmpty from '@/components/AppEmpty.vue'

const scheduleList = ref([])
const loading = ref(false)
const searchForm = reactive({
  keyword: '',
  semester: '',
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const scheduleFormRef = ref(null)
const courseOptions = ref([])
const teacherOptions = ref([])
const scheduleForm = reactive({
  id: null,
  courseId: null,
  teacherId: null,
  semester: '',
  weekDay: null,
  startTime: '',
  endTime: '',
  classroom: '',
  capacity: 10,
})

const formRules = {
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  teacherId: [{ required: true, message: '请选择教师', trigger: 'change' }],
  semester: [{ required: true, message: '请选择学期', trigger: 'change' }],
  weekDay: [{ required: true, message: '请选择上课星期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  classroom: [{ required: true, message: '请输入上课地点', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入课程容量', trigger: 'change' }],
}

// 获取开课计划列表
const fetchCourseSchedules = async () => {
  loading.value = true
  try {
    const res = await getCourseScheduleList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    })
    if (res.code === 200) {
      scheduleList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取开课计划列表失败:', error)
    ElMessage.error('获取开课计划失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

// 获取课程选项
const fetchCourseOptions = async () => {
  if (courseOptions.value.length > 0) return
  try {
    const res = await getCourseList({ current: 1, size: 1000 }) // 获取所有课程
    if (res.code === 200) {
      courseOptions.value = res.data.records
    }
  } catch (error) {
    console.error('获取课程选项失败:', error)
  }
}

// 获取教师选项
const fetchTeacherOptions = async () => {
  if (teacherOptions.value.length > 0) return
  try {
    const res = await getUserList({ role: 'teacher', current: 1, size: 1000 }) // 获取所有教师
    if (res.code === 200) {
      teacherOptions.value = res.data.records
    }
  } catch (error) {
    console.error('获取教师选项失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchCourseSchedules()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.semester = ''
  pagination.current = 1
  fetchCourseSchedules()
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchCourseSchedules()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  fetchCourseSchedules()
}

const formatSchedule = (row) => {
  const weekDays = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
  return `${weekDays[row.weekDay]} ${row.startTime}-${row.endTime} @ ${row.classroom}`
}

const handleAddCourseSchedule = () => {
  isEdit.value = false
  dialogVisible.value = true
  // nextTick(() => {
  //   scheduleFormRef.value?.resetFields()
  // })
}

const handleEditCourseSchedule = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  nextTick(() => {
    Object.assign(scheduleForm, row)
  })
}

const handleDeleteCourseSchedule = async (row) => {
  ElMessageBox.confirm(
    `确定要删除ID为 ${row.id} 的开课计划吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await deleteCourseSchedule(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchCourseSchedules()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请稍后再试')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const handleSubmit = () => {
  scheduleFormRef.value?.validate(async (valid) => {
    if (valid) {
      const conflictMessage = checkScheduleConflict(scheduleForm);
      if (conflictMessage) {
        ElMessage.error(conflictMessage);
        return;
      }
      try {
        let res
        if (isEdit.value) {
          res = await updateCourseSchedule(scheduleForm.id, scheduleForm)
        } else {
          res = await addCourseSchedule(scheduleForm)
        }
        if (res.code === 200) {
          ElMessage.success(`${isEdit.value ? '编辑' : '添加'}成功`)
          dialogVisible.value = false
          fetchCourseSchedules()
        } else {
          ElMessage.error(res.message || `${isEdit.value ? '编辑' : '添加'}失败`)
        }
      } catch (error) {
        console.error(`${isEdit.value ? '编辑' : '添加'}失败:`, error)
        ElMessage.error(`${isEdit.value ? '编辑' : '添加'}失败，请稍后再试`)
      }
    }
  })
}

const handleDialogClose = () => {
  scheduleFormRef.value?.resetFields()
  Object.assign(scheduleForm, {
    id: null,
    courseId: null,
    teacherId: null,
    semester: '',
    weekDay: null,
    startTime: '',
    endTime: '',
    classroom: '',
    capacity: 10,
  })
}

// 模拟自动排课功能
const handleAutoSchedule = async () => {
  ElMessage.info('正在进行自动排课，请稍候...');
  // 模拟后端自动排课API调用
  await new Promise(resolve => setTimeout(resolve, 1500));
  ElMessage.success('自动排课完成，请检查排课结果。');
  fetchCourseSchedules(); // 刷新列表
};

// 模拟课表冲突检测
const checkScheduleConflict = (newSchedule) => {
  const newStart = parseTime(newSchedule.startTime);
  const newEnd = parseTime(newSchedule.endTime);

  for (const existingSchedule of scheduleList.value) {
    // 排除自身（编辑模式下）
    if (isEdit.value && existingSchedule.id === newSchedule.id) {
      continue;
    }

    // 检查是否是同一学期、同一星期
    if (existingSchedule.semester === newSchedule.semester && existingSchedule.weekDay === newSchedule.weekDay) {
      const existingStart = parseTime(existingSchedule.startTime);
      const existingEnd = parseTime(existingSchedule.endTime);

      // 检查时间是否有重叠
      if (Math.max(newStart, existingStart) < Math.min(newEnd, existingEnd)) {
        // 教师冲突
        if (existingSchedule.teacherId === newSchedule.teacherId) {
          return `教师 ${existingSchedule.teacherName} 在同一学期、同一时间段内已有排课：课程 [${existingSchedule.courseName}]，时间 ${formatSchedule(existingSchedule)}`;
        }
        // 教室冲突
        if (existingSchedule.classroom === newSchedule.classroom) {
          return `教室 ${existingSchedule.classroom} 在同一学期、同一时间段内已被占用：课程 [${existingSchedule.courseName}]，时间 ${formatSchedule(existingSchedule)}`;
        }
        // 学生冲突（假设：如果选课系统接入，这里可以添加学生选课冲突检测）
        // if (existingSchedule.courseId === newSchedule.courseId && existingSchedule.teacherId === newSchedule.teacherId) {
        //   // 可以在此处添加更复杂的学生选课冲突逻辑
        //   // 例如：检查学生是否已选择该课程，或者该学生是否在同一时间段内有其他课程
        //   return `学生（潜在）与课程 [${existingSchedule.courseName}] 发生时间冲突，时间 ${formatSchedule(existingSchedule)}`;
        // }
      }
    }
  }
  return null; // 无冲突
};

// 时间字符串转分钟数
const parseTime = (timeStr) => {
  const [hours, minutes] = timeStr.split(':').map(Number);
  return hours * 60 + minutes;
};

onMounted(() => {
  fetchCourseSchedules()
})
</script>

<style scoped lang="scss">
.course-schedule-manage-container {
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

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.el-select {
  width: 100%;
}

.el-input-number {
  width: 100%;
}
</style>
