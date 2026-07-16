<script setup>
import { computed, ref } from 'vue'

const WEEKDAY_LABELS = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb']

const WEEKLY_SERVICES = {
  0: { name: 'Culto de Celebração', color: 'gold' },
  1: { name: 'Culto da Vitória', color: 'red' },
  3: { name: 'Culto da Palavra', color: 'blue' },
  4: { name: 'Interligados', color: 'purple' },
  6: { name: 'Connect Jovens', color: 'orange' },
}

const LEGEND = [
  { day: 'Segunda', name: 'Culto da Vitória', color: 'red' },
  { day: 'Quarta', name: 'Culto da Palavra', color: 'blue' },
  { day: 'Quinta', name: 'Interligados', color: 'purple' },
  { day: 'Sábado', name: 'Connect Jovens', color: 'orange' },
  { day: 'Domingo', name: 'Culto de Celebração', color: 'gold' },
]

const today = new Date()
const viewYear = ref(today.getFullYear())
const viewMonth = ref(today.getMonth())

function prevMonth() {
  if (viewMonth.value === 0) {
    viewMonth.value = 11
    viewYear.value -= 1
  } else {
    viewMonth.value -= 1
  }
}

function nextMonth() {
  if (viewMonth.value === 11) {
    viewMonth.value = 0
    viewYear.value += 1
  } else {
    viewMonth.value += 1
  }
}

function goToday() {
  viewYear.value = today.getFullYear()
  viewMonth.value = today.getMonth()
}

const monthLabel = computed(() => {
  const label = new Date(viewYear.value, viewMonth.value, 1).toLocaleDateString('pt-BR', {
    month: 'long',
    year: 'numeric',
  })
  return label.charAt(0).toUpperCase() + label.slice(1)
})

function nthWeekday(year, month, weekday, n) {
  const firstDow = new Date(year, month, 1).getDay()
  return 1 + ((weekday - firstDow + 7) % 7) + (n - 1) * 7
}

const santaCeiaTransmitida = computed(() => {
  const first = nthWeekday(viewYear.value, viewMonth.value, 0, 1)
  return first === 1 ? nthWeekday(viewYear.value, viewMonth.value, 0, 2) : first
})

const santaCeiaLocal = computed(() => nthWeekday(viewYear.value, viewMonth.value, 3, 2))

const calendarDays = computed(() => {
  const y = viewYear.value
  const m = viewMonth.value
  const firstDow = new Date(y, m, 1).getDay()
  const daysInMonth = new Date(y, m + 1, 0).getDate()
  const cells = []

  for (let i = 0; i < firstDow; i++) {
    cells.push({ key: `blank-${i}`, blank: true })
  }

  for (let day = 1; day <= daysInMonth; day++) {
    const dow = new Date(y, m, day).getDay()
    const events = []
    const service = WEEKLY_SERVICES[dow]
    if (service) events.push(service)
    if (day === santaCeiaTransmitida.value) {
      events.push({ name: 'Santa Ceia (transmitida)', color: 'green' })
    }
    if (day === santaCeiaLocal.value) {
      events.push({ name: 'Santa Ceia (local)', color: 'green' })
    }

    cells.push({
      key: `day-${day}`,
      day,
      events,
      isToday:
        day === today.getDate() && m === today.getMonth() && y === today.getFullYear(),
    })
  }

  return cells
})
</script>

<template>
  <section class="schedule" aria-label="Cronograma de cultos">
    <h1 class="st">Cronograma de Cultos</h1>
    <p class="ss">Programação semanal da ADVEC Moreno</p>

    <div class="cal-card">
      <div class="cal-nav">
        <button class="cal-btn" type="button" aria-label="Mês anterior" @click="prevMonth">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M15 18l-6-6 6-6" />
          </svg>
        </button>
        <div class="cal-title">
          <span>{{ monthLabel }}</span>
          <button class="cal-today" type="button" @click="goToday">Hoje</button>
        </div>
        <button class="cal-btn" type="button" aria-label="Próximo mês" @click="nextMonth">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M9 18l6-6-6-6" />
          </svg>
        </button>
      </div>

      <div class="cal-grid cal-grid--head" aria-hidden="true">
        <div v-for="label in WEEKDAY_LABELS" :key="label" class="cal-dow">{{ label }}</div>
      </div>

      <div class="cal-grid">
        <div
          v-for="cell in calendarDays"
          :key="cell.key"
          class="cal-cell"
          :class="{ 'cal-cell--blank': cell.blank, 'cal-cell--today': cell.isToday }"
        >
          <template v-if="!cell.blank">
            <span class="cal-day">{{ cell.day }}</span>
            <div class="cal-events">
              <span
                v-for="event in cell.events"
                :key="event.name"
                class="cal-event"
                :class="`c-${event.color}`"
                :title="event.name"
              >
                <span class="cal-dot" aria-hidden="true"></span>
                <span class="cal-event-name">{{ event.name }}</span>
              </span>
            </div>
          </template>
        </div>
      </div>
    </div>

    <div class="legend">
      <h2 class="legend-title">Programação semanal</h2>
      <ul class="legend-list">
        <li v-for="item in LEGEND" :key="item.name" class="legend-item">
          <span class="cal-dot" :class="`c-${item.color}`" aria-hidden="true"></span>
          <span class="legend-day">{{ item.day }}</span>
          <span class="legend-name">{{ item.name }}</span>
        </li>
      </ul>

      <h2 class="legend-title">Santa Ceia</h2>
      <ul class="legend-list">
        <li class="legend-item">
          <span class="cal-dot c-green" aria-hidden="true"></span>
          <span class="legend-day">Transmitida</span>
          <span class="legend-name">1º domingo do mês (se cair dia 01, passa para o 2º domingo)</span>
        </li>
        <li class="legend-item">
          <span class="cal-dot c-green" aria-hidden="true"></span>
          <span class="legend-day">Local</span>
          <span class="legend-name">2ª quarta-feira do mês</span>
        </li>
      </ul>
    </div>
  </section>
