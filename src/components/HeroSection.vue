<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import CountdownTimer from './CountdownTimer.vue'
import { useCountUp } from '../composables/useCountUp'
import levelUpLogo from '../assets/logos/level-up-hero.png'

const heroBannerUrl = '/hero-banner.jpg'

const props = defineProps({
  groups: { type: Array, default: () => [] },
  leaderId: { type: String, default: null },
})

function pointsFor(id) {
  return props.groups.find((g) => g.id === id)?.points ?? 0
}

const levelUpPointsRaw = computed(() => pointsFor('level-up'))
const os300PointsRaw = computed(() => pointsFor('os-300'))

const { displayValue: levelUpDisplay } = useCountUp(levelUpPointsRaw)
const { displayValue: os300Display } = useCountUp(os300PointsRaw)

const diff = computed(() => Math.abs(levelUpPointsRaw.value - os300PointsRaw.value))
const total = computed(() => levelUpPointsRaw.value + os300PointsRaw.value)

const levelUpPct = computed(() => (total.value > 0 ? (levelUpPointsRaw.value / total.value) * 100 : 50))
const os300Pct = computed(() => 100 - levelUpPct.value)

const category = computed(() => {
  if (diff.value === 0) return 'tie'
  if (diff.value < 100) return 'close'
  if (diff.value > 500) return 'blowout'
  return 'normal'
})

function isLeader(id) {
  return props.leaderId === id
}

function isTrailing(id) {
  return props.leaderId !== null && props.leaderId !== id
}

function showCrown(id) {
  return isLeader(id) && (category.value === 'normal' || category.value === 'blowout')
}

function badgeFor(id) {
  if (!showCrown(id)) return null
  return category.value === 'blowout' ? 'DOMINANDO!' : 'LIDERANDO'
}

const centerBadgeText = computed(() => {
  if (category.value === 'tie') return 'EMPATADOS!'
  if (category.value === 'close') return 'DISPUTA ACIRRADA!'
  return null
})

const blowoutText = computed(() =>
  category.value === 'blowout' ? `+${diff.value.toLocaleString('pt-BR')} pontos na frente` : null,
)

function cardClasses(id) {
  return {
    'team-card--leader': showCrown(id),
    'team-card--trailing': isTrailing(id) && category.value === 'blowout',
    'team-card--close-pulse': category.value === 'close',
  }
}

function formatScore(value) {
  return Math.round(value).toLocaleString('pt-BR')
}

// Parallax sutil: desloca o fundo em função do scroll, throttled via rAF.
const bgOffset = ref(0)
const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
let ticking = false

