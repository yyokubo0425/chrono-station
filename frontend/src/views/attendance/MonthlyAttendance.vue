<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/lib/api'

// APIレスポンス
const monthly = ref(null)
const loading = ref(false)
const error = ref(null)
const router = useRouter()

// API通信
const fetchMonthlyAttendance = async () => {
  loading.value = true
  error.value = null

  try {
    const res = await api.get('/monthly-attendance', {
      params: { year: year.value, month: month.value },
    })
    monthly.value = res.data
  } catch (e) {
    console.error(e)
    error.value = '月次勤怠の取得に失敗しました。'
  } finally {
    loading.value = false
  }
}

//日次勤怠
const dailyAttendance = (date) => {
  if (isFutureDate(date)) {
    return
  }
  router.push(`/daily-attendance/${date}`)
}

//現在の日付から未来日か判定
const isFutureDate = (date) => {
  const target = new Date(date)
  now.setHours(0, 0, 0, 0)
  target.setHours(0, 0, 0, 0)
  return target > now
}

onMounted(fetchMonthlyAttendance)

// 年月の状態管理
const now = new Date()
const year = ref(now.getFullYear())
const month = ref(now.getMonth() + 1)
const today = ref(now.toLocaleDateString('sv-SE')) //yyyy-mm-dd形式
const dayOfWeekClass = (day) => {
  if (day === '土') return 'is-saturday'
  if (day === '日') return 'is-sunday'
  return ''
}
const dayCategoryClass = (category) => {
  if (category === 'FUTURE') return 'is-future'
  if (category === 'HOLIDAY') return 'is-holiday'
  return ''
}
const workTypeClass = (type) => {
  if (type === 'HOLIDAY_WORK') return 'is-holiday-work'
  return ''
}

//年月の上下範囲
const isValidYearMonth = (y, m) => {
  const now = new Date()
  const min = new Date(2000, 0, 1)
  const max = new Date(now.getFullYear() + 1, 11, 1)
  const target = new Date(y, m - 1, 1)
  return target >= min && target <= max
}

//月切り替え
const changeMonth = (diff) => {
  const nextDate = new Date(year.value, month.value - 1 + diff)
  const nextYear = nextDate.getFullYear()
  const nextMonth = nextDate.getMonth() + 1

  if (!isValidYearMonth(nextYear, nextMonth)) {
    return
  }

  year.value = nextYear
  month.value = nextMonth
  fetchMonthlyAttendance()
}

// 年月表示
const displayYearMonth = computed(() => `${year.value}年${month.value}月`)

//年月picker
const showMonthPicker = ref(false)
const pickerYear = ref(year.value)
const months = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]

const openMonthPicker = () => {
  pickerYear.value = year.value
  showMonthPicker.value = true
}

const prevYear = () => {
  const prev = pickerYear.value - 1
  if (!isValidYearMonth(prev, 1)) {
    return
  }
  pickerYear.value = prev
}

const nextYear = () => {
  const next = pickerYear.value + 1
  if (!isValidYearMonth(next, 12)) {
    return
  }
  pickerYear.value = next
}

const selectMonth = (m) => {
  if (year.value === pickerYear.value && month.value === m) {
    showMonthPicker.value = false
    return
  }
  year.value = pickerYear.value
  month.value = m
  showMonthPicker.value = false
  fetchMonthlyAttendance()
}

const closeMonthPicker = () => {
  showMonthPicker.value = false
}

//今月へ戻る
const currentMonth = () => {
  const now = new Date()
  const y = now.getFullYear()
  const m = now.getMonth() + 1
  if (!isValidYearMonth(y, m)) {
    return
  }
  year.value = y
  month.value = m
  pickerYear.value = y
  showMonthPicker.value = false
  fetchMonthlyAttendance()
}

//分変換
const formatMinutes = (dateTime) => {
  if (dateTime == null) return '―'
  const d = new Date(dateTime)
  const h = d.getHours()
  const m = d.getMinutes()
  return `${h}:${String(m).padStart(2, '0')}`
}

//時間変換
const formatHours = (hours) => {
  if (hours == null) return '―'

  const h = Math.floor(hours / 60)
  const m = hours % 60
  return `${h}:${String(m).padStart(2, '0')}`
}

//日付変換
const formatDate = (datestr) => {
  const d = new Date(datestr)
  return `${d.getMonth() + 1}/${d.getDate()}`
}
</script>

