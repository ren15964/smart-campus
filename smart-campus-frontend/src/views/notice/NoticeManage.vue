<template>
  <div class="notice-manage-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>通知管理</span>
          <el-button type="primary" @click="handleAddNotice">发布通知</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="searchForm.keyword" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="searchForm.priority" placeholder="请选择优先级" clearable>
            <el-option label="普通" :value="1" />
            <el-option label="重要" :value="2" />
            <el-option label="紧急" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="noticeList" v-loading="loading" border style="width: 100%">
        <template #empty>
          <AppEmpty description="暂无通知">
            <el-button type="primary" @click="handleReset">清空筛选</el-button>
          </AppEmpty>
        </template>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="publisherName" label="发布人" width="120" />
        <el-table-column label="优先级" width="100">
          <template #default="scope">
            <el-tag :type="getPriorityTagType(scope.row.priority)">
              {{ formatPriority(scope.row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" link @click="handleEditNotice(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDeleteNotice(scope.row)">
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

    <!-- 发布/编辑通知弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑通知' : '发布通知'"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        :model="noticeForm"
        :rules="formRules"
        ref="noticeFormRef"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="noticeForm.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="noticeForm.content" type="textarea" rows="5" />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="noticeForm.priority" placeholder="请选择优先级">
            <el-option label="普通" :value="1" />
            <el-option label="重要" :value="2" />
            <el-option label="紧急" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布范围" prop="targetRole">
          <el-select v-model="noticeForm.targetRole" placeholder="请选择发布范围">
            <el-option label="所有用户" value="all" />
            <el-option label="学生" value="student" />
            <el-option label="教师" value="teacher" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="附件" prop="attachment">
          <el-input v-model="noticeForm.attachment" placeholder="请输入附件URL（可选）" />
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
  getNoticeList,
  publishNotice,
  updateNotice,
  deleteNotice,
} from '@/api/notice'
import AppEmpty from '@/components/AppEmpty.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const noticeList = ref([])
const loading = ref(false)
const searchForm = reactive({
  keyword: '',
  priority: null,
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const noticeFormRef = ref(null)
const noticeForm = reactive({
  id: null,
  title: '',
  content: '',
  priority: 1,
  targetRole: 'all', // 默认发布给所有用户
  attachment: '',
})

const formRules = {
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  targetRole: [{ required: true, message: '请选择发布范围', trigger: 'change' }],
}

const fetchNotices = async () => {
  loading.value = true
  try {
    const res = await getNoticeList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm,
    })
    if (res.code === 200) {
      noticeList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取通知列表失败:', error)
    ElMessage.error('获取通知失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchNotices()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.priority = null
  pagination.current = 1
  fetchNotices()
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchNotices()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  fetchNotices()
}

const formatPriority = (priority) => {
  switch (priority) {
    case 1: return '普通'
    case 2: return '重要'
    case 3: return '紧急'
    default: return '未知'
  }
}

const getPriorityTagType = (priority) => {
  switch (priority) {
    case 1: return 'info'
    case 2: return 'warning'
    case 3: return 'danger'
    default: return 'info'
  }
}

const handleAddNotice = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const handleEditNotice = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  nextTick(() => {
    Object.assign(noticeForm, row)
  })
}

const handleDeleteNotice = async (row) => {
  ElMessageBox.confirm(
    `确定要删除通知《${row.title}》吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await deleteNotice(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchNotices()
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
  noticeFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await updateNotice(noticeForm.id, noticeForm)
        } else {
          res = await publishNotice(noticeForm)
        }
        if (res.code === 200) {
          ElMessage.success(`${isEdit.value ? '编辑' : '发布'}成功`)
          dialogVisible.value = false
          fetchNotices()
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
  noticeFormRef.value?.resetFields()
  Object.assign(noticeForm, {
    id: null,
    title: '',
    content: '',
    priority: 1,
    targetRole: 'all',
    attachment: '',
  })
}

onMounted(() => {
  fetchNotices()
})
</script>

<style scoped lang="scss">
.notice-manage-container {
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
</style>