function onScroll() {
  if (reduceMotion || ticking) return
  ticking = true
  requestAnimationFrame(() => {
    bgOffset.value = Math.min(60, window.scrollY * 0.15)
    ticking = false
  })
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<template>
  <section id="scoreboard" class="hero" aria-label="Placar principal">
    <img
      :src="heroBannerUrl"
      alt=""
      class="hero-bg"
      loading="eager"
      fetchpriority="high"
      :style="{ transform: `translateY(${bgOffset}px)` }"
    />
    <div class="hero-overlay"></div>
    <div class="hero-glow" aria-hidden="true"></div>
    <div class="hero-grain" aria-hidden="true"></div>

    <div class="hero-content">
      <div class="hero-countdown">
        <CountdownTimer />
      </div>

      <div class="hero-scoreboard">
        <article class="team-card team-card--level-up" :class="cardClasses('level-up')">
          <svg v-if="showCrown('level-up')" class="crown" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
            <path d="M5 16L3 6l5.5 4L12 4l3.5 6L21 6l-2 10H5zm14 3a1 1 0 01-1 1H6a1 1 0 01-1-1v-1h14v1z" />
          </svg>
          <span v-if="badgeFor('level-up')" class="team-badge">{{ badgeFor('level-up') }}</span>
          <img :src="levelUpLogo" alt="Level Up" class="team-logo" loading="eager" />
          <p class="team-name team-name--level-up">Level Up</p>
          <p class="team-score team-score--level-up">{{ formatScore(levelUpDisplay) }}</p>
        </article>

        <div class="vs-central">
          <span class="vs-divider" aria-hidden="true"></span>
          <span class="vs-text">VS</span>
          <span class="vs-divider" aria-hidden="true"></span>
          <p v-if="centerBadgeText" class="center-badge" :class="`center-badge--${category}`">
            {{ centerBadgeText }}
          </p>
          <p v-if="blowoutText" class="blowout-text">{{ blowoutText }}</p>
        </div>

        <article class="team-card team-card--os-300" :class="cardClasses('os-300')">
          <svg v-if="showCrown('os-300')" class="crown" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
            <path d="M5 16L3 6l5.5 4L12 4l3.5 6L21 6l-2 10H5zm14 3a1 1 0 01-1 1H6a1 1 0 01-1-1v-1h14v1z" />
          </svg>
          <span v-if="badgeFor('os-300')" class="team-badge">{{ badgeFor('os-300') }}</span>
          <div class="os300-badge" aria-hidden="true">
            <span class="os300-number">300</span>
          </div>
          <p class="team-name team-name--os-300">Os 300</p>
          <p class="team-score team-score--os-300">{{ formatScore(os300Display) }}</p>
        </article>
      </div>

      <div class="tug-of-war">
        <div class="tug-bar">
          <div class="tug-fill tug-fill--level-up" :style="{ width: levelUpPct + '%' }"></div>
          <div class="tug-fill tug-fill--os-300" :style="{ width: os300Pct + '%' }"></div>
          <div class="tug-marker" aria-hidden="true"></div>
        </div>
        <div class="tug-labels">
          <span>{{ levelUpPct.toFixed(0) }}%</span>
          <span>{{ os300Pct.toFixed(0) }}%</span>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.hero {
  position: relative;
  scroll-margin-top: 56px;
  min-height: 700px;
  height: 100vh;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  overflow: hidden;
  isolation: isolate;
  /* Fallback visivel enquanto a imagem carrega (ou se faltar) */
  background: linear-gradient(160deg, #1a0f08 0%, #0a0a14 55%, #14060a 100%);
}

.hero-bg {
  position: absolute;
  inset: -10% 0;
  width: 100%;
  height: 120%;
  object-fit: cover;
  object-position: center;
  will-change: transform;
  z-index: -3;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    rgba(10, 10, 20, 0.7) 0%,
    rgba(10, 10, 20, 0.3) 45%,
    rgba(10, 10, 20, 0.95) 100%
  );
  z-index: -2;
}

.hero-glow {
  position: absolute;
  top: 30%;
  left: 50%;
  width: 600px;
  height: 600px;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 215, 0, 0.16) 0%, rgba(255, 215, 0, 0) 70%);
  animation: glow-pulse 3s ease-in-out infinite;
  z-index: -1;
  pointer-events: none;
}

@keyframes glow-pulse {
  0%,
  100% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
}

.hero-grain {
  position: absolute;
  inset: 0;
  z-index: -1;
  pointer-events: none;
  opacity: 0.05;
  background-image: radial-gradient(rgba(255, 255, 255, 0.8) 1px, transparent 1px);
  background-size: 3px 3px;
  animation: grain-drift 20s linear infinite;
}

@keyframes grain-drift {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 120px 80px;
  }
}

.hero-content {
  position: relative;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--space-8) var(--space-4) var(--space-12);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-12);
}

.hero-countdown {
  animation: fade-slide-down 0.6s ease both;
  animation-delay: 0.2s;
}

@keyframes fade-slide-down {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-scoreboard {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-8);
  animation: fade-scale-in 0.6s ease both;
  animation-delay: 0.4s;
}

@keyframes fade-scale-in {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.team-card {
  position: relative;
  flex: 1;
  max-width: 340px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-8) var(--space-6);
  border-radius: 12px;
  background-color: rgba(10, 10, 20, 0.5);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.06);
  transition: opacity var(--transition-base), box-shadow var(--transition-base);
}

.team-card--leader.team-card--level-up {
  box-shadow: 0 0 20px rgba(30, 144, 255, 0.3);
  border-color: rgba(30, 144, 255, 0.4);
}

.team-card--leader.team-card--os-300 {
  box-shadow: 0 0 20px rgba(220, 20, 60, 0.3);
  border-color: rgba(220, 20, 60, 0.4);
}

.team-card--leader.team-card--level-up.team-card--trailing,
.team-card--leader.team-card--os-300.team-card--trailing {
  /* dominando: glow ainda mais intenso no lider */
  box-shadow: 0 0 32px 4px rgba(255, 215, 0, 0.25);
}

.team-card--trailing:not(.team-card--leader) {
  opacity: 0.65;
}

.team-card--close-pulse {
  animation: card-pulse 1.6s ease-in-out infinite;
}

@keyframes card-pulse {
  0%,
  100% {
    box-shadow: 0 0 0 rgba(255, 215, 0, 0);
  }
  50% {
    box-shadow: 0 0 18px rgba(255, 215, 0, 0.35);
  }
}

