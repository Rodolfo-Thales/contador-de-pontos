<script setup>
import levelUpMini from '../assets/logos/level-up-mini.png'
import os300Mini from '../assets/logos/os300-mini.jpg'

const props = defineProps({
  admin: { type: Boolean, default: false },
  groups: { type: Array, default: () => [] },
  leaderId: { type: String, default: null },
})

defineEmits(['login', 'logout'])

function scoreFor(groupId) {
  return props.groups.find((g) => g.id === groupId)?.points ?? 0
}

function formatScore(points) {
  return points.toLocaleString('pt-BR')
}

function scrollToScoreboard() {
  const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  document.getElementById('scoreboard')?.scrollIntoView({
    behavior: reduceMotion ? 'auto' : 'smooth',
    block: 'start',
  })
}
</script>

<template>
  <header class="header">
    <a href="/" class="h-logos" aria-label="Connect ADVEC — início">
      <div class="h-icon"></div>
      <div class="h-txts">
        <div class="h-main">CONNECT</div>
        <div class="h-sub">ADVEC</div>
      </div>
    </a>

    <button class="h-score" type="button" aria-label="Ir para o placar principal" @click="scrollToScoreboard">
      <div class="ht">
        <img class="ht-logo b" :src="levelUpMini" alt="Level Up" loading="eager" />
        <span class="ht-n b">Level Up</span>
        <span class="ht-p b" :class="{ 'ht-p--leader': leaderId === 'level-up' }">
          {{ formatScore(scoreFor('level-up')) }}
        </span>
      </div>
      <span class="ht-vs">VS</span>
      <div class="ht">
        <span class="ht-p r" :class="{ 'ht-p--leader': leaderId === 'os-300' }">
          {{ formatScore(scoreFor('os-300')) }}
        </span>
        <span class="ht-n r">Os 300</span>
        <img class="ht-logo r" :src="os300Mini" alt="Os 300" loading="eager" />
      </div>
    </button>

    <nav class="h-nav" aria-label="Navegação principal">
      <a class="nbtn" href="/quadro" aria-label="Quadro de pontuação">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M8 21h8M12 15v6M17 4H7a2 2 0 00-2 2v6a7 7 0 0014 0V6a2 2 0 00-2-2z" />
        </svg>
        <span>Quadro</span>
      </a>
      <a class="nbtn" href="/cronograma" aria-label="Cronograma">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <rect x="3" y="4" width="18" height="18" rx="2" />
          <path d="M16 2v4M8 2v4M3 10h18" />
        </svg>
        <span>Cronograma</span>
      </a>
      <button v-if="!admin" class="nbtn log" type="button" @click="$emit('login')">Entrar</button>
      <button v-else class="nbtn log" type="button" @click="$emit('logout')">Sair</button>
    </nav>
  </header>
</template>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 66px;
  background: rgba(6, 6, 16, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 clamp(16px, 3vw, 40px);
  animation: slide-down 0.5s ease;
}

.header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--color-connect-solid), var(--color-group-a), transparent);
  opacity: 0.2;
}

@keyframes slide-down {
  from {
    transform: translateY(-100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.h-logos {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.h-icon {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  background: var(--gradient-connect);
  -webkit-mask: url("data:image/svg+xml,%3Csvg viewBox='0 0 100 60' xmlns='http://www.w3.org/2000/svg'%3E%3Ccircle cx='30' cy='30' r='22' fill='none' stroke='white' stroke-width='10'/%3E%3Ccircle cx='70' cy='30' r='22' fill='none' stroke='white' stroke-width='10'/%3E%3C/svg%3E")
    center / contain no-repeat;
  mask: url("data:image/svg+xml,%3Csvg viewBox='0 0 100 60' xmlns='http://www.w3.org/2000/svg'%3E%3Ccircle cx='30' cy='30' r='22' fill='none' stroke='white' stroke-width='10'/%3E%3Ccircle cx='70' cy='30' r='22' fill='none' stroke='white' stroke-width='10'/%3E%3C/svg%3E")
    center / contain no-repeat;
  transition: transform 0.3s;
}

.h-logos:hover .h-icon {
  transform: scale(1.08);
}

.h-txts {
  display: flex;
  flex-direction: column;
  line-height: 1;
}

.h-main {
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 1.05rem;
  letter-spacing: 2.5px;
  background: var(--gradient-connect);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.h-sub {
  font-size: 0.5rem;
  font-weight: 600;
  letter-spacing: 4px;
  color: var(--color-muted-dim);
  text-transform: uppercase;
  margin-top: 2px;
}

.h-score {
  display: flex;
  align-items: center;
  gap: 12px;
  background: none;
  border: none;
  cursor: pointer;
}

.ht {
  display: flex;
  align-items: center;
  gap: 7px;
}

.ht-logo {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 1.5px solid rgba(255, 255, 255, 0.1);
}

.ht-logo.b {
  border-color: rgba(59, 158, 255, 0.4);
}

.ht-logo.r {
  border-color: rgba(232, 67, 48, 0.4);
}

.ht-n {
  font-size: 0.6rem;
  font-weight: 700;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.ht-n.b {
  color: var(--color-group-b);
}

.ht-n.r {
  color: var(--color-group-a);
}

.ht-p {
  font-family: var(--font-display);
  font-size: 1rem;
  font-weight: 900;
  transition: text-shadow var(--transition-base);
}

.ht-p.b {
  color: #60b4ff;
}

.ht-p.r {
  color: #ff6347;
}

.ht-p--leader {
  text-shadow: 0 0 10px currentColor;
}

.ht-vs {
  font-size: 0.5rem;
  color: var(--color-muted-dim);
  letter-spacing: 2px;
  font-weight: 600;
}

.h-nav {
  display: flex;
  align-items: center;
  gap: 6px;
}

.nbtn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 7px 14px;
  border-radius: 6px;
  background: none;
  border: 1px solid var(--color-border);
  color: var(--color-muted);
  text-decoration: none;
  font-family: var(--font-body);
  font-size: 0.7rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.nbtn:hover {
  border-color: var(--color-border-strong);
  color: var(--color-foreground);
  background: rgba(255, 255, 255, 0.03);
}

.nbtn.log {
  border-color: rgba(232, 101, 42, 0.3);
  color: var(--color-connect-solid);
}

.nbtn.log:hover {
  background: rgba(232, 101, 42, 0.08);
  border-color: rgba(232, 101, 42, 0.5);
}

.nbtn svg {
  width: 13px;
  height: 13px;
}

@media (max-width: 768px) {
  .header {
    height: 54px;
  }

  .h-score {
    display: none;
  }

  .h-nav .nbtn span {
    display: none;
  }
}
</style>
