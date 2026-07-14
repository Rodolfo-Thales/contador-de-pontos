<script setup>
import { computed, onMounted, ref } from 'vue'
import { MISSIONS, useMissions } from '../composables/useMissions'
import { useAuth } from '../composables/useAuth'

const props = defineProps({
  groups: { type: Array, default: () => [] },
})

const TEAM_NAMES = { 'level-up': 'Level Up', 'os-300': 'Os 300' }
const TEAM_IDS = ['level-up', 'os-300']

const { completions, loading, error, refresh, isCompleted, setCompletion } = useMissions()
const { isAdmin } = useAuth()

onMounted(refresh)

const savingKey = ref(null)
const actionError = ref(null)

function teamName(groupId) {
  return props.groups.find((g) => g.id === groupId)?.name ?? TEAM_NAMES[groupId]
}

function bothCompleted(missionId) {
  return TEAM_IDS.every((groupId) => isCompleted(missionId, groupId))
}

async function toggle(missionId, groupId) {
  if (!isAdmin.value) return
  const key = missionId + groupId
  savingKey.value = key
  actionError.value = null
  try {
    await setCompletion(missionId, groupId, !isCompleted(missionId, groupId))
  } catch (e) {
    actionError.value = e.message
  } finally {
    savingKey.value = null
  }
}

function completedCount(groupId) {
  return MISSIONS.filter((m) => isCompleted(m.id, groupId)).length
}

function progressPct(groupId) {
  return Math.round((completedCount(groupId) / MISSIONS.length) * 100)
}
</script>

<template>
  <div class="missions">
    <div class="missions-header">
      <h3 class="missions-title">Missões de Lançamento</h3>
      <p class="missions-subtitle">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M4.5 16.5c-1.5 1.26-2 5-2 5s3.74-.5 5-2c.71-.84.7-2.13-.09-2.91a2.18 2.18 0 0 0-2.91-.09z" />
          <path d="M12 15l-3-3a22 22 0 0 1 2-3.95A12.88 12.88 0 0 1 22 2c0 2.72-.78 7.5-6 11a22.35 22.35 0 0 1-4 2z" />
          <path d="M9 12H4s.55-3.03 2-4c1.62-1.08 5 0 5 0M12 15v5s3.03-.55 4-2c1.08-1.62 0-5 0-5" />
        </svg>
        Cumpra primeiro e largue na frente!
      </p>
    </div>

    <p v-if="loading" class="status">Carregando missões…</p>
    <p v-else-if="error" class="status status--error" role="alert">
      Não foi possível carregar as missões: {{ error }}
    </p>

    <template v-else>
      <p v-if="actionError" class="status status--error" role="alert">{{ actionError }}</p>

      <div class="missions-list">
        <article
          v-for="mission in MISSIONS"
          :key="mission.id"
          class="mission-card"
          :class="{ 'mission-card--done': bothCompleted(mission.id) }"
        >
          <span class="mission-number">{{ mission.number }}</span>
          <div class="mission-body">
            <h4 class="mission-title">{{ mission.title }}</h4>
            <p class="mission-desc">{{ mission.description }}</p>
          </div>
          <div class="mission-status">
            <button
              v-for="groupId in TEAM_IDS"
              :key="groupId"
              type="button"
              class="status-pill"
              :class="{
                'status-pill--done': isCompleted(mission.id, groupId),
                'status-pill--clickable': isAdmin,
              }"
              :disabled="!isAdmin || savingKey === mission.id + groupId"
              @click="toggle(mission.id, groupId)"
            >
              <svg
                v-if="isCompleted(mission.id, groupId)"
                class="status-icon status-icon--done"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                aria-hidden="true"
              >
                <circle cx="12" cy="12" r="9" />
                <path d="M8 12l3 3 5-6" />
              </svg>
              <svg
                v-else
                class="status-icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                aria-hidden="true"
              >
                <circle cx="12" cy="12" r="9" />
              </svg>
              {{ teamName(groupId) }}: {{ isCompleted(mission.id, groupId) ? 'Completa' : 'Pendente' }}
            </button>
          </div>
        </article>
      </div>

      <div class="missions-progress">
        <div v-for="groupId in TEAM_IDS" :key="groupId" class="progress-row">
          <span class="progress-label">
            {{ teamName(groupId) }}: {{ completedCount(groupId) }}/{{ MISSIONS.length }} missões completas
          </span>
          <div class="progress-bar">
            <div
              class="progress-fill"
              :class="`progress-fill--${groupId}`"
              :style="{ width: progressPct(groupId) + '%' }"
            ></div>
          </div>
          <span class="progress-pct">{{ progressPct(groupId) }}%</span>
          <p v-if="completedCount(groupId) === MISSIONS.length" class="all-complete-badge">
            <span class="sparkle" aria-hidden="true">✦</span>
            Todas as missões completas!
            <span class="sparkle" aria-hidden="true">✦</span>
          </p>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.missions {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.missions-header {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.missions-title {
  font-size: 1.3rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.missions-subtitle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  color: var(--color-muted);
}

.missions-subtitle svg {
  width: 18px;
  height: 18px;
  color: var(--color-gold);
}

.status {
  text-align: center;
  color: var(--color-muted);
  padding: var(--space-6) 0;
}

.status--error {
  color: #f87171;
  padding: var(--space-3);
  border: 1px solid rgba(248, 113, 113, 0.35);
  border-radius: var(--radius-sm);
}

.missions-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.mission-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  background-color: rgba(18, 19, 26, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: var(--space-4) var(--space-6);
  transition: transform var(--transition-base), box-shadow var(--transition-base), border-color var(--transition-base);
}

.mission-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 0 20px rgba(255, 106, 0, 0.12);
}

.mission-card--done {
  border-color: rgba(34, 197, 94, 0.35);
  opacity: 0.92;
}

.mission-number {
  font-family: var(--font-score);
  font-size: 2rem;
  font-weight: 800;
  color: var(--color-gold);
  flex-shrink: 0;
  min-width: 48px;
}

.mission-body {
  flex: 1;
  min-width: 0;
}

.mission-title {
  font-weight: 700;
  text-transform: uppercase;
  font-size: 0.95rem;
  letter-spacing: 0.5px;
}

.mission-desc {
  color: var(--color-muted);
  font-size: 0.875rem;
}

.mission-status {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  flex-shrink: 0;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-2);
  border-radius: 999px;
  border: 1px solid var(--color-border);
  background: transparent;
  color: var(--color-muted);
  font-size: 0.8rem;
  white-space: nowrap;
  cursor: default;
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
}

