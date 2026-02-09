<template>
  <div class="homework-submit-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>作业详情与提交</span>
          <el-button type="primary" link @click="handleBack">返回</el-button>
        </div>
      </template>

      <div v-if="homework.id" class="detail-content">
        <h2 class="homework-title">{{ homework.title }}</h2>
        <div class="homework-meta">
          <span>课程: {{ homework.courseName }}</span>
          <span>教师: {{ homework.teacherName }}</span>
          <span>截止日期: {{ homework.deadline }}</span>
          <span>总分: {{ homework.totalScore }}</span>
        </div>
        <el-divider />
        <div class="homework-body" v-html="homework.content"></div>

        <div v-if="homework.attachment" class="homework-attachment">
          <el-link :href="homework.attachment" target="_blank" type="primary">
            <el-icon><Download /></el-icon> 附件下载
          </el-link>
        </div>

        <el-divider content-position="left">我的提交</el-divider>

        <div v-if="homework.submitInfo && homework.submitInfo.submitId" class="submission-info">
          <p><strong>提交状态:</strong>
            <el-tag :type="getSubmitStatusTagType(homework.submitInfo.status)">
              {{ formatSubmitStatus(homework.submitInfo.status) }}
            </el-tag>
            <el-tag v-if="homework.submitInfo.isLate" type="danger" style="margin-left: 10px;">迟交</el-tag>
          </p>
          <p><strong>提交时间:</strong> {{ homework.submitInfo.submitTime }}</p>
          <p><strong>提交内容:</strong></p>
          <div class="submission-content" v-html="homework.submitInfo.content"></div>
          <div v-if="homework.submitInfo.attachment" class="submission-attachment">
            <el-link :href="homework.submitInfo.attachment" target="_blank" type="primary">
              <el-icon><Download /></el-icon> 已提交附件下载
            </el-link>
          </div>

          <div v-if="homework.submitInfo.status === 2" class="grade-info">
            <el-divider content-position="left">批改信息</el-divider>
            <p><strong>分数:</strong> {{ homework.submitInfo.score }}</p>
            <p><strong>评语:</strong> {{ homework.submitInfo.comment || '无' }}</p>
          </div>
        </div>
        <div v-else class="submission-form">
          <el-form :model="submitForm" :rules="submitRules" ref="submitFormRef" label-width="80px">
            <el-form-item label="提交内容" prop="content">
              <el-input v-model="submitForm.content" type="textarea" rows="5" placeholder="请输入作业内容" />
            </el-form-item>
            <el-form-item label="上传附件" prop="file">
              <el-upload
                class="upload-demo"
                action="/api/file/upload"  
                :on-success="handleFileUploadSuccess"
                :on-error="handleFileUploadError"
                :on-remove="handleFileRemove"
                :before-upload="beforeFileUpload"
                :limit="1"
                :file-list="fileList"
                accept=".doc,.docx,.pdf,.zip,.rar,.txt,.ppt,.pptx,.xls,.xlsx,.jpg,.jpeg,.png"
              >
                <el-button type="primary">点击上传</el-button>
                <template #tip>
                  <div class="el-upload__tip">可上传 Word, PDF, 压缩包, TXT, 图片等文件，大小不超过10MB</div>
                </template>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmitHomework" :loading="submitLoading">
                提交作业
              </el-button>
              <el-button @click="handleCancelSubmit">取消</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <AppEmpty v-else description="作业不存在或已删除" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getHomeworkDetail, submitHomework } from '@/api/homework'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import AppEmpty from '@/components/AppEmpty.vue'

const route = useRoute()
const router = useRouter()
const homeworkId = route.params.id

const homework = ref({
  id: null,
  title: '',
  content: '',
  attachment: '',
  deadline: '',
  totalScore: 0,
  courseName: '',
  teacherName: '',
  submitInfo: null, // 学生提交信息
})
const loading = ref(false)
const submitLoading = ref(false)

const submitFormRef = ref(null)
const submitForm = reactive({
  homeworkId: homeworkId,
  content: '',
  file: null,
})

