<template>
  <div class="auth-bg" aria-hidden="true">
    <Particles class="auth-bg__particles" :options="options" />
    <div class="auth-bg__overlay" />
  </div>
</template>

<script setup>
const options = {
  fullScreen: { enable: false },
  fpsLimit: 60,
  background: { color: { value: 'transparent' } },
  detectRetina: true,
  particles: {
    number: { value: 70, density: { enable: true, area: 900 } },
    color: { value: ['#e0e2e5', '#f0f2f5', '#409eff'] }, // 调整为浅色主题颜色
    links: { enable: true, color: '#e0e2e5', distance: 140, opacity: 0.3, width: 1 }, // 调整为浅色链接线
    move: { enable: true, speed: 1.2, direction: 'none', outModes: { default: 'out' } },
    opacity: { value: 0.5 }, // 略微增加不透明度
    size: { value: { min: 1, max: 3 } }
  },
  interactivity: {
    events: {
      onHover: { enable: false, mode: 'repulse' },
      onClick: { enable: false, mode: 'push' }
    }
  }
}
</script>

<style scoped lang="scss">
.auth-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  pointer-events: none; // 不拦截表单交互
}

.auth-bg__particles {
  position: absolute;
  inset: 0;
}

.auth-bg__overlay {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(1000px 600px at 10% 10%, rgba(64, 158, 255, 0.1), transparent 55%), // Element Plus primary blue
    radial-gradient(900px 560px at 90% 20%, rgba(144, 147, 153, 0.1), transparent 52%), // Element Plus info grey
    radial-gradient(900px 700px at 40% 100%, rgba(245, 108, 108, 0.08), transparent 58%), // Element Plus danger red
    linear-gradient(135deg, var(--app-bg) 0%, var(--app-bg-soft) 45%, var(--app-bg) 100%); // 浅色主题背景
  filter: saturate(1.05) contrast(1.02);
}
</style>

