<script setup>
defineProps({
  revealed: { type: Boolean, default: false },
})
</script>

<template>
  <div class="qg">
    <div class="qc rv dl1" :class="{ v: revealed }">
      <div class="qi">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
          <circle cx="9" cy="7" r="4" />
          <path d="M19 8v6M22 11h-6" />
        </svg>
      </div>
      <div class="qt">Recrutar Visitantes</div>
      <div class="qd">Traga novos visitantes para os cultos e fortaleça sua equipe</div>
      <div class="qr">+50 pts por visitante</div>
      <div class="qb">Converteu? +100 pts</div>
    </div>

    <div class="qc ep rv dl2" :class="{ v: revealed }">
      <span class="qeb">Quest Épica</span>
      <div class="qi qi--epic">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M12 2s7 8.5 7 13a7 7 0 0 1-14 0c0-4.5 7-13 7-13z" />
        </svg>
      </div>
      <div class="qt qt--epic">Inscrição para Batismo</div>
      <div class="qd">Inspire alguém a dar o próximo passo na fé</div>
      <div class="qr qr--epic">+200 pts por inscrição</div>
    </div>

    <div class="qc rv dl3" :class="{ v: revealed }">
      <div class="qi">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M21 8l-9-5-9 5 9 5 9-5z" />
          <path d="M3 8v8l9 5 9-5V8" />
          <path d="M12 13v8" />
        </svg>
      </div>
      <div class="qt">Arrecadar Alimentos</div>
      <div class="qd">Cada quilograma de alimento arrecadado conta pontos</div>
      <div class="qr">+5 pts por kg</div>
      <div class="qx">10kg = 50pts · 50kg = 250pts · 100kg = 500pts</div>
    </div>

    <div class="qc rv dl4" :class="{ v: revealed }">
      <div class="qi">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M12 2v3M10 3h4" />
          <path d="M12 5l9 7h-2v9H5v-9H3l9-7z" />
          <path d="M10 21v-6h4v6" />
        </svg>
      </div>
      <div class="qt">Presença nos Cultos</div>
      <div class="qd">Marque presença e pontue nos cultos da semana</div>
      <div class="qr">Pontos por presença</div>
      <ul class="qcl">
        <li><span class="dia">Segunda</span> Culto da Vitória</li>
        <li><span class="dia">Quarta</span> Culto da Palavra</li>
        <li><span class="dia">Quinta</span> Interligados</li>
        <li class="cr"><span class="dia">Sábado</span> Connect <span class="cchip">Jovens</span></li>
        <li><span class="dia">Domingo</span> Culto de Celebração</li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.qg {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
  max-width: 860px;
  margin: 0 auto;
}

.qc {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  padding: 26px 22px;
  position: relative;
  transition: all 0.25s;
}

.qc.v:hover {
  transform: translateY(-2px);
  border-color: var(--color-border-strong);
}

.qc.ep {
  border-color: rgba(212, 168, 64, 0.2);
}

.qc.ep:hover {
  box-shadow: 0 0 25px rgba(212, 168, 64, 0.06);
  border-color: rgba(212, 168, 64, 0.35);
}

.qc.ep::before {
  content: '';
  position: absolute;
  top: 0;
  left: 20%;
  right: 20%;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--color-gold), transparent);
}

.qeb {
  position: absolute;
  top: 10px;
  right: 10px;
  font-family: var(--font-display);
  font-size: 0.45rem;
  font-weight: 800;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  padding: 2px 8px;
  border-radius: 3px;
  background: var(--color-gold);
  color: var(--color-badge-ink);
}

.qi {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  margin-bottom: 14px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--color-border);
  color: var(--color-connect-solid);
}

.qi svg {
  width: 22px;
  height: 22px;
}

.qi--epic {
  border-color: rgba(212, 168, 64, 0.15);
  color: var(--color-gold);
}

.qt {
  font-family: var(--font-display);
  font-size: 0.9rem;
  font-weight: 800;
  letter-spacing: 1px;
  text-transform: uppercase;
  margin-bottom: 6px;
  color: var(--color-foreground);
}

.qt--epic {
  color: var(--color-gold-bright);
}

.qd {
  font-size: 0.8rem;
  color: var(--color-muted);
  margin-bottom: 14px;
}

.qr {
  font-family: var(--font-display);
  font-size: 0.85rem;
  font-weight: 800;
  color: var(--color-success);
}

.qr--epic {
  font-size: 0.95rem;
}

.qb {
  display: inline-block;
  margin-top: 8px;
  font-family: var(--font-display);
  font-size: 0.7rem;
  font-weight: 700;
  color: var(--color-gold-bright);
  padding: 4px 12px;
  border-radius: 5px;
  background: rgba(212, 168, 64, 0.08);
  border: 1px solid rgba(212, 168, 64, 0.12);
}

.qx {
  font-size: 0.72rem;
  color: var(--color-muted);
  margin-top: 6px;
}

.qcl {
  list-style: none;
  margin-top: 10px;
}

.qcl li {
  font-size: 0.75rem;
  color: var(--color-muted);
  padding: 4px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.qcl .dia {
  font-weight: 700;
  min-width: 62px;
  color: var(--color-foreground);
}

.qcl .cr {
  color: var(--color-connect-solid);
  font-weight: 700;
}

.qcl .cr .dia {
  color: var(--color-connect-solid);
}

.cchip {
  font-size: 0.45rem;
  padding: 1px 6px;
  border-radius: 3px;
  background: rgba(232, 101, 42, 0.12);
  color: var(--color-connect-solid);
  font-weight: 700;
  letter-spacing: 1px;
  text-transform: uppercase;
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

.dl1.v {
  transition-delay: 0.1s;
}

.dl2.v {
  transition-delay: 0.2s;
}

.dl3.v {
  transition-delay: 0.25s;
}

.dl4.v {
  transition-delay: 0.3s;
}

@media (max-width: 768px) {
  .qg {
    grid-template-columns: 1fr;
  }
}
</style>
