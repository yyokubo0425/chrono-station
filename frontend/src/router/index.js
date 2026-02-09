import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/attendance/HomeView.vue'
import MonthlyAttendance from '@/views/attendance/MonthlyAttendance.vue'
import DailyAttendance from '@/views/attendance/DailyAttendance.vue'
import RequestList from '@/views/admin/RequestList.vue'
import AttendanceCorrection from '@/views/request/AttendanceCorrection.vue'
import AttendanceUnrecord from '@/views/request/AttendanceUnrecord.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: {
        title: null,
      },
    },
    {
      path: '/monthly-attendance',
      name: 'monthly-attendance',
      component: MonthlyAttendance,
      meta: {
        title: '月次勤怠',
      },
    },
    {
      path: '/daily-attendance/:date',
      name: 'daily-attendance',
      component: DailyAttendance,
      meta: {
        title: '日次勤怠',
      },
    },
    {
      path: '/admin/corrections',
      name: 'requestList',
      component: RequestList,
      meta: {
        title: '申請一覧',
      },
    },
    {
      path: '/attendance-corrections/:attendanceId',
      name: 'attendance-correction',
      component: AttendanceCorrection,
      meta: {
        title: '打刻修正申請',
      },
    },
    {
      path: '/attendance-unrecord/:workDate',
      name: 'attendance-unrecord',
      component: AttendanceUnrecord,
      meta: {
        title: '打刻漏れ申請',
      },
    },
  ],
  // 画面遷移時のスクロール位置制御
  scrollBehavior(to, from, savedPosition) {
    //戻る/進ボタンの時
    if (savedPosition) {
      return savedPosition
    }
    // hash があれば該当要素へ
    if (to.hash) {
      return { el: to.hash, behavior: 'smooth' }
    }
    //通常の画面遷移は常に先頭へ
    return { top: 0 }
  },
})
export default router
