<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Bell } from 'lucide-vue-next'
const route = useRoute()
const router = useRouter()
const pageTitle = computed(() => route.meta.title ?? '')
const home = () => {
  setTimeout(() => {
    router.push('/')
  }, 100)
}
</script>

<template>
  <header class="header">
    <!-- ハンバーガーメニュー -->
    <button class="menu-btn">
      <div class="menu-lines">
        <span></span>
        <span></span>
        <span></span>
      </div>
    </button>

    <!-- 中央タイトル -->
    <div class="title-wrapper">
      <button class="title-link" @click="home">Chrono Station</button>

      <span v-if="pageTitle" class="subtitle">{{ pageTitle }}</span>
    </div>

    <!-- 右アイコン -->
    <div class="right-actions">
      <button class="icon-btn bell-btn">
        <Bell :size="18" />
      </button>
      <button class="profile">
        <img src="/image.png" alt="profile" />
      </button>
    </div>
  </header>
</template>

<style scoped>
.header {
  height: auto;
  border-radius: 18px;
  margin: 10px 16px;
  padding: 8px 10px;
  background: radial-gradient(circle at top, #0a2a44 0%, #020a16 55%, #01070f 100%);
  border: 1px solid rgba(0, 200, 255, 0.6);
  box-shadow:
    0 0 12px rgba(0, 220, 255, 0.6),
    0 0 28px rgba(0, 150, 255, 0.35),
    inset 0 0 16px rgba(0, 180, 255, 0.25);
  text-shadow:
    0 0 8px rgba(0, 200, 255, 0.9),
    0 0 20px rgba(0, 200, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  line-height: 1.2;
}

.title-link {
  background: none;
  border: none;
  padding: 0;
  margin: 0;
  font-size: 23px;
  font-weight: 600;
  letter-spacing: 0.12rem;
  color: #e8fbff;
  line-height: inherit;
  text-decoration: none;
  cursor: pointer;
  display: inline-block;
  transition:
    transform 0.08s ease,
    opacity 0.08s ease,
    text-shadow 0.08s ease;
  text-shadow:
    1px 0 #00e6ff,
    -1px 0 #00e6ff,
    0 1px #00e6ff,
    0 -1px #00e6ff,
    0 0 8px rgba(0, 230, 255, 1),
    0 0 20px rgba(0, 180, 255, 0.9);
}

.title-link:active {
  transform: scale(0.94);
  opacity: 0.75;
  text-shadow: none;
}

.subtitle {
  margin-top: 10px;
  font-size: 20px;
  opacity: 0.8;
  letter-spacing: 0.05rem;
  color: rgba(0, 230, 255, 0.7);
  text-shadow: 0 0 4px rgba(0, 230, 255, 0.4);
}

.menu-btn {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  border: 1px solid #00c8ff;
  background-color: rgba(0, 10, 18, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.menu-lines {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
}

.menu-lines span {
  width: 18px;
  height: 2.2px;
  background-color: #00c8ff;
  border-radius: 2px;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon-btn {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: 1px solid #00c8ff;
  background-color: rgba(0, 10, 18, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-btn svg {
  color: #00c8ff;
  filter: drop-shadow(0 0 6px rgba(0, 200, 255, 0.6));
}

.profile {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid rgba(0, 200, 255, 0.9);
  box-shadow:
    0 0 6px rgba(0, 200, 255, 0.8),
    inset 0 0 6px rgba(0, 200, 255, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.profile img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 50%;
  transform: scale(1.7);
}
</style>
