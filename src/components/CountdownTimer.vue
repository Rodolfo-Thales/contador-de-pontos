<script setup>
import { useCountdown } from '../composables/useCountdown'

const TARGET_DATE = new Date(2026, 7, 30, 23, 59, 59)

const { days, hours, minutes, seconds, isUrgent, isCritical, isOver } = useCountdown(TARGET_DATE)

function pad(value) {
  return String(value).padStart(2, '0')
}
</script>

<template>
  <div class="countdown" :class="{ 'countdown--urgent': isUrgent, 'countdown--critical': isCritical }">
    <p class="countdown-title">{{ isOver ? 'A gincana terminou!' : 'A batalha termina em:' }}</p>

    <div v-if="!isOver" class="countdown-grid" role="timer" aria-live="off">
      <div class="unit">
        <span class="unit-value">{{ pad(days) }}</span>
        <span class="unit-label">Dias</span>
      </div>
      <span class="colon" aria-hidden="true">:</span>
      <div class="unit">
        <span class="unit-value">{{ pad(hours) }}</span>
        <span class="unit-label">Horas</span>
      </div>
      <span class="colon" aria-hidden="true">:</span>
      <div class="unit">
        <span class="unit-value">{{ pad(minutes) }}</span>
        <span class="unit-label">Min</span>
      </div>
      <span class="colon" aria-hidden="true">:</span>
      <div class="unit">
        <span class="unit-value">{{ pad(seconds) }}</span>
        <span class="unit-label">Seg</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.countdown {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
}

.countdown-title {
  text-transform: uppercase;
  font-size: 0.9rem;
  letter-spacing: 3px;
  color: var(--color-muted);
  font-weight: 600;
}

.countdown-grid {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.unit {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  min-width: 72px;
  padding: var(--space-3) var(--space-2);
  border-radius: var(--radius-sm);
  background-color: rgba(10, 10, 20, 0.6);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: border-color var(--transition-base), box-shadow var(--transition-base);
}

.unit-value {
  font-family: var(--font-score);
  font-weight: 700;
  font-size: 2.5rem;
  line-height: 1;
  color: var(--color-foreground);
  font-variant-numeric: tabular-nums;
  transition: color var(--transition-base), text-shadow var(--transition-base);
}

.unit-label {
  font-size: 0.65rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--color-muted);
}

.colon {
  font-family: var(--font-score);
  font-weight: 700;
  font-size: 2rem;
  color: var(--color-muted);
  animation: colon-pulse 1s step-start infinite;
}

@keyframes colon-pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}

.countdown--urgent .unit-value {
  color: var(--color-group-a);
  text-shadow: 0 0 10px var(--color-group-a);
}

.countdown--critical .unit {
  border-color: var(--color-group-a);
  animation: unit-pulse 1.2s ease-in-out infinite;
}

@keyframes unit-pulse {
  0%,
  100% {
    box-shadow: 0 0 0 rgba(220, 20, 60, 0);
  }
  50% {
    box-shadow: 0 0 16px rgba(220, 20, 60, 0.5);
  }
}

@media (max-width: 480px) {
  .countdown-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-3);
  }

  .colon {
    display: none;
  }

  .unit-value {
    font-size: 1.5rem;
  }
}
</style>
