import { defineStore } from 'pinia'
import { login, register } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    ui: JSON.parse(localStorage.getItem('ui') || '{}')
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

    setMenuCollapsed(v) {
      const next = { ...(this.ui || {}), menuCollapsed: !!v }
      this.ui = next
      localStorage.setItem('ui', JSON.stringify(next))
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
      this.ui = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('ui')
    }
  }
})
