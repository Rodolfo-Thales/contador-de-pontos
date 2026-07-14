<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import createGlobe from 'cobe'

// Frases inspiradas em Marcos 16:15 e na missão da igreja
const MARKERS = [
  { id: 'm1', location: [31.77, 35.21], text: 'Ide por todo o mundo', color: '#7c3aed', rotate: -6 },
  { id: 'm2', location: [-22.91, -43.17], text: 'pregai o evangelho', color: '#e11d48', rotate: 4 },
  { id: 'm3', location: [35.68, 139.65], text: 'a toda criatura', color: '#2563eb', rotate: -4 },
  { id: 'm4', location: [40.71, -74.01], text: 'Boas novas!', color: '#e76f51', rotate: 5 },
  { id: 'm5', location: [51.51, -0.13], text: 'Cristo vive', color: '#2a9d8f', rotate: -5 },
  { id: 'm6', location: [-33.87, 151.21], text: 'até os confins da terra', color: '#b0813c', rotate: 6 },
  { id: 'm7', location: [-1.29, 36.82], text: 'toda tribo e nação', color: '#457b9d', rotate: -3 },
  { id: 'm8', location: [14.6, 120.98], text: 'Ele voltará!', color: '#9d4edd', rotate: 3 },
]

function hexToRgb(hex) {
  const n = Number.parseInt(hex.slice(1), 16)
  return [((n >> 16) & 255) / 255, ((n >> 8) & 255) / 255, (n & 255) / 255]
}

const BASE_THETA = 0.2
const DEG = Math.PI / 180

const canvasRef = ref(null)
const labelRefs = []

let globe = null
let resizeObserver = null
let animationId = 0
let phi = 0
let phiOffset = 0
let thetaOffset = 0
let drag = { phi: 0, theta: 0 }
let pointerStart = null
let paused = false

const speed = window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 0 : 0.003

function onPointerDown(e) {
  pointerStart = { x: e.clientX, y: e.clientY }
  paused = true
  canvasRef.value.style.cursor = 'grabbing'
}

function onPointerMove(e) {
  if (!pointerStart) return
  drag = {
    phi: (e.clientX - pointerStart.x) / 300,
    theta: (e.clientY - pointerStart.y) / 1000,
  }
}

function onPointerUp() {
  if (!pointerStart) return
  phiOffset += drag.phi
  thetaOffset += drag.theta
  drag = { phi: 0, theta: 0 }
  pointerStart = null
  paused = false
  if (canvasRef.value) canvasRef.value.style.cursor = 'grab'
}

/**
 * Projeta lat/long na tela para posicionar as etiquetas junto dos marcadores.
 * Convenção do cobe: phi = 3π/2 − longitude centraliza aquela longitude.
 */
function updateLabels(currentPhi, currentTheta, width) {
  const radius = width * 0.49
  const center = width / 2
  for (let i = 0; i < MARKERS.length; i++) {
    const el = labelRefs[i]
    if (!el) continue
    const [lat, lng] = MARKERS[i].location
    const latRad = lat * DEG
    const delta = lng * DEG + currentPhi - Math.PI * 1.5
    const x = Math.cos(latRad) * Math.sin(delta)
    const y = Math.sin(latRad)
    const z = Math.cos(latRad) * Math.cos(delta)
    const yTilted = y * Math.cos(currentTheta) - z * Math.sin(currentTheta)
    const depth = y * Math.sin(currentTheta) + z * Math.cos(currentTheta)
    const visible = Math.min(1, Math.max(0, (depth - 0.15) / 0.3))
    el.style.left = `${center + radius * x}px`
    el.style.top = `${center - radius * yTilted}px`
    el.style.opacity = visible
    el.style.filter = `blur(${(1 - visible) * 6}px)`
  }
}

function init() {
  const canvas = canvasRef.value
  const width = canvas.offsetWidth
  if (!width || globe) return

  const dpr = Math.min(window.devicePixelRatio || 1, 2)
  globe = createGlobe(canvas, {
    devicePixelRatio: dpr,
    width: width * dpr,
    height: width * dpr,
    phi: 0,
    theta: BASE_THETA,
    dark: 1,
    diffuse: 1.2,
    mapSamples: 16000,
    mapBrightness: 9,
    baseColor: [0.55, 0.6, 0.75],
    markerColor: [0.49, 0.23, 0.93],
    glowColor: [0.15, 0.16, 0.22],
    markers: MARKERS.map((m) => ({
      id: m.id,
      location: m.location,
      size: 0.06,
      color: hexToRgb(m.color),
    })),
  })

  // cobe v2 nao tem loop interno: cada frame e desenhado via update()
  function animate() {
    if (!paused) phi += speed
    const p = phi + phiOffset + drag.phi
    const t = BASE_THETA + thetaOffset + drag.theta
    globe.update({ phi: p, theta: t })
    updateLabels(p, t, canvas.offsetWidth)
    animationId = requestAnimationFrame(animate)
  }
  animate()

  requestAnimationFrame(() => {
    canvas.style.opacity = '1'
  })
}

onMounted(() => {
  window.addEventListener('pointermove', onPointerMove, { passive: true })
  window.addEventListener('pointerup', onPointerUp, { passive: true })

  if (canvasRef.value.offsetWidth > 0) {
    init()
  } else {
    resizeObserver = new ResizeObserver((entries) => {
      if (entries[0]?.contentRect.width > 0) {
        resizeObserver.disconnect()
        init()
      }
    })
    resizeObserver.observe(canvasRef.value)
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('pointermove', onPointerMove)
  window.removeEventListener('pointerup', onPointerUp)
  cancelAnimationFrame(animationId)
  resizeObserver?.disconnect()
  globe?.destroy()
})
</script>

<template>
  <div class="globe">
    <canvas ref="canvasRef" class="globe-canvas" @pointerdown="onPointerDown" />
    <div
      v-for="(m, i) in MARKERS"
      :key="m.id"
      :ref="(el) => (labelRefs[i] = el)"
      class="globe-label"
      :style="{ background: m.color, '--rotate': m.rotate + 'deg' }"
      aria-hidden="true"
    >
      <span class="globe-label-gloss"></span>
      {{ m.text }}
    </div>
  </div>
</template>

<style scoped>
.globe {
  position: relative;
  aspect-ratio: 1;
  user-select: none;
}

.globe-canvas {
  width: 100%;
  height: 100%;
  cursor: grab;
  opacity: 0;
  transition: opacity 1.2s ease;
  border-radius: 50%;
  touch-action: none;
  display: block;
}

.globe-label {
  position: absolute;
  transform: translate(-50%, calc(-100% - 12px)) rotate(var(--rotate));
  padding: 0.4rem 0.65rem 0.35rem;
  color: #fff;
  font-family: var(--font-display);
  font-size: 0.8rem;
  font-weight: 600;
  letter-spacing: 0.01em;
  white-space: nowrap;
  border-radius: 4px;
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.2),
    0 3px 8px rgba(0, 0, 0, 0.1),
    inset 0 -1px 0 rgba(0, 0, 0, 0.15);
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.25);
  pointer-events: none;
  overflow: hidden;
  opacity: 0;
}

.globe-label-gloss {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 50%;
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.35) 0%,
    rgba(255, 255, 255, 0.1) 60%,
    transparent 100%
  );
  border-radius: 4px 4px 50% 50%;
  pointer-events: none;
}
</style>
