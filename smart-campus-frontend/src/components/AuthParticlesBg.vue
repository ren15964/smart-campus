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
    color: { value: ['#ffffff', '#a5b4fc', '#22d3ee'] },
    links: { enable: true, color: '#ffffff', distance: 140, opacity: 0.16, width: 1 },
    move: { enable: true, speed: 1.2, direction: 'none', outModes: { default: 'out' } },
    opacity: { value: 0.35 },
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
    radial-gradient(1000px 600px at 10% 10%, rgba(34, 211, 238, 0.28), transparent 55%),
    radial-gradient(900px 560px at 90% 20%, rgba(165, 180, 252, 0.26), transparent 52%),
    radial-gradient(900px 700px at 40% 100%, rgba(236, 72, 153, 0.18), transparent 58%),
    linear-gradient(135deg, #0b1020 0%, #111a3a 45%, #0b1020 100%);
  filter: saturate(1.12) contrast(1.02);
}
</style>

