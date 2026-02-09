<template>
  <div class="password-change-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
          <el-button type="primary" link @click="handleBack">返回</el-button>
        </div>
      </template>

      <el-form
        :model="passwordForm"
        :rules="rules"
        ref="passwordFormRef"
        label-width="100px"
        class="password-form"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmitChange">确认修改</el-button>
          <el-button @click="handleResetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { updatePassword } from '@/api/user'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else {
    if (passwordForm.confirmPassword !== '') {
      passwordFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validateConfirmPass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, validator: validatePass, trigger: 'blur' }],
  confirmPassword: [
    { required: true, validator: validateConfirmPass, trigger: 'blur' },
  ],
}

const handleSubmitChange = () => {
  passwordFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        const res = await updatePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword,
        })
        if (res.code === 200) {
          ElMessage.success('密码修改成功，请重新登录')
          userStore.logout() // 修改密码成功后强制登出
          router.push('/login')
        } else {
          ElMessage.error(res.message || '密码修改失败')
        }
      } catch (error) {
        console.error('密码修改失败:', error)
        ElMessage.error('密码修改失败，请稍后再试')
      }
    }
  })
}

const handleResetForm = () => {
  passwordFormRef.value?.resetFields()
}

const handleBack = () => {
  router.back()
}
</script>

<style scoped lang="scss">
.password-change-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.password-form {
  width: 500px;
  margin: 20px auto;
}
</style>
