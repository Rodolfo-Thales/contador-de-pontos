<script setup>
import { ref } from 'vue'

const props = defineProps({
  group: { type: Object, required: true },
  isLeader: { type: Boolean, default: false },
  admin: { type: Boolean, default: false },
})

const emit = defineEmits(['add-points'])

const quickActions = [10, 50, 100]

const customValue = ref('')
const MAX_AMOUNT = 10000

function submitCustom() {
  const amount = Number.parseInt(customValue.value, 10)
  if (!Number.isFinite(amount) || amount === 0) return
  if (Math.abs(amount) > MAX_AMOUNT) return
  emit('add-points', amount)
  customValue.value = ''
}

const COLOR_VARS = {
  'os-300': '--color-group-a',
  'level-up': '--color-group-b',
}

const colorVar = COLOR_VARS[props.group.id] ?? '--color-accent'
const groupColor = `var(${colorVar})`
const groupColorSoft = `var(${colorVar}-soft)`
</script>

<template>
  <article
    class="card"
    :class="{ 'card--leader': isLeader }"
    :style="{ '--group-color': groupColor, '--group-color-soft': groupColorSoft }"
  >
    <div v-if="isLeader" class="leader-badge">
      <svg
        width="14"
        height="14"
        viewBox="0 0 24 24"
        fill="currentColor"
        aria-hidden="true"
      >
        <path
          d="M5 16L3 6l5.5 4L12 4l3.5 6L21 6l-2 10H5zm14 3a1 1 0 01-1 1H6a1 1 0 01-1-1v-1h14v1z"
        />
      </svg>
      Liderando
    </div>

    <h2 class="group-name">{{ group.name }}</h2>

    <p class="points" aria-live="polite">
      <span class="points-value">{{ group.points.toLocaleString('pt-BR') }}</span>
      <span class="points-label">pontos</span>
    </p>

    <div
      v-if="admin"
      class="actions"
      role="group"
      :aria-label="`Adicionar pontos para ${group.name}`"
    >
      <button
        v-for="amount in quickActions"
        :key="amount"
        class="btn btn--add"
        type="button"
        @click="emit('add-points', amount)"
      >
        +{{ amount }}
      </button>
      <button
        class="btn btn--remove"
        type="button"
        :aria-label="`Remover 10 pontos de ${group.name}`"
        @click="emit('add-points', -10)"
      >
        −10
      </button>

      <form class="custom-form" @submit.prevent="submitCustom">
        <label class="sr-only" :for="`custom-${group.id}`">
          Valor personalizado para {{ group.name }}
        </label>
        <input
          :id="`custom-${group.id}`"
          v-model="customValue"
          class="custom-input"
          type="number"
          inputmode="numeric"
          :min="-MAX_AMOUNT"
          :max="MAX_AMOUNT"
          step="1"
          placeholder="Outro valor (ex.: -25)"
        />
        <button class="btn btn--add" type="submit" :disabled="!customValue">
          Aplicar
        </button>
      </form>
    </div>
  </article>
</template>

<style scoped>
.card {
  position: relative;
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--space-8) var(--space-6);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-6);
  box-shadow: var(--shadow-card);
  transition: border-color var(--transition-base);
}

.card--leader {
  border-color: var(--group-color);
}

.leader-badge {
  position: absolute;
  top: calc(-1 * var(--space-3));
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  background-color: var(--group-color);
  color: #fff;
  font-size: 0.75rem;
  font-weight: 600;
  padding: var(--space-1) var(--space-3);
  border-radius: 999px;
}

.group-name {
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--group-color);
  text-align: center;
}

.points {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-1);
}

.points-value {
  font-family: var(--font-display);
  font-variant-numeric: tabular-nums;
  font-size: clamp(3rem, 8vw, 4.5rem);
  font-weight: 800;
  line-height: 1;
}

.points-label {
  color: var(--color-muted);
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: var(--space-2);
}

.btn {
  min-width: 64px;
  min-height: 44px;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-sm);
  font-weight: 600;
  font-size: 1rem;
  transition: background-color var(--transition-fast), transform var(--transition-fast);
}

.btn:active {
  transform: scale(0.96);
}

.btn--add {
  background-color: var(--group-color-soft);
  color: var(--group-color);
  border: 1px solid var(--group-color);
}

.btn--add:hover {
  background-color: var(--group-color);
  color: #fff;
}

.btn--remove {
  background-color: transparent;
  color: var(--color-muted);
  border: 1px solid var(--color-border);
}

.btn--remove:hover {
  border-color: var(--color-muted);
  color: var(--color-foreground);
}

.custom-form {
  display: flex;
  gap: var(--space-2);
  width: 100%;
  justify-content: center;
  margin-top: var(--space-2);
}

.custom-input {
  min-height: 44px;
  width: 180px;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  background-color: var(--color-background);
  color: var(--color-foreground);
  font-size: 1rem;
  font-variant-numeric: tabular-nums;
}

.custom-input:focus {
  outline: 2px solid var(--group-color);
  outline-offset: 1px;
  border-color: var(--group-color);
}

.custom-input::placeholder {
  color: var(--color-muted);
}

.btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>
