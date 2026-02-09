import { defineStore } from 'pinia'
import { login, register } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),

  getters: {
    isLogin: (state) => !!state.token,
    role: (state) => state.userInfo.role || ''
  },

  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },

    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },

    async login(loginForm) {
      const res = await login(loginForm)
      if (res.code === 200) {
        this.setToken(res.data.token)
        this.setUserInfo(res.data.userInfo)
        return true
      }
      return false
    },

    async register(registerForm) {
      const res = await register(registerForm)
      if (res.code === 200) {
        this.setToken(res.data.token)
        this.setUserInfo(res.data.userInfo)
        return true
      }
      return false
    },

    logout() {
      this.token = ''
      this.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
