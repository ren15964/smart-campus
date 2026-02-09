<template>
  <div class="homework-manage-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>作业管理</span>
          <el-button type="primary" @click="handleAddHomework">发布作业</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入课程名称或作业标题" clearable />
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

      <el-table :data="homeworkList" v-loading="loading" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="title" label="作业标题" show-overflow-tooltip />
        <el-table-column prop="deadline" label="截止日期" width="180" />
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewSubmissions(scope.row)">
              查看提交
            </el-button>
            <el-button type="warning" link @click="handleEditHomework(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDeleteHomework(scope.row)">
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

    <!-- 发布/编辑作业弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑作业' : '发布作业'"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        :model="homeworkForm"
        :rules="formRules"
        ref="homeworkFormRef"
        label-width="100px"
      >
        <el-form-item label="课程选择" prop="scheduleId">
          <el-select
            v-model="homeworkForm.scheduleId"
            placeholder="请选择课程"
            filterable
            :disabled="isEdit"
            @focus="fetchTeacherCoursesOptions"
          >
            <el-option
              v-for="course in teacherCoursesOptions"
              :key="course.id"
              :label="`${course.courseName} (${course.semester})`"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="作业标题" prop="title">
          <el-input v-model="homeworkForm.title" />
        </el-form-item>
        <el-form-item label="作业内容" prop="content">
          <el-input v-model="homeworkForm.content" type="textarea" rows="5" />
        </el-form-item>
        <el-form-item label="截止日期" prop="deadline">
          <el-date-picker
            v-model="homeworkForm.deadline"
            type="datetime"
            placeholder="选择日期时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="总分" prop="totalScore">
          <el-input-number v-model="homeworkForm.totalScore" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="附件" prop="attachment">
          <el-input v-model="homeworkForm.attachment" placeholder="请输入附件URL（可选）" />
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
  getMyHomeworkList,
  publishHomework,
  updateHomework,
  deleteHomework,
} from '@/api/homework'
import { getTeacherCourses } from '@/api/course'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const homeworkList = ref([])
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
const homeworkFormRef = ref(null)
const teacherCoursesOptions = ref([])
const homeworkForm = reactive({
  id: null,
  scheduleId: null,
  title: '',
  content: '',
  deadline: '',
  totalScore: 100,
  attachment: '',
})

const formRules = {
  scheduleId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  title: [{ required: true, message: '请输入作业标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入作业内容', trigger: 'blur' }],
  deadline: [{ required: true, message: '请选择截止日期', trigger: 'change' }],
  totalScore: [{ required: true, message: '请输入总分', trigger: 'change' }],
}

// 获取教师发布的作业列表
const fetchHomeworks = async () => {
  loading.value = true
  try {
    // 注意：getMyHomeworkList 是学生端的接口，教师端应该有自己的接口或者通过参数区分
    // 这里暂时复用，但在实际后端接口明确后需要调整
    const res = await getMyHomeworkList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
      // role: 'teacher' // 假设可以根据角色筛选
    })
    if (res.code === 200) {
      homeworkList.value = res.data.records || res.data // 兼容后端可能返回数组或带records的分页对象
      pagination.total = res.data.total || res.data.length
    }
  } catch (error) {
    console.error('获取作业列表失败:', error)
    ElMessage.error('获取作业列表失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

// 获取教师所授课程选项
const fetchTeacherCoursesOptions = async () => {
  if (teacherCoursesOptions.value.length > 0) return
  try {
    const res = await getTeacherCourses({ current: 1, size: 1000 }) // 获取所有教师所授课程
    if (res.code === 200) {
      teacherCoursesOptions.value = res.data.records
    }
  } catch (error) {
    console.error('获取教师课程选项失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchHomeworks()
}

const handleReset = () => {
  searchForm.keyword = '',
  searchForm.semester = '',
  pagination.current = 1
  fetchHomeworks()
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchHomeworks()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  fetchHomeworks()
}

const handleAddHomework = () => {
  isEdit.value = false
  dialogVisible.value = true
  nextTick(() => {
    homeworkFormRef.value?.resetFields()
    Object.assign(homeworkForm, {
      id: null,
      scheduleId: null,
      title: '',
      content: '',
      deadline: '',
      totalScore: 100,
      attachment: '',
    })
  })
}

const handleEditHomework = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  nextTick(() => {
    Object.assign(homeworkForm, row)
  })
}

const handleDeleteHomework = async (row) => {
  ElMessageBox.confirm(
    `确定要删除作业《${row.title}》吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await deleteHomework(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchHomeworks()
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
  homeworkFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateHomework(homeworkForm.id, homeworkForm)
        } else {
          res = await publishHomework(homeworkForm)
        }
        if (res.code === 200) {
          ElMessage.success(`${isEdit.value ? '编辑' : '发布'}成功`)
          dialogVisible.value = false
          fetchHomeworks()
        } else {
          ElMessage.error(res.message || `${isEdit.value ? '编辑' : '发布'}失败`)
        }
      } catch (error) {
        console.error(`${isEdit.value ? '编辑' : '发布'}失败:`, error)
        ElMessage.error(`${isEdit.value ? '编辑' : '发布'}失败，请稍后再试`)
      }
    }
  })
}

const handleDialogClose = () => {
  homeworkFormRef.value?.resetFields()
  Object.assign(homeworkForm, {
    id: null,
    scheduleId: null,
    title: '',
    content: '',
    deadline: '',
    totalScore: 100,
    attachment: '',
  })
}

const handleViewSubmissions = (row) => {
  router.push({ name: 'HomeworkGrade', params: { id: row.id } })
}

onMounted(() => {
  fetchHomeworks()
})
</script>

<style scoped lang="scss">
.homework-manage-container {
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

.el-select,
.el-date-picker,
.el-input-number {
  width: 100%;
}
</style>
