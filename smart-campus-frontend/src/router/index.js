import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

export const routes = [
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/Forbidden.vue'),
    meta: { title: '无权限' }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页', requiresAuth: true },
    children: [
      {
        path: 'user-manage',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      {
        path: 'course-students/:scheduleId',
        name: 'CourseStudents',
        component: () => import('@/views/course/CourseStudents.vue'),
        meta: { title: '课程学生名单', requiresAuth: true, hideInMenu: true }
      },
      {
        path: 'notice-list',
        name: 'NoticeList',
        component: () => import('@/views/notice/NoticeList.vue'),
        meta: { title: '通知列表', requiresAuth: true }
      },
      {
        path: 'notice-detail/:id',
        name: 'NoticeDetail',
        component: () => import('@/views/notice/NoticeDetail.vue'),
        meta: { title: '通知详情', requiresAuth: true, hideInMenu: true }
      },
      {
        path: 'notice-manage',
        name: 'NoticeManage',
        component: () => import('@/views/notice/NoticeManage.vue'),
        meta: { title: '通知管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: 'homework-list',
        name: 'HomeworkList',
        component: () => import('@/views/homework/HomeworkList.vue'),
        meta: { title: '我的作业', requiresAuth: true, roles: ['student'] }
      },
      {
        path: 'homework-submit/:id',
        name: 'HomeworkSubmit',
        component: () => import('@/views/homework/HomeworkSubmit.vue'),
        meta: { title: '作业详情与提交', requiresAuth: true, roles: ['student'], hideInMenu: true }
      },
      {
        path: 'homework-manage',
        name: 'HomeworkManage',
        component: () => import('@/views/homework/HomeworkManage.vue'),
        meta: { title: '作业管理', requiresAuth: true, roles: ['teacher'] }
      },
      {
        path: 'homework-grade/:id',
        name: 'HomeworkGrade',
        component: () => import('@/views/homework/HomeworkGrade.vue'),
        meta: { title: '作业批改', requiresAuth: true, roles: ['teacher'], hideInMenu: true }
      },
      {
        path: 'resource-list/:scheduleId',
        name: 'ResourceList',
        component: () => import('@/views/resource/ResourceList.vue'),
        meta: { title: '课程资源', requiresAuth: true, roles: ['student', 'teacher'], hideInMenu: true }
      },
      {
        path: 'resource-manage',
        name: 'ResourceManage',
        component: () => import('@/views/resource/ResourceManage.vue'),
        meta: { title: '资源管理', requiresAuth: true, roles: ['teacher'] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人资料', requiresAuth: true, hideInMenu: true }
      },
      {
        path: 'password-change',
        name: 'PasswordChange',
        component: () => import('@/views/user/PasswordChange.vue'),
        meta: { title: '修改密码', requiresAuth: true }
      },
      {
        path: 'personal-center',
        name: 'PersonalCenter',
        component: () => import('@/views/PersonalCenter.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'selection-manage',
        name: 'SelectionManage',
        component: () => import('@/views/admin/SelectionManage.vue'),
        meta: { title: '选课管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: 'grade-entry',
        name: 'TeacherGradeEntry',
        component: () => import('@/views/grade/TeacherGradeEntry.vue'),
        meta: { title: '成绩录入', requiresAuth: true, roles: ['teacher'] }
      },
      {
        path: 'grade-manage',
        name: 'TeacherGradeManage',
        component: () => import('@/views/grade/TeacherGradeManage.vue'),
        meta: { title: '成绩管理', requiresAuth: true, roles: ['teacher'] }
      },
      {
        path: 'grade-statistics',
        name: 'GradeStatistics',
        component: () => import('@/views/admin/GradeStatistics.vue'),
        meta: { title: '全校成绩统计', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: 'grade-audit',
        name: 'GradeAudit',
        component: () => import('@/views/admin/GradeAudit.vue'),
        meta: { title: '成绩审核管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: 'grade-exception-monitor',
        name: 'GradeExceptionMonitor',
        component: () => import('@/views/admin/GradeExceptionMonitor.vue'),
        meta: { title: '成绩异常监控', requiresAuth: true, roles: ['admin'] }
      },
      // 新闻管理
      {
        path: 'news-manage',
        name: 'NewsManage',
        component: () => import('@/views/news/NewsManage.vue'),
        meta: { title: '新闻管理', requiresAuth: true, roles: ['admin', 'teacher'] }
      },
      // 新闻列表
      {
        path: 'news-list',
        name: 'NewsList',
        component: () => import('@/views/news/NewsList.vue'),
        meta: { title: '校园新闻', requiresAuth: true }
      },
      // 新闻详情
      {
        path: 'news-detail/:id',
        name: 'NewsDetail',
        component: () => import('@/views/news/NewsDetail.vue'),
        meta: { title: '新闻详情', requiresAuth: true, hideInMenu: true }
      },
      {
        path: 'course-manage',
        name: 'CourseManage',
        component: () => import('@/views/admin/CourseManage.vue'),
        meta: { title: '课程管理', requiresAuth: true, roles: ['admin', 'teacher'] }
      },
      {
        path: 'classroom-manage',
        name: 'ClassroomManage',
        component: () => import('@/views/admin/ClassroomManage.vue'),
        meta: { title: '教室资源管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: 'grade-query',
        name: 'GradeQuery',
        component: () => import('@/views/grade/GradeQuery.vue'),
        meta: { title: '成绩查询', requiresAuth: true, roles: ['student'] }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLogin) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLogin) {
    next('/')
  } else if (to.path === '/register' && userStore.isLogin) {
    next('/')
  } else {
    // 检查角色权限
    if (to.meta.roles && to.meta.roles.length > 0) {
      if (!userStore.role || !to.meta.roles.includes(userStore.role)) {
        ElMessage.error('无权限访问')
        next({ name: 'Forbidden', query: { from: to.fullPath } })
      } else {
        next()
      }
    } else {
      next()
    }
  }
})

router.afterEach((to) => {
  const base = '智慧校园管理平台'
  const title = to?.meta?.title ? `${to.meta.title} - ${base}` : base
  document.title = title
})

export default router
