<script setup>
import { onMounted, ref, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/lib/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const error = ref(null)
const daily = ref(null)

//月次勤怠画面遷移
const monthly = () => {
  router.push({
    name: 'monthly-attendance',
  })
}

//打刻修正画面遷移
const getForCorrection = (attendanceId) => {
  router.push({
    name: 'attendance-correction',
    params: {
      attendanceId: attendanceId,
    },
  })
}

//打刻漏れ画面遷移
const getForUnrecord = (workDate) => {
  router.push({
    name: 'attendance-unrecord',
    params: {
      workDate: workDate,
    },
  })
}

const fetchDailyAttendance = async () => {
  const date = route.params.date
  if (!date) return
  loading.value = true
  error.value = null

  try {
    const res = await api.get('/daily-attendance', {
      params: { date },
    })
    daily.value = res.data
  } catch (e) {
    console.error(e)
    error.value = '日次勤怠の取得に失敗しました。'
  } finally {
    loading.value = false
  }
}
//初回表示
onMounted(fetchDailyAttendance)

//日付URL監視
watch(
  () => route.params.date,
  () => {
    fetchDailyAttendance()
  },
)

//現在の日付から未来日か判定
const isFutureDate = (date) => {
  const target = new Date(date)
  const now = new Date()
  now.setHours(0, 0, 0, 0)
  target.setHours(0, 0, 0, 0)
  return target > now
}

//未来日は矢印押下不可
const isNextDayDisable = computed(() => {
  const current = new Date(route.params.date)
  current.setDate(current.getDate() + 1)
  return isFutureDate(current.toLocaleDateString('sv-SE'))
})

//2000/1/1より過去は押下不可
const isPrevDisable = computed(() => {
  const MIN_DATE = new Date('2000-01-01')
  const current = new Date(route.params.date)
  current.setDate(current.getDate() - 1)
  return current < MIN_DATE
})

//日付変更
const changeDay = (diff) => {
  const current = new Date(route.params.date)
  current.setDate(current.getDate() + diff)

  const next = current.toLocaleDateString('sv-SE')
  router.push(`/daily-attendance/${next}`)
}

//日付変換
const formatDate = (date) => {
  const d = new Date(date)
  return `${d.getFullYear()}/${d.getMonth() + 1}/${d.getDate()}`
}

//時間変換
const formatHours = (hours) => {
  if (hours == null) return '―'

  const h = Math.floor(hours / 60)
  const m = hours % 60
  return `${h}時間${String(m).padStart(2, '0')}分`
}

//分変換
const formatMinutes = (dateTime) => {
  if (dateTime == null) return '―'
  const d = new Date(dateTime)
  const h = d.getHours()
  const m = d.getMinutes()
  return `${h}:${String(m).padStart(2, '0')}`
}
</script>

<template>
  <div v-if="loading">読み込み中・・・</div>
  <div v-else-if="error">{{ error }}</div>

  <template v-else-if="daily">
    <div class="daily-container">
      <!-- 日付 -->
      <div class="date-nav">
        <button class="float-card arrow" :disabled="isPrevDisable" @click="changeDay(-1)">
          <svg class="arrow-icon" viewBox="0 0 24 24" aria-hidden="true">
            <path
              d="M15 18l-6-6 6-6"
              fill="none"
              stroke="currentColor"
              stroke-width="2.2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </button>
        <div class="float-card date">{{ formatDate(daily.workDate) }}</div>
        <button class="float-card arrow" :disabled="isNextDayDisable" @click="changeDay(1)">
          <svg class="arrow-icon" viewBox="0 0 24 24" aria-hidden="true">
            <path
              d="M9 6l6 6-6 6"
              fill="none"
              stroke="currentColor"
              stroke-width="2.2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </button>
      </div>

      <div class="work-type">{{ daily.workType }}</div>

      <div class="cards">
        <div class="card">
          <span class="label">出勤</span>
          <span class="value">{{ formatMinutes(daily.workStart) ?? '—' }}</span>
        </div>

        <div class="card">
          <span class="label">退勤</span>
          <span class="value">{{ formatMinutes(daily.workEnd) ?? '—' }}</span>
        </div>

        <div class="card">
          <span class="label">休憩</span>
          <span class="value">{{ formatHours(daily.breakMinutes) ?? '—' }}</span>
        </div>

        <div class="card">
          <span class="label">残業</span>
          <span class="value">(仮)0時間00分</span>
        </div>

        <div class="card">
          <span class="label">労働時間</span>
          <span class="value">{{ formatHours(daily.totalWorkMinutes) ?? '—' }}</span>
        </div>
      </div>

      <!-- ボタン -->
      <div class="btn-actions">
        <button class="primary-btn" @click="monthly">月次勤怠</button>
        <!-- 勤怠あり -->
        <button
          v-if="daily.exists"
          class="primary-btn"
          @click="getForCorrection(daily.attendanceId)"
        >
          打刻修正
        </button>
        <!-- 勤怠なし -->
        <button v-else class="primary-btn" @click="getForUnrecord(daily.workDate)">打刻漏れ</button>
      </div>
    </div>
  </template>
</template>

<style scoped>
.daily-container {
  padding: 0 16px;
  background: linear-gradient(180deg, #020b17 0%, #031222 100%);
  color: #e6f7ff;
}

.date-nav {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 16px;
  gap: 14px;
}

.float-card {
  height: 42px;
  width: 42px;
  min-width: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: linear-gradient(180deg, rgba(10, 34, 64, 0.9), rgba(4, 18, 36, 0.9));
  border: 1px solid rgba(0, 200, 255, 0.8);
  box-shadow:
    0 0 10px rgba(0, 200, 255, 0.55),
    inset 0 0 10px rgba(0, 150, 220, 0.25);

  color: #eaffff;
  text-shadow: 0 0 6px rgba(0, 200, 255, 0.45);
}

.float-card.date {
  font-size: 20px;
  font-weight: 600;
  min-width: 140px;
  padding: 0 10px;
  letter-spacing: 0.06rem;
  text-align: center;
}

.float-card.arrow {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  font-size: 20px;
  color: rgba(0, 200, 255, 0.6);
  transition:
    transform 0.08s ease,
    opacity 0.08s ease,
    box-shadow 0.08s ease;
}

.float-card.arrow:active {
  transform: translateY(1px) scale(0.94);
  opacity: 0.75;
  box-shadow:
    0 0 6px rgba(0, 200, 255, 0.35),
    inset 0 0 6px rgba(0, 150, 220, 0.2);
}

.work-type {
  font-size: 20px;
  text-align: center;
  color: #9fe9ff;
  letter-spacing: 0.04rem;
  margin: 16px 0;
}

.cards {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card {
  display: grid;
  grid-template-columns: auto 1fr;
  align-items: center;
  padding: 20px;
  border-radius: 18px;
  background: radial-gradient(circle at top, #0a2a44 0%, #020a16 55%, #01070f 100%);
  border: 1px solid rgba(0, 200, 255, 0.6);
  box-shadow:
    0 0 12px rgba(0, 200, 255, 0.4),
    inset 0 0 16px rgba(0, 150, 220, 0.2);
  color: #e8f8ff;
  box-sizing: border-box;
}

.card .label {
  font-size: 16px;
  color: rgba(232, 251, 255, 0.75);
  white-space: nowrap;
}

.card .value {
  font-size: 16px;
  justify-self: end;
  font-weight: 600;
}

.card.total {
  background: #f5f5f5;
}

.btn-actions {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  gap: 16px;
}

.primary-btn {
  width: 100%;
  height: 48px;
  padding: 0 20px;
  border-radius: 14px;
  color: #cfefff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid rgba(0, 220, 255, 0.9);
  background: linear-gradient(180deg, rgba(0, 200, 255, 0.15), rgba(0, 120, 200, 0.05));
  box-shadow:
    0 0 6px rgba(0, 220, 255, 0.6),
    0 0 14px rgba(0, 220, 255, 0.45),
    inset 0 0 10px rgba(0, 200, 255, 0.25);
  transition:
    transform 0.08s ease,
    opacity 0.08s ease,
    box-shadow 0.08s ease;
}

.primary-btn:active {
  transform: scale(0.93);
  opacity: 0.7;
  filter: brightness(0.9);
}

.primary-btn:hover {
  box-shadow:
    0 0 20px rgba(0, 220, 255, 0.8),
    inset 0 0 14px rgba(0, 220, 255, 0.35);
}
</style>
