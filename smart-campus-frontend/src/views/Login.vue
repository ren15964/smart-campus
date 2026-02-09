<template>
  <div class="login-container">
    <AuthParticlesBg />
    <div class="login-box">
      <h2 class="login-title">智慧校园管理平台</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            style="width: 100%"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-actions">
        <span>没有账号？</span>
        <el-link type="primary" @click="goRegister">去注册</el-link>
      </div>
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
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.login(loginForm)
        if (success) {
          ElMessage.success('登录成功')
          router.push('/')
        } else {
          ElMessage.error('用户名或密码错误')
        }
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const goRegister = () => router.push('/register')
</script>

<style scoped lang="scss">
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 24px;
}

.login-box {
  width: 400px;
  padding: 40px;
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

.login-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 26px 70px rgba(0, 0, 0, 0.42);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  letter-spacing: 0.5px;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.92), rgba(165, 180, 252, 0.92));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  text-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
}

.login-form {
  margin-top: 20px;
}

.login-actions {
  margin-top: 12px;
  display: flex;
  justify-content: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.82);
  font-size: 14px;
}

:deep(.el-form-item) {
  margin-bottom: 18px;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  transition: box-shadow 180ms ease, transform 180ms ease;
}

:deep(.el-input__wrapper.is-focus) {
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
