<script setup></script>

<template>
  <footer class="ai-footer">
    <p class="ai-status">
      AI学習状況:安定（精度92%）<br />
      直近7日間の行動データを反映済み
    </p>
  </footer>
</template>

<style scoped>
.ai-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64px;
  margin-top: 16px;
  font-size: 12px;
  font-weight: 600;
  line-height: 1.6;
  color: rgba(180, 240, 255, 0.85);
  text-shadow: 0 0 6px rgba(0, 200, 255, 0.25);
}
</style>
