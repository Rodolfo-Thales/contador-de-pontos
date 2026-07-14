<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import GradientLogo from './GradientLogo.vue'
import connectIcon from '../assets/logos/connect-icon.png'
import connectText from '../assets/logos/connect-text.png'
import advecText from '../assets/logos/advec-text.png'
import levelUpMini from '../assets/logos/level-up-mini.png'

const props = defineProps({
  admin: { type: Boolean, default: false },
  groups: { type: Array, default: () => [] },
  leaderId: { type: String, default: null },
})

defineEmits(['login', 'logout'])

const COLOR_VARS = {
  'os-300': '--color-group-a',
  'level-up': '--color-group-b',
}

function colorVarFor(groupId) {
  return COLOR_VARS[groupId] ?? '--color-accent'
}

function scoreFor(groupId) {
  return props.groups.find((g) => g.id === groupId)?.points ?? 0
}

function formatScore(points) {
  return points.toLocaleString('pt-BR')
}

const scrolled = ref(false)
function onScroll() {
  scrolled.value = window.scrollY > 8
}

const mobileMenuOpen = ref(false)
function closeMobileMenu() {
  mobileMenuOpen.value = false
}
function onKeydown(e) {
  if (e.key === 'Escape') closeMobileMenu()
}

watch(mobileMenuOpen, (open) => {
  document.body.style.overflow = open ? 'hidden' : ''
  if (open) window.addEventListener('keydown', onKeydown)
  else window.removeEventListener('keydown', onKeydown)
})

function scrollToScoreboard() {
  closeMobileMenu()
  const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  document.getElementById('scoreboard')?.scrollIntoView({
    behavior: reduceMotion ? 'auto' : 'smooth',
    block: 'start',
  })
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  window.removeEventListener('keydown', onKeydown)
  document.body.style.overflow = ''
})
</script>

<template>
  <header class="header" :class="{ 'header--scrolled': scrolled }">
    <div class="header-inner">
      <a href="/" class="brand" aria-label="Connect ADVEC — início">
        <GradientLogo :src="connectIcon" alt="Connect" background="var(--gradient-connect)" class="brand-icon" />
        <div class="brand-text">
          <GradientLogo :src="connectText" alt="Connect" background="var(--gradient-connect)" class="brand-connect" />
          <GradientLogo :src="advecText" alt="ADVEC" background="var(--color-advec-tint)" class="brand-advec" />
        </div>
      </a>

      <button
        class="mini-scoreboard"
        type="button"
        aria-label="Ir para o placar principal"
        @click="scrollToScoreboard"
      >
        <img :src="levelUpMini" alt="Level Up" class="mini-logo" />
        <span
          class="mini-score"
          :class="{ 'mini-score--leader': leaderId === 'level-up' }"
          :style="{ '--glow-color': `var(${colorVarFor('level-up')})` }"
        >
          {{ formatScore(scoreFor('level-up')) }}
        </span>
        <span class="mini-vs">VS</span>
        <span
          class="mini-score"
          :class="{ 'mini-score--leader': leaderId === 'os-300' }"
          :style="{ '--glow-color': `var(${colorVarFor('os-300')})` }"
        >
          {{ formatScore(scoreFor('os-300')) }}
        </span>
        <span class="mini-badge">300</span>
      </button>

      <nav class="nav nav--desktop" aria-label="Navegação principal">
        <a class="nav-link" href="/quadro">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M8 21h8M12 17v4M7 4h10v4a5 5 0 0 1-10 0V4z" />
            <path d="M7 5H4a1 1 0 0 0-1 1 4 4 0 0 0 4 4M17 5h3a1 1 0 0 1 1 1 4 4 0 0 1-4 4" />
          </svg>
          Quadro de Pontuação
        </a>
        <a class="nav-link" href="/cronograma">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <rect x="3" y="5" width="18" height="16" rx="2" />
            <path d="M16 3v4M8 3v4M3 10h18" />
          </svg>
          Cronograma
        </a>
        <button v-if="!admin" class="btn-auth" type="button" @click="$emit('login')">Entrar</button>
        <button v-else class="btn-auth" type="button" @click="$emit('logout')">Sair</button>
      </nav>

      <button
        class="hamburger"
        type="button"
        aria-label="Abrir menu"
        :aria-expanded="mobileMenuOpen"
        @click="mobileMenuOpen = true"
      >
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" aria-hidden="true">
          <path d="M3 6h18M3 12h18M3 18h18" />
        </svg>
      </button>
    </div>

    <Transition name="slide">
      <div v-if="mobileMenuOpen" class="mobile-overlay" role="dialog" aria-modal="true" aria-label="Menu de navegação">
        <button class="mobile-close" type="button" aria-label="Fechar menu" @click="closeMobileMenu">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" aria-hidden="true">
            <path d="M6 6l12 12M18 6L6 18" />
          </svg>
        </button>

        <button
          class="mini-scoreboard mini-scoreboard--mobile"
          type="button"
          aria-label="Ir para o placar principal"
          @click="scrollToScoreboard"
        >
          <img :src="levelUpMini" alt="Level Up" class="mini-logo" />
          <span
            class="mini-score"
            :class="{ 'mini-score--leader': leaderId === 'level-up' }"
            :style="{ '--glow-color': `var(${colorVarFor('level-up')})` }"
          >
            {{ formatScore(scoreFor('level-up')) }}
          </span>
          <span class="mini-vs">VS</span>
          <span
            class="mini-score"
            :class="{ 'mini-score--leader': leaderId === 'os-300' }"
            :style="{ '--glow-color': `var(${colorVarFor('os-300')})` }"
          >
            {{ formatScore(scoreFor('os-300')) }}
          </span>
          <span class="mini-badge">300</span>
        </button>

        <nav class="nav nav--mobile" aria-label="Navegação móvel">
          <a class="nav-link" href="/quadro" @click="closeMobileMenu">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M8 21h8M12 17v4M7 4h10v4a5 5 0 0 1-10 0V4z" />
              <path d="M7 5H4a1 1 0 0 0-1 1 4 4 0 0 0 4 4M17 5h3a1 1 0 0 1 1 1 4 4 0 0 1-4 4" />
            </svg>
            Quadro de Pontuação
          </a>
          <a class="nav-link" href="/cronograma" @click="closeMobileMenu">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <rect x="3" y="5" width="18" height="16" rx="2" />
              <path d="M16 3v4M8 3v4M3 10h18" />
            </svg>
            Cronograma
          </a>
          <button
            v-if="!admin"
            class="btn-auth"
            type="button"
            @click="closeMobileMenu(); $emit('login')"
          >
            Entrar
          </button>
          <button v-else class="btn-auth" type="button" @click="closeMobileMenu(); $emit('logout')">
            Sair
          </button>
        </nav>
      </div>
    </Transition>
  </header>
