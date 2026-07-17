<script setup>
import { onMounted, ref } from 'vue'
import { MISSIONS, useMissions } from '../composables/useMissions'
import { useAuth } from '../composables/useAuth'

const props = defineProps({
  groups: { type: Array, default: () => [] },
  revealed: { type: Boolean, default: false },
})

const TEAM_NAMES = { 'level-up': 'Level Up', 'os-300': 'Os 300' }
const TEAM_IDS = ['level-up', 'os-300']

const { loading, error, refresh, isCompleted, setCompletion } = useMissions()
const { isAdmin } = useAuth()

onMounted(refresh)

const savingKey = ref(null)
const actionError = ref(null)

function teamName(groupId) {
  return props.groups.find((g) => g.id === groupId)?.name ?? TEAM_NAMES[groupId]
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
  <p v-if="loading" class="status">Carregando missões…</p>
  <p v-else-if="error" class="status status--error" role="alert">
    Não foi possível carregar as missões: {{ error }}
  </p>

  <template v-else>
    <p v-if="actionError" class="status status--error" role="alert">{{ actionError }}</p>

    <p v-if="isAdmin" class="admin-hint">
      Modo admin: clique na equipe para marcar ou desmarcar a missão — pode marcar as duas.
    </p>

    <div class="mw">
      <div v-for="mission in MISSIONS" :key="mission.id" class="mc rv" :class="{ v: revealed }">
        <span class="mn">{{ mission.number }}</span>
        <div class="mb">
          <div class="mtl">{{ mission.title }}</div>
          <div class="mds">{{ mission.description }}</div>
          <div class="mss">
            <button
              v-for="groupId in TEAM_IDS"
              :key="groupId"
              type="button"
              class="ms"
              :class="{ 'ms--clickable': isAdmin, 'ms--done': isCompleted(mission.id, groupId) }"
              :disabled="!isAdmin || savingKey === mission.id + groupId"
              :aria-pressed="isCompleted(mission.id, groupId)"
              @click="toggle(mission.id, groupId)"
            >
              <span class="ck" :class="isCompleted(mission.id, groupId) ? 'y' : 'n'">
                {{ isCompleted(mission.id, groupId) ? '✓' : '○' }}
              </span>
              <span :class="isCompleted(mission.id, groupId) ? 'sy' : 'sn'">
                {{ teamName(groupId) }} — {{ isCompleted(mission.id, groupId) ? 'Completa' : 'Pendente' }}
              </span>
            </button>
          </div>
        </div>
      </div>

      <div class="pw rv" :class="{ v: revealed }">
        <div v-for="groupId in TEAM_IDS" :key="groupId" class="pr">
          <span class="pl" :class="groupId === 'level-up' ? 'pl--b' : 'pl--r'">{{ teamName(groupId) }}</span>
          <div class="pt">
            <div
              class="pf"
              :class="groupId === 'level-up' ? 'pf--b' : 'pf--r'"
              :style="{ width: progressPct(groupId) + '%' }"
            ></div>
          </div>
          <span class="pp">{{ completedCount(groupId) }}/{{ MISSIONS.length }}</span>
        </div>
        <p v-for="groupId in TEAM_IDS" :key="`badge-${groupId}`" class="all-complete">
          <template v-if="completedCount(groupId) === MISSIONS.length">
            <span class="sparkle" aria-hidden="true">✦</span>
            {{ teamName(groupId) }}: todas as missões completas!
            <span class="sparkle" aria-hidden="true">✦</span>
          </template>
        </p>
      </div>
    </div>
  </template>
</template>

<style scoped>
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

.mw {
  max-width: 780px;
  margin: 0 auto;
}

.mc {
  display: flex;
  align-items: flex-start;
  gap: 18px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  padding: 22px;
  margin-bottom: 14px;
  transition: all 0.25s;
}

.mc.v:hover {
  border-color: var(--color-border-strong);
}

.mn {
  font-family: var(--font-display);
  font-size: 1.4rem;
  font-weight: 900;
  color: var(--color-gold);
  line-height: 1;
  flex-shrink: 0;
  min-width: 38px;
}

.mb {
  flex: 1;
  min-width: 0;
}

.mtl {
  font-family: var(--font-display);
  font-size: 0.9rem;
  font-weight: 800;
  letter-spacing: 1px;
  text-transform: uppercase;
  margin-bottom: 4px;
  color: var(--color-foreground);
}

.mds {
  font-size: 0.8rem;
  color: var(--color-muted);
  margin-bottom: 12px;
}

.mss {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.ms {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  background: none;
  border: none;
  padding: 0;
  cursor: default;
  font-family: var(--font-body);
}

.ms--clickable {
  cursor: pointer;
  padding: 8px 14px;
  border-radius: 8px;
  border: 1px dashed var(--color-border-strong);
  background: rgba(255, 255, 255, 0.02);
  transition: border-color var(--transition-fast), background var(--transition-fast),
    transform var(--transition-fast);
}

.ms--clickable:hover:enabled {
  border-color: rgba(52, 210, 123, 0.45);
  background: rgba(52, 210, 123, 0.05);
  transform: translateY(-1px);
}

.ms--clickable.ms--done {
  border-style: solid;
  border-color: rgba(52, 210, 123, 0.35);
  background: rgba(52, 210, 123, 0.07);
}

.ms--clickable.ms--done:hover:enabled {
  border-color: rgba(248, 113, 113, 0.4);
  background: rgba(248, 113, 113, 0.05);
}

.ms--clickable:disabled {
  opacity: 0.55;
  cursor: wait;
}

.admin-hint {
  text-align: center;
  font-size: 0.72rem;
  color: var(--color-muted-dim);
  margin-bottom: 18px;
}

.ck {
  width: 18px;
  height: 18px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.6rem;
  font-weight: 700;
  flex-shrink: 0;
  transition: background var(--transition-fast), border-color var(--transition-fast);
}

.ck.y {
  background: rgba(52, 210, 123, 0.12);
  color: var(--color-success);
  border: 1px solid rgba(52, 210, 123, 0.25);
}

.ck.n {
  background: rgba(255, 255, 255, 0.03);
  color: var(--color-muted-dim);
  border: 1px solid var(--color-border);
}

.sy {
  color: var(--color-success);
}

.sn {
  color: var(--color-muted-dim);
}

.pw {
  margin-top: 28px;
}

.pr {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.pl {
  font-size: 0.7rem;
  font-weight: 700;
  min-width: 68px;
  letter-spacing: 0.5px;
}

.pl--b {
  color: var(--color-group-b);
}

.pl--r {
  color: var(--color-group-a);
}

.pt {
  flex: 1;
  height: 5px;
  border-radius: 3px;
  background: rgba(255, 255, 255, 0.04);
  overflow: hidden;
}

.pf {
  height: 100%;
  border-radius: 3px;
  transition: width 1s ease;
}

.pf--b {
  background: var(--gradient-level-up);
}

.pf--r {
  background: linear-gradient(90deg, #6b1010, var(--color-group-a));
}

.pp {
  font-family: var(--font-display);
  font-size: 0.65rem;
  font-weight: 700;
  min-width: 30px;
  text-align: right;
  color: var(--color-muted);
}

.all-complete {
  text-align: center;
  margin-top: var(--space-2);
  color: var(--color-gold);
  font-weight: 700;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
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

.rv {
  opacity: 0;
  transform: translateY(18px);
  transition: opacity 0.5s ease, transform 0.5s ease;
}

.rv.v {
  opacity: 1;
  transform: translateY(0);
}

@media (max-width: 600px) {
  .mc {
    flex-direction: column;
    gap: 10px;
  }

  .mss {
    flex-direction: column;
    gap: 6px;
  }

  .ms--clickable {
    width: 100%;
    min-height: 44px;
  }
}
</style>
