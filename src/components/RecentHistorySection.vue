<script setup>
import { computed } from 'vue'
import { useRevealOnScroll } from '../composables/useRevealOnScroll'

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

const { targetRef, revealed } = useRevealOnScroll()
</script>

<template>
  <section ref="targetRef" class="sect" aria-label="Últimas pontuações">
    <h2 class="st rv" :class="{ v: revealed }">Últimas Pontuações</h2>
    <p class="ss rv" :class="{ v: revealed }">Acompanhe cada movimentação da gincana</p>

    <div class="hc rv" :class="{ v: revealed }">
      <div class="hc-h">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <circle cx="12" cy="12" r="9" />
          <path d="M12 7v5l3 3" />
        </svg>
        Histórico
      </div>

      <p v-if="!recent.length" class="empty">Nenhuma pontuação registrada ainda.</p>

      <TransitionGroup v-else tag="ul" name="row" class="entries">
        <li v-for="entry in recent" :key="entry.id" class="hr">
          <span class="hrd" :style="{ backgroundColor: colorVarFor(entry) }" aria-hidden="true"></span>
          <span class="hrt" :style="{ color: colorVarFor(entry) }">{{ entry.groupName }}</span>
          <span class="hra">{{ actionLabel(entry) }}</span>
          <span class="hrp" :class="entry.amount > 0 ? 'positive' : 'negative'">
            {{ entry.amount > 0 ? '+' : '' }}{{ entry.amount }}
          </span>
          <time class="hrdt hrdt--full" :datetime="entry.createdAt">{{ formatDate(entry.createdAt) }}</time>
          <time class="hrdt hrdt--short" :datetime="entry.createdAt">{{ formatDateShort(entry.createdAt) }}</time>
        </li>
      </TransitionGroup>
    </div>
  </section>
</template>

<style scoped>
.sect {
  padding: 70px 20px;
  background-color: var(--color-background);
}

.st {
  font-family: var(--font-display);
  font-size: 1.4rem;
  font-weight: 800;
  letter-spacing: 4px;
  text-transform: uppercase;
  text-align: center;
  margin-bottom: 6px;
  color: var(--color-foreground);
}

.ss {
  font-size: 0.85rem;
  font-weight: 500;
  text-align: center;
  color: var(--color-muted);
  margin-bottom: 44px;
}

.rv {
  opacity: 0;
  transform: translateY(18px);
  transition: opacity 0.5s ease, transform 0.5s ease;
}

.rv.v {
  opacity: 1;
  transform: translateY(0);
}

.hc {
  max-width: 780px;
  margin: 0 auto;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  overflow: hidden;
}

.hc-h {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 22px;
  border-bottom: 1px solid var(--color-border);
  font-family: var(--font-display);
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--color-muted);
}

.hc-h svg {
  width: 16px;
  height: 16px;
  color: var(--color-gold);
}

.empty {
  color: var(--color-muted);
  text-align: center;
  padding: var(--space-6) 0;
}

.entries {
  list-style: none;
}

.hr {
  display: flex;
  align-items: center;
  padding: 13px 22px;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.025);
  transition: background 0.15s;
}

.hr:hover {
  background: rgba(255, 255, 255, 0.015);
}

.hr:last-child {
  border-bottom: none;
}

.hrd {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  flex-shrink: 0;
}

.hrt {
  font-weight: 700;
  font-size: 0.8rem;
  min-width: 72px;
  flex-shrink: 0;
}

.hra {
  flex: 1;
  min-width: 0;
  color: var(--color-muted);
  font-size: 0.8rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hrp {
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 0.8rem;
  color: var(--color-success);
  flex-shrink: 0;
}

.hrp.negative {
  color: #f87171;
}

.hrdt {
  font-size: 0.7rem;
  color: var(--color-muted-dim);
  white-space: nowrap;
  flex-shrink: 0;
}

.hrdt--short {
  display: none;
}

.row-enter-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.row-enter-from {
  opacity: 0;
  transform: translateX(-24px);
}

@media (max-width: 600px) {
  .hra {
    display: none;
  }

  .hrdt--full {
    display: none;
  }

  .hrdt--short {
    display: inline;
  }
}
</style>
