<template>
  <div class="app-empty">
    <el-empty :description="description" :image-size="imageSize">
      <template v-if="$slots.default" #default>
        <slot />
      </template>
    </el-empty>
  </div>
</template>

<script setup>
defineProps({
  description: { type: String, default: '暂无数据' },
  imageSize: { type: Number, default: 110 }
})
</script>

<style scoped lang="scss">
.app-empty {
  padding: 10px 0;
}
</style>

