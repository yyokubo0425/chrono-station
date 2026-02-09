<script setup>
import { RouterView } from 'vue-router'
import MainHeader from './components/MainHeader.vue'
import MainFooter from './components/MainFooter.vue'
</script>

<template>
  <div class="app-layout">
    <MainHeader />

    <main class="page-content">
      <RouterView />
    </main>

    <MainFooter />
  </div>
</template>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
}

.page-content {
  display: flex;
  flex-direction: column;
}
</style>