.crown {
  position: absolute;
  top: -22px;
  width: 28px;
  height: 28px;
  color: var(--color-gold);
  filter: drop-shadow(0 0 6px rgba(255, 215, 0, 0.7));
  animation: crown-glow 2s ease-in-out infinite;
}

@keyframes crown-glow {
  0%,
  100% {
    filter: drop-shadow(0 0 4px rgba(255, 215, 0, 0.5));
  }
  50% {
    filter: drop-shadow(0 0 10px rgba(255, 215, 0, 0.9));
  }
}

.team-badge {
  display: inline-flex;
  align-items: center;
  padding: 2px var(--space-3);
  border-radius: 999px;
  background-color: var(--color-gold);
  color: var(--color-badge-ink);
  font-size: 0.7rem;
  font-weight: 800;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.team-logo {
  height: 160px;
  width: auto;
  border-radius: 8px;
}

.os300-badge {
  height: 160px;
  width: 160px;
  display: grid;
  place-items: center;
  clip-path: polygon(25% 0%, 75% 0%, 100% 50%, 75% 100%, 25% 100%, 0% 50%);
  background: #8b0000;
  border: 3px solid #daa520;
}

.os300-number {
  font-family: Georgia, 'Times New Roman', serif;
  font-weight: 700;
  font-size: 2.5rem;
  color: #f0e6c8;
}

.team-name {
  text-transform: uppercase;
  font-weight: 800;
  font-size: 1.25rem;
  letter-spacing: 1px;
  /* Garante legibilidade sobre a foto do banner, independente do que estiver atras */
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.8);
}

.team-name--level-up {
  color: var(--color-group-b);
}

.team-name--os-300 {
  color: var(--color-group-a);
}

.team-score {
  font-family: var(--font-score);
  font-weight: 800;
  font-size: 4rem;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}

.team-score--level-up {
  background: var(--gradient-level-up);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.team-score--os-300 {
  background: var(--gradient-os-300);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.vs-central {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
  flex-shrink: 0;
}

.vs-divider {
  width: 2px;
  height: 24px;
  background: linear-gradient(to bottom, transparent, var(--color-gold), transparent);
}

.vs-text {
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 1.5rem;
  color: var(--color-gold);
  text-shadow: 0 0 12px rgba(255, 215, 0, 0.6);
  animation: vs-pulse 2s ease-in-out infinite;
}

@keyframes vs-pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.center-badge {
  white-space: nowrap;
  font-size: 0.8rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: var(--space-1) var(--space-3);
  border-radius: 999px;
}

.center-badge--close {
  color: var(--color-badge-ink);
  background-color: var(--color-gold);
  animation: badge-pulse 1.2s ease-in-out infinite;
}

.center-badge--tie {
  color: var(--color-foreground);
  background-color: var(--color-surface-hover);
  border: 1px solid var(--color-gold);
}

@keyframes badge-pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}

.blowout-text {
  font-size: 0.75rem;
  color: var(--color-gold);
  font-weight: 600;
  white-space: nowrap;
}

.tug-of-war {
  width: 100%;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  animation: grow-from-center 0.6s ease both;
  animation-delay: 0.6s;
}

@keyframes grow-from-center {
  from {
    opacity: 0;
    transform: scaleX(0);
  }
  to {
    opacity: 1;
    transform: scaleX(1);
  }
}

.tug-bar {
  position: relative;
  height: 8px;
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  background-color: var(--color-surface);
}

.tug-fill {
  height: 100%;
  transition: width 1s ease;
}

.tug-fill--level-up {
  background: var(--gradient-level-up);
}

.tug-fill--os-300 {
  background: var(--gradient-os-300);
}

.tug-marker {
  position: absolute;
  top: 0;
  left: 50%;
  width: 2px;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.7);
}

.tug-labels {
  display: flex;
  justify-content: space-between;
  font-size: 0.75rem;
  color: var(--color-muted);
  font-variant-numeric: tabular-nums;
}

@media (max-width: 768px) {
  .hero-scoreboard {
    flex-direction: column;
  }

  .team-card {
    max-width: none;
    width: 100%;
  }

  .team-logo,
  .os300-badge {
    height: 80px;
    width: 80px;
  }

  .os300-number {
    font-size: 1.5rem;
  }

  .team-score {
    font-size: 2.5rem;
  }

  .vs-central {
    flex-direction: row;
    gap: var(--space-3);
  }

  .vs-divider {
    width: 24px;
    height: 2px;
  }
}
</style>
