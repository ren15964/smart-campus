<template>
  <div class="homework-grade-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>作业批改 - {{ homeworkTitle }}</span>
          <el-button type="primary" link @click="handleBack">返回</el-button>
        </div>
      </template>

      <el-table :data="submissionList" v-loading="loading" border style="width: 100%">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getSubmitStatusTagType(scope.row.status)">
              {{ formatSubmitStatus(scope.row.status) }}
            </el-tag>
            <el-tag v-if="scope.row.isLate" type="danger" style="margin-left: 5px;">迟交</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="80">
          <template #default="scope">
            {{ scope.row.score === null ? '-' : scope.row.score }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" link @nclick="handleViewSubmission(scope.row)">
              查看
            </el-button>
            <el-button
              type="success"
              link
              :disabled="scope.row.status === 2" 
              @click="handleGradeSubmission(scope.row)"
            >
              {{ scope.row.status === 2 ? '已批改' : '批改' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 提交详情和批改弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentSubmission.studentName ? `${currentSubmission.studentName} 的提交` : '提交详情'"
      width="800px"
      @close="handleDialogClose"
    >
      <div v-if="currentSubmission.submitId" class="submission-detail-content">
        <p><strong>提交内容:</strong></p>
        <div class="submission-content" v-html="currentSubmission.content"></div>
        <div v-if="currentSubmission.attachment" class="submission-attachment">
          <el-link :href="currentSubmission.attachment" target="_blank" type="primary">
            <el-icon><Download /></el-icon> 附件下载
          </el-link>
        </div>

        <el-divider content-position="left">批改</el-divider>
        <el-form
          :model="gradeForm"
          :rules="gradeRules"
          ref="gradeFormRef"
          label-width="80px"
          v-if="currentSubmission.status !== 2"
        >
          <el-form-item label="分数" prop="score">
            <el-input-number v-model="gradeForm.score" :min="0" :max="homeworkTotalScore" />
          </el-form-item>
          <el-form-item label="评语" prop="comment">
            <el-input v-model="gradeForm.comment" type="textarea" rows="3" />
          </el-form-item>
        </el-form>
        <div v-else class="graded-info">
          <p><strong>分数:</strong> {{ currentSubmission.score }}</p>
          <p><strong>评语:</strong> {{ currentSubmission.comment || '无' }}</p>
        </div>
      </div>
      <template #footer v-if="currentSubmission.status !== 2">
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitGrade">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getHomeworkDetail, getHomeworkSubmissions, gradeHomework } from '@/api/homework'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const homeworkId = route.params.id

const homeworkTitle = ref('加载中...')
const homeworkTotalScore = ref(100) // 作业总分，从作业详情获取
const submissionList = ref([])
const loading = ref(false)

// 提交详情弹窗相关
const dialogVisible = ref(false)
const currentSubmission = ref({}) // 当前查看或批改的提交
const gradeFormRef = ref(null)
const gradeForm = reactive({
  submitId: null,
  score: null,
  comment: '',
})

const gradeRules = {
  score: [{ required: true, message: '请输入分数', trigger: 'change' }],
}

// 获取作业详情和提交列表
const fetchHomeworkAndSubmissions = async () => {
  if (!homeworkId) {
    ElMessage.error('缺少作业ID')
    return
  }
  loading.value = true
  try {
    // 获取作业详情以显示标题和总分
    const homeworkRes = await getHomeworkDetail(homeworkId)
    if (homeworkRes.code === 200) {
      homeworkTitle.value = homeworkRes.data.title
      homeworkTotalScore.value = homeworkRes.data.totalScore
    } else {
      ElMessage.error(homeworkRes.message || '获取作业详情失败')
      loading.value = false
      return
    }

    // 获取提交列表
    const submissionsRes = await getHomeworkSubmissions(homeworkId)
    if (submissionsRes.code === 200) {
      submissionList.value = submissionsRes.data
    }
  } catch (error) {
    console.error('获取作业提交列表失败:', error)
    ElMessage.error('获取作业提交列表失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const formatSubmitStatus = (status) => {
  switch (status) {
    case 0: return '未提交' // 实际上这里应该是已提交但未批改
    case 1: return '已提交'
    case 2: return '已批改'
    default: return '未知'
  }
}

const getSubmitStatusTagType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'warning'
    case 2: return 'success'
    default: return 'info'
  }
}

const handleBack = () => {
  router.back()
}

const handleViewSubmission = (row) => {
  currentSubmission.value = { ...row }
  dialogVisible.value = true
  // 如果已批改，则不显示批改表单
  if (row.status === 2) {
    gradeForm.score = row.score
    gradeForm.comment = row.comment
  } else {
    gradeForm.score = null
    gradeForm.comment = ''
  }
}

const handleGradeSubmission = (row) => {
  currentSubmission.value = { ...row }
  dialogVisible.value = true
  nextTick(() => {
    gradeFormRef.value?.resetFields()
    gradeForm.submitId = row.submitId
    // 预填已批改的分数和评语
    if (row.status === 2) {
      gradeForm.score = row.score
      gradeForm.comment = row.comment
    }
  })
}

const handleSubmitGrade = () => {
  gradeFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        const res = await gradeHomework(gradeForm)
        if (res.code === 200) {
          ElMessage.success('批改成功')
          dialogVisible.value = false
          fetchHomeworkAndSubmissions() // 刷新列表
        } else {
          ElMessage.error(res.message || '批改失败')
        }
      } catch (error) {
        console.error('批改失败:', error)
        ElMessage.error('批改失败，请稍后再试')
      }
    }
  })
}

const handleDialogClose = () => {
  gradeFormRef.value?.resetFields()
  Object.assign(gradeForm, {
    submitId: null,
    score: null,
    comment: '',
  })
  currentSubmission.value = {} // 清空当前提交信息
}

onMounted(() => {
  fetchHomeworkAndSubmissions()
})
</script>

<style scoped lang="scss">
.homework-grade-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.submission-detail-content {
  padding: 0 20px;
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

.submission-attachment {
  margin-top: 20px;
  text-align: right;
}

.graded-info {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #e1f3d8; /* 浅绿色边框 */
  background-color: #f0f9eb; /* 浅绿色背景 */
  color: #67c23a; /* 绿色文本 */
  border-radius: 4px;
}

.graded-info p {
  margin: 5px 0;
}

.el-input-number {
  width: 100%;
}
</style>
