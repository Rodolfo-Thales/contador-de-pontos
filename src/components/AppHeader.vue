<script setup>
defineProps({
  admin: { type: Boolean, default: false },
})

defineEmits(['login', 'logout'])
</script>

<template>
  <header class="header">
    <div class="header-inner">
      <div class="brand">
        <!-- Substitua pelo <img> da logo quando estiver pronta -->
        <div class="logo-placeholder" aria-hidden="true">
          <svg
            width="28"
            height="28"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <circle cx="12" cy="12" r="10" />
            <path d="M8 12h8M12 8v8" />
          </svg>
        </div>
        <div class="brand-text">
          <span class="brand-name">Connect</span>
          <span class="brand-sub">ADVEC</span>
        </div>
      </div>

      <div class="header-right">
        <p class="tagline">Contador de Pontos</p>
        <button
          v-if="!admin"
          class="btn-auth"
          type="button"
          @click="$emit('login')"
        >
          Entrar
        </button>
        <button v-else class="btn-auth" type="button" @click="$emit('logout')">
          Sair
        </button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.header {
  border-bottom: 1px solid var(--color-border);
  background-color: var(--color-surface);
}

.header-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: var(--space-4);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
}

.brand {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.logo-placeholder {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: grid;
  place-items: center;
  background-color: var(--color-accent-soft);
  color: var(--color-accent);
  border: 1px dashed var(--color-accent);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.15;
}

.brand-name {
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 1.375rem;
}

.brand-sub {
  font-size: 0.8125rem;
  font-weight: 600;
  color: var(--color-muted);
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.tagline {
  color: var(--color-muted);
  font-weight: 500;
  font-size: 0.9375rem;
}

.btn-auth {
  min-height: 44px;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  color: var(--color-muted);
  font-weight: 500;
  font-size: 0.875rem;
  transition: color var(--transition-fast), border-color var(--transition-fast);
}

.btn-auth:hover {
  color: var(--color-foreground);
  border-color: var(--color-muted);
}

@media (max-width: 480px) {
  .tagline {
    display: none;
  }
}
</style>
