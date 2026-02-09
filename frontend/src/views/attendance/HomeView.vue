<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/lib/api'

const router = useRouter()

//月次勤怠画面遷移
const monthly = () => {
  setTimeout(() => {
    router.push('/monthly-attendance')
  }, 100)
}

const attendance = ref(null)
const loading = ref(true)
const isSubmitting = ref(false) //ボタン連打防止用

//出勤状態ラベル
const statusLabel = computed(() => {
  if (attendance.value === null) return '未出勤'

  switch (attendance.value.workingStatus) {
    case 'WORKING':
      return '出勤中'
    case 'BREAK':
      return '休憩中'
    case 'FINISHED':
      return '退勤済み'
    default:
      return '未出勤'
  }
})

// 時間変換
const formatTime = (dataTime) => {
  if (!dataTime) return '―'
  return new Date(dataTime).toLocaleTimeString('ja-JP', {
    hour: '2-digit',
    minute: '2-digit',
  })
}

//労務時間変換
const formatMinutes = (minutes) => {
  if (minutes == null) return '―'
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return `${h}時間${String(m).padStart(2, '0')}分`
}

const breakElapsedMinutes = ref(null)
let breakTimerId = null
//休憩中タイマー開始
const startBreakTimer = () => {
  stopBreakTimer()

  breakTimerId = setInterval(() => {
    if (!attendance.value?.breakStart) {
      breakElapsedMinutes.value = 0
      return
    }

    const start = new Date(attendance.value.breakStart)
    const now = new Date()

    const diffMs = now - start
    breakElapsedMinutes.value = Math.floor(diffMs / 1000 / 60)
  }, 1000)
}

//休憩中タイマー停止
const stopBreakTimer = () => {
  if (breakTimerId) {
    clearInterval(breakTimerId)
    breakTimerId = null
  }
}

// 休憩中タイマーの管理
watch(
  () => attendance.value?.workingStatus,
  (status) => {
    if (status === 'BREAK') {
      startBreakTimer()
    } else {
      stopBreakTimer()
      breakElapsedMinutes.value = null
    }
  },
  { immediate: true },
)

//今日の勤怠を取得
const fetchTodayAttendance = async () => {
  loading.value = true

  try {
    const res = await api.get('/attendance/today')
    attendance.value = res.data && res.data.workingStatus ? res.data : null
  } catch (e) {
    console.error(e)
    attendance.value = null
  }
  loading.value = false
}

//出勤
const clockIn = async () => {
  if (isSubmitting.value) return
  isSubmitting.value = true

  try {
    await api.post(`/attendance/clock-in`)
    await fetchTodayAttendance()
  } catch (e) {
    console.error(e)
  } finally {
    isSubmitting.value = false
  }
}

//休憩
const breakToggle = async () => {
  if (isSubmitting.value) return
  isSubmitting.value = true

  try {
    await api.post(`/attendance/break`)
    await fetchTodayAttendance()
  } catch (e) {
    console.error(e)
  } finally {
    isSubmitting.value = false
  }
}

//退勤
const clockOut = async () => {
  if (isSubmitting.value) return
  isSubmitting.value = true

  try {
    await api.post(`/attendance/clock-out`)
    await fetchTodayAttendance()
  } catch (e) {
    console.error(e)
  } finally {
    isSubmitting.value = false
  }
}

//初回起動時にAPIを呼び出す
onMounted(fetchTodayAttendance)
</script>

