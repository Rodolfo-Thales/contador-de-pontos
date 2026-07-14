<script setup>
defineProps({
  src: { type: String, required: true },
  alt: { type: String, required: true },
  background: { type: String, required: true },
})
</script>

<template>
  <span
    class="gradient-logo"
    role="img"
    :aria-label="alt"
    :style="{ '--logo-src': `url(${src})`, '--logo-bg': background }"
  />
</template>

<style scoped>
.gradient-logo {
  display: inline-block;
  background: var(--logo-bg);
  -webkit-mask: var(--logo-src) center / contain no-repeat;
  mask: var(--logo-src) center / contain no-repeat;
}
</style>
