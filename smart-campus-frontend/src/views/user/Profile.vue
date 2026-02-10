<template>
  <div class="user-profile-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>

      <el-form :model="userInfoForm" :rules="rules" ref="userInfoFormRef" label-width="100px" class="profile-form">
        <el-form-item label="头像">
          <!-- 通用文件上传接口：/api/file/upload（需后端实现） -->
          <el-upload
            class="avatar-uploader"
            action="/api/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <el-avatar :size="80" :src="userInfoForm.avatar" />
          </el-upload>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="userInfoForm.username" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="userInfoForm.realName" />
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="userInfoForm.role" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userInfoForm.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userInfoForm.phone" />
        </el-form-item>
        <el-form-item label="创建时间">
          <el-input v-model="userInfoForm.createTime" disabled />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmitUpdate">保存修改</el-button>
          <el-button @click="handleResetForm">重置</el-button>
          <el-button @click="handleChangePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfo, updateProfile } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userInfoFormRef = ref(null)
const userInfoForm = reactive({
  id: null,
  username: '',
  realName: '',
  role: '',
  email: '',
  phone: '',
  avatar: '',
  createTime: '',
})

const rules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] },
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: ['blur', 'change'] },
  ],
}

const fetchUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === 200) {
      Object.assign(userInfoForm, res.data)
    } else {
      ElMessage.error(res.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败，请稍后再试')
  }
}

const handleSubmitUpdate = () => {
  userInfoFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        const res = await updateProfile(userInfoForm)
        if (res.code === 200) {
          ElMessage.success('个人信息更新成功')
          // 可以选择刷新页面或更新本地store中的用户信息
        } else {
          ElMessage.error(res.message || '个人信息更新失败')
        }
      } catch (error) {
        console.error('个人信息更新失败:', error)
        ElMessage.error('个人信息更新失败，请稍后再试')
      }
    }
  })
}

const handleResetForm = () => {
  fetchUserInfo() // 重新获取原始信息进行重置
}

// 文件上传相关
const handleAvatarSuccess = (response, file) => {
  if (response.code === 200) {
    userInfoForm.avatar = response.data.filePath
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isJPGPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGPNG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPGPNG && isLt2M
}

const handleChangePassword = () => {
  router.push({ name: 'PasswordChange' })
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped lang="scss">
.user-profile-container {
  padding: 24px; // 统一内边距
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-form {
  max-width: 600px; // 使用最大宽度
  width: 100%; // 确保宽度自适应
  margin: 20px auto; // 居中
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--app-border); // 使用全局边框变量
    border-radius: 50%;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }

  :deep(.el-upload:hover) {
    border-color: var(--primary-light-color); // 使用 Element Plus 主色作为悬停颜色
  }
}

.el-avatar {
  --el-avatar-bg-color: var(--app-bg-soft); // 使用浅色主题的柔和背景色
}
</style>