const fileList = ref([]) // 用于el-upload的文件列表

const submitRules = {
  content: [{
    required: false,
    message: '请输入提交内容或上传附件',
    trigger: 'blur',
    validator: (rule, value, callback) => {
      if (!value && fileList.value.length === 0) {
        callback(new Error(rule.message))
      } else {
        callback()
      }
    },
  }],
}

const fetchHomeworkDetail = async () => {
  if (!homeworkId) {
    ElMessage.error('缺少作业ID')
    return
  }
  loading.value = true
  try {
    const res = await getHomeworkDetail(homeworkId)
    if (res.code === 200) {
      Object.assign(homework.value, res.data)
      submitForm.homeworkId = homeworkId
    } else {
      ElMessage.error(res.message || '获取作业详情失败')
    }
  } catch (error) {
    console.error('获取作业详情失败:', error)
    ElMessage.error('获取作业详情失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const formatSubmitStatus = (status) => {
  switch (status) {
    case 0: return '未提交'
    case 1: return '已提交'
    case 2: return '已批改'
    default: return '未知'
  }
}

const getSubmitStatusTagType = (status) => {
  switch (status) {
    case 0: return 'danger'
    case 1: return 'warning'
    case 2: return 'success'
    default: return 'info'
  }
}

// 文件上传相关
const handleFileUploadSuccess = (response, file, fileLists) => {
  if (response.code === 200) {
    submitForm.file = response.data.filePath
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
  submitForm.file = null
}

const beforeFileUpload = (file) => {
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('上传附件大小不能超过 10MB!')
  }
  return isLt10M
}

const handleSubmitHomework = () => {
  submitFormRef.value?.validate(async (valid) => {
    if (valid) {
      if (!submitForm.content && !submitForm.file) {
        ElMessage.warning('请输入提交内容或上传附件')
        return
      }

      submitLoading.value = true
      try {
        const formData = new FormData()
        formData.append('homeworkId', submitForm.homeworkId)
        formData.append('content', submitForm.content)
        // 如果是文件对象，添加到formData，如果是URL字符串，直接传字符串
        if (fileList.value.length > 0 && fileList.value[0].raw) {
          formData.append('file', fileList.value[0].raw)
        } else if (submitForm.file) { // 如果是已上传的附件URL
          formData.append('attachment', submitForm.file)
        }

        const res = await submitHomework(formData)
        if (res.code === 200) {
          ElMessage.success('作业提交成功')
          fetchHomeworkDetail() // 刷新作业详情，显示提交信息
        } else {
          ElMessage.error(res.message || '作业提交失败')
        }
      } catch (error) {
        console.error('作业提交失败:', error)
        ElMessage.error('作业提交失败，请稍后再试')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleCancelSubmit = () => {
  ElMessageBox.confirm(
    '确定要取消提交吗？已填写的内容将不会保存。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    router.back()
  }).catch(() => {
    ElMessage.info('已取消')
  })
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  fetchHomeworkDetail()
})
</script>

<style scoped lang="scss">
.homework-submit-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-content {
  padding: 0 20px;
}

.homework-title {
  font-size: 26px;
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}

.homework-meta {
  display: flex;
  justify-content: center;
  gap: 20px;
  font-size: 14px;
  color: #909399;
  margin-bottom: 20px;

  span {
    display: flex;
    align-items: center;
    gap: 5px;
  }
}

.homework-body {
  line-height: 1.8;
  font-size: 16px;
  color: #606266;
  min-height: 100px;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.homework-attachment,
.submission-attachment {
  margin-top: 20px;
  text-align: right;
}

.submission-info,
.submission-form {
  margin-top: 30px;
  padding: 20px;
  border: 1px dashed #DCDFE6;
  border-radius: 8px;
  background-color: #FAFAFA;
}

.submission-content {
  margin-top: 10px;
  padding: 10px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  background-color: #FFF;
  min-height: 80px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.grade-info {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px dashed #DCDFE6;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
}
</style>
