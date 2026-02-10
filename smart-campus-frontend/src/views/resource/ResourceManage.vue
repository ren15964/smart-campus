<template>
  <div class="resource-manage-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>课程资源管理 - {{ courseName }}</span>
          <el-button type="primary" @click="handleAddResource">上传资源</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="章节">
          <el-input v-model="searchForm.chapter" placeholder="请输入章节" clearable />
        </el-form-item>
        <el-form-item label="资源名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入资源名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="resourceList" v-loading="loading" border style="width: 100%">
        <template #empty>
          <AppEmpty description="暂无资源">
            <el-button type="primary" @click="handleReset">清空筛选</el-button>
          </AppEmpty>
        </template>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="resourceName" label="资源名称" show-overflow-tooltip />
        <el-table-column prop="resourceType" label="类型" width="100" />
        <el-table-column prop="chapter" label="章节" width="120" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="downloadCount" label="下载量" width="100" />
        <el-table-column prop="createTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" link @click="handleEditResource(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDeleteResource(scope.row)">
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

    <!-- 上传/编辑资源弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑资源' : '上传资源'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        :model="resourceForm"
        :rules="formRules"
        ref="resourceFormRef"
        label-width="100px"
      >
        <el-form-item label="所属课程" prop="scheduleId" v-if="!isEdit">
          <el-select
            v-model="resourceForm.scheduleId"
            placeholder="请选择课程"
            filterable
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
        <el-form-item label="资源名称" prop="resourceName">
          <el-input v-model="resourceForm.resourceName" />
        </el-form-item>
        <el-form-item label="资源类型" prop="resourceType">
          <el-select v-model="resourceForm.resourceType" placeholder="请选择资源类型">
            <el-option label="文档" value="document" />
            <el-option label="视频" value="video" />
            <el-option label="图片" value="image" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="章节" prop="chapter">
          <el-input v-model="resourceForm.chapter" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="resourceForm.description" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="文件上传" prop="file" v-if="!isEdit">
          <el-upload
            class="upload-demo"
            action="/api/file/upload" 
            :on-success="handleFileUploadSuccess"
            :on-error="handleFileUploadError"
            :on-remove="handleFileRemove"
            :before-upload="beforeFileUpload"
            :limit="1"
            :file-list="fileList"
          >
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip" style="color: var(--app-text-muted);">可上传各类文件，大小不超过50MB</div>
            </template>
          </el-upload>
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
import { ref, reactive, onMounted, nextTick, computed, watch } from 'vue'
import { getResourceList, uploadResource, updateResource, deleteResource } from '@/api/resource'
import { getTeacherCourses } from '@/api/course'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppEmpty from '@/components/AppEmpty.vue'

const allResources = ref([])
const resourceList = ref([])
const loading = ref(false)
const searchForm = reactive({
  chapter: '',
  keyword: '',
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const pagedResources = computed(() => {
  const start = (pagination.current - 1) * pagination.size
  const end = start + pagination.size
  return allResources.value.slice(start, end)
})

watch(
  () => [pagination.current, pagination.size, allResources.value.length],
  () => {
    resourceList.value = pagedResources.value
  }
)

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const resourceFormRef = ref(null)
const teacherCoursesOptions = ref([])
const resourceForm = reactive({
  id: null,
  scheduleId: null,
  resourceName: '',
  resourceType: 'document',
  chapter: '',
  description: '',
  filePath: '',
})

const fileList = ref([]) // 用于el-upload的文件列表

const formRules = {
  scheduleId: [{ required: true, message: '请选择所属课程', trigger: 'change' }],
  resourceName: [{ required: true, message: '请输入资源名称', trigger: 'blur' }],
  resourceType: [{ required: true, message: '请选择资源类型', trigger: 'change' }],
  // 文件上传校验，在非编辑模式下且没有filePath时需要文件
  file: [{
    required: false,
    message: '请上传文件',
    trigger: 'change',
    validator: (rule, value, callback) => {
      if (!isEdit.value && !resourceForm.filePath && fileList.value.length === 0) {
        callback(new Error(rule.message))
      } else {
        callback()
      }
    },
  }],
}

// 获取资源列表
const fetchResources = async () => {
  loading.value = true
  try {
    const res = await getResourceList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
      // scheduleId: 可以根据实际需求从路由或某个全局状态中获取教师当前管理的课程ID
      // 这里简化为不传scheduleId，让后端返回教师所有课程资源，或者前端筛选
    })
    if (res.code === 200) {
      allResources.value = Array.isArray(res.data) ? res.data : []
      pagination.total = allResources.value.length
      resourceList.value = pagedResources.value
    }
  } catch (error) {
    console.error('获取资源列表失败:', error)
    ElMessage.error('获取资源失败，请稍后再试')
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
  fetchResources()
}

const handleReset = () => {
  searchForm.chapter = '',
  searchForm.keyword = '',
  pagination.current = 1
  fetchResources()
}

const handleSizeChange = (val) => {
  pagination.size = val
}

const handleCurrentChange = (val) => {
  pagination.current = val
}

const handleAddResource = () => {
  isEdit.value = false
  dialogVisible.value = true
  nextTick(() => {
    resourceFormRef.value?.resetFields()
    Object.assign(resourceForm, {
      id: null,
      scheduleId: null,
      resourceName: '',
      resourceType: 'document',
      chapter: '',
      description: '',
      filePath: '',
    })
    fileList.value = []
  })
}

const handleEditResource = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  nextTick(() => {
    Object.assign(resourceForm, row)
    fileList.value = row.filePath ? [{ name: row.resourceName, url: row.filePath }] : []
  })
}

