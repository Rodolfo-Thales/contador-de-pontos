<script setup></script>

<template>
  <div class="quest-grid">
    <article class="quest-card" style="--i: 0">
      <div class="quest-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
          <circle cx="9" cy="7" r="4" />
          <path d="M19 8v6M22 11h-6" />
        </svg>
      </div>
      <h3 class="quest-title">Recrutar Visitantes</h3>
      <p class="quest-desc">Traga novos visitantes para os cultos</p>
      <p class="quest-points">+50 pts por visitante</p>
      <p class="quest-bonus">Converteu? +100 pts!</p>
    </article>

    <article class="quest-card quest-card--epic" style="--i: 1">
      <span class="quest-badge">Épica</span>
      <div class="quest-icon quest-icon--epic">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M12 2s7 8.5 7 13a7 7 0 0 1-14 0c0-4.5 7-13 7-13z" />
        </svg>
      </div>
      <h3 class="quest-title">Inscrição para Batismo</h3>
      <p class="quest-desc">Inspire alguém a se inscrever para o batismo</p>
      <p class="quest-points">+200 pts por inscrição</p>
    </article>

    <article class="quest-card" style="--i: 2">
      <div class="quest-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M21 8l-9-5-9 5 9 5 9-5z" />
          <path d="M3 8v8l9 5 9-5V8" />
          <path d="M12 13v8" />
        </svg>
      </div>
      <h3 class="quest-title">Arrecadar Alimentos</h3>
      <p class="quest-desc">Cada kg de alimento arrecadado conta</p>
      <p class="quest-points">+5 pts por kg</p>
      <p class="quest-subinfo">10kg = 50pts · 50kg = 250pts · 100kg = 500pts</p>
    </article>

    <article class="quest-card" style="--i: 3">
      <div class="quest-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M12 2v3M10 3h4" />
          <path d="M12 5l9 7h-2v9H5v-9H3l9-7z" />
          <path d="M10 21v-6h4v6" />
        </svg>
      </div>
      <h3 class="quest-title">Presença nos Cultos</h3>
      <p class="quest-desc">Marque presença e pontue para sua equipe</p>
      <ul class="quest-schedule">
        <li><span>Segunda</span> Culto da Vitória</li>
        <li><span>Quarta</span> Culto da Palavra</li>
        <li><span>Quinta</span> Interligados</li>
        <li class="quest-schedule--highlight">
          <span>Sábado</span> Connect
          <span class="quest-schedule-badge">Jovens</span>
        </li>
        <li><span>Domingo</span> Culto de Celebração</li>
      </ul>
    </article>
  </div>
</template>

<style scoped>
.quest-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-6);
}

.quest-card {
  position: relative;
  background-color: rgba(18, 19, 26, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  transition: transform var(--transition-base), box-shadow var(--transition-base);
  animation: card-in 0.6s ease both;
  animation-delay: calc(var(--i) * 0.15s);
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.quest-card:hover {
  transform: translateY(-4px) scale(1.01);
  box-shadow: 0 0 20px rgba(255, 106, 0, 0.15);
}

.quest-card--epic {
  border-color: var(--color-gold);
  box-shadow: 0 0 24px rgba(255, 215, 0, 0.2);
}

.quest-card--epic:hover {
  box-shadow: 0 0 30px rgba(255, 215, 0, 0.35);
}

.quest-badge {
  position: absolute;
  top: var(--space-4);
  right: var(--space-4);
  padding: 2px var(--space-2);
  border-radius: 999px;
  background-color: var(--color-gold);
  color: var(--color-badge-ink);
  font-size: 0.7rem;
  font-weight: 800;
  text-transform: uppercase;
}

.quest-icon {
  width: 48px;
  height: 48px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background-color: rgba(255, 106, 0, 0.12);
  color: var(--color-connect-solid);
  margin-bottom: var(--space-2);
}

.quest-icon svg {
  width: 24px;
  height: 24px;
}

.quest-icon--epic {
  background-color: rgba(255, 215, 0, 0.15);
  color: var(--color-gold);
  filter: drop-shadow(0 0 6px rgba(255, 215, 0, 0.5));
}

.quest-title {
  font-size: 1.1rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.quest-desc {
  color: var(--color-muted);
  font-size: 0.9rem;
}

.quest-points {
  font-family: var(--font-score);
  font-weight: 700;
  color: var(--color-foreground);
}

.quest-bonus {
  color: var(--color-gold);
  font-weight: 700;
  font-size: 0.85rem;
  text-shadow: 0 0 8px rgba(255, 215, 0, 0.5);
}

.quest-subinfo {
  color: var(--color-muted);
  font-size: 0.8rem;
}

.quest-schedule {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  font-size: 0.875rem;
}

.quest-schedule li {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-1) 0;
  color: var(--color-muted);
}

.quest-schedule li span:first-child {
  min-width: 68px;
  font-weight: 600;
  color: var(--color-foreground);
}

.quest-schedule--highlight {
  color: var(--color-connect-solid) !important;
  font-weight: 700;
}

.quest-schedule--highlight span:first-child {
  color: var(--color-connect-solid);
}

.quest-schedule-badge {
  min-width: 0 !important;
  padding: 1px var(--space-2);
  border-radius: 999px;
  background-color: var(--color-badge-fill);
  color: var(--color-foreground);
  font-size: 0.65rem;
  font-weight: 800;
  text-transform: uppercase;
}

@media (max-width: 768px) {
  .quest-grid {
    grid-template-columns: 1fr;
  }

  .quest-card {
    padding: var(--space-4);
  }
}
</style>