<template>
  <div class="monthly-page">
    <div v-if="loading">読み込み中・・・</div>
    <div v-else-if="error">{{ error }}</div>

    <template v-else-if="monthly">
      <!-- 月次サマリー -->
      <section class="card summary">
        <div class="year-month-container">
          <div class="month-switch">
            <button class="changeMonth-btn" @click="changeMonth(-1)">
              <svg viewBox="0 0 24 24" aria-hidden="true">
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
            <button class="yearMonth-btn" @click="openMonthPicker">{{ displayYearMonth }}</button>
            <button class="changeMonth-btn" @click="changeMonth(1)">
              <svg viewBox="0 0 24 24" aria-hidden="true">
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

          <!-- 背景クリック検知 -->
          <div v-if="showMonthPicker" class="picker-backdrop" @click="closeMonthPicker"></div>
          <!-- 年月picker -->
          <div v-if="showMonthPicker" class="month-picker">
            <!-- 年ステッパー -->
            <div class="year-header">
              <button class="year-btn" @click="prevYear">
                <svg viewBox="0 0 24 24" aria-hidden="true">
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
              <span>{{ pickerYear }}</span>
              <button class="year-btn" @click="nextYear">
                <svg viewBox="0 0 24 24" aria-hidden="true">
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

              <button class="month-btn" @click="currentMonth">今月</button>
            </div>

            <!-- 月グリッド -->
            <div class="month-grid">
              <button v-for="m in months" :key="m" class="month-select" @click="selectMonth(m)">
                {{ String(m).padStart(2, '0') }}
              </button>
            </div>
          </div>
        </div>

        <div class="summary-grid">
          <div class="label">所定労働</div>
          <div class="value">{{ monthly.summary.scheduledWorkDays }}日</div>
          <div class="label">出勤</div>
          <div class="value">{{ monthly.summary.workDays }}日</div>
          <div class="label">欠勤</div>
          <div class="value">{{ monthly.summary.absenceDays }}日</div>
          <div class="label">有給</div>
          <div class="value">{{ monthly.summary.paidLeaveDays }}日</div>
          <div class="label">残業</div>
          <div class="value">(仮){{ formatHours(monthly.summary.overtimeMinutes) }}</div>
          <div class="label">総労働</div>
          <div class="value">{{ formatHours(monthly.summary.monthlyTotalWorkMinutes) }}</div>
        </div>
      </section>

      <!-- 日次勤怠一覧 -->
      <section class="card daily-card">
        <div class="table-head">
          <div>日付</div>
          <div>曜</div>
          <div>出勤</div>
          <div>退勤</div>
          <div>実働</div>
          <div>区分</div>
        </div>
        <div class="table-body">
          <div
            class="daily-row"
            :class="[{ 'is-today': d.date === today }, dayCategoryClass(d.dayCategory)]"
            v-for="d in monthly.dailyAttendances"
            :key="d.date"
            @click="dailyAttendance(d.date)"
          >
            <div>{{ formatDate(d.date) }}</div>
            <div class="weekday" :class="dayOfWeekClass(d.dayOfWeek)">{{ d.dayOfWeek }}</div>
            <div>{{ formatMinutes(d.workStart) ?? '―' }}</div>
            <div>{{ formatMinutes(d.workEnd) ?? '―' }}</div>
            <div>{{ formatHours(d.totalWorkMinutes) }}</div>
            <div class="work-type" :class="workTypeClass(d.workType)">{{ d.workTypeLabel }}</div>
          </div>
        </div>
      </section>

      <div class="btn-actions">
        <button class="primary-btn">申請一覧</button>
        <button class="primary-btn">月締め</button>
      </div>
    </template>
  </div>
</template>

<style>
:root {
  --daily-cols: 48px 32px 1fr 1fr 1fr 1fr;
}
</style>

