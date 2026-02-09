<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/lib/api'

const loading = ref(false)
// const error = ref(null)
const router = useRouter()

//状態
const requests = ref([])
const selectedStatus = ref('申請中')
const sortOrder = ref('ASC') //ASC:古い順、DESC:新しい順

const filteredRequests = computed(() => {
  let list = requests.value.filter((r) => r.status === selectedStatus.value)

  list.sort((a, b) => {
    return sortOrder.value === 'ASC'
      ? new Date(a.createdAt) - new Date(b.createdAt)
      : new Date(b.createdAt) - new Date(a.createdAt)
  })
  return list
})

//API通信
const fetchRequests = async () => {
  loading.value = true
  try {
    const res = await api.get('/admin/corrections')
    requests.value = res.data
  } catch (error) {
    error.value = '申請中の取得に失敗しました。'
  } finally {
    loading.value = false
  }
}

//詳細画面遷移
const detail = (id) => {
  router.push(`/admin/corrections/${id}`)
}

onMounted(fetchRequests)
</script>

<template>
  <div class="page">
    <!-- フィルター -->
    <div class="filter-area">
      <button
        v-for="status in ['申請中', '承認済', '不承認']"
        :key="status"
        class="filter-btn"
        :class="{ active: selectedStatus === status }"
        @click="selectedStatus = status"
      >
        {{ status }}
      </button>
    </div>

    <!-- ソート -->
    <div class="sort-area">
      並び順：
      <select v-model="sortOrder">
        <option value="ASC">日時（古い順）</option>
        <option value="DESC">日時（新しい順）</option>
      </select>
    </div>

    <!-- 一覧 -->
    <div class="request-card">
      <table>
        <thead>
          <tr>
            <th>種別</th>
            <th>氏名</th>
            <th>対象日</th>
            <th>状態</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="r in filteredRequests"
            :key="r.correctionId"
            class="request-row"
            @click="detail(r.correctionId)"
          >
            <td>{{ r.type }}</td>
            <td>{{ r.employeeName }}</td>
            <td>{{ r.workDate }}</td>
            <td>
              <span
                class="status"
                :class="{
                  refuested: r.status === '申請中',
                  approved: r.status === '承認済',
                  rejected: r.status === '不承認',
                }"
              >
                {{ r.status }}
              </span>
            </td>
          </tr>

          <!-- データなし -->
          <tr v-if="!filteredRequests.length">
            <td colspan="4" class="empty">該当する申請がありません。</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
