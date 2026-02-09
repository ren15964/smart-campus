<template>
  <div class="register-container">
    <AuthParticlesBg />
    <div class="register-box">
      <h2 class="register-title">用户注册</h2>

      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        class="register-form"
        label-position="top"
      >
        <el-form-item prop="role" label="角色">
          <el-select v-model="form.role" placeholder="请选择角色" size="large" style="width: 100%">
            <el-option label="学生" value="student" />
            <el-option label="教师" value="teacher" />
          </el-select>
        </el-form-item>

        <el-form-item prop="username" label="用户名（学号/工号）">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>

        <el-form-item prop="realName" label="姓名">
          <el-input v-model="form.realName" placeholder="请输入姓名" size="large" />
        </el-form-item>

        <el-form-item prop="password" label="密码">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="confirmPassword" label="确认密码">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入密码"
            size="large"
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <el-form-item prop="email" label="邮箱（可选）">
          <el-input v-model="form.email" placeholder="请输入邮箱" size="large" />
        </el-form-item>

        <el-form-item prop="phone" label="电话（可选）">
          <el-input v-model="form.phone" placeholder="请输入电话" size="large" />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleRegister"
            style="width: 100%"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="register-actions">
          <span>已有账号？</span>
          <el-link type="primary" @click="goLogin">去登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import AuthParticlesBg from '@/components/AuthParticlesBg.vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  role: 'student',
  username: '',
  realName: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) return callback(new Error('请再次输入密码'))
  if (value !== form.password) return callback(new Error('两次输入的密码不一致'))
  callback()
}

const rules = {
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const ok = await userStore.register(form)
      if (ok) {
        ElMessage.success('注册成功')
        router.push('/')
      }
    } catch (e) {
      // 统一错误提示在 request 拦截器中处理，这里只兜底
      console.error('注册失败:', e)
    } finally {
      loading.value = false
    }
  })
}

const goLogin = () => router.push('/login')
</script>

<style scoped lang="scss">
.register-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 24px;
}

.register-box {
  width: 460px;
  padding: 32px 40px;
  background: rgba(255, 255, 255, 0.82);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.35);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  position: relative;
  z-index: 1;
  animation: authEnter 520ms cubic-bezier(0.2, 0.8, 0.2, 1) both;
  transition: transform 220ms ease, box-shadow 220ms ease;
}

.register-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 26px 70px rgba(0, 0, 0, 0.42);
}

.register-title {
  text-align: center;
  margin-bottom: 18px;
  font-size: 22px;
  letter-spacing: 0.5px;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.92), rgba(165, 180, 252, 0.92));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  text-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
}

.register-actions {
  margin-top: 6px;
  display: flex;
  justify-content: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.82);
  font-size: 14px;
}

:deep(.el-form-item) {
  margin-bottom: 16px;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  border-radius: 12px;
  transition: box-shadow 180ms ease, transform 180ms ease;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focus) {
  box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.22);
  transform: translateY(-1px);
}

:deep(.el-button--primary) {
  border-radius: 12px;
  height: 44px;
}

@keyframes authEnter {
  from {
    opacity: 0;
    transform: translateY(10px) scale(0.985);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>