<style scoped>
.monthly-page {
  width: 100%;
  padding: 0 16px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.card {
  width: 100%;
  padding: 20px;
  margin-top: 16px;
  border-radius: 18px;
  background: radial-gradient(circle at top, #0a2a44 0%, #020a16 55%, #01070f 100%);
  border: 1px solid rgba(0, 200, 255, 0.6);
  box-shadow:
    0 0 12px rgba(0, 220, 255, 0.6),
    0 0 28px rgba(0, 150, 255, 0.35),
    inset 0 0 16px rgba(0, 180, 255, 0.25);
  color: #e8f8ff;
  box-sizing: border-box;
}

.month-switch {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.yearMonth-btn {
  background: transparent;
  border: none;
  color: #e8f8ff;
  font-size: 20px;
  font-weight: bold;
  transition:
    transform 0.08s ease,
    opacity 0.08s ease,
    box-shadow 0.08s ease;
}

.changeMonth-btn {
  background: transparent;
  border: none;
  display: block;
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

.changeMonth-btn:active {
  transform: scale(0.93);
  opacity: 0.7;
  filter: brightness(0.9);
}

.summary-grid {
  display: grid;
  grid-template-columns: auto 1fr auto 1fr;
  row-gap: 6px;
  column-gap: 12px;
  align-items: center;
  font-size: 16px;
}

.summary-grid .label {
  color: rgba(232, 251, 255, 0.75);
  white-space: nowrap;
}

.summary-grid .value {
  font-weight: 600;
  text-align: right;
}

.table-head,
.daily-row {
  display: grid;
  grid-template-columns: var(--daily-cols);
  padding: 6px 4px;
  text-align: center;
  white-space: nowrap;
}

.table-head {
  font-weight: 600;
  letter-spacing: 0.05em;
  color: rgba(232, 251, 255, 0.9);
  border-bottom: 1px solid rgba(0, 230, 255, 0.25);
}

.table-body {
  max-height: 300px;
  overflow-y: auto;
}

.table-body::-webkit-scrollbar {
  display: none;
}

.daily-card {
  height: 330px;
  display: flex;
  flex-direction: column;
}

.daily-row {
  font-size: 16px;
  line-height: 22px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.daily-row.is-today {
  background: linear-gradient(90deg, rgba(0, 230, 255, 0.12), rgba(0, 230, 255, 0.04));
  border-left: 3px solid rgba(0, 230, 255, 0.9);
  box-shadow:
    inset 0 0 12px rgba(0, 230, 255, 0.25),
    0 0 10px rgba(0, 230, 255, 0.35);
}

.daily-row.is-today.is-saturday {
  border-left-color: #6ecbff;
}

.daily-row.is-today.is-sunday {
  border-left-color: #ff6b6b;
}

.weekday.is-saturday {
  color: #6ecbff;
}

.weekday.is-sunday {
  color: #ff6b6b;
}

.daily-row.is-today.is-saturday {
  background: rgba(110, 203, 255, 0.12);
}

.daily-row.is-today.is-sunday {
  background: rgba(255, 107, 107, 0.12);
}

.is-future {
  opacity: 0.35;
  filter: grayscale(40%);
}

.work-type.is-holiday-work {
  color: #ffb347;
  font-weight: 600;
  text-shadow: 0 0 6px rgba(255, 179, 71, 0.5);
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

/* 年月picker */
.year-month-container {
  position: relative;
}

.month-picker {
  position: absolute;
  top: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 500px;
  box-sizing: border-box;
  padding: 16px;
  border-radius: 16px;
  background: linear-gradient(180deg, #05203c, #031222);
  border: 1px solid rgba(0, 200, 255, 0.6);
  box-shadow:
    0 0 20px rgba(0, 200, 255, 0.6),
    inset 0 0 12px rgba(0, 150, 220, 0.3);

  z-index: 2;
}

.year-header {
  display: flex;
  font-size: 20px;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  color: #e8fbff;
  gap: 20px;
  position: relative;
}

.year-btn {
  background: transparent;
  width: 44px;
  height: 44px;
  border: none;
  font-size: 20px;
  color: rgba(0, 200, 255, 0.6);
  transition:
    transform 0.08s ease,
    opacity 0.08s ease,
    box-shadow 0.08s ease;
  touch-action: manipulation;
}

.year-btn:active {
  transform: scale(0.93);
  opacity: 0.7;
  filter: brightness(0.9);
}

.month-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.month-grid button {
  padding: 10px 0;
  border-radius: 8px;
  border: 1px solid #00c8ff;
  background: rgba(0, 10, 18, 0.8);
  color: #e8fbff;
}

.picker-backdrop {
  position: fixed;
  inset: 0;
  background: transparent;
  z-index: 1;
}

.month-btn {
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  color: #e8fbff;
  background: rgba(0, 200, 255, 0.15);
  border: 1px solid rgba(0, 200, 255, 0.5);
  position: absolute;
  right: 0;
}

.month-btn:active {
  transform: scale(0.95);
  opacity: 0.85;
}

@media (min-width: 768px) {
  .month-btn {
    right: 40px;
  }
}
</style>