.status-pill--clickable {
  cursor: pointer;
}

.status-pill--clickable:hover {
  border-color: var(--color-muted);
}

.status-pill:disabled {
  cursor: not-allowed;
}

.status-pill--done {
  color: var(--color-success);
  border-color: rgba(34, 197, 94, 0.4);
  box-shadow: 0 0 8px rgba(34, 197, 94, 0.25);
}

.status-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.status-icon--done {
  animation: stamp 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes stamp {
  0% {
    transform: scale(0);
  }
  60% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

.missions-progress {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.progress-row {
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: center;
  gap: var(--space-2) var(--space-3);
}

.progress-label {
  grid-column: 1 / -1;
  font-size: 0.875rem;
  color: var(--color-muted);
}

.progress-bar {
  height: 10px;
  border-radius: 5px;
  background-color: var(--color-surface);
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  transition: width 0.6s ease;
}

.progress-fill--level-up {
  background: var(--gradient-level-up);
}

.progress-fill--os-300 {
  background: var(--gradient-os-300);
}

.progress-pct {
  font-variant-numeric: tabular-nums;
  font-weight: 700;
  font-size: 0.875rem;
}

.all-complete-badge {
  grid-column: 1 / -1;
  text-align: center;
  padding: var(--space-2);
  border: 1px solid var(--color-gold);
  border-radius: var(--radius-sm);
  color: var(--color-gold);
  font-weight: 800;
  text-transform: uppercase;
  font-size: 0.8rem;
}

.sparkle {
  display: inline-block;
  animation: sparkle-pulse 1.4s ease-in-out infinite;
}

.sparkle:last-child {
  animation-delay: 0.4s;
}

@keyframes sparkle-pulse {
  0%,
  100% {
    opacity: 0.4;
    transform: scale(0.8);
  }
  50% {
    opacity: 1;
    transform: scale(1.15);
  }
}

@media (max-width: 600px) {
  .mission-card {
    flex-direction: column;
    align-items: flex-start;
    padding: var(--space-4);
  }

  .mission-status {
    width: 100%;
  }
}
</style>
