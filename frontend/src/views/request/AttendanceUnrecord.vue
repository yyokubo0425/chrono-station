<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/lib/api'

const route = useRoute()
const router = useRouter()

//route param
const workDate = route.params.workDate

//状態
// ２重送信防止フラグ
const submitting = ref(false)
const error = ref(null)

//修正後の打刻情報(申請内容)
const after = ref({
  workStart: '',
  workEnd: '',
  breakMinutes: null,
  reason: '',
})

//入力情報全クリア
const clearAll = () => {
  after.value.workStart = null
  after.value.workEnd = null
  after.value.breakMinutes = null
}

//申請内容送信
const submitUnrecord = async () => {
  if (submitting.value) return
  const message = validationErrorMessage.value
  if (message) {
    return
  }

  if (!after.value.workStart) {
    error.value = '出勤時刻は必須です。'
    window.scrollTo({ top: 0, behavior: 'smooth' })
    return
  }

  if (!after.value.reason) {
    error.value = '申請理由は必須です。'
    window.scrollTo({ top: 0, behavior: 'smooth' })
    return
  }

  submitting.value = true
  error.value = null

  try {
    await api.post(`/attendance-unrecord/${workDate}`, {
      requestedWorkStart: after.value.workStart,
      requestedWorkEnd: after.value.workEnd || null,
      requestedBreakMinutes: after.value.breakMinutes,
      reason: after.value.reason,
    })
    //成功→日次勤怠画面に遷移
    router.back()
  } catch (e) {
    error.value = e.response?.data?.message ?? '打刻  漏れ申請の送信に失敗しました。'
    window.scrollTo({ top: 0, behavior: 'smooth' })
  } finally {
    submitting.value = false
  }
}

//送信可能判定
const cansubmit = computed(() => validationErrorMessage.value === null)

// 入力チェックエラー
const validationErrorMessage = computed(() => {
  if (after.value.workStart && after.value.workEnd) {
    if (after.value.workStart >= after.value.workEnd) {
      return '出勤時刻は退勤時刻より前の時間を指定してください。'
    }
  }
  if (after.value.breakMinutes !== null && after.value.breakMinutes < 0) {
    return '休憩時間は0分以上で入力してください。'
  }
  return null
})

//日付変換
const formatDate = (date) => {
  const d = new Date(date)
  return `${d.getFullYear()}/${d.getMonth() + 1}/${d.getDate()}`
}
</script>

<template>
  <div class="page">
    <!-- サーバーエラー -->
    <p v-if="error" class="error">{{ error }}</p>

    <div class="date">対象日：{{ formatDate(workDate) }}</div>

    <p class="note">※ 変更前の打刻を確認してから修正してください。</p>

    <!-- 元打刻情報 -->
    <section class="card before-card">
      <h3 class="card-title">元の打刻情報(変更前)</h3>
      <div class="">この日は打刻がありません</div>
    </section>

    <!-- 申請内容(修正後) -->
    <section class="card after-card">
      <!-- 入力チェックエラー -->
      <p v-if="validationErrorMessage" class="errorMessage">{{ validationErrorMessage }}</p>
      <div class="section-header">
        <h3 class="card-title">修正後の勤怠(申請内容)</h3>
        <button class="clear-all-btn" @click="clearAll">クリア</button>
      </div>
      <div class="form-row">
        <div class="field-row">
          <label> 出勤 </label>
          <div class="field">
            <input type="time" v-model="after.workStart" />
            <button v-if="after.workStart" class="clear-btn" @click="after.workStart = null">
              ×
            </button>
          </div>
        </div>

        <div class="field">
          <label> 退勤 </label>
          <input type="time" v-model="after.workEnd" />
        </div>

        <div class="field">
          <label> 休憩 </label>
          <div class="break-wrap">
            <input class="break-input" type="number" min="0" v-model.number="after.breakMinutes" />
            <span class="unit">分</span>
          </div>
        </div>
        <div></div>
      </div>

      <small class="hint">※休憩は「分」で入力(例：1時間 → 60)</small>
    </section>

    <!-- 申請理由 -->
    <section class="card">
      <h2 class="card-title">申請理由(必須)</h2>
      <button class="clear-all-btn" @click="clearAll">クリア</button>
      <textarea
        rows="4"
        maxlength="200"
        placeholder="例：打刻漏れのため修正します。"
        v-model="after.reason"
      />
      <div class="count">{{ after.reason.length }} / 200</div>
    </section>

    <div class="actions">
      <p class="note">※申請後は管理者の承認が必要です。</p>
      <!-- ボタン -->
      <div class="btn-actions">
        <button class="primary-btn" @click="router.back()">キャンセル</button>
        <button class="primary-btn" :disabled="!cansubmit || submitting" @click="submitUnrecord">
          申請する
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 0 16px;
  color: #e6f7ff;
}