<template>
  <div class="top-page">
    <!-- 本日の記録 -->
    <section class="card today-card">
      <h2 class="card-title">本日の記録</h2>

      <!-- 読み込み中 -->
      <div v-if="loading" class="loading">読み込み中・・・</div>

      <!-- データ取得後 -->
      <div v-else>
        <!-- ステータス表示 -->
        <p class="status">{{ statusLabel }}</p>

        <!-- 出勤前 -->
        <template v-if="attendance === null">
          <div class="record-list">
            <div class="record-row">
              <div class="label">出勤：</div>
              <div class="value">―</div>
            </div>
            <div class="record-row">
              <div class="label">休憩：</div>
              <div class="value">―</div>
            </div>
            <div class="record-row">
              <div class="label">退勤：</div>
              <div class="value">―</div>
              <div class="action">
                <button class="primary-btn" :disabled="isSubmitting" @click="clockIn">
                  出勤する
                </button>
              </div>
            </div>
          </div>
        </template>

        <!-- 出勤中 -->
        <template v-else-if="attendance.workingStatus === 'WORKING'">
          <div class="record-list">
            <div class="record-row">
              <div class="label">出勤：</div>
              <div class="value">{{ formatTime(attendance.workStart) }}</div>
            </div>
            <div class="record-row">
              <div class="label">休憩：</div>
              <div class="value">
                {{ formatMinutes(attendance.breakMinutes) }}
              </div>
              <div class="action">
                <button class="primary-btn" :disabled="isSubmitting" @click="breakToggle">
                  休憩開始
                </button>
              </div>
            </div>
            <div class="record-row">
              <div class="label">退勤：</div>
              <div class="value">―</div>
              <div class="action">
                <button class="primary-btn" :disabled="isSubmitting" @click="clockOut">
                  退勤する
                </button>
              </div>
            </div>
          </div>
        </template>

        <!-- 休憩中 -->
        <template v-else-if="attendance.workingStatus === 'BREAK'">
          <div class="record-list">
            <div class="record-row">
              <div class="label">出勤：</div>
              <div class="value">{{ formatTime(attendance.workStart) }}</div>
            </div>
            <div class="record-row">
              <div class="label">休憩：</div>
              <div class="value">
                {{ breakElapsedMinutes !== null ? formatMinutes(breakElapsedMinutes) : '―' }}
              </div>
              <div class="action">
                <button class="primary-btn" :disabled="isSubmitting" @click="breakToggle">
                  休憩終了
                </button>
              </div>
            </div>
          </div>
        </template>

        <!-- 出勤済み -->
        <template v-else-if="attendance.workingStatus === 'FINISHED'">
          <div class="record-list">
            <div class="record-row">
              <div class="label">出勤：</div>
              <div class="value">{{ formatTime(attendance.workStart) }}</div>
            </div>
            <div class="record-row">
              <div class="label">休憩：</div>
              <div class="value">{{ formatMinutes(attendance.breakMinutes) }}</div>
            </div>
            <div class="record-row">
              <div class="label">退勤：</div>
              <div class="value">{{ formatTime(attendance.workEnd) }}</div>
            </div>
            <div class="record-row total">
              <div class="label">実働：</div>
              <div class="value">{{ formatMinutes(attendance.totalWorkMinutes) }}</div>
            </div>
          </div>
        </template>

        <!-- 想定外 -->
        <template v-else>
          <p style="color: red">状態不明</p>
        </template>
      </div>
    </section>

    <!-- AI通知 -->
    <section class="card ai-card">
      <h2 class="card-title">AI通知</h2>
      <p class="ai-text">
        普段利用している◯◯線で10分の遅延が発生しています。<br />
        過去の傾向からいつもより早めの出勤を推奨します。
      </p>
    </section>

    <div class="btn-actions">
      <button class="primary-btn">申請一覧</button>
      <button class="primary-btn" @click="monthly">月次勤怠</button>
    </div>
  </div>
</template>

<style scoped>
.top-page {
  width: 100%;
  padding: 0 16px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  margin: 0 auto;
}

.card {
  width: 100%;
  padding: 20px;
  margin-top: 16px;
  border-radius: 18px;
  /* background: linear-gradient(180deg, #05203c, #031222); */
  background: radial-gradient(circle at top, #0a2a44 0%, #020a16 55%, #01070f 100%);
  border: 1px solid rgba(0, 200, 255, 0.6);
  box-shadow:
    0 0 12px rgba(0, 220, 255, 0.6),
    0 0 28px rgba(0, 150, 255, 0.35),
    inset 0 0 16px rgba(0, 180, 255, 0.25);
  color: #e8f8ff;
  box-sizing: border-box;
}

.today-card {
  box-shadow:
    0 0 12px rgba(0, 220, 255, 0.6),
    0 0 28px rgba(0, 150, 255, 0.35),
    inset 0 0 16px rgba(0, 180, 255, 0.25);
}

.card-title {
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 6px;
}

.status {
  text-align: center;
  font-size: 18px;
  color: #9fe9ff;
  margin-bottom: 12px;
}

.record-list {
  flex: 1;
}

.record-row {
  display: grid;
  grid-template-columns: 50px 1fr 160px;
  align-items: center;
  min-height: 64px;
  white-space: nowrap;
}

.record-row .action {
  display: flex;
  justify-content: stretch;
}

.record-row.total {
  font-weight: 600;
  color: #00e6ff;
}

.ai-card {
  min-height: unset;
  height: 210px;
}

.ai-text {
  font-size: 14px;
  line-height: 1.6;
  color: #cfefff;
  min-height: 3.5em;
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

.btn-actions {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  gap: 16px;
}
</style>
