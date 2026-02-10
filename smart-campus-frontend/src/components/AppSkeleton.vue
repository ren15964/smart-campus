<template>
  <el-skeleton :loading="loading" animated>
    <template #template>
      <div class="app-sk">
        <div v-for="i in rows" :key="i" class="app-sk__line" />
      </div>
    </template>
    <template #default>
      <slot />
    </template>
  </el-skeleton>
</template>

<script setup>
defineProps({
  loading: { type: Boolean, default: false },
  rows: { type: Number, default: 6 }
})
</script>

<style scoped lang="scss">
.app-sk {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 6px 0;
}

.app-sk__line {
  height: 12px;
  border-radius: 10px; // 增大圆角
  background: rgba(0, 0, 0, 0.08); // 使用更柔和的背景色
}
</style>