</template>

<style scoped>
.schedule {
  max-width: 1100px;
  margin: 0 auto;
  padding: calc(66px + var(--space-12)) var(--space-4) var(--space-16);
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

.cal-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  padding: var(--space-6);
}

.cal-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-6);
}

.cal-title {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 1.1rem;
  letter-spacing: 1px;
}

.cal-today {
  padding: 4px 10px;
  border-radius: 6px;
  border: 1px solid var(--color-border);
  color: var(--color-muted);
  font-size: 0.65rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: all var(--transition-fast);
}

.cal-today:hover {
  border-color: var(--color-border-strong);
  color: var(--color-foreground);
}

.cal-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  color: var(--color-muted);
  transition: all var(--transition-fast);
}

.cal-btn:hover {
  border-color: var(--color-border-strong);
  color: var(--color-foreground);
  background: var(--color-surface-hover);
}

.cal-btn svg {
  width: 16px;
  height: 16px;
}

.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: var(--space-1);
}

.cal-grid--head {
  margin-bottom: var(--space-2);
}

.cal-dow {
  text-align: center;
  font-family: var(--font-display);
  font-size: 0.65rem;
  font-weight: 700;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--color-muted-dim);
  padding: var(--space-1) 0;
}

.cal-cell {
  min-height: 92px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  padding: var(--space-2);
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.cal-cell--blank {
  border-color: transparent;
}

.cal-cell--today {
  border-color: var(--color-connect-solid);
  background: rgba(232, 101, 42, 0.06);
}

.cal-day {
  font-family: var(--font-display);
  font-size: 0.8rem;
  font-weight: 700;
  color: var(--color-muted);
}

.cal-cell--today .cal-day {
  color: var(--color-connect-solid);
}

.cal-events {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.cal-event {
  display: flex;
  align-items: center;
  gap: 5px;
  min-width: 0;
}

.cal-event-name {
  font-size: 0.62rem;
  font-weight: 600;
  color: var(--color-muted);
  line-height: 1.25;
}

.cal-dot {
  flex-shrink: 0;
  width: 7px;
  height: 7px;
  border-radius: 50%;
}

.c-gold .cal-dot,
.cal-dot.c-gold {
  background: var(--color-gold-bright);
}

.c-red .cal-dot,
.cal-dot.c-red {
  background: var(--color-group-a);
}

.c-blue .cal-dot,
.cal-dot.c-blue {
  background: var(--color-group-b);
}

.c-purple .cal-dot,
.cal-dot.c-purple {
  background: var(--color-accent);
}

.c-orange .cal-dot,
.cal-dot.c-orange {
  background: var(--color-connect-solid);
}

.c-green .cal-dot,
.cal-dot.c-green {
  background: var(--color-success);
}

.c-green .cal-event-name {
  color: var(--color-success);
}

.legend {
  margin-top: var(--space-8);
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.legend-title {
  font-family: var(--font-display);
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--color-muted-dim);
}

.legend-title + .legend-list {
  margin-bottom: var(--space-3);
}

.legend-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.legend-item {
  display: flex;
  align-items: baseline;
  gap: var(--space-2);
  font-size: 0.8rem;
}

.legend-item .cal-dot {
  align-self: center;
}

.legend-day {
  font-family: var(--font-display);
  font-weight: 700;
  color: var(--color-foreground);
  min-width: 92px;
}

.legend-name {
  color: var(--color-muted);
}

@media (max-width: 640px) {
  .cal-card {
    padding: var(--space-3);
  }

  .cal-cell {
    min-height: 58px;
    padding: 4px;
  }

  .cal-events {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .cal-event-name {
    display: none;
  }

  .legend-day {
    min-width: 80px;
  }
}
</style>