const handleDeleteResource = async (row) => {
  ElMessageBox.confirm(
    `确定要删除资源《${row.resourceName}》吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await deleteResource(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchResources()
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
  resourceFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        let res
        const formData = new FormData()
        Object.keys(resourceForm).forEach(key => {
          if (key !== 'id' && resourceForm[key] !== null) {
            formData.append(key, resourceForm[key])
          }
        })

        if (isEdit.value) {
          // 编辑时，文件不强制上传，只更新文字信息
          res = await updateResource(resourceForm.id, resourceForm)
        } else {
          // 新增时，如果有文件，则通过formData上传
          if (fileList.value.length > 0 && fileList.value[0].raw) {
            formData.append('file', fileList.value[0].raw)
          } else if (!resourceForm.filePath) {
            ElMessage.error('请上传文件或提供文件路径')
            return
          }
          res = await uploadResource(formData)
        }

        if (res.code === 200) {
          ElMessage.success(`${isEdit.value ? '编辑' : '上传'}成功`)
          dialogVisible.value = false
          fetchResources()
        } else {
          ElMessage.error(res.message || `${isEdit.value ? '编辑' : '上传'}失败`)
        }
      } catch (error) {
        console.error(`${isEdit.value ? '编辑' : '上传'}失败:`, error)
        ElMessage.error(`${isEdit.value ? '编辑' : '上传'}失败，请稍后再试`)
      }
    }
  })
}

const handleDialogClose = () => {
  resourceFormRef.value?.resetFields()
  Object.assign(resourceForm, {
    id: null,
    scheduleId: null,
    resourceName: '',
    resourceType: 'document',
    chapter: '',
    description: '',
    filePath: '',
  })
  fileList.value = []
}

// 文件上传相关
const handleFileUploadSuccess = (response, file, fileLists) => {
  if (response.code === 200) {
    resourceForm.filePath = response.data.filePath
    ElMessage.success('文件上传成功')
  } else {
    fileList.value = [] // 清空文件列表
    ElMessage.error(response.message || '文件上传失败')
  }
}

const handleFileUploadError = (error, file, fileLists) => {
  ElMessage.error('文件上传失败，请重试')
  console.error('文件上传失败:', error)
}

const handleFileRemove = (file, fileLists) => {
  resourceForm.filePath = null
}

const beforeFileUpload = (file) => {
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('上传附件大小不能超过 50MB!')
  }
  return isLt50M
}

onMounted(() => {
  fetchResources()
})
</script>

<style scoped lang="scss">
.resource-manage-container {
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 24px; // 略微增大间距
  padding-bottom: 15px; // 调整内边距
  border-bottom: 1px solid var(--app-border); // 使用全局边框变量
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px; // 略微增大间距
}

.el-select,
.el-input-number {
  width: 100%;
}
</style>
