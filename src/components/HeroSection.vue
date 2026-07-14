<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import CountdownTimer from './CountdownTimer.vue'
import { useCountUp } from '../composables/useCountUp'
import levelUpLogo from '../assets/logos/level-up-hero.png'
import os300Logo from '../assets/logos/os300-hero.jpg'

const props = defineProps({
  groups: { type: Array, default: () => [] },
  leaderId: { type: String, default: null },
})

const heroBannerUrl = '/hero-banner.jpg'

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
  return isLeader(id) && category.value !== 'close'
}

function badgeFor(id) {
  if (!showCrown(id)) return null
  return category.value === 'blowout' ? 'Dominando' : 'Liderando'
}

const diffBadgeText = computed(() => {
  if (category.value === 'tie') return 'Empatados!'
  if (category.value === 'close') return 'Disputa acirrada!'
  return `+${diff.value.toLocaleString('pt-BR')} à frente`
})

function cardClasses(id) {
  return {
    winner: showCrown(id),
    'team-side--trailing': isTrailing(id) && category.value === 'blowout',
    'team-side--close': category.value === 'close',
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

// Sistema de particulas: azul flutuando a esquerda, vermelho a direita.
const canvasRef = ref(null)
let ctx, particles, w, h, animationId, resizeObserver

class Particle {
  constructor() {
    this.reset()
  }

  reset() {
    this.side = Math.random() < 0.5 ? 'left' : 'right'
    if (this.side === 'left') {
      this.x = Math.random() * (w * 0.4)
      this.color = [59, 158, 255]
    } else {
      this.x = w * 0.6 + Math.random() * (w * 0.4)
      this.color = [232, 67, 48]
    }
    this.y = h + Math.random() * 40
    this.r = Math.random() * 2.2 + 0.6
    this.vy = -(Math.random() * 1.2 + 0.3)
    this.vx = (Math.random() - 0.5) * 0.4
    this.alpha = Math.random() * 0.5 + 0.15
    this.maxAlpha = this.alpha
    this.life = 0
    this.maxLife = Math.random() * 250 + 150
    this.shimmer = Math.random() * Math.PI * 2
    this.shimmerSpeed = Math.random() * 0.03 + 0.01
  }

  update() {
    this.life++
    this.x += this.vx
    this.y += this.vy
    this.shimmer += this.shimmerSpeed
    const lifeRatio = this.life / this.maxLife
    if (lifeRatio < 0.2) {
      this.alpha = this.maxAlpha * (lifeRatio / 0.2)
    } else if (lifeRatio > 0.7) {
      this.alpha = this.maxAlpha * (1 - (lifeRatio - 0.7) / 0.3)
    }
    if (this.life >= this.maxLife || this.y < -20) this.reset()
  }

  draw() {
    const a = Math.max(0, this.alpha + Math.sin(this.shimmer) * 0.15)
    const [r, g, b] = this.color
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.r, 0, Math.PI * 2)
    ctx.fillStyle = `rgba(${r},${g},${b},${a.toFixed(3)})`
    ctx.fill()
    if (this.r > 1.4) {
      ctx.beginPath()
      ctx.arc(this.x, this.y, this.r * 3, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(${r},${g},${b},${(a * 0.12).toFixed(3)})`
      ctx.fill()
    }
  }
}

function setupParticles() {
  const canvas = canvasRef.value
  ctx = canvas.getContext('2d')
  w = canvas.width = canvas.offsetWidth
  h = canvas.height = canvas.offsetHeight

  const count = Math.min(90, Math.floor((w * h) / 12000))
  particles = Array.from({ length: count }, () => {
    const p = new Particle()
    p.life = Math.floor(Math.random() * p.maxLife)
    p.y = Math.random() * h
    return p
  })

  function animate() {
    ctx.clearRect(0, 0, w, h)
    for (const p of particles) {
      p.update()
      p.draw()
    }
    animationId = requestAnimationFrame(animate)
  }
  animate()
}

function handleResize() {
  const canvas = canvasRef.value
  if (!canvas) return
  w = canvas.width = canvas.offsetWidth
  h = canvas.height = canvas.offsetHeight
}

onMounted(() => {
  window.addEventListener('scroll', onScroll, { passive: true })
  if (!reduceMotion) {
    setupParticles()
    window.addEventListener('resize', handleResize)
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  window.removeEventListener('resize', handleResize)
  cancelAnimationFrame(animationId)
})
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
    <canvas ref="canvasRef" class="hero-particles" aria-hidden="true"></canvas>

    <div class="hero-content">
      <p class="hero-tagline">Somente um será lendário. O resto será história.</p>

      <div class="hero-countdown">
        <CountdownTimer />
      </div>

      <div class="hero-scoreboard">
        <div class="team-side team-side--level-up" :class="cardClasses('level-up')">
          <svg v-if="showCrown('level-up')" class="crown" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
            <path d="M5 16L3 6l5.5 4L12 4l3.5 6L21 6l-2 10H5zm14 3a1 1 0 01-1 1H6a1 1 0 01-1-1v-1h14v1z" />
          </svg>
          <span v-if="badgeFor('level-up')" class="team-badge">{{ badgeFor('level-up') }}</span>
          <div class="team-logo team-logo--blue">
            <img :src="levelUpLogo" alt="Level Up" loading="eager" />
          </div>
          <p class="team-name team-name--level-up">Level Up</p>
          <p class="team-score team-score--level-up">{{ formatScore(levelUpDisplay) }}</p>
          <p class="team-score-label">Pontos</p>
          <p class="team-motto">Crescer · Superar · Impactar</p>
        </div>

        <div class="vs-central">
          <div class="vs-diamond">
            <span>VS</span>
          </div>
          <p class="diff-badge" :class="`diff-badge--${category}`">{{ diffBadgeText }}</p>
        </div>

        <div class="team-side team-side--os-300" :class="cardClasses('os-300')">
          <svg v-if="showCrown('os-300')" class="crown" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
            <path d="M5 16L3 6l5.5 4L12 4l3.5 6L21 6l-2 10H5zm14 3a1 1 0 01-1 1H6a1 1 0 01-1-1v-1h14v1z" />
          </svg>
          <span v-if="badgeFor('os-300')" class="team-badge">{{ badgeFor('os-300') }}</span>
          <div class="team-logo team-logo--red">
            <img :src="os300Logo" alt="Os 300" loading="eager" />
          </div>
          <p class="team-name team-name--os-300">Os 300</p>
          <p class="team-score team-score--os-300">{{ formatScore(os300Display) }}</p>
          <p class="team-score-label">Pontos</p>
          <p class="team-motto">Unidos · Fortes · Fiéis</p>
        </div>
      </div>

      <div class="tug-of-war">
        <div class="tug-labels">
          <span class="tug-label tug-label--level-up">Level Up — {{ levelUpPct.toFixed(0) }}%</span>
          <span class="tug-label tug-label--os-300">Os 300 — {{ os300Pct.toFixed(0) }}%</span>
        </div>
        <div class="tug-bar">
          <div class="tug-fill tug-fill--level-up" :style="{ width: levelUpPct + '%' }"></div>
          <div class="tug-marker" aria-hidden="true"></div>
        </div>
      </div>

      <p class="hero-subtagline">Não é sobre quem começa. É sobre quem persiste.</p>
    </div>
  </section>
</template>

<style scoped>
.hero {
  position: relative;
  scroll-margin-top: 66px;
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  isolation: isolate;
  padding-top: 80px;
  padding-bottom: 40px;
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
    180deg,
    rgba(6, 6, 16, 0.55) 0%,
    rgba(6, 6, 16, 0.15) 22%,
    rgba(6, 6, 16, 0.08) 42%,
    rgba(6, 6, 16, 0.25) 62%,
    rgba(6, 6, 16, 0.85) 84%,
    rgba(6, 6, 16, 1) 100%
  );
  z-index: -2;
}

.hero-particles {
  position: absolute;
  inset: 0;
  z-index: -1;
  pointer-events: none;
  width: 100%;
  height: 100%;
}

.hero-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 1160px;
  margin: 0 auto;
  padding: 0 var(--space-4);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-6);
}

.hero-tagline {
  font-family: var(--font-display);
  font-weight: 800;
  font-size: clamp(0.7rem, 1.2vw, 0.9rem);
  letter-spacing: 5px;
  text-transform: uppercase;
  color: var(--color-gold-bright);
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.8), 0 0 30px rgba(212, 168, 64, 0.2);
  text-align: center;
  animation: fade-in 1s ease 0.2s both;
}

@keyframes fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.hero-countdown {
  animation: fade-slide-down 0.6s ease 0.3s both;
}

@keyframes fade-slide-down {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-scoreboard {
  width: 100%;
  max-width: 980px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fade-scale-in 0.7s ease 0.5s both;
}

@keyframes fade-scale-in {
  from {
    opacity: 0;
    transform: scale(0.94);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.team-side {
  flex: 1;
  position: relative;
  text-align: center;
  padding: var(--space-4) var(--space-6);
  transition: opacity var(--transition-base);
}

.team-side.winner::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: -1;
  pointer-events: none;
  border-radius: 50%;
}

.team-side--level-up.winner::before {
  background: radial-gradient(ellipse 70% 80% at 50% 40%, rgba(59, 158, 255, 0.18), transparent 65%);
}

.team-side--os-300.winner::before {
  background: radial-gradient(ellipse 70% 80% at 50% 40%, rgba(232, 67, 48, 0.18), transparent 65%);
}

.team-side--trailing:not(.winner) {
  opacity: 0.65;
}

.team-side--close .team-logo {
  animation: logo-pulse 1.6s ease-in-out infinite;
}

@keyframes logo-pulse {
  0%,
  100% {
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.5);
  }
  50% {
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.5), 0 0 24px rgba(212, 168, 64, 0.4);
  }
}

.crown {
  position: absolute;
  top: -14px;
  left: 50%;
  transform: translateX(-50%);
  width: 32px;
  height: 32px;
  color: var(--color-gold-bright);
  z-index: 10;
  filter: drop-shadow(0 4px 15px rgba(212, 168, 64, 0.5));
  animation: crown-float 3s ease-in-out infinite;
}

@keyframes crown-float {
  0%,
  100% {
    transform: translateX(-50%) translateY(0) rotate(-3deg);
  }
  50% {
    transform: translateX(-50%) translateY(-5px) rotate(3deg);
  }
}

.team-badge {
  display: inline-flex;
  align-items: center;
  font-family: var(--font-display);
  font-size: 0.55rem;
  font-weight: 800;
  letter-spacing: 2px;
  text-transform: uppercase;
  padding: 4px 16px;
  border-radius: 3px;
  margin-bottom: var(--space-3);
  background: linear-gradient(135deg, var(--color-gold), var(--color-gold-bright));
  color: var(--color-badge-ink);
  box-shadow: 0 2px 20px rgba(212, 168, 64, 0.3);
  animation: badge-glow 2.5s ease-in-out infinite;
}

@keyframes badge-glow {
  0%,
  100% {
    box-shadow: 0 2px 20px rgba(212, 168, 64, 0.25);
  }
  50% {
    box-shadow: 0 2px 30px rgba(212, 168, 64, 0.45);
  }
}

.team-logo {
  width: 140px;
  height: 140px;
  margin: 0 auto var(--space-3);
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.7);
  transition: border-color var(--transition-base), box-shadow var(--transition-base);
}

.team-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.team-logo--blue {
  border-color: rgba(59, 158, 255, 0.25);
}

.team-logo--red {
  border-color: rgba(232, 67, 48, 0.25);
}

.winner .team-logo--blue {
  border-color: rgba(59, 158, 255, 0.5);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.5), 0 0 30px rgba(59, 158, 255, 0.25);
}

.winner .team-logo--red {
  border-color: rgba(232, 67, 48, 0.5);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.5), 0 0 30px rgba(232, 67, 48, 0.25);
}

.team-name {
  font-family: var(--font-display);
  font-size: 1.2rem;
  font-weight: 800;
  letter-spacing: 3px;
  text-transform: uppercase;
  margin-bottom: 2px;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.9), 0 0 30px rgba(0, 0, 0, 0.6);
}

.team-name--level-up {
  color: var(--color-group-b);
}

.team-name--os-300 {
  color: var(--color-group-a);
}

.team-score {
  font-family: var(--font-score);
  font-size: 3.4rem;
  font-weight: 900;
  line-height: 1.05;
  color: var(--color-foreground);
}

.team-score--level-up {
  text-shadow: 0 0 40px rgba(59, 158, 255, 0.5), 0 2px 8px rgba(0, 0, 0, 0.6);
}

.team-score--os-300 {
  text-shadow: 0 0 40px rgba(232, 67, 48, 0.5), 0 2px 8px rgba(0, 0, 0, 0.6);
}

.team-score-label {
  font-size: 0.58rem;
  font-weight: 700;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--color-muted);
  margin-top: 2px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.9);
}

.team-motto {
  font-size: 0.58rem;
  font-weight: 600;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--color-muted);
  margin-top: var(--space-3);
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.9);
}

.vs-central {
  position: relative;
  z-index: 5;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
  margin: 0 calc(-1 * var(--space-4));
}

.vs-diamond {
  width: 64px;
  height: 64px;
  position: relative;
  background: linear-gradient(135deg, #1a1408, #0d0a04);
  transform: rotate(45deg);
  border: 2px solid var(--color-gold);
  box-shadow: 0 0 30px rgba(212, 168, 64, 0.35), inset 0 0 20px rgba(212, 168, 64, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  animation: diamond-glow 3s ease-in-out infinite;
}

@keyframes diamond-glow {
  0%,
  100% {
    box-shadow: 0 0 25px rgba(212, 168, 64, 0.3), inset 0 0 20px rgba(212, 168, 64, 0.08);
  }
  50% {
    box-shadow: 0 0 40px rgba(212, 168, 64, 0.5), inset 0 0 25px rgba(212, 168, 64, 0.15);
  }
}

.vs-diamond span {
  transform: rotate(-45deg);
  font-family: var(--font-accent);
  font-size: 1.5rem;
  letter-spacing: 2px;
  color: var(--color-gold-bright);
  text-shadow: 0 0 15px rgba(212, 168, 64, 0.5);
}

.diff-badge {
  font-family: var(--font-display);
  font-size: 0.62rem;
  font-weight: 800;
  color: var(--color-badge-ink);
  padding: 5px 14px;
  background: linear-gradient(135deg, var(--color-gold), var(--color-gold-bright));
  border-radius: 3px;
  white-space: nowrap;
  letter-spacing: 0.8px;
  text-transform: uppercase;
  box-shadow: 0 4px 20px rgba(212, 168, 64, 0.35);
}

.tug-of-war {
  width: 100%;
  max-width: 560px;
  animation: fade-slide-down 0.8s ease 0.9s both;
}

.tug-labels {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}

.tug-label {
  font-size: 0.68rem;
  font-weight: 700;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.8);
}

.tug-label--level-up {
  color: var(--color-group-b);
}

.tug-label--os-300 {
  color: var(--color-group-a);
}

.tug-bar {
  position: relative;
  width: 100%;
  height: 6px;
  border-radius: 3px;
  background-color: rgba(0, 0, 0, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.08);
  overflow: hidden;
}

.tug-fill {
  height: 100%;
  border-radius: 3px 0 0 3px;
  transition: width 1s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 0 12px rgba(59, 158, 255, 0.4);
}

.tug-fill--level-up {
  background: var(--gradient-level-up);
}

.tug-marker {
  position: absolute;
  left: 50%;
  top: -2px;
  width: 1px;
  height: 10px;
  background-color: rgba(255, 255, 255, 0.25);
}

.hero-subtagline {
  font-family: var(--font-display);
  font-size: 0.7rem;
  font-weight: 600;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--color-muted);
  text-align: center;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.9);
  animation: fade-in 1s ease 1.2s both;
}

@media (max-width: 768px) {
  .hero {
    min-height: auto;
    padding: 74px 12px 50px;
  }

  .hero-scoreboard {
    flex-direction: column;
    gap: var(--space-6);
  }

  .team-side {
    padding: var(--space-2) var(--space-4);
  }

  .vs-central {
    flex-direction: row;
    margin: 0;
    gap: var(--space-4);
  }

  .team-logo {
    width: 100px;
    height: 100px;
  }

  .team-score {
    font-size: 2.6rem;
  }
}
</style>