</template>

<style scoped>
.header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background-color: rgba(10, 10, 20, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  animation: header-enter 0.4s ease;
}

.header::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 1px;
  background: var(--gradient-connect);
  opacity: 0.3;
}

@keyframes header-enter {
  from {
    opacity: 0;
    transform: translateY(-16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 72px;
  padding: 0 var(--space-4);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
  transition: height var(--transition-base);
}

.header--scrolled .header-inner {
  height: 56px;
}

.brand {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  text-decoration: none;
  flex-shrink: 0;
}

.brand-icon {
  width: 44px;
  height: 44px;
  flex-shrink: 0;
  transform: scale(1);
  transition: transform var(--transition-base);
}

.header--scrolled .brand-icon {
  /* Encolhe visualmente sem alterar o box (evita reflow) */
  transform: scale(0.818);
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-connect {
  width: 128px;
  height: 30px;
}

.brand-advec {
  width: 82px;
  height: 20px;
  opacity: 0.8;
}

.mini-scoreboard {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  background-color: var(--color-surface);
  transition: border-color var(--transition-fast), background-color var(--transition-fast);
}

.mini-scoreboard:hover {
  border-color: var(--color-muted);
}

.mini-logo {
  height: 32px;
  width: auto;
  border-radius: 4px;
}

.mini-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 32px;
  min-width: 32px;
  padding: 0 var(--space-2);
  border-radius: 4px;
  background-color: var(--color-badge-fill);
  color: var(--color-foreground);
  font-family: var(--font-score);
  font-weight: 700;
  font-size: 0.8125rem;
}

.mini-score {
  font-family: var(--font-score);
  font-weight: 700;
  font-size: 1.2rem;
  font-variant-numeric: tabular-nums;
  color: var(--color-foreground);
  transition: text-shadow var(--transition-base), color var(--transition-base);
}

.mini-score--leader {
  color: var(--glow-color);
  text-shadow: 0 0 12px var(--glow-color);
}

.mini-vs {
  font-family: var(--font-display);
  font-weight: 700;
  font-size: 0.75rem;
  color: var(--color-muted);
}

.nav {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.nav-link {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--color-muted);
  text-decoration: none;
  font-size: 0.85rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: color var(--transition-fast);
}

.nav-link svg {
  width: 16px;
  height: 16px;
}

.nav-link:hover {
  color: var(--color-foreground);
}

.btn-auth {
  min-height: 44px;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  background: transparent;
  color: var(--color-foreground);
  font-size: 0.85rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
}

.btn-auth:hover {
  border-color: var(--color-gold);
  box-shadow: 0 0 12px rgba(255, 215, 0, 0.25);
}

.hamburger {
  display: none;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: var(--radius-sm);
  color: var(--color-foreground);
}

.hamburger svg {
  width: 22px;
  height: 22px;
}

.mobile-overlay {
  position: fixed;
  inset: 0;
  z-index: 1001;
  background-color: rgba(10, 10, 20, 0.98);
  backdrop-filter: blur(12px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-8);
  padding: var(--space-8);
}

.mobile-close {
  position: absolute;
  top: var(--space-4);
  right: var(--space-4);
  width: 44px;
  height: 44px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--color-foreground);
}

.mobile-close svg {
  width: 22px;
  height: 22px;
}

.nav--mobile {
  flex-direction: column;
  gap: var(--space-6);
}

.nav--mobile .nav-link {
  font-size: 1.1rem;
}

.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  transform: translateX(100%);
  opacity: 0;
}

@media (max-width: 1024px) {
  .header-inner {
    flex-wrap: wrap;
    height: auto;
    padding: var(--space-3) var(--space-4);
    justify-content: center;
  }

  .header--scrolled .header-inner {
    height: auto;
  }

  .nav--desktop {
    flex-basis: 100%;
    justify-content: center;
  }
}

@media (max-width: 767px) {
  .mini-scoreboard:not(.mini-scoreboard--mobile),
  .nav--desktop {
    display: none;
  }

  .header-inner {
    justify-content: space-between;
  }

  .hamburger {
    display: inline-flex;
  }
}
</style>
