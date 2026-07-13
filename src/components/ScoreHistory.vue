<script setup>
defineProps({
  history: { type: Array, required: true },
  admin: { type: Boolean, default: false },
})

const emit = defineEmits(['reset'])

const COLOR_VARS = {
  'os-300': '--color-group-a',
  'level-up': '--color-group-b',
}

function formatDate(iso) {
  return new Date(iso).toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function colorVarFor(entry) {
  const colorVar = COLOR_VARS[entry.groupId]
  return colorVar ? `var(${colorVar})` : 'var(--color-muted)'
}
</script>

<template>
  <section class="history" aria-label="Histórico de pontuações">
    <div class="history-header">
      <h2 class="history-title">Histórico</h2>
      <button
        v-if="admin && history.length"
        class="btn-reset"
        type="button"
        @click="emit('reset')"
      >
        Zerar placar
      </button>
    </div>

    <p v-if="!history.length" class="empty">
      Nenhuma pontuação registrada ainda. Use os botões acima para começar.
    </p>

    <ul v-else class="entries">
      <li v-for="entry in history.slice(0, 20)" :key="entry.id" class="entry">
        <span
          class="entry-dot"
          :style="{ backgroundColor: colorVarFor(entry) }"
          aria-hidden="true"
        ></span>
        <span class="entry-group">{{ entry.groupName }}</span>
        <span class="entry-amount" :class="entry.amount > 0 ? 'positive' : 'negative'">
          {{ entry.amount > 0 ? '+' : '' }}{{ entry.amount }}
        </span>
        <time class="entry-time" :datetime="entry.createdAt">
          {{ formatDate(entry.createdAt) }}
        </time>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.history {
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
}

.history-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-4);
}

.history-title {
  font-size: 1.25rem;
  font-weight: 700;
}

.btn-reset {
  min-height: 44px;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  color: var(--color-muted);
  font-weight: 500;
  font-size: 0.875rem;
  transition: color var(--transition-fast), border-color var(--transition-fast);
}

.btn-reset:hover {
  color: #fca5a5;
  border-color: #fca5a5;
}

.empty {
  color: var(--color-muted);
  padding: var(--space-6) 0;
  text-align: center;
}

.entries {
  list-style: none;
  display: flex;
  flex-direction: column;
}

.entry {
  display: grid;
  grid-template-columns: auto 1fr auto auto;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-2);
  border-bottom: 1px solid var(--color-border);
}

.entry:last-child {
  border-bottom: none;
}

.entry-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.entry-group {
  font-weight: 500;
}

.entry-amount {
  font-variant-numeric: tabular-nums;
  font-weight: 600;
}

.entry-amount.positive {
  color: #4ade80;
}

.entry-amount.negative {
  color: #f87171;
}

.entry-time {
  color: var(--color-muted);
  font-size: 0.8125rem;
  font-variant-numeric: tabular-nums;
}
</style>