.date {
  text-align: center;
  font-weight: 600;
  font-size: 23px;
  margin: 16px 0 8px 0;
}

.note {
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 10px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card {
  align-items: center;
  padding: 20px;
  margin-bottom: 16px;
  border-radius: 18px;
  color: #e6f7ff;
  background: radial-gradient(circle at top, #0a2a44 0%, #020a16 55%, #01070f 100%);
  border: 1px solid rgba(0, 200, 255, 0.6);
  box-shadow:
    0 0 12px rgba(0, 220, 255, 0.6),
    0 0 28px rgba(0, 150, 255, 0.35),
    inset 0 0 16px rgba(0, 180, 255, 0.25);
  box-sizing: border-box;
}

.card-title {
  font-size: 20px;
  padding-bottom: 10px;
  margin: 0 0 12px 0;
  opacity: 0.9;
  position: relative;
}

.card-title::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 1px;
  background: linear-gradient(
    to right,
    rgba(0, 50, 70, 0.85) 0%,
    rgba(0, 130, 180, 0.45) 35%,
    rgba(0, 200, 240, 0.75) 50%,
    rgba(0, 130, 180, 0.45) 65%,
    rgba(0, 50, 70, 0.85) 100%
  );
  box-shadow: 0 0 2px rgba(0, 140, 180, 0.35);
}

.row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 6px;
}

.field-row {
  display: grid;
  grid-template-columns: 80px 1fr;
  gap: 4px;
  align-items: center;
}

.field {
  display: grid;
  grid-template-columns: 80px 1fr;
  position: relative;
  align-items: center;
  gap: 8px;
}

.clear-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  color: #7de7ff;
  font-size: 18px;
  cursor: pointer;
}

.clear-btn:hover {
  color: #ffffff;
}

label {
  font-size: 13px;
  align-content: center;
}

.form-row input {
  width: 100px;
  height: 40px;
  padding: 6px 8px;
  border-radius: 8px;
  border: 1px solid rgba(0, 200, 255, 0.9);
  background: rgba(3, 20, 40, 0.9);
  color: #e6f7ff;
  box-shadow:
    inset 0 0 6px rgba(0, 200, 255, 0.4),
    0 0 6px rgba(0, 200, 255, 0.4);
  box-sizing: border-box;
}

.form-row input:focus {
  outline: none;
  box-shadow:
    0 0 10px rgba(0, 220, 255, 0.9),
    inset 0 0 8px rgba(0, 220, 255, 0.6);
}

.break-wrap {
  display: flex;
  align-items: center;
  gap: 6px;
}

textarea {
  width: 100%;
  min-height: 96px;
  border-radius: 14px;
  padding: 12px;
  border: 1px solid rgba(0, 200, 255, 0.9);
  background: rgba(3, 20, 40, 0.95);
  color: #e6f7ff;
  box-shadow:
    inset 0 0 10px rgba(0, 200, 255, 0.4),
    0 0 10px rgba(0, 200, 255, 0.4);
  box-sizing: border-box;
}

textarea:focus {
  outline: none;
  box-shadow:
    0 0 10px rgba(0, 220, 255, 0.9),
    inset 0 0 8px rgba(0, 220, 255, 0.6);
}

.count {
  text-align: right;
  font-size: 11px;
  opacity: 0.7;
  margin-top: 4px;
}

.hint {
  font-size: 11px;
  opacity: 0.7;
}

.error,
.errorMessage {
  color: #ff6b6b;
  font-size: 12px;
  margin: 6px 0;
  text-align: center;
}

.btn-actions {
  display: flex;
  justify-content: center;
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
