<template>
  <div class="course-manage-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
          <el-button type="primary" @click="handleAddCourse">添加课程</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="searchForm.courseType" placeholder="请选择课程类型" clearable>
            <el-option label="必修" value="必修" />
            <el-option label="选修" value="选修" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="courseList" v-loading="loading" border style="width: 100%">
        <template #empty>
          <AppEmpty description="暂无课程">
            <el-button type="primary" @click="handleReset">清空筛选</el-button>
          </AppEmpty>
        </template>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="courseCode" label="课程编码" width="120" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="hours" label="学时" width="80" />
        <el-table-column prop="courseType" label="课程类型" width="100" />
        <el-table-column prop="description" label="课程简介" show-overflow-tooltip />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" link @click="handleEditCourse(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDeleteCourse(scope.row)">
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

    <!-- 添加/编辑课程弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑课程' : '添加课程'"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        :model="courseForm"
        :rules="formRules"
        ref="courseFormRef"
        label-width="100px"
      >
        <el-form-item label="课程编码" prop="courseCode">
          <el-input v-model="courseForm.courseCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="courseForm.courseName" />
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="courseForm.credit" :min="0" :max="10" :precision="1" />
        </el-form-item>
        <el-form-item label="学时" prop="hours">
          <el-input-number v-model="courseForm.hours" :min="0" :max="200" />
        </el-form-item>
        <el-form-item label="课程类型" prop="courseType">
          <el-select v-model="courseForm.courseType" placeholder="请选择课程类型">
            <el-option label="必修" value="必修" />
            <el-option label="选修" value="选修" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程简介" prop="description">
          <el-input v-model="courseForm.description" type="textarea" rows="3" />
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
import { getCourseList, addCourse, updateCourse, deleteCourse } from '@/api/course'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppEmpty from '@/components/AppEmpty.vue'

const courseList = ref([])
const loading = ref(false)
const searchForm = reactive({
  keyword: '',
  courseType: '',
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const courseFormRef = ref(null)
const courseForm = reactive({
  id: null,
  courseCode: '',
  courseName: '',
  credit: 0,
  hours: 0,
  courseType: '',
  description: '',
})

const formRules = {
  courseCode: [{ required: true, message: '请输入课程编码', trigger: 'blur' }],
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'change' }],
  hours: [{ required: true, message: '请输入学时', trigger: 'change' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
}

const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await getCourseList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    })
    if (res.code === 200) {
      courseList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchCourses()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.courseType = ''
  pagination.current = 1
  fetchCourses()
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchCourses()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  fetchCourses()
}

const handleAddCourse = () => {
  isEdit.value = false
  dialogVisible.value = true
  // nextTick(() => {
  //   courseFormRef.value?.resetFields()
  // })
}

const handleEditCourse = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  nextTick(() => {
    Object.assign(courseForm, row)
  })
}

const handleDeleteCourse = async (row) => {
  ElMessageBox.confirm(
    `确定要删除课程《${row.courseName}》吗?`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await deleteCourse(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchCourses()
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
  courseFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateCourse(courseForm.id, courseForm)
        } else {
          res = await addCourse(courseForm)
        }
        if (res.code === 200) {
          ElMessage.success(`${isEdit.value ? '编辑' : '添加'}成功`)
          dialogVisible.value = false
          fetchCourses()
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
  courseFormRef.value?.resetFields()
  Object.assign(courseForm, {
    id: null,
    courseCode: '',
    courseName: '',
    credit: 0,
    hours: 0,
    courseType: '',
    description: '',
  })
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped lang="scss">
.course-manage-container {
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
</style>
