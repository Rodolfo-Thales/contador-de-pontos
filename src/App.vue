<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import AppHeader from './components/AppHeader.vue'
import HeroSection from './components/HeroSection.vue'
import RecentHistorySection from './components/RecentHistorySection.vue'
import QuestSection from './components/QuestSection.vue'
import GroupCard from './components/GroupCard.vue'
import ScoreHistory from './components/ScoreHistory.vue'
import AdminLoginDialog from './components/AdminLoginDialog.vue'
import GlobeLabels from './components/GlobeLabels.vue'
import { useScoreboard } from './composables/useScoreboard'
import { useAuth } from './composables/useAuth'

const { groups, history, loading, error, refresh, addPoints, resetAll } = useScoreboard()
const { isAdmin, checkSession, login, logout } = useAuth()

const loginOpen = ref(false)
const actionError = ref(null)

const leaderId = computed(() => {
  if (groups.value.length < 2) return null
  const [a, b] = groups.value
  if (a.points === b.points) return null
  return a.points > b.points ? a.id : b.id
})

let pollTimer = null

onMounted(async () => {
  await Promise.all([checkSession(), refresh()])
  pollTimer = setInterval(refresh, 15000)
})

onUnmounted(() => clearInterval(pollTimer))

async function handleAddPoints(groupId, amount) {
  actionError.value = null
  try {
    await addPoints(groupId, amount)
  } catch (e) {
    if (e.status === 401) {
      isAdmin.value = false
      actionError.value = 'Sua sessão expirou. Entre novamente.'
    } else {
      actionError.value = e.message
    }
  }
}

async function handleReset() {
  if (!window.confirm('Zerar o placar e apagar todo o histórico?')) return
  actionError.value = null
  try {
    await resetAll()
  } catch (e) {
    actionError.value = e.message
  }
}
</script>

<template>
  <AppHeader
    :admin="isAdmin"
    :groups="groups"
    :leader-id="leaderId"
    @login="loginOpen = true"
    @logout="logout"
  />

  <HeroSection :groups="groups" :leader-id="leaderId" />

  <div class="section-transition"><span>Cada ponto conta</span></div>

  <RecentHistorySection v-if="!loading && !error" :history="history" />

  <div class="section-transition"><span>Missões ativas</span></div>

  <QuestSection :groups="groups" />

  <main class="main">
    <p v-if="loading" class="status">Carregando placar…</p>
    <p v-else-if="error" class="status status--error" role="alert">
      Não foi possível carregar o placar: {{ error }}
    </p>

    <template v-else>
      <p v-if="actionError" class="status status--error" role="alert">
        {{ actionError }}
      </p>

      <section class="scoreboard" aria-label="Placar dos grupos (detalhado, com ações de admin)">
        <GroupCard
          v-if="groups[0]"
          :group="groups[0]"
          :is-leader="leaderId === groups[0].id"
          :admin="isAdmin"
          @add-points="(amount) => handleAddPoints(groups[0].id, amount)"
        />

        <div class="versus" aria-hidden="true">VS</div>

        <GroupCard
          v-if="groups[1]"
          :group="groups[1]"
          :is-leader="leaderId === groups[1].id"
          :admin="isAdmin"
          @add-points="(amount) => handleAddPoints(groups[1].id, amount)"
        />
      </section>

      <ScoreHistory :history="history" :admin="isAdmin" @reset="handleReset" />
    </template>
  </main>

  <footer class="footer">
    <GlobeLabels class="footer-globe" />
    <blockquote class="footer-verse">
      «Ide por todo o mundo e pregai o evangelho a toda criatura.»
      <cite>Marcos 16:15</cite>
    </blockquote>
    <p>Connect ADVEC — Departamento de Jovens</p>
  </footer>

  <AdminLoginDialog :open="loginOpen" :login-fn="login" @close="loginOpen = false" />
</template>

<style scoped>
.main {
  max-width: 1100px;
  margin: 0 auto;
  padding: var(--space-8) var(--space-4) var(--space-16);
  display: flex;
  flex-direction: column;
  gap: var(--space-12);
}

.status {
  text-align: center;
  color: var(--color-muted);
  padding: var(--space-8) 0;
}

.status--error {
  color: #f87171;
  padding: var(--space-3);
  border: 1px solid rgba(248, 113, 113, 0.35);
  border-radius: var(--radius-sm);
}

.section-transition {
  position: relative;
  height: 90px;
  background-color: var(--color-background);
  display: flex;
  align-items: center;
  justify-content: center;
}

.section-transition span {
  position: relative;
  font-family: var(--font-display);
  font-size: clamp(0.6rem, 1vw, 0.75rem);
  font-weight: 700;
  letter-spacing: 6px;
  text-transform: uppercase;
  color: var(--color-muted-dim);
}

.section-transition span::before,
.section-transition span::after {
  content: '';
  position: absolute;
  top: 50%;
  width: clamp(30px, 8vw, 80px);
  height: 1px;
}

.section-transition span::before {
  right: calc(100% + 16px);
  background: linear-gradient(90deg, transparent, var(--color-border-strong));
}

.section-transition span::after {
  left: calc(100% + 16px);
  background: linear-gradient(90deg, var(--color-border-strong), transparent);
}

.scoreboard {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: stretch;
  gap: var(--space-4);
}

.versus {
  align-self: center;
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 1.5rem;
  color: var(--color-muted);
  padding: var(--space-2);
}

.footer {
  text-align: center;
  padding: var(--space-12) var(--space-4) var(--space-8);
  color: var(--color-muted);
  font-size: 0.875rem;
  border-top: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-6);
  overflow: hidden;
}

.footer-globe {
  width: min(380px, 78vw);
}

.footer-verse {
  max-width: 42ch;
  font-family: var(--font-display);
  font-size: 1rem;
  color: var(--color-foreground);
  line-height: 1.6;
}

.footer-verse cite {
  display: block;
  margin-top: var(--space-2);
  font-style: normal;
  font-size: 0.8rem;
  color: var(--color-muted);
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

@media (max-width: 768px) {
  .scoreboard {
    grid-template-columns: 1fr;
  }

  .versus {
    justify-self: center;
  }
}
</style>
