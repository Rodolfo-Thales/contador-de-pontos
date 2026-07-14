<script setup>
import { computed } from 'vue'

const props = defineProps({
  history: { type: Array, required: true },
})

const COLOR_VARS = {
  'os-300': '--color-group-a',
  'level-up': '--color-group-b',
}

function colorVarFor(entry) {
  const colorVar = COLOR_VARS[entry.groupId]
  return colorVar ? `var(${colorVar})` : 'var(--color-muted)'
}

function actionLabel(entry) {
  return entry.amount > 0 ? 'Pontos adicionados' : 'Pontos removidos'
}

function formatDate(iso) {
  return new Date(iso).toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function formatDateShort(iso) {
  return new Date(iso).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' })
}

const recent = computed(() => props.history.slice(0, 10))
</script>

<template>
  <section class="history-section" aria-label="Últimas pontuações">
    <div class="history-card">
      <h2 class="title">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <circle cx="12" cy="12" r="9" />
          <path d="M12 7v5l3 3" />
        </svg>
        Últimas Pontuações
      </h2>

      <p v-if="!recent.length" class="empty">Nenhuma pontuação registrada ainda.</p>

      <TransitionGroup v-else tag="ul" name="row" class="entries">
        <li v-for="(entry, i) in recent" :key="entry.id" class="entry" :class="{ 'entry--odd': i % 2 === 1 }">
          <span class="entry-dot" :style="{ backgroundColor: colorVarFor(entry) }" aria-hidden="true"></span>
          <span class="entry-group">{{ entry.groupName }}</span>
          <span class="entry-action">{{ actionLabel(entry) }}</span>
          <span class="entry-amount" :class="entry.amount > 0 ? 'positive' : 'negative'">
            {{ entry.amount > 0 ? '+' : '' }}{{ entry.amount }}
          </span>
          <time class="entry-time entry-time--full" :datetime="entry.createdAt">{{ formatDate(entry.createdAt) }}</time>
          <time class="entry-time entry-time--short" :datetime="entry.createdAt">{{ formatDateShort(entry.createdAt) }}</time>
        </li>
      </TransitionGroup>
    </div>
  </section>
</template>

<style scoped>
.history-section {
  padding: var(--space-8) var(--space-4);
}

.history-card {
  max-width: 800px;
  margin: 0 auto;
  background-color: rgba(18, 19, 26, 0.8);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: var(--space-6);
}

.title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: 1.1rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: var(--space-4);
}

.title svg {
  width: 20px;
  height: 20px;
  color: var(--color-gold);
}

.empty {
  color: var(--color-muted);
  text-align: center;
  padding: var(--space-6) 0;
}

.entries {
  list-style: none;
  max-height: 440px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.entry {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-2);
  border-bottom: 1px solid var(--color-border);
}

.entry--odd {
  background-color: rgba(255, 255, 255, 0.02);
}

.entry:last-child {
  border-bottom: none;
}

.entry-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.entry-group {
  font-weight: 600;
  flex-shrink: 0;
}

.entry-action {
  color: var(--color-muted);
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.entry-amount {
  font-variant-numeric: tabular-nums;
  font-weight: 700;
  flex-shrink: 0;
}

.entry-amount.positive {
  color: var(--color-success);
}

.entry-amount.negative {
  color: #f87171;
}

.entry-time {
  color: var(--color-muted);
  font-size: 0.8125rem;
  font-variant-numeric: tabular-nums;
  flex-shrink: 0;
}

.row-enter-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.row-enter-from {
  opacity: 0;
  transform: translateX(-24px);
}

.entry-time--short {
  display: none;
}

@media (max-width: 600px) {
  .entry-action {
    display: none;
  }

  .entry-time--full {
    display: none;
  }

  .entry-time--short {
    display: inline;
    font-size: 0.75rem;
  }
}
</style>
